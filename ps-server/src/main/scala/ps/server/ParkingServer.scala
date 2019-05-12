package ps.server

import cats.implicits._
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Resource, Timer}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import monix.execution.Scheduler
import ps.db.DatabaseContext
import ps.db.config.{DatabaseConfig, DatabaseConfigAlgebra}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final class ParkingServer[F[_]: ConcurrentEffect: Timer] private (
    implicit
    val F: Concurrent[F],
    val scheduler: Scheduler,
    val contextShift: ContextShift[F],
    val logger: SelfAwareStructuredLogger[F]
) {

  def init: F[(ParkingServerConfig, ModuleParkingServer[F])] = {
    for {
      serverConfig <- ParkingServerConfig.default[F]
      dbConfig: DatabaseConfig <- DatabaseConfig.default[F]
      transactor <- DatabaseConfigAlgebra.transactor[F](dbConfig)
      nrOfMigs <- DatabaseConfigAlgebra.initializeSQLDb[F](dbConfig)
      _ <- logger.info(s"Successfully ran $nrOfMigs migration(s)")
      dbContext <- F.delay(
        DatabaseContext.create[F](dbConfig.connectionPoolSize))
      mtwModule <- moduleInit(
        transactor,
        dbContext
      )

      _ <- logger.info("Successfully initialized ps-server")
    } yield (serverConfig, mtwModule)
  }

  private def moduleInit(
      transactor: Transactor[F],
      dbContext: Resource[F, DatabaseContext[F]]
  ): F[ModuleParkingServer[F]] =
    Concurrent
      .apply[F]
      .delay(
        ModuleParkingServer
          .concurrent(
            )(
            implicitly,
            transactor,
            logger,
            dbContext
          )
      )

}

object ParkingServer {

  def concurrent[F[_]: ConcurrentEffect: Timer](
      implicit scheduler: Scheduler,
      contextShift: ContextShift[F],
      logger: SelfAwareStructuredLogger[F]
  ): F[ParkingServer[F]] =
    Concurrent.apply[F].delay(new ParkingServer[F]())

}
