package ps.algebra.auth.impl

import busymachines.core.{InvalidInputFailure, NotFoundFailure}
import cats.effect.{Async, Resource}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import ps.algebra.auth.{AuthAlgebra, AuthCtx}
import ps.algebra.user.impl.UserRepository
import ps.core.{
  AuthenticationToken,
  BlockingAlgebra,
  DeviceID,
  Email,
  Name,
  Password
}
import ps.db.{DatabaseAlgebra, DatabaseContext}
import tsec.jws.mac.JWTMac
import tsec.jwt.JWTClaims
import tsec.mac.jca.HMACSHA256
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.BCrypt
import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import ps.algebra.user.UserID
import ps.algebra.user.entities.{User, UserPwHashDefinition, UserRegistration}

import scala.concurrent.duration._

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
final private[auth] class AsyncAlgebraImpl[F[_]](
    val authRepository: AuthRepository,
    val userRepository: UserRepository
)(
    implicit F: Async[F],
    val transactor: Transactor[F],
    val databaseContext: Resource[F, DatabaseContext[F]],
    val logger: SelfAwareStructuredLogger[F]
) extends AuthAlgebra[F]
    with DatabaseAlgebra[F]
    with BlockingAlgebra[F] {

  override def register(userRegistration: UserRegistration): F[UserID] =
    transact {
      for {
        userEmailDb <- userRepository.findByEmail(userRegistration.email)
        _ <- if (userEmailDb.nonEmpty)
          raiseError[Unit](
            InvalidInputFailure("Email already is associated with an account"))
        else unit
        userNameDb <- userRepository.findByName(
          Name(userRegistration.username.trim))
        _ <- if (userNameDb.nonEmpty)
          raiseError[Unit](
            InvalidInputFailure(
              "Username already is associated with an account"))
        else unit
        hashed <- hashPWWithBcrypt(userRegistration.password)
        userPwHash = UserPwHashDefinition(userRegistration.username,
                                          userRegistration.email,
                                          hashed)
        userId <- authRepository.insertUser(userPwHash)
      } yield userId
    }

  override def authenticate(email: Email, password: Password): F[AuthCtx] =
    transact {
      for {
        userPwHash <- authRepository.findByEmail(email).flatMap {
          exists(_, NotFoundFailure(s"User with email ${email} was not found"))
        }
        cars <- userRepository.findCarsForUser(userPwHash.userId)
        user = User.fromPwHash(userPwHash, cars)
        deviceId <- authRepository.insertDevice(userPwHash.userId)
        auth <- checkUserPassword(password, userPwHash.hashedPassword).flatMap {
          case true => storeAuth(user, deviceId)
          case false =>
            raiseError[AuthCtx](
              InvalidInputFailure("Invalid email or password"))
        }

      } yield auth
    }

  override def authenticate(token: AuthenticationToken): F[AuthCtx] = transact {
    for {
      userDb <- authRepository
        .findUserByAuthToken(token)
        .flatMap(
          exists(_, NotFoundFailure(s"User with token ${token} was not found")))
      cars <- userRepository.findCarsForUser(userDb.userId)
      user = User.fromUserDB(userDb, cars)
      device <- authRepository
        .findDeviceByAuthToken(token)
        .flatMap(
          exists(_,
                 NotFoundFailure(s"Device with token ${token} was not found")))
    } yield AuthCtx(user, device, token)
  }

  override def deleteAuthToken(userId: UserID, deviceId: DeviceID): F[Int] =
    transact(authRepository.deleteAuthTokenForUserAndDevice(userId, deviceId))

  override def logout(implicit authCtx: AuthCtx): F[Unit] = transact {
    for {
      _ <- authRepository.deleteAuthTokenForUserAndDevice(authCtx.user.userId,
                                                          authCtx.deviceId)
      _ <- authRepository.deleteDeviceById(authCtx.deviceId)
    } yield ()
  }

  private def storeAuth(user: User, deviceId: DeviceID): ConnectionIO[AuthCtx] =
    for {
      token <- generateToken
      _ <- authRepository.insertAuthToken(user.userId,
                                          deviceId,
                                          AuthenticationToken(token))
    } yield AuthCtx(user, deviceId, AuthenticationToken(token))

  private def generateToken: ConnectionIO[String] =
    for {
      key <- HMACSHA256.generateKey[ConnectionIO]
      claims <- JWTClaims.withDuration[ConnectionIO](
        expiration = Some(10.minutes))
      token <- JWTMac.buildToString[ConnectionIO, HMACSHA256](claims, key)
    } yield token

  private def hashPWWithBcrypt(
      password: Password): ConnectionIO[PasswordHash[BCrypt]] =
    BCrypt.hashpw[ConnectionIO](password)

  private def checkUserPassword(
      p: String,
      hash: PasswordHash[BCrypt]): ConnectionIO[Boolean] = {
    BCrypt.checkpw[ConnectionIO](p, hash).map {
      case tsec.common.Verified           => true
      case tsec.common.VerificationFailed => false
    }
  }
}
