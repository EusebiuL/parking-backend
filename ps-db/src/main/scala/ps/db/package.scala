package ps

import ps.core.BlockingContext

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object db {

  type DatabaseContext[F[_]] = BlockingContext[F]

}
