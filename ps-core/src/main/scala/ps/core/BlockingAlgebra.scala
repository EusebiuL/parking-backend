package ps.core

import cats.effect.{Async, Resource}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait BlockingAlgebra[F[_]] {

  protected def block[M](thunk: => F[M])(implicit F: Async[F], context: Resource[F, BlockingContext[F]]): F[M] = {
    context.use(_.block(thunk))
  }
}
