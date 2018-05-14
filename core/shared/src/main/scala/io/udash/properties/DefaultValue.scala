package io.udash.properties

import com.avsystem.commons.misc.{AbstractCase, Opt}

import scala.annotation.implicitNotFound
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

@implicitNotFound(
  "Class ${A} does not have a default value. Please, specify the value of this property " +
  "or add `implicit val default: DefaultValue[${A}] = ???` in ${A}'s companion object."
)
trait DefaultValue[A] {
  def value: A
}

object DefaultValue extends LowPrioImplicits {
  case class Simple[A](value: A) extends AbstractCase with DefaultValue[A]
  case class Factory[A](factory: () => A) extends AbstractCase with DefaultValue[A] {
    override def value: A = factory()
  }

  implicit val double: DefaultValue[Double] = Simple(0.0)
  implicit val float: DefaultValue[Float] = Simple(0.0f)
  implicit val long: DefaultValue[Long] = Simple(0L)
  implicit val int: DefaultValue[Int] = Simple(0)
  implicit val short: DefaultValue[Short] = Simple(0)
  implicit val byte: DefaultValue[Byte] = Simple(0)
  implicit val char: DefaultValue[Char] = Simple(' ')
  implicit val boolean: DefaultValue[Boolean] = Simple(false)
  implicit val unit: DefaultValue[Unit] = Simple(())
  implicit val string: DefaultValue[String] = Simple("")

  implicit def option[A]: DefaultValue[Option[A]] = Simple(None)
  implicit def opt[A]: DefaultValue[Opt[A]] = Simple(Opt.Empty)
  implicit def traversable[T, A[_] <: Traversable[_]](implicit ev: CanBuildFrom[Seq[T], T, A[T]]): DefaultValue[A[T]] = Simple(Seq.empty[T].to[A])
}

trait LowPrioImplicits {
  @deprecated("Setting Property value to `null` is highly unrecommended. Please, define implicit `DefaultValue` for your type.", "0.7.0")
  implicit def fallbackNull[A]: DefaultValue[A] = DefaultValue.Simple[A](null.asInstanceOf[A])

  private type D[T] = DefaultValue[T]
  implicit def tuple1[T : D]: D[Tuple1[T]] =
    DefaultValue.Simple(Tuple1(implicitly[D[T]].value))
  implicit def tuple2[T1 : D, T2 : D]: D[(T1, T2)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value))
  implicit def tuple3[T1 : D, T2 : D, T3 : D]: D[(T1, T2, T3)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value))
  implicit def tuple4[T1 : D, T2 : D, T3 : D, T4 : D]: D[(T1, T2, T3, T4)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value))
  implicit def tuple5[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D]: D[(T1, T2, T3, T4, T5)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value))
  implicit def tuple6[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D]: D[(T1, T2, T3, T4, T5, T6)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value))
  implicit def tuple7[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D]: D[(T1, T2, T3, T4, T5, T6, T7)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value))
  implicit def tuple8[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value))
  implicit def tuple9[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value))
  implicit def tuple10[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value))
  implicit def tuple11[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value))
  implicit def tuple12[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value))
  implicit def tuple13[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value))
  implicit def tuple14[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value))
  implicit def tuple15[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value))
  implicit def tuple16[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value))
  implicit def tuple17[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value))
  implicit def tuple18[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D, T18 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value, implicitly[D[T18]].value))
  implicit def tuple19[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D, T18 : D, T19 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value, implicitly[D[T18]].value, implicitly[D[T19]].value))
  implicit def tuple20[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D, T18 : D, T19 : D, T20 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value, implicitly[D[T18]].value, implicitly[D[T19]].value, implicitly[D[T20]].value))
  implicit def tuple21[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D, T18 : D, T19 : D, T20 : D, T21 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value, implicitly[D[T18]].value, implicitly[D[T19]].value, implicitly[D[T20]].value, implicitly[D[T21]].value))
  implicit def tuple22[T1 : D, T2 : D, T3 : D, T4 : D, T5 : D, T6 : D, T7 : D, T8 : D, T9 : D, T10 : D, T11 : D, T12 : D, T13 : D, T14 : D, T15 : D, T16 : D, T17 : D, T18 : D, T19 : D, T20 : D, T21 : D, T22 : D]: D[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] =
    DefaultValue.Simple((implicitly[D[T1]].value, implicitly[D[T2]].value, implicitly[D[T3]].value, implicitly[D[T4]].value, implicitly[D[T5]].value, implicitly[D[T6]].value, implicitly[D[T7]].value, implicitly[D[T8]].value, implicitly[D[T9]].value, implicitly[D[T10]].value, implicitly[D[T11]].value, implicitly[D[T12]].value, implicitly[D[T13]].value, implicitly[D[T14]].value, implicitly[D[T15]].value, implicitly[D[T16]].value, implicitly[D[T17]].value, implicitly[D[T18]].value, implicitly[D[T19]].value, implicitly[D[T20]].value, implicitly[D[T21]].value, implicitly[D[T22]].value))
}