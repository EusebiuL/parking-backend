package ps.algebra.user.entities

import ps.algebra.user.UserID
import ps.core.{Email, Name, Password}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final case class User(
    userId: UserID,
    email: Email,
    username: Name,
    cars: List[Car]
) extends Serializable

final case class UserDB(
    userId: UserID,
    email: Email,
    username: Name,
) extends Serializable

object User {
  def fromUserDB(udb: UserDB, cars: List[Car]): User =
    User(
      udb.userId,
      udb.email,
      udb.username,
      cars
    )

  def fromPwHash(pwHash: UserPwHash, cars: List[Car]): User =
    User(
      pwHash.userId,
      pwHash.email,
      pwHash.name,
      cars
    )
}

final case class UserRegistration(
    email: Email,
    username: Name,
    password: Password
) extends Serializable

final case class UserDefinition(
    userId: UserID,
    email: Email,
    username: Name
) extends Serializable
