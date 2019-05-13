package ps.algebra.user.impl

import doobie.ConnectionIO
import ps.algebra.user.{CarID, CarNumber, UserID}
import ps.algebra.user.entities.{Car, UserDB, UserDefinition}
import ps.core.{DeviceID, Email, Name}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait UserRepository {

  def findActiveUserById(userId: UserID): ConnectionIO[Option[UserDB]]

  def findByEmail(email: Email): ConnectionIO[Option[UserDB]]

  def findByName(name: Name): ConnectionIO[Option[UserDB]]

  def updateUser(userDefinition: UserDefinition): ConnectionIO[UserID]

  def insertDevice(userId: UserID): ConnectionIO[DeviceID]

  def deactivateUserById(userId: UserID): ConnectionIO[Int]

  def deleteDeviceById(deviceId: DeviceID): ConnectionIO[Int]

  def findCarsForUser(userId: UserID): ConnectionIO[List[Car]]

  def findCarByNumber(carNumber: CarNumber): ConnectionIO[Option[Car]]

  def insertCarForUser(userId: UserID, carNumber: CarNumber): ConnectionIO[CarID]

  def deleteCarById(carId: CarID): ConnectionIO[Int]

}
