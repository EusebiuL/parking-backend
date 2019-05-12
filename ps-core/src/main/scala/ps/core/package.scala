package ps

import java.time.LocalDateTime

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object core {

  type PhantomType[T] = busymachines.pureharm.core.PhantomType[T]

  object Email extends PhantomType[String]
  type Email = Email.Type

  object Name extends PhantomType[String]
  type Name = Name.Type

  object Password extends PhantomType[String]
  type Password = Password.Type

  object DeviceID extends PhantomType[Long]
  type DeviceID = DeviceID.Type

  object CreatedAt extends PhantomType[LocalDateTime]
  type CreatedAt = CreatedAt.Type

  object AuthenticationToken extends PhantomType[String]
  type AuthenticationToken = AuthenticationToken.Type




}

