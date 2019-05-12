package ps.algebra.user.entities

import ps.algebra.user.UserID
import ps.core.{Email, Name}
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
final case class UserPwHash(
    userId: UserID,
    name: Name,
    email: Email,
    hashedPassword: PasswordHash[BCrypt]
)

final case class UserPwHashDefinition(
    name: Name,
    email: Email,
    hashedPassword: PasswordHash[BCrypt]
)
