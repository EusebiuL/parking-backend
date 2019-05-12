package ps.server

import cats.data.NonEmptyList
import cats.effect.{Async, ConcurrentEffect, Resource}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import org.http4s.{Header, HttpRoutes}
import org.http4s.server.middleware.CORS
import ps.algebra.auth.ModuleAuthAsync
import ps.algebra.http.{AuthCtxMiddleware, AuthCtxService, AuthedHttp4s}
import ps.algebra.user.ModuleUserAsync
import ps.db.DatabaseContext
import ps.organizer.user.ModuleUserOrganizerConcurrent

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait ModuleParkingServer[F[_]]
    extends ModuleUserAsync[F]
    with ModuleUserOrganizerConcurrent[F]
    with ModuleAuthAsync[F] {

  implicit override def async: Async[F]

  implicit override def transactor: Transactor[F]

  implicit override def logger: SelfAwareStructuredLogger[F]

  implicit override def databaseContext: Resource[F, DatabaseContext[F]]

  def authCtxMiddleware: AuthCtxMiddleware[F] =
    AuthedHttp4s.userTokenAuthMiddleware[F](authAlgebra)

  private lazy val cors = List(
    Header("Access-Control-Allow-Origin", "*"),
    Header("Access-Control-Allow-Methods",
           "GET, POST, PATCH, PUT, DELETE, OPTIONS"),
    Header("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token")
  )

  /*_*/
  def psServerService: HttpRoutes[F] = {
    CORS {
      import cats.implicits._

      val authedServices: AuthCtxService[F] =
        NonEmptyList
          .of[AuthCtxService[F]](
            authedUserModuleService,
          )
          .reduceK

      val service: HttpRoutes[F] = {
        NonEmptyList
          .of[HttpRoutes[F]](
            userModuleService
          )
          .reduceK
      }
      service <+> authCtxMiddleware(authedServices).map(
        _.putHeaders(
          cors: _*
        )
      )
    }
  }
  /*_*/

}

object ModuleParkingServer {

  def concurrent[F[_]](
      )(
      implicit c: ConcurrentEffect[F],
      t: Transactor[F],
      log: SelfAwareStructuredLogger[F],
      dbc: Resource[F, DatabaseContext[F]]
  ): ModuleParkingServer[F] = {
    new ModuleParkingServer[F] {

      implicit override def async: Async[F] = c

      implicit override def transactor: Transactor[F] = t

      implicit override def logger: SelfAwareStructuredLogger[F] = log

      implicit override def databaseContext: Resource[F, DatabaseContext[F]] =
        dbc

    }
  }
}
