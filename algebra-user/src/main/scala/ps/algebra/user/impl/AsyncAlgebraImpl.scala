package ps.algebra.user.impl

import busymachines.core.{InvalidInputFailure, NotFoundFailure}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.user.entities.{Car, ReportDefinition, UserDefinition}
import ps.algebra.user.{CarID, CarNumber, ReportID, UserAlgebra, UserID}
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

  override def updateUser(userDefinition: UserDefinition): F[UserID] = transact {
    for {
      _ <- userRepository.findActiveUserById(userDefinition.userId).flatMap(exists(_, NotFoundFailure(s"User with id ${userDefinition.userId} was not found")))
      _ <- userRepository.updateUser(userDefinition)
    } yield userDefinition.userId
  }

  override def createDevice(userId: UserID): F[DeviceID] =
    transact(UserSql.insertDevice(userId))

  override def deleteDevice(deviceId: DeviceID): F[Int] = {
    transact(userRepository.deleteDeviceById(deviceId))
  }

  override def updateCar(userId: UserID, carNumber: CarNumber): F[Car] =
    transact {
      for {
        _ <- userRepository
          .findActiveUserById(userId)
          .flatMap(
            exists(_, NotFoundFailure(s"User with id ${userId} was not found")))
        _ <- userRepository.findCarByNumber(carNumber).flatMap {
          case Some(_) =>
            raiseError[Unit](
              InvalidInputFailure(
                s"Car with number ${carNumber} already exists"))
          case None => unit
        }
        carId <- userRepository.insertCarForUser(userId, carNumber)
      } yield Car(carId, carNumber)
    }

  override def deleteCar(carId: CarID): F[Int] =
    transact(userRepository.deleteCarById(carId))


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
