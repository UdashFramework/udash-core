package io.udash.rest
package tsgen

import com.avsystem.commons.misc.{NamedEnum, NamedEnumCompanion, Timestamp}
import com.avsystem.commons.{BMap, BSeq, BSet, JDate, Opt, OptArg, classTag}
import io.udash.rest.raw.RawRest.AsyncEffect

import scala.reflect.ClassTag

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
  def apply[T](tsType: => TsT): TsTypeTag[TsT, T] = TsTypeTag(tsType)
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
  implicit val ByteTag: TsPlainAndJsonTypeTag[Byte] = TsPlainAndJsonTypeTag(TsType.Number)
  implicit val ShortTag: TsPlainAndJsonTypeTag[Short] = TsPlainAndJsonTypeTag(TsType.Number)
  implicit val IntTag: TsPlainAndJsonTypeTag[Int] = TsPlainAndJsonTypeTag(TsType.Number)
  implicit val FloatTag: TsPlainAndJsonTypeTag[Float] = TsPlainAndJsonTypeTag(TsType.Number)
  implicit val DoubleTag: TsPlainAndJsonTypeTag[Double] = TsPlainAndJsonTypeTag(TsType.Number)
  implicit val CharTag: TsPlainAndJsonTypeTag[Char] = TsPlainAndJsonTypeTag(TsType.String)
  implicit val StringTag: TsPlainAndJsonTypeTag[String] = TsPlainAndJsonTypeTag(TsType.String)
  implicit val TimestampTag: TsPlainAndJsonTypeTag[Timestamp] = TsPlainAndJsonTypeTag(TsType.Timestamp)
  implicit val DateTag: TsPlainAndJsonTypeTag[JDate] = TsPlainAndJsonTypeTag(TsType.Timestamp)

  implicit def seqTag[C[X] <: BSeq[X], T: TsJsonTypeTag]: TsJsonTypeTag[C[T]] =
    TsJsonTypeTag(TsType.arrayJson(TsJsonType[T]))

  implicit def setTag[C[X] <: BSet[X], T: TsJsonTypeTag]: TsJsonTypeTag[C[T]] =
    TsJsonTypeTag(TsType.arrayJson(TsJsonType[T]))

  implicit def mapTag[M[X, Y] <: BMap[X, Y], K: TsPlainTypeTag, V: TsJsonTypeTag]: TsJsonTypeTag[M[K, V]] =
    TsJsonTypeTag(TsType.dictionaryJson(TsPlainType[K], TsJsonType[V]))

  implicit def optTag[T: TsJsonTypeTag]: TsJsonTypeTag[Opt[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))

  implicit def optArgTag[T: TsJsonTypeTag]: TsJsonTypeTag[OptArg[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))

  implicit def optionTag[T: TsJsonTypeTag]: TsJsonTypeTag[Option[T]] =
    TsJsonTypeTag(TsType.nullableJson(TsJsonType[T]))

  implicit def namedEnumTag[T <: NamedEnum : TsModuleTag : ClassTag](
    implicit companion: NamedEnumCompanion[T]
  ): TsPlainAndJsonTypeTag[T] = {
    val name = classTag[T].runtimeClass.getSimpleName
    val values = companion.values.map(_.name)
    TsPlainAndJsonTypeTag(TsEnum(TsModule[T], name, values))
  }
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
