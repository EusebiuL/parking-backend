package ps.json

import java.time.{DayOfWeek, LocalDate, LocalDateTime, LocalTime}

import ps.core.TimeFormatters
/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

object TimeJson extends TimeJson

trait TimeJson {

  implicit val localDateCodec: Codec[LocalDate] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(TimeFormatters.LocalDateFormatter)),
    Decoder.apply[String].map(s => LocalDate.parse(s, TimeFormatters.LocalDateFormatter))
  )

  implicit val localTimeCodec: Codec[LocalTime] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(TimeFormatters.LocalTimeFormatter)),
    Decoder.apply[String].map(s => LocalTime.parse(s, TimeFormatters.LocalTimeFormatter))
  )

  implicit val localDateTimeCodec: Codec[LocalDateTime] = Codec.instance(
    Encoder.apply[String].contramap(m => m.format(TimeFormatters.LocalDateTimeFormatter)),
    Decoder.apply[String].map(s => LocalDateTime.parse(s, TimeFormatters.LocalDateTimeFormatter))
  )

  implicit val dayOfWeekCodec: Codec[DayOfWeek] = Codec.instance(
    Encoder.apply[String].contramap(dow => dow.name().toUpperCase),
    Decoder.apply[String].map(s => DayOfWeek.valueOf(s.toUpperCase))
  )

}

