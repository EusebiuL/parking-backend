package ps.algebra.user.impl

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

import doobie.util.Meta
import ps.core.CreatedAt
import ps.db.GenericComposite
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait UserComposites extends GenericComposite {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[java.sql.Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val localDateMeta: Meta[LocalDate] =
    Meta[Timestamp].imap(_.toLocalDateTime.toLocalDate)(d => Timestamp.valueOf(d.atStartOfDay()))

  implicit val createdAtMeta: Meta[CreatedAt] =
    Meta[LocalDateTime].imap(CreatedAt.apply)(CreatedAt.despook)

  implicit val passwordMeta: Meta[PasswordHash[BCrypt]] =
    Meta[String].imap(PasswordHash.apply[BCrypt])(_.toString)
}
