package ps.db.config

import ps.config.ConfigLoader
import ps.effects.Sync
import cats.implicits._
/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
final case class DatabaseConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    clean: Boolean,
    schema: String,
    locations: List[String],
    connectionPoolSize: Int
)

object DatabaseConfig extends ConfigLoader[DatabaseConfig] {

  override def default[F[_]: Sync]: F[DatabaseConfig] =
    this.load[F]("ps.db")
}
