package ps

import org.http4s.server.HttpMiddleware

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object http extends Http4sCirceInstances with QueryParamInstances {

  type ErrorHandlingMiddleware[F[_]] = HttpMiddleware[F]

}
