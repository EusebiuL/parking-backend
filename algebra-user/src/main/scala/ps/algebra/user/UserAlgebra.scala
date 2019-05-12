package ps.algebra.user


import cats.effect.Resource
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.user.entities.Car
import ps.algebra.user.impl.UserRepository
import ps.core.{ DeviceID}
import ps.db.DatabaseContext
import ps.effects.Async

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait UserAlgebra[F[_]] {

  def createDevice(userId: UserID): F[DeviceID]

  def updateCar(userId: UserID, carNumber: CarNumber): F[Car]

  def deleteCar(carId: CarID): F[Int]

  def deactivateUser(userId: UserID): F[UserID]

  def deleteDevice(deviceId: DeviceID): F[Int]
}

object UserAlgebra {


  def async[F[_]: Async](userRepository: UserRepository)(implicit transactor: Transactor[F], databaseContext: Resource[F, DatabaseContext[F]], logger: SelfAwareStructuredLogger[F]): UserAlgebra[F] = new impl.AsyncAlgebraImpl[F](userRepository = userRepository)
}
