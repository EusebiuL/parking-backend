package ps.http

import fs2.Chunk
import io.circe.{Decoder, Encoder, Json, Printer}
import org.http4s.{EntityDecoder, EntityEncoder, MediaType}
import org.http4s.circe.CirceInstances
import org.http4s.headers.`Content-Type`
import ps.effects.{Applicative, Sync}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait Http4sCirceInstances {

  import Http4sCirceInstances._

  implicit def syncEntityJsonEncoder[F[_]: Applicative, T: Encoder]: EntityEncoder[F, T] =
    EntityEncoder[F, Chunk[Byte]]
      .contramap[Json] { json =>
      val bytes = printer.prettyByteBuffer(json)
      Chunk.byteBuffer(bytes)
    }
      .withContentType(`Content-Type`(MediaType.application.json))
      .contramap(t => Encoder[T].apply(t))
  implicit def syncEntityJsonDecoder[F[_]: Sync, T: Decoder]: EntityDecoder[F, T] =
    circeInstances.jsonOf[F, T]
}

object Http4sCirceInstances {
  private val printer        = Printer.noSpaces.copy(dropNullValues = true)
  private val circeInstances = CirceInstances.withPrinter(printer)
}
