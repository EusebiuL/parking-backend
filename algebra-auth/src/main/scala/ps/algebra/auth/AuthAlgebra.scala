package ps.algebra.auth

import cats.effect.{Async, Resource}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.auth.impl.AuthRepository
import ps.algebra.user.UserID
import ps.algebra.user.entities.UserRegistration
import ps.algebra.user.impl.UserRepository
import ps.core.{AuthenticationToken, DeviceID, Email, Password}
import ps.db.DatabaseContext

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait AuthAlgebra[F[_]] {

  def register(definition: UserRegistration): F[UserID]

  def authenticate(email: Email, password: Password): F[AuthCtx]

  def authenticate(token: AuthenticationToken): F[AuthCtx]

  def deleteAuthToken(userId: UserID, deviceID: DeviceID): F[Int]

  def logout(implicit authCtx: AuthCtx): F[Unit]

}

object AuthAlgebra {

  def async[F[_]: Async](authRepository: AuthRepository,
                         userRepository: UserRepository)(
      implicit transactor: Transactor[F],
      databaseContext: Resource[F, DatabaseContext[F]],
      logger: SelfAwareStructuredLogger[F]): AuthAlgebra[F] =
    new impl.AsyncAlgebraImpl[F](authRepository = authRepository,
                                 userRepository = userRepository)

}
