package io.udash.properties.model

import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, SeqProperty}
import io.udash.properties.single.{CastableProperty, CastableReadableProperty, Property, ReadableProperty}

import scala.concurrent.Future

object ModelProperty {
  /** Creates an empty ModelProperty[T].
    * It's not recommended to use this method. Use `apply` with initial value if possible. */
  def empty[T : ModelPropertyCreator]: ModelProperty[T] =
    Property.empty(implicitly[ModelPropertyCreator[T]]).asModel

  /** Creates an empty ModelProperty[T]. */
  @deprecated("Use `ModelProperty.empty` instead.", "0.6.0")
  def apply[T : ModelPropertyCreator]: ModelProperty[T] =
    empty

  /** Creates ModelProperty[T] with initial value. */
  def apply[T : ModelPropertyCreator](init: T): ModelProperty[T] =
    Property[T](init)(implicitly[ModelPropertyCreator[T]]).asModel
}

/** Property based on trait representing data model. Read only access. */
trait ReadableModelProperty[A] extends ReadableProperty[A] {
  protected val properties = CrossCollections.createDictionary[Property[_]]

  /** Returns child ModelProperty[B]. */
  def roSubModel[B](f: A => B)(implicit ev: ModelPropertyCreator[B]): ReadableModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectProperty[B]. */
  def roSubProp[B](f: A => B)(implicit ev: PropertyCreator[B]): ReadableProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def roSubSeq[B](f: A => Seq[B])(implicit ev: SeqPropertyCreator[B]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /**
    * ModelProperty is valid if all validators return [[io.udash.properties.Valid]] and all subproperties are valid.
    *
    * @return Validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed.
    */
  override def isValid: Future[ValidationResult] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import Validator._

    if (validationResult == null) {
      validationResult = Future.sequence(
        (Iterator(super.isValid) ++ properties.values.iterator.map(p => p.isValid)).toSeq
      ).foldValidationResult
    }
    validationResult
  }
}

/** Property based on trait representing data model. */
trait ModelProperty[A] extends ReadableModelProperty[A] with Property[A] {
  /** Returns child ModelProperty[B]. */
  def subModel[B](f: A => B)(implicit ev: ModelPropertyCreator[B]): ModelProperty[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectProperty[B]. */
  def subProp[B](f: A => B)(implicit ev: PropertyCreator[B]): Property[B] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def subSeq[B](f: A => Seq[B])(implicit ev: SeqPropertyCreator[B]): SeqProperty[B, CastableProperty[B]] =
    macro io.udash.macros.PropertyMacros.reifySubProp[A, B]
}

