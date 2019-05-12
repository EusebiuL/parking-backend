package ps.algebra.auth

import cats.effect.{Async, Resource}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.auth.impl.{AuthRepository, AuthSql}
import ps.algebra.user.impl.{UserRepository, UserSql}
import ps.db.DatabaseContext

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait ModuleAuthAsync[F[_]] {

  implicit def async: Async[F]

  implicit def transactor: Transactor[F]

  implicit def databaseContext: Resource[F, DatabaseContext[F]]

  implicit def logger: SelfAwareStructuredLogger[F]

  def authAlgebra: AuthAlgebra[F] = _authAlgebra

  private lazy val userRepository: UserRepository = UserSql

  private lazy val authRepository: AuthRepository = AuthSql

  private lazy val _authAlgebra =
    new impl.AsyncAlgebraImpl[F](authRepository, userRepository)

}
