package ps.algebra.auth.impl

import doobie.ConnectionIO
import ps.algebra.user.UserID
import ps.algebra.user.entities.{User, UserDB, UserPwHash, UserPwHashDefinition}
import ps.core.{AuthenticationToken, DeviceID, Email}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
trait AuthRepository {

  def insertAuthToken(userId: UserID,
                      deviceId: DeviceID,
                      token: AuthenticationToken): ConnectionIO[Int]

  def insertUser(definition: UserPwHashDefinition): ConnectionIO[UserID]

  def deleteAuthTokenForUserAndDevice(userId: UserID,
                                      deviceId: DeviceID): ConnectionIO[Int]

  def findUserByAuthToken(
      authenticationToken: AuthenticationToken): ConnectionIO[Option[UserDB]]

  def findDeviceByAuthToken(
      authenticationToken: AuthenticationToken): ConnectionIO[Option[DeviceID]]

  def findByEmail(email: Email): ConnectionIO[Option[UserPwHash]]

  def insertDevice(userId: UserID): ConnectionIO[DeviceID]

  def deleteDeviceById(deviceId: DeviceID): ConnectionIO[Int]
}
