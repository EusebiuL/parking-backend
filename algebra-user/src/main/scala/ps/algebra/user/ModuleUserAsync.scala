package ps.algebra.user

import cats.effect.Resource
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.user.impl.{UserRepository, UserSql}
import ps.db.DatabaseContext
import ps.effects.Async

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait ModuleUserAsync[F[_]] {

  implicit def async: Async[F]

  implicit def transactor: Transactor[F]

  implicit def databaseContext: Resource[F, DatabaseContext[F]]

  implicit def logger: SelfAwareStructuredLogger[F]

  def userAlgebra: UserAlgebra[F] = _moduleAlgebra

  private lazy val userRepository: UserRepository = UserSql

  private lazy val _moduleAlgebra: UserAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](userRepository = userRepository)


}
