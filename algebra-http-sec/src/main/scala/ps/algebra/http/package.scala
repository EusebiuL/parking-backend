package ps.algebra

import org.http4s.AuthedService
import org.http4s.server.AuthMiddleware
import ps.algebra.auth.AuthCtx

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
package object http {

  type AuthCtxService[F[_]] = AuthedService[AuthCtx, F]
  type AuthCtxMiddleware[F[_]] = AuthMiddleware[F, AuthCtx]

}
