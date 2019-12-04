package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.meta.MacroInstances.materializeWith
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.udash.rest.raw.{JsonValue, PlainValue}

import scala.annotation.implicitNotFound
import scala.concurrent.Future

trait CirceRestImplicits extends FloatingPointRestImplicits {
  implicit def encoderBasedAsJson[T: Encoder]: AsRaw[JsonValue, T] =
    v => JsonValue(v.asJson.noSpaces)

  implicit def decoderBasedFromJson[T: Decoder]: AsReal[JsonValue, T] =
    json => parse(json.value).fold(throw _, _.as[T].fold(throw _, identity))

  implicit def keyEncoderBasedAsPlain[T: KeyEncoder]: AsRaw[PlainValue, T] =
    v => PlainValue(KeyEncoder[T].apply(v))

  implicit def keyDecoderBasedFromPlain[T: KeyDecoder]: AsReal[PlainValue, T] =
    pv => KeyDecoder[T].apply(pv.value)
      .getOrElse(throw new IllegalArgumentException(s"Invalid key: ${pv.value}"))

  @implicitNotFound("#{forEncoder}")
  implicit def asJsonNotFound[T](
    implicit forEncoder: ImplicitNotFound[Encoder[T]]
  ): ImplicitNotFound[AsRaw[JsonValue, T]] = ImplicitNotFound()

  @implicitNotFound("#{forDecoder}")
  implicit def fromJsonNotFound[T](
    implicit forDecoder: ImplicitNotFound[Decoder[T]]
  ): ImplicitNotFound[AsReal[JsonValue, T]] = ImplicitNotFound()

  @implicitNotFound("#{forKeyEncoder}")
  implicit def asPlainNotFound[T: KeyEncoder](
    implicit forKeyEncoder: ImplicitNotFound[KeyEncoder[T]]
  ): ImplicitNotFound[AsRaw[PlainValue, T]] = ImplicitNotFound()

  @implicitNotFound("#{forKeyDecoder}")
  implicit def fromPlainNotFound[T: KeyDecoder](
    implicit forKeyDecoder: ImplicitNotFound[KeyDecoder[T]]
  ): ImplicitNotFound[AsReal[PlainValue, T]] = ImplicitNotFound()
}
object CirceRestImplicits extends CirceRestImplicits

trait CirceInstances[T] {
  @materializeWith(io.circe.derivation.`package`, "deriveEncoder")
  def encoder: Encoder.AsObject[T]
  @materializeWith(io.circe.derivation.`package`, "deriveDecoder")
  def decoder: Decoder[T]
}

abstract class HasCirceCodec[T](
  implicit instances: MacroInstances[Unit, CirceInstances[T]]
) {
  implicit final lazy val objectEncoder: Encoder.AsObject[T] = instances((), this).encoder
  implicit final lazy val decoder: Decoder[T] = instances((), this).decoder
}

trait CirceCustomizedInstances[T] {
  @materializeWith(io.circe.derivation.`package`, "deriveEncoder")
  def encoder(nameTransform: String => String, discriminator: Option[String]): Encoder.AsObject[T]
  @materializeWith(io.circe.derivation.`package`, "deriveDecoder")
  def decoder(nameTransform: String => String, useDefaults: Boolean, discriminator: Option[String]): Decoder[T]
}

abstract class HasCirceCustomizedCodec[T](
  nameTransform: String => String,
  useDefaults: Boolean = true,
  discriminator: Option[String] = None
)(implicit instances: MacroInstances[Unit, CirceCustomizedInstances[T]]) {
  implicit final lazy val objectEncoder: Encoder.AsObject[T] = instances((), this).encoder(nameTransform, discriminator)
  implicit final lazy val decoder: Decoder[T] = instances((), this).decoder(nameTransform, useDefaults, discriminator)
}

case class CirceAddress(city: String, zip: String)
object CirceAddress extends HasCirceCustomizedCodec[CirceAddress](_.toUpperCase)

case class CircePerson(id: Long, name: String, address: Option[CirceAddress] = None)
object CircePerson extends HasCirceCodec[CircePerson]

abstract class CirceRestApiCompanion[T](
  implicit instances: MacroInstances[(CirceRestImplicits, FutureRestImplicits), FullInstances[T]]
) extends RestApiCompanion[(CirceRestImplicits, FutureRestImplicits), T]((CirceRestImplicits, FutureRestImplicits))

trait CirceRestApi {
  def createPerson(person: CircePerson): Future[String]
}
object CirceRestApi extends CirceRestApiCompanion[CirceRestApi]
