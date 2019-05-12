package ps.db

import cats.effect.{ContextShift, Resource, Sync}
import ps.core.BlockingContext

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object DatabaseContext {

  def create[F[_]: Sync](connectionPoolSize: Int)(implicit C: ContextShift[F]): Resource[F, DatabaseContext[F]] =
    BlockingContext.createFixedPool(connectionPoolSize)
}
