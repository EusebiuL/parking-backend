package ps.http

import busymachines.core._
import busymachines.json.AnomalyJsonCodec
import cats.implicits._
import ps.effects._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import org.http4s._
import org.http4s.headers._
import org.http4s.server._
import org.http4s.util.CaseInsensitiveString

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object ParkingErrorHandler extends Http4sCirceInstances with AnomalyJsonCodec {

  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(
    Challenge(
      scheme = "Basic",
      realm  = "Condica",
    )
  )

  private lazy val wwwAuthenticateHeader: Header = `WWW-Authenticate`(challenges)

  def apply[F[_]](implicit F: Monad[F], logger: SelfAwareStructuredLogger[F]): ServiceErrorHandler[F] = req => {
    case e: InvalidInputFailure =>
      for {
        _ <- logger.info(s"InvalidInputFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr
          .getOrElse("<unknown>")}. Message: ${e.message}")
        resp <- F.pure(
          Response[F](status = Status.BadRequest, httpVersion = req.httpVersion)
            .withEntity(e.asInstanceOf[Anomaly])
        )
      } yield resp
    case e: UnauthorizedFailure =>
      for {
        _ <- logger.info(s"UnauthorizedFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr
          .getOrElse("<unknown>")}. Message: ${e.message}")
        resp <- F.pure {
          Response[F](
            status      = Status.Unauthorized,
            httpVersion = req.httpVersion,
            headers     = Headers(wwwAuthenticateHeader)
          ).withEntity(e.asInstanceOf[Anomaly])
        }
      } yield resp
    case e: ForbiddenFailure =>
      for {
        _ <- logger.info(s"ForbiddenFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr
          .getOrElse("<unknown>")}. Message: ${e.message}")
        resp <- F.pure(
          Response[F](status = Status.Forbidden, httpVersion = req.httpVersion)
            .withEntity(e.asInstanceOf[Anomaly])
        )
      } yield resp
    case e: NotFoundFailure =>
      for {
        _ <- logger.info(
          s"NotFoundFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}. Message: ${e.message}"
        )
        resp <- F.pure(
          Response[F](status = Status.NotFound, httpVersion = req.httpVersion)
            .withEntity(e.asInstanceOf[Anomaly])
        )
      } yield resp
    case e: ConflictFailure =>
      for {
        _ <- logger.warn(
          s"ConflictFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}. Message: ${e.message}"
        )
        resp <- F.pure(
          Response[F](status = Status.NotFound, httpVersion = req.httpVersion)
            .withEntity(e.asInstanceOf[Anomaly])
        )
      } yield resp
    case mf: MessageFailure =>
      for {
        _ <- logger.warn(
          s"MessageFailure: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}. ${mf.message}"
        )
        resp <- mf.toHttpResponse(req.httpVersion)(F)
      } yield resp
    case t if !t.isInstanceOf[VirtualMachineError] =>
      for {
        _ <- logger.error(t)(
          message =
            s"Servicing request: ${req.method} ${req.pathInfo} from ${req.remoteAddr.getOrElse("<unknown>")}"
        )
        resp = Response[F](
          status      = Status.InternalServerError,
          httpVersion = req.httpVersion,
          headers     = Headers(Connection(CaseInsensitiveString("close")), `Content-Length`.zero)
        )
      } yield resp
  }

}


