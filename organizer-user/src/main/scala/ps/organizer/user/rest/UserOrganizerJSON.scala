package ps.organizer.user.rest

import ps.algebra.auth.AuthCtx
import ps.algebra.user.entities.{Car, User, UserRegistration}
import ps.algebra.user.{CarID, CarNumber, UserID}
import ps.core.{AuthenticationToken, DeviceID, Email, Name, Password}
import ps.json._
/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object UserOrganizerJSON extends UserOrganizerJSON

trait UserOrganizerJSON extends ParkingJson with TimeJson {

  implicit val userIDCirceCodec: Codec[UserID] = phantomCodec[Long, UserID.Tag]

  implicit val deviceIDCirceCodec: Codec[DeviceID] = phantomCodec[Long, DeviceID.Tag]

  implicit val authTokenCirceCodec: Codec[AuthenticationToken] = phantomCodec[String, AuthenticationToken.Tag]

  implicit val emailCirceCodec: Codec[Email] = phantomCodec[String, Email.Tag]

  implicit val nameCirceCodec: Codec[Name] = phantomCodec[String, Name.Tag]

  implicit val passwordCirceCodec: Codec[Password] = phantomCodec[String, Password.Tag]

  implicit val carIdCirceCodec: Codec[CarID] = phantomCodec[Long, CarID.Tag]

  implicit val carNumberCirceCodec: Codec[CarNumber] = phantomCodec[String, CarNumber.Tag]

  implicit val carCirceCodec: Codec[Car] = derive.codec[Car]

  implicit val userRegistrationCirceCodec: Codec[UserRegistration] = derive.codec[UserRegistration]

  implicit val userCirceCodec: Codec[User] = derive.codec[User]

  implicit val authCtxCirceCodec: Codec[AuthCtx] = derive.codec[AuthCtx]

}