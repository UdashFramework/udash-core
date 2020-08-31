package io.udash.rest
package tsgen

import com.avsystem.commons.{BSeq, BSet, Opt, OptArg}
import io.udash.rest.raw.RawRest.AsyncEffect

final class TsTypeTag[+TsT <: TsType, T](lazyTsType: => TsT) {
  lazy val tsType: TsT = lazyTsType
}

object `package` {
  type TsPlainTypeTag[T] = TsTypeTag[TsPlainType, T]
  type TsJsonTypeTag[T] = TsTypeTag[TsJsonType, T]
  type TsPlainAndJsonTypeTag[T] = TsTypeTag[TsPlainType with TsJsonType, T]
  type TsBodyTypeTag[T] = TsTypeTag[TsBodyType, T]
  type TsResponseTypeTag[T] = TsTypeTag[TsResponseType, T]
}

sealed abstract class TsTypeTagCompanion[TsT <: TsType] {
  def apply[T](tsType: TsT): TsTypeTag[TsT, T] = TsTypeTag(tsType)
}
object TsPlainTypeTag extends TsTypeTagCompanion[TsPlainType]
object TsJsonTypeTag extends TsTypeTagCompanion[TsJsonType]
object TsPlainAndJsonTypeTag extends TsTypeTagCompanion[TsPlainType with TsJsonType]
object TsBodyTypeTag extends TsTypeTagCompanion[TsBodyType]
object TsResponseTypeTag extends TsTypeTagCompanion[TsResponseType]

object TsTypeTag extends TsTypeTagLowPrio {
  def apply[TsT <: TsType, T](tsType: => TsT): TsTypeTag[TsT, T] =
    new TsTypeTag(tsType)

  implicit val UnitResponseTag: TsResponseTypeTag[Unit] = TsResponseTypeTag(TsType.Void)
  implicit val NothingTag: TsPlainAndJsonTypeTag[Nothing] = TsPlainAndJsonTypeTag(TsType.Never)
  implicit val BooleanTag: TsPlainAndJsonTypeTag[Boolean] = TsPlainAndJsonTypeTag(TsType.Boolean)
  implicit val ByteTag: TsPlainAndJsonTypeTag[Byte] = TsPlainAndJsonTypeTag(TsType.Integer)
  implicit val ShortTag: TsPlainAndJsonTypeTag[Short] = TsPlainAndJsonTypeTag(TsType.Integer)
  implicit val IntTag: TsPlainAndJsonTypeTag[Int] = TsPlainAndJsonTypeTag(TsType.Integer)
  implicit val FloatTag: TsPlainAndJsonTypeTag[Float] = TsPlainAndJsonTypeTag(TsType.Float)
  implicit val DoubleTag: TsPlainAndJsonTypeTag[Double] = TsPlainAndJsonTypeTag(TsType.Float)
  implicit val CharTag: TsPlainAndJsonTypeTag[Char] = TsPlainAndJsonTypeTag(TsType.String)
  implicit val StringTag: TsPlainAndJsonTypeTag[String] = TsPlainAndJsonTypeTag(TsType.String)

  implicit def seqTag[C[X] <: BSeq[X], T: TsJsonTypeTag]: TsJsonTypeTag[C[T]] =
    TsJsonTypeTag(TsType.arrayJson(TsJsonType[T]))

  implicit def setTag[C[X] <: BSet[X], T: TsJsonTypeTag]: TsJsonTypeTag[C[T]] =
    TsJsonTypeTag(TsType.arrayJson(TsJsonType[T]))

  implicit def optTag[T: TsJsonTypeTag]: TsJsonTypeTag[Opt[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))

  implicit def optArgTag[T: TsJsonTypeTag]: TsJsonTypeTag[OptArg[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))

  implicit def optionTag[T: TsJsonTypeTag]: TsJsonTypeTag[Option[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))
}
trait TsTypeTagLowPrio { this: TsTypeTag.type =>
  implicit def bodyTagFromJsonTag[T: TsJsonTypeTag]: TsBodyTypeTag[T] =
    TsBodyTypeTag(TsType.jsonAsBody(TsJsonType[T]))

  implicit def responseTagFromBodyTag[T: TsBodyTypeTag]: TsResponseTypeTag[T] =
    TsResponseTypeTag(TsType.bodyAsResponse(TsBodyType[T]))
}

case class TsResultTypeTag[T](tsType: TsResponseType) extends AnyVal
object TsResultTypeTag {
  implicit def fromAsyncEffect[F[_] : AsyncEffect, T: TsResponseTypeTag]: TsResultTypeTag[F[T]] =
    TsResultTypeTag(TsResponseType[T])
}
