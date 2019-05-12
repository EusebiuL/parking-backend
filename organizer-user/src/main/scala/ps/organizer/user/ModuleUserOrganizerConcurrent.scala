package ps.organizer.user

import cats.data.NonEmptyList
import org.http4s.HttpRoutes
import ps.algebra.auth.ModuleAuthAsync
import ps.algebra.http.AuthCtxService
import ps.algebra.user.ModuleUserAsync
import ps.effects.Async
import ps.organizer.user.rest.UserRestOrganizer
import cats.implicits._

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait ModuleUserOrganizerConcurrent[F[_]] {
  this: ModuleUserAsync[F] with ModuleAuthAsync[F] =>

  implicit def async: Async[F]

  def userRestOrganizer: UserRestOrganizer[F] = _userRestOrganizer

  def userModuleService: HttpRoutes[F] = _service

  def authedUserModuleService: AuthCtxService[F] = _authedService

  private lazy val _userRestOrganizer: UserRestOrganizer[F] =
    new UserRestOrganizer[F](
      userAlgebra = userAlgebra,
      authAlgebra = authAlgebra
    )

  private lazy val _authedService: AuthCtxService[F] = {
    NonEmptyList
      .of(
        userRestOrganizer.logoutService,
      )
      .reduceK
  }

  private lazy val _service: HttpRoutes[F] = {
    NonEmptyList
      .of[HttpRoutes[F]](
        userRestOrganizer.loginService
      )
      .reduceK
  }

}
