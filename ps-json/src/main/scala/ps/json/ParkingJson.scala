package ps.json

import cats.syntax.contravariant._
import io.circe.syntax._
import ps.core.PhantomType
import shapeless.tag.@@

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */
trait ParkingJson {

  implicit def encodeEither[A, B](
      implicit
      encoderA: Encoder[A],
      encoderB: Encoder[B]
  ): Encoder[Either[A, B]] = { o: Either[A, B] =>
    o.fold(_.asJson, _.asJson)
  }

  implicit def decodeEither[A, B](
      implicit
      decoderA: Decoder[A],
      decoderB: Decoder[B]
  ): Decoder[Either[A, B]] = { c: HCursor =>
    c.as[A] match {
      case Right(a) => Right(Left(a))
      case _        => c.as[B].map(Right(_))
    }
  }

  implicit final def phantomCodec[P, Tag <: PhantomType[P]](
      implicit enc: Encoder[P],
      dec: Decoder[P]): Codec[P @@ Tag] = Codec.instance[P @@ Tag](
    encode = Encoder.apply[P].narrow,
    decode = Decoder.apply[P].map(shapeless.tag[Tag](_))
  )

}
