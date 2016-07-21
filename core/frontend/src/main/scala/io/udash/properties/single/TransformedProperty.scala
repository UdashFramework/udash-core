package io.udash.properties
package single

import java.util.UUID

import io.udash.utils.Registration

import scala.concurrent.{ExecutionContext, Future}

/** Represents ReadableProperty[A] transformed to ReadableProperty[B]. */
private[properties]
class TransformedReadableProperty[A, B](private val origin: ReadableProperty[A], transformer: A => B,
                                        override val id: UUID) extends ReadableProperty[B] {
  override def listen(l: (B) => Any): Registration =
    origin.listen((a: A) => l(transformer(a)))

  override def get: B =
    transformer(origin.get)

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()

  override protected[properties] def parent: ReadableProperty[_] =
    origin.parent

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()

  override implicit protected[properties] def executionContext: ExecutionContext =
    origin.executionContext
}

/** Represents Property[A] transformed to Property[B]. */
private[properties]
class TransformedProperty[A, B](private val origin: Property[A], transformer: A => B,
                                revert: B => A, override val id: UUID)
  extends TransformedReadableProperty[A, B](origin, transformer, id) with Property[B] {

  override def set(t: B): Unit =
    origin.set(revert(t))

  override def setInitValue(t: B): Unit =
    origin.setInitValue(revert(t))

  override def addValidator(v: Validator[B]): Registration =
    origin.addValidator(new Validator[A] {
      override def apply(element: A)(implicit ec: ExecutionContext): Future[ValidationResult] =
        v(transformer(element))(ec)
    })
}
