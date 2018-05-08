package io.udash.properties.model

import io.udash.properties._
import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.{AbstractReadableProperty, CastableReadableProperty, Property, ReadableProperty}

import scala.concurrent.Future
import scala.language.higherKinds

/** Property based on trait representing data model. Read only access. */
trait ReadableModelProperty[A] extends ReadableProperty[A] {
  /** Returns child ModelProperty[B]. */
  def roSubModel[B](f: A => B)(implicit ev: ModelPropertyCreator[B]): ReadableModelProperty[B] =
  macro io.udash.macros.PropertyMacros.reifyRoSubModel[A, B]

  /** Returns child DirectProperty[B]. */
  def roSubProp[B](f: A => B)(implicit ev: PropertyCreator[B]): ReadableProperty[B] =
  macro io.udash.macros.PropertyMacros.reifyRoSubProp[A, B]

  /** Returns child DirectSeqProperty[B] */
  def roSubSeq[B](f: A => Seq[B])(implicit ev: SeqPropertyCreator[B]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
  macro io.udash.macros.PropertyMacros.reifyRoSubSeq[A, B]

}

trait ModelPropertyMacroApi[A] {
  type ModelSubProperty[_] <: ModelPropertyMacroApi[_]
  def getSubProperty[T](getter: A => T, key: String): ReadableProperty[T]
  def getSubModel[T](getter: A => T, key: String): ReadableModelProperty[T]
  def getSubSeq[T](getter: A => Seq[T], key: String): ReadableSeqProperty[T, ReadableProperty[T]]
}

private[properties] trait AbstractReadableModelProperty[A] extends ReadableModelProperty[A] with AbstractReadableProperty[A] {
  protected val properties = CrossCollections.createDictionary[Property[_]]

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
