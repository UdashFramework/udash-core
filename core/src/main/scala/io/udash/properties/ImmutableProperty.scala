package io.udash.properties

import io.udash.properties.model.{ModelPropertyMacroApi, ReadableModelProperty}
import io.udash.properties.seq.{Patch, ReadableSeqProperty}
import io.udash.properties.single.{Property, ReadableProperty}
import io.udash.utils.Registration

import scala.collection.immutable
import scala.concurrent.Future

private[properties] class ImmutableProperty[A](value: A) extends ReadableProperty[A] {
  /** Unique property ID. */
  override val id: PropertyId = PropertyCreator.newID()

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
  override protected[properties] def listenersUpdate(): Unit = {}
  override def listenersCount(): Int = 0

  override def transform[B](transformer: A => B): ReadableProperty[B] =
    new ImmutableProperty[B](transformer(value))

  override def transformToSeq[B: PropertyCreator](transformer: A => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ImmutableSeqProperty[B](transformer(value))

  override def streamTo[B](target: Property[B], initUpdate: Boolean)(transformer: A => B): Registration = {
    if (initUpdate) target.set(transformer(value))
    ImmutableProperty.noopRegistration
  }

  override def readable: ReadableProperty[A] = this
}

private[properties] class ImmutableModelProperty[A](value: A)
  extends ImmutableProperty[A](value) with ModelPropertyMacroApi[A] {

  override def getSubProperty[T](getter: A => T, key: String): ImmutableProperty[T] =
    new ImmutableProperty[T](getter(value))

  override def getSubModel[T](getter: A => T, key: String): ReadableModelProperty[T] =
    new ImmutableModelProperty[T](getter(value))

  override def getSubSeq[T](getter: A => Seq[T], key: String): ReadableSeqProperty[T, ReadableProperty[T]] =
    new ImmutableSeqProperty[T](getter(value))

  override def readable: ReadableModelProperty[A] = this
}

private[properties] class ImmutableSeqProperty[A](value: immutable.Seq[A]) extends ImmutableProperty[Seq[A]](value) with ReadableSeqProperty[A, ImmutableProperty[A]] {
  def this(value: Seq[A]) = this(value match {
    case v: immutable.Seq[A] => v
    case _ => value.to[immutable.Seq]
  })

  override lazy val elemProperties: Seq[ImmutableProperty[A]] =
    value.map(v => new ImmutableProperty(v))

  override def size: Int = value.size
  override def isEmpty: Boolean = value.isEmpty
  override def nonEmpty: Boolean = value.nonEmpty
  override def structureListenersCount(): Int = 0

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

  override def readable: ReadableSeqProperty[A, ImmutableProperty[A]] = this
}

private[properties] object ImmutableProperty {
  val validProp: ImmutableProperty[ValidationResult] = new ImmutableProperty(Valid)
  val noopRegistration = new Registration {
    override def cancel(): Unit = {}
    override def restart(): Unit = {}
    override def isActive: Boolean = true
  }
}