package ps.algebra.user.impl

import busymachines.core.NotFoundFailure
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.user.entities.Car
import ps.algebra.user.{CarID, CarNumber, UserAlgebra, UserID}
import ps.core.{BlockingAlgebra, DeviceID}
import ps.db.{DatabaseAlgebra, DatabaseContext}
import ps.effects.Async

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final private[user] class AsyncAlgebraImpl[F[_]](
    val userRepository: UserRepository,
)(
    implicit
    val F: Async[F],
    val transactor: Transactor[F],
    val databaseContext: cats.effect.Resource[F, DatabaseContext[F]],
    val logger: SelfAwareStructuredLogger[F],
) extends UserAlgebra[F]
    with DatabaseAlgebra[F]
    with BlockingAlgebra[F] {

  override def createDevice(userId: UserID): F[DeviceID] =
    transact(UserSql.insertDevice(userId))

  override def deleteDevice(deviceId: DeviceID): F[Int] = {
    transact(userRepository.deleteDeviceById(deviceId))
  }

  override def updateCar(userId: UserID, carNumber: CarNumber): F[Car] = ???

  override def deleteCar(carId: CarID): F[Int] = ???

  override def deactivateUser(userId: UserID): F[UserID] = transact {
    for {
      user <- userRepository
        .findActiveUserById(userId)
        .flatMap(
          exists(_, NotFoundFailure(s"User with id ${userId} was not found")))
      _ <- userRepository.deactivateUserById(userId)
    } yield user.userId
  }

}
