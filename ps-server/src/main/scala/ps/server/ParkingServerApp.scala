package ps.server

import busymachines.effects.Scheduler
import cats.effect._
import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp, Timer}
import fs2.Stream
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.server.ServiceErrorHandler
import org.http4s.server.blaze.BlazeBuilder
import ps.http.ParkingErrorHandler

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
object ParkingServerApp extends IOApp {

  implicit val scheduler: Scheduler = Scheduler.global
  implicit val logger: SelfAwareStructuredLogger[IO] =
    Slf4jLogger.unsafeCreate[IO]

  override def run(args: List[String]): IO[ExitCode] = {
    for {

      server <- ParkingServer.concurrent[IO]
      (serverConfig, mtwModule) <- server.init
      exitCode: ExitCode <- serverStream[IO](
        config = serverConfig,
        service = mtwModule.psServerService,
        errorHandler = ParkingErrorHandler.apply[IO]
      ).compile.lastOrError
    } yield exitCode
  }

  private def serverStream[F[_]: ConcurrentEffect: Timer](
      config: ParkingServerConfig,
      service: HttpRoutes[F],
      errorHandler: ServiceErrorHandler[F]
  ): Stream[F, ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .withServiceErrorHandler(errorHandler)
      .withWebSockets(true)
      .serve

}
