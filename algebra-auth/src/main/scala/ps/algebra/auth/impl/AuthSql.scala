package ps.algebra.auth.impl

import doobie.ConnectionIO
import ps.algebra.user.UserID
import ps.core.{AuthenticationToken, DeviceID, Email}
import doobie.implicits._
import ps.algebra.user.entities.{UserDB, UserPwHash, UserPwHashDefinition}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
object AuthSql extends AuthRepository with AuthComposites {

  override def insertDevice(userId: UserID): doobie.ConnectionIO[DeviceID] =
    sql"INSERT INTO parking.device(u_user_id) VALUES(${userId})".update
      .withUniqueGeneratedKeys[DeviceID]("device_id")

  override def insertAuthToken(
      userId: UserID,
      deviceId: DeviceID,
      token: AuthenticationToken
  ): ConnectionIO[Int] =
    sql"INSERT INTO parking.authentication(u_user_id, d_device_id, authentication_token) VALUES (${userId}, ${deviceId}, ${token})".update.run

  override def insertUser(definition: UserPwHashDefinition): ConnectionIO[UserID] =
    sql"INSERT INTO parking.user(email, name, password, created_at) VALUES(${definition.email}, ${definition.name}, ${definition.hashedPassword}, NOW())".update.withUniqueGeneratedKeys[UserID]("user_id")

  override def deleteAuthTokenForUserAndDevice(
      userId: UserID,
      deviceId: DeviceID
  ): ConnectionIO[Int] =
    sql"DELETE FROM parking.authentication WHERE u_user_id=${userId} AND d_device_id=${deviceId}".update.run

  override def findUserByAuthToken(
      authenticationToken: AuthenticationToken
  ): ConnectionIO[Option[UserDB]] =
    sql"""SELECT user_id, "name", email, is_active, created_at
         | FROM parking.authentication
         | JOIN parking."user" ON authentication.u_user_id = "user".user_id
         | WHERE authentication_token=${authenticationToken}""".stripMargin
      .query[UserDB]
      .option

  override def findDeviceByAuthToken(
      authenticationToken: AuthenticationToken
  ): ConnectionIO[Option[DeviceID]] =
    sql"SELECT d_device_id FROM parking.authentication WHERE authentication_token=${authenticationToken}"
      .query[DeviceID]
      .option

  override def findByEmail(email: Email): ConnectionIO[Option[UserPwHash]] =
    sql"SELECT user_id, name, email, password FROM parking.user WHERE email=${email}".query[UserPwHash].option

  override def deleteDeviceById(deviceId: DeviceID): ConnectionIO[Int] =
    sql"DELETE FROM parking.device WHERE device_id=${deviceId}".update.run

}
