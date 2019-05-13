package ps.algebra.user.impl

import doobie._
import doobie.implicits._
import ps.algebra.user.{CarID, CarNumber, UserID}
import ps.algebra.user.entities.{Car, UserDB, UserDefinition}
import ps.core.{DeviceID, Email, Name}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object UserSql extends UserRepository with UserComposites {

  override def findActiveUserById(userId: UserID): ConnectionIO[Option[UserDB]] =
    sql"SELECT user_id, email, name, password, is_active, created_at FROM parking.user WHERE user_id = ${userId} AND is_active=true"
      .query[UserDB]
      .option

  override def findByEmail(email: Email): ConnectionIO[Option[UserDB]] =
    sql"SELECT user_id, email, name, password, is_active, created_at FROM parking.user WHERE email=${email} AND is_active=true"
      .query[UserDB]
      .option

  override def findByName(name: Name): ConnectionIO[Option[UserDB]] =
    sql"SELECT user_id, email, name, password, is_active, created_at FROM parking.user WHERE name=${name}".query[UserDB].option

  override def updateUser(userDefinition: UserDefinition): ConnectionIO[UserID] =
    sql"UPDATE parking.user SET name=${userDefinition.username}, email=${userDefinition.email} WHERE user_id=${userDefinition.userId}".update.withUniqueGeneratedKeys[UserID]("user_id")

  override def insertDevice(userId: UserID): doobie.ConnectionIO[DeviceID] =
    sql"INSERT INTO parking.device(u_user_id) VALUES(${userId})".update
      .withUniqueGeneratedKeys[DeviceID]("device_id")

  override def deactivateUserById(userId: UserID): ConnectionIO[Int] =
    sql"UPDATE parking.user SET is_active=false WHERE user_id=${userId}".update.run

  override def deleteDeviceById(deviceId: DeviceID): ConnectionIO[Int] =
    sql"DELETE FROM condica.device WHERE device_id=${deviceId}".update.run

  override def findCarsForUser(userId: UserID): ConnectionIO[List[Car]] =
    sql"SELECT car_id, car_number FROM parking.car WHERE u_user_id=${userId}".query[Car].to[List]

  override def findCarByNumber(carNumber: CarNumber): ConnectionIO[Option[Car]] =
    sql"SELECT car_id, car_number FROM parking.car WHERE car_number=${carNumber}".query[Car].option

  override def insertCarForUser(userId: UserID, carNumber: CarNumber): ConnectionIO[CarID] =
    sql"INSERT INTO parking.car(car_number, u_user_id) VALUES(${carNumber}, ${userId})".update.withUniqueGeneratedKeys[CarID]("car_id")

  override def deleteCarById(carId: CarID): ConnectionIO[Int] =
    sql"DELETE FROM parking.car WHERE car_id=${carId}".update.run

}
