package ps.config

import busymachines.core.{Anomaly, AnomalyID, InvalidInputFailure}
import pureconfig.error.ConfigReaderFailure

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

final case class ConfigReadingAnomaly(c: ConfigReaderFailure)
  extends InvalidInputFailure(s"Failed to read config because: ${c.description}") {

  override def id: AnomalyID = ConfigReadingAnomalies.ID

  override def parameters: Anomaly.Parameters = {
    val orig: Anomaly.Parameters = Anomaly.Parameters(
      "reason" -> c.description
    )
    val loc = c.location.map(l => ("location" -> l.description): (String, Anomaly.Parameter))
    orig.++(loc.toMap: Anomaly.Parameters)
  }
}
