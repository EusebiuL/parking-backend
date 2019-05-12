package ps.algebra

import ps.core.PhantomType

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object user {

  object UserID extends PhantomType[Long]
  type UserID = UserID.Type

  object CarID extends PhantomType[Long]
  type CarID = CarID.Type

  object CarNumber extends PhantomType[String]
  type CarNumber = CarNumber.Type

}
