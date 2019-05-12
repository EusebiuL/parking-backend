package ps.algebra.http

import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import org.http4s.{AuthedRequest, AuthedService, Response}
import ps.algebra.auth.AuthCtx

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
object AuthCtxService {

  def apply[F[_]](
      pf: PartialFunction[AuthedRequest[F, AuthCtx], F[Response[F]]]
  )(implicit S: Sync[F]): AuthedService[AuthCtx, F] = {
    Kleisli(
      req =>
        pf.andThen(OptionT.liftF(_))
          .applyOrElse(req, Function.const(OptionT.none)))
  }

}
