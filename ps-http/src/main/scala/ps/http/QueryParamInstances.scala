package ps.http

import java.time.{LocalDate, LocalDateTime}

import org.http4s.QueryParamDecoder
import ps.core.TimeFormatters

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait QueryParamInstances {

  implicit val localDateTimeQueryParamDecoder: QueryParamDecoder[LocalDateTime] =
    QueryParamDecoder.stringQueryParamDecoder.map(s => LocalDateTime.parse(s, TimeFormatters.LocalDateTimeFormatter))

  implicit val localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder.stringQueryParamDecoder.map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter))

}
