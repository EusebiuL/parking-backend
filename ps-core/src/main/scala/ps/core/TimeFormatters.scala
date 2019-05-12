package ps.core

import java.time.format.DateTimeFormatter

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object TimeFormatters {

  val LocalDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  val LocalTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  val LocalDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")


}
