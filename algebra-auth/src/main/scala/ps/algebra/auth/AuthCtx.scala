package ps.algebra.auth

import ps.algebra.user.entities.User
import ps.core.{AuthenticationToken, DeviceID}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final case class AuthCtx(
    user: User,
    deviceId: DeviceID,
    token: AuthenticationToken
) extends Serializable
