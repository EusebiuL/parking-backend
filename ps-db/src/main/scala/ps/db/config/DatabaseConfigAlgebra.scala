package ps.db.config

import cats.effect.{Async, ContextShift, Sync}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object DatabaseConfigAlgebra {

  def transactor[F[_]: Async: ContextShift](config: DatabaseConfig): F[Transactor[F]] = Async[F].delay {
    Transactor.fromDriverManager[F](config.driver, config.url, config.user, config.password)
  }

  def initializeSQLDb[F[_]: Sync](config: DatabaseConfig): F[Int] = Sync[F].delay {
    val fwConfig = Flyway.configure()
    fwConfig.dataSource(config.url, config.user, config.password)
    fwConfig.mixed(true)
    fwConfig.schemas(config.schema)
    fwConfig.locations(config.locations: _*)
    val fw = new Flyway(fwConfig)
    if(config.clean) fw.clean()
    fw.migrate()
  }

}

