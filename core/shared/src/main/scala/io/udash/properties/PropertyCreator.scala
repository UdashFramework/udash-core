package io.udash.properties

import java.util.UUID

import io.udash.properties.seq.DirectSeqPropertyImpl
import io.udash.properties.single.{CastableProperty, DirectPropertyImpl, ReadableProperty}

import scala.annotation.implicitNotFound

class PropertyCreator[T](creator: (ReadableProperty[_]) => CastableProperty[T]) {
  def newProperty(prt: ReadableProperty[_]): CastableProperty[T] =
    creator(prt)

  def newProperty(value: T, prt: ReadableProperty[_]): CastableProperty[T] = {
    val prop = newProperty(prt)
    prop.setInitValue(value)
    prop
  }
}

object PropertyCreator {
  implicit def propertyCreator[T]: PropertyCreator[T] =
    new PropertyCreator[T](prt => new DirectPropertyImpl[T](prt, PropertyCreator.newID()))

  implicit def seqPropertyCreator[T](implicit ev: PropertyCreator[T]): PropertyCreator[Seq[T]] =
    new PropertyCreator[Seq[T]](prt => new DirectSeqPropertyImpl[T](prt, PropertyCreator.newID())(ev))

  def newID(): UUID = UUID.randomUUID()
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
class ModelPropertyCreator[T](creator: (ReadableProperty[_]) => CastableProperty[T]) extends PropertyCreator[T](creator)
object ModelPropertyCreator {
  def materialize[T](implicit ev: IsModelPropertyTemplate[T]): ModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyModelPropertyCreator[T]

  implicit def tuple1[T]: ModelPropertyCreator[Tuple1[T]] = ModelPropertyCreator.materialize[Tuple1[T]]
  implicit def tuple2[T1, T2]: ModelPropertyCreator[(T1, T2)] = ModelPropertyCreator.materialize[(T1, T2)]
  implicit def tuple3[T1, T2, T3]: ModelPropertyCreator[(T1, T2, T3)] = ModelPropertyCreator.materialize[(T1, T2, T3)]
  implicit def tuple4[T1, T2, T3, T4]: ModelPropertyCreator[(T1, T2, T3, T4)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4)]
  implicit def tuple5[T1, T2, T3, T4, T5]: ModelPropertyCreator[(T1, T2, T3, T4, T5)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5)]
  implicit def tuple6[T1, T2, T3, T4, T5, T6]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6)]
  implicit def tuple7[T1, T2, T3, T4, T5, T6, T7]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7)]
  implicit def tuple8[T1, T2, T3, T4, T5, T6, T7, T8]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8)]
  implicit def tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9)]
  implicit def tuple10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)]
  implicit def tuple11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)]
  implicit def tuple12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)]
  implicit def tuple13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)]
  implicit def tuple14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)]
  implicit def tuple15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)]
  implicit def tuple16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)]
  implicit def tuple17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)]
  implicit def tuple18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)]
  implicit def tuple19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)]
  implicit def tuple20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)]
  implicit def tuple21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)]
  implicit def tuple22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] = ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)]
}

@implicitNotFound("Class ${T} cannot be used as ModelProperty template. Add `extends HasModelPropertyCreator[${T}]` to companion object of ${T}.")
case class MacroModelPropertyCreator[T](pc: ModelPropertyCreator[T]) extends AnyVal
object MacroModelPropertyCreator {
  implicit def materialize[T](implicit ev: IsModelPropertyTemplate[T]): MacroModelPropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyMacroModelPropertyCreator[T]
}