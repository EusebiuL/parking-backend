package ps.db

import java.time.LocalDate

import doobie.Meta

import scala.reflect.runtime.universe.TypeTag
import shapeless.tag.@@


/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait GenericComposite extends LowPrioritiesGenericComposite {

  implicit def phantomLongMeta[Tag](implicit tt: TypeTag[Long @@ Tag]): Meta[Long @@ Tag] =
    Meta.LongMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomFloatMeta[Tag](implicit tt: TypeTag[Float @@ Tag]): Meta[Float @@ Tag] =
    Meta.FloatMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomDoubleMeta[Tag](implicit tt: TypeTag[Double @@ Tag]): Meta[Double @@ Tag] =
    Meta.DoubleMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomBooleanMeta[Tag](implicit tt: TypeTag[Boolean @@ Tag]): Meta[Boolean @@ Tag] =
    Meta.BooleanMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomIntMeta[Tag](implicit tt: TypeTag[Int @@ Tag]): Meta[Int @@ Tag] =
    Meta.IntMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomStringMeta[Tag](implicit tt: TypeTag[String @@ Tag]): Meta[String @@ Tag] =
    Meta.StringMeta.timap(v => shapeless.tag[Tag](v))(identity)

  implicit def phantomLocalDateMeta[Tag](implicit tt: TypeTag[LocalDate @@ Tag]): Meta[LocalDate @@ Tag] =
    Meta.JavaTimeLocalDateMeta.timap(v => shapeless.tag[Tag](v))(identity)

}

trait LowPrioritiesGenericComposite {

  implicit def anyPhantomMeta[A: Meta, Tag](implicit tt: TypeTag[A @@ Tag]): Meta[A @@ Tag] =
    Meta[A].timap(v => shapeless.tag[Tag](v))(identity)

}

object GenericComposite extends GenericComposite

