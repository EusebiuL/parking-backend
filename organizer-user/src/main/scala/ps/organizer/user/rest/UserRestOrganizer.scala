package ps.organizer.user.rest

import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import org.http4s.{HttpRoutes, Uri}
import org.http4s.dsl.Http4sDsl
import ps.algebra.auth.AuthAlgebra
import ps.algebra.http.AuthCtxService
import ps.algebra.user.UserAlgebra
import ps.algebra.user.entities.UserRegistration
import ps.effects.Async
import cats.implicits._
import ps.core.{Email, Password}
import ps.http._

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final class UserRestOrganizer[F[_]](
    private val userAlgebra: UserAlgebra[F],
    private val authAlgebra: AuthAlgebra[F]
)(
    implicit val F: Async[F],
    logger: SelfAwareStructuredLogger[F]
) extends Http4sDsl[F]
    with UserOrganizerJSON {

  private object EmailMatcher extends QueryParamDecoderMatcher[String]("email")
  private object PasswordMatcher
      extends QueryParamDecoderMatcher[String]("password")

  private val logInService: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "user" / "register" =>
      for {
        definition <- req.as[UserRegistration]
        userId <- authAlgebra.register(definition)
        resp <- Created(userId)
        _ <- logger.info(s"User is being registered")
      } yield resp

    case POST -> Root / "user" / "login" :? EmailMatcher(email) +& PasswordMatcher(
          password) =>
      for {
        authCtx <- authAlgebra.authenticate(Email(email), Password(password))
        resp <- Ok(authCtx)
      } yield resp
  }

  private val logOutService: AuthCtxService[F] = AuthCtxService[F] {
    case (GET -> Root / "user" / "logout") as user =>
      for {
        _ <- authAlgebra.logout(user)
        resp <- Ok()
        _ <- logger.info(s"User '${user.user.username} has logged out")
      } yield resp
  }

  val loginService: HttpRoutes[F] = logInService

  val logoutService: AuthCtxService[F] = logOutService

}
