package ps.algebra.http

import cats.implicits._
import busymachines.core.{Anomaly, UnauthorizedFailure}
import busymachines.effects.sync.Result
import busymachines.json.AnomalyJsonCodec
import cats.data.{Kleisli, NonEmptyList, OptionT}
import cats.effect.Async
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRequest, AuthedService, Challenge, Request, headers}
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString
import ps.algebra.auth.{AuthAlgebra, AuthCtx}
import ps.core.AuthenticationToken
import ps.http.Http4sCirceInstances
import ps.effects._

import scala.util.control.NonFatal

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
object AuthedHttp4s extends AnomalyJsonCodec with Http4sCirceInstances {

  def userTokenAuthMiddleware[F[_]: Async](
      authAlgebra: AuthAlgebra[F]
  ): AuthMiddleware[F, AuthCtx] =
    AuthMiddleware(verifyToken[F](authAlgebra), onFailure)

  private val `X-Auth-Token` = CaseInsensitiveString("X-AUTH-TOKEN")
  private val challenges: NonEmptyList[Challenge] = NonEmptyList.of(
    Challenge(
      scheme = "Basic",
      realm = "ParkingApp"
    )
  )

  private val wwwHeader = headers.`WWW-Authenticate`(challenges)

  private def onFailure[F[_]: Async]: AuthedService[Anomaly, F] = Kleisli {
    req: AuthedRequest[F, Anomaly] =>
      val fdsl = Http4sDsl[F]
      import fdsl._
      OptionT.liftF(Unauthorized(wwwHeader, req.authInfo.asInstanceOf[Anomaly]))
  }

  /*_*/
  private def verifyToken[F[_]: Async](
      authAlgebra: AuthAlgebra[F]
  ): Kleisli[F, Request[F], Result[AuthCtx]] =
    Kleisli { req: Request[F] =>
      val F = Async.apply[F]

      val optHeader = req.headers.get(`X-Auth-Token`)
      optHeader match {
        case None =>
          F.pure(
            Result.fail(UnauthorizedFailure(s"No ${`X-Auth-Token`} provided")))
        case Some(header) =>
          authAlgebra
            .authenticate(AuthenticationToken(header.value))
            .map(Result.pure)
            .recover {
              case NonFatal(a: Anomaly) =>
                Result.fail(a)
              case NonFatal(a) =>
                Result.failThr(a)
            }
      }
    }
  /*_*/

}
