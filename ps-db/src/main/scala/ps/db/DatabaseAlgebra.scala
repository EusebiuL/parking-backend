package ps.db

import cats.implicits._
import busymachines.core.{AnomalousFailure, ConflictFailure}
import cats.effect.{Async, Resource}
import doobie._
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import ps.core.{BlockingAlgebra, BlockingContext}
import ps.effects._

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait DatabaseAlgebra[F[_]] { this: BlockingAlgebra[F] =>

  private val UNIQUE_VIOLATION = SqlState("23505")

  protected def transact[A](
      query: ConnectionIO[A]
  )(
      implicit F: Async[F],
      transactor: Transactor[F],
      context: Resource[F, BlockingContext[F]]
  ): F[A] = {
    block(
      handleSqlStateErrors(query).transact(transactor).flatMap {
        case Left(anomaly) => F.raiseError[A](anomaly)
        case Right(x)      => F.pure[A](x)
      }
    )
  }

  protected def unit: ConnectionIO[Unit] = AsyncConnectionIO.unit
  protected def pure[A](value: A): ConnectionIO[A] =
    AsyncConnectionIO.pure(value)
  protected def raiseError[A](error: Throwable): ConnectionIO[A] =
    AsyncConnectionIO.raiseError(error)

  protected def exists[A](value: Option[A],
                          failure: AnomalousFailure): ConnectionIO[A] =
    value match {
      case Some(x) => pure[A](x)
      case None    => raiseError(failure)
    }

  private def handleSqlStateErrors[A](
      query: ConnectionIO[A]
  ): ConnectionIO[Either[AnomalousFailure, A]] =
    query.attemptSomeSqlState {
      case UNIQUE_VIOLATION =>
        ConflictFailure("There was a conflict. Please try again")
    }

}
