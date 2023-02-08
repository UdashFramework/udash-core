package io.udash.properties

import com.avsystem.commons.Opt
import com.avsystem.commons.misc.AbstractCase
import io.udash.utils.CrossCollections

import scala.annotation.implicitNotFound
import scala.collection.{Factory => CFactory}

@implicitNotFound(
  "Class ${A} does not have a blank value. Please, specify the value of this property or add `implicit val blank: Blank[${A}] = ???` in ${A}'s companion object."
)
trait Blank[A] {
  def value: A
}

object Blank {
  def apply[A](implicit ev: Blank[A]): Blank[A] = ev

  final case class Simple[A](value: A) extends AbstractCase with Blank[A]
  final case class Factory[A](factory: () => A) extends AbstractCase with Blank[A] {
    override def value: A = factory()
  }

  implicit val Double: Blank[Double] = Simple(0.0)
  implicit val Float: Blank[Float] = Simple(0.0f)
  implicit val Long: Blank[Long] = Simple(0L)
  implicit val Int: Blank[Int] = Simple(0)
  implicit val Short: Blank[Short] = Simple(0)
  implicit val Byte: Blank[Byte] = Simple(0)
  implicit val Boolean: Blank[Boolean] = Simple(false)
  implicit val Unit: Blank[Unit] = Simple(())
  implicit val String: Blank[String] = Simple("")

  implicit def option[A]: Blank[Option[A]] = Simple(None)
  implicit def opt[A]: Blank[Opt[A]] = Simple(Opt.Empty)
  implicit def map[K, V]: Blank[Map[K, V]] = Simple(Map.empty)
  implicit def traversable[T, A[_] <: Iterable[_]](implicit fac: CFactory[T, A[T]]): Blank[A[T]] = Simple(CrossCollections.createArray[T].to(fac))
}