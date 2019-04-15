package io.udash.properties

import com.avsystem.commons.misc.{AbstractCase, Opt}

import scala.annotation.implicitNotFound
import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds

@implicitNotFound(
  "Class ${A} does not have a blank value. Please, specify the value of this property or add `implicit val blank: Blank[${A}] = ???` in ${A}'s companion object."
)
trait Blank[A] {
  def value: A
}

object Blank extends LowPrioImplicits {
  def apply[A](implicit ev: Blank[A]): Blank[A] = ev
  
  case class Simple[A](value: A) extends AbstractCase with Blank[A]
  case class Factory[A](factory: () => A) extends AbstractCase with Blank[A] {
    override def value: A = factory()
  }

  implicit val double: Blank[Double] = Simple(0.0)
  implicit val float: Blank[Float] = Simple(0.0f)
  implicit val long: Blank[Long] = Simple(0L)
  implicit val int: Blank[Int] = Simple(0)
  implicit val short: Blank[Short] = Simple(0)
  implicit val byte: Blank[Byte] = Simple(0)
  implicit val boolean: Blank[Boolean] = Simple(false)
  implicit val unit: Blank[Unit] = Simple(())
  implicit val string: Blank[String] = Simple("")

  private val simpleNone = Simple(None)
  implicit def option[A]: Blank[Option[A]] = simpleNone.asInstanceOf[Blank[Option[A]]]
  private val simpleOptEmpty = Simple(Opt.Empty)
  implicit def opt[A]: Blank[Opt[A]] = simpleOptEmpty.asInstanceOf[Blank[Opt[A]]]
  private val simpleMapEmpty = Simple(Map.empty)
  implicit def map[K, V]: Blank[Map[K, V]] = simpleMapEmpty.asInstanceOf[Blank[Map[K, V]]]
  implicit def traversable[T, A[_] <: Traversable[_]](implicit ev: CanBuildFrom[Nothing, T, A[T]]): Blank[A[T]] = Simple(Seq.empty[T].to[A])
}

trait LowPrioImplicits { this: Blank.type =>
  implicit def tuple1[T: Blank]: Blank[Tuple1[T]] =
    Blank.Simple(Tuple1(Blank[T].value))
  implicit def tuple2[T1: Blank, T2: Blank]: Blank[(T1, T2)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value))
  implicit def tuple3[T1: Blank, T2: Blank, T3: Blank]: Blank[(T1, T2, T3)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value))
  implicit def tuple4[T1: Blank, T2: Blank, T3: Blank, T4: Blank]: Blank[(T1, T2, T3, T4)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value))
  implicit def tuple5[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank]: Blank[(T1, T2, T3, T4, T5)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value))
  implicit def tuple6[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank]: Blank[(T1, T2, T3, T4, T5, T6)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value))
  implicit def tuple7[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value))
  implicit def tuple8[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value))
  implicit def tuple9[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value))
  implicit def tuple10[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value))
  implicit def tuple11[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value))
  implicit def tuple12[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value))
  implicit def tuple13[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value))
  implicit def tuple14[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value))
  implicit def tuple15[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value))
  implicit def tuple16[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value))
  implicit def tuple17[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value))
  implicit def tuple18[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank, T18: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value, Blank[T18].value))
  implicit def tuple19[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank, T18: Blank, T19: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value, Blank[T18].value, Blank[T19].value))
  implicit def tuple20[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank, T18: Blank, T19: Blank, T20: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value, Blank[T18].value, Blank[T19].value, Blank[T20].value))
  implicit def tuple21[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank, T18: Blank, T19: Blank, T20: Blank, T21: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value, Blank[T18].value, Blank[T19].value, Blank[T20].value, Blank[T21].value))
  implicit def tuple22[T1: Blank, T2: Blank, T3: Blank, T4: Blank, T5: Blank, T6: Blank, T7: Blank, T8: Blank, T9: Blank, T10: Blank, T11: Blank, T12: Blank, T13: Blank, T14: Blank, T15: Blank, T16: Blank, T17: Blank, T18: Blank, T19: Blank, T20: Blank, T21: Blank, T22: Blank]: Blank[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] =
    Blank.Simple((Blank[T1].value, Blank[T2].value, Blank[T3].value, Blank[T4].value, Blank[T5].value, Blank[T6].value, Blank[T7].value, Blank[T8].value, Blank[T9].value, Blank[T10].value, Blank[T11].value, Blank[T12].value, Blank[T13].value, Blank[T14].value, Blank[T15].value, Blank[T16].value, Blank[T17].value, Blank[T18].value, Blank[T19].value, Blank[T20].value, Blank[T21].value, Blank[T22].value))
}