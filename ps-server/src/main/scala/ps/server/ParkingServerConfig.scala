package ps.server

import cats.effect.Sync
import ps.config.ConfigLoader

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final case class ParkingServerConfig(
    port: Int,
    host: String,
    apiRoot: String
)

object ParkingServerConfig extends ConfigLoader[ParkingServerConfig] {
  override def default[F[_]: Sync]: F[ParkingServerConfig] =
    this.load[F]("ps.server")
}
