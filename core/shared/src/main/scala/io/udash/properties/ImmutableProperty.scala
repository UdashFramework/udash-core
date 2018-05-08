package io.udash.properties

import java.util.UUID

import io.udash.properties.model.{ModelPropertyMacroApi, ReadableModelProperty}
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

import scala.concurrent.Future

private[properties] class ImmutableProperty[A](value: A) extends ReadableProperty[A] with ReadableModelProperty[A] with ModelPropertyMacroApi[A] {
  override type ModelSubProperty[_] = ImmutableProperty[_]

  /** Unique property ID. */
  override val id: UUID = UUID.randomUUID()

  /** @return Current property value. */
  @inline override def get: A = value

  /** @return validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  @inline override def isValid: Future[ValidationResult] = Future.successful(Valid)

  /** Property containing validation result. */
  @inline override def valid: ReadableProperty[ValidationResult] = ImmutableProperty.validProp

  /**
    * Registers listener which will be called on value change.
    *
    * @param initUpdate If `true`, listener will be instantly triggered with current value of property.
    */
  override def listen(valueListener: A => Any, initUpdate: Boolean): Registration = {
    if (initUpdate) valueListener(value)
    ImmutableProperty.noopRegistration
  }

  /** Registers listener which will be called on the next value change. This listener will be fired only once. */
  override def listenOnce(valueListener: A => Any): Registration = ImmutableProperty.noopRegistration

  override protected[properties] def parent: ReadableProperty[_] = null
  override protected[properties] def fireValueListeners(): Unit = {}
  override protected[properties] def valueChanged(): Unit = {}
  override protected[properties] def validate(): Unit = {}
  override private[properties] def listenersCount() = 0

  override def getSubProperty[T](getter: A => T, key: String): ImmutableProperty[T] =
    new ImmutableProperty[T](getter(value))

  override def getSubModel[T](getter: A => T, key: String): ReadableModelProperty[T] =
    new ImmutableProperty[T](getter(value))

  override def getSubSeq[T](getter: A => Seq[T], key: String): ReadableSeqProperty[T, ReadableProperty[T]] =
    new ImmutableSeqProperty[T](getter(value))

  override def transform[B](transformer: A => B): ReadableProperty[B] =
    new ImmutableProperty[B](transformer(value))

  override def transformToSeq[B: PropertyCreator](transformer: A => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ImmutableSeqProperty[B](transformer(value))

  override def streamTo[B](target: Property[B], initUpdate: Boolean)(transformer: A => B): Registration = {
    if (initUpdate) target.set(transformer(value))
    ImmutableProperty.noopRegistration
  }
}

private[properties] class ImmutableSeqProperty[A](value: Seq[A]) extends ImmutableProperty[Seq[A]](value) with ReadableSeqProperty[A, ImmutableProperty[A]] {
  override lazy val elemProperties: Seq[ImmutableProperty[A]] =
    value.map(v => new ImmutableProperty(v))

  override def size: Int = value.size
  override def isEmpty: Boolean = value.isEmpty
  override def nonEmpty: Boolean = value.nonEmpty

  override def listenStructure(structureListener: Patch[ImmutableProperty[A]] => Any): Registration =
    ImmutableProperty.noopRegistration

  override def transform[B](transformer: A => B): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ImmutableSeqProperty(value.map(transformer))

  override def reversed(): ReadableSeqProperty[A, ReadableProperty[A]] =
    new ImmutableSeqProperty(value.reverse)

  override def filter(matcher: A => Boolean): ReadableSeqProperty[A, _ <: ImmutableProperty[A]] =
    new ImmutableSeqProperty(value.filter(matcher))

  override def zipWithIndex: ReadableSeqProperty[(A, Int), ReadableProperty[(A, Int)]] =
    new ImmutableSeqProperty(value.zipWithIndex)
}

private[properties] object ImmutableProperty {
  val validProp: ImmutableProperty[ValidationResult] = new ImmutableProperty(Valid)
  val noopRegistration = new Registration {
    override def cancel(): Unit = {}
    override def restart(): Unit = {}
    override def isActive: Boolean = true
  }
}