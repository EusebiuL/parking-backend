package ps

import busymachines.{json => bmj}
/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object json extends bmj.JsonTypeDefinitions with bmj.DefaultTypeDiscriminatorConfig {
  type Codec[A] = bmj.Codec[A]
  @inline def Codec: bmj.Codec.type = bmj.Codec
}
