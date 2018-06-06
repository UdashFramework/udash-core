package io.udash.properties

import com.avsystem.commons._

trait Properties {
  val Property = single.Property
  val ModelProperty = model.ModelProperty
  val SeqProperty = seq.SeqProperty
  type CallbackSequencer = io.udash.properties.CallbackSequencer
  val CallbackSequencer = io.udash.properties.CallbackSequencer

  type CastableReadableProperty[A] = single.CastableReadableProperty[A]
  type CastableProperty[A] = single.CastableProperty[A]
  type ReadableProperty[A] = single.ReadableProperty[A]
  type Property[A] = single.Property[A]
  type ReadableModelProperty[A] = model.ReadableModelProperty[A]
  type ModelProperty[A] = model.ModelProperty[A]
  type ReadableSeqProperty[A] = seq.ReadableSeqProperty[A, _ <: ReadableProperty[A]]
  type SeqProperty[A] = seq.SeqProperty[A, _ <: Property[A]]

  type Patch[+P <: ReadableProperty[_]] = seq.Patch[P]

  type ValidationError = io.udash.properties.ValidationError
  type DefaultValidationError = io.udash.properties.DefaultValidationError
  type Validator[T] = io.udash.properties.Validator[T]
  type ValidationResult = io.udash.properties.ValidationResult
  val Valid = io.udash.properties.Valid
  val Invalid = io.udash.properties.Invalid
  val DefaultValidationError = io.udash.properties.DefaultValidationError

  import Properties._
  implicit def any2Property[A](value: A): Any2Property[A] = new Any2Property(value)
  implicit def any2ModelProperty[A: ModelPropertyCreator](value: A): Any2ModelProperty[A] = new Any2ModelProperty(value)
  implicit def any2SeqProperty[A](value: Seq[A]): Any2SeqProperty[A] = new Any2SeqProperty(value)
  implicit def propertySeq2SeqProperty[A](value: ISeq[Property[A]]): PropertySeq2SeqProperty[A] = new PropertySeq2SeqProperty(value)
}

object Properties extends Properties {
  class Any2Property[A] private[properties](private val value: A) extends AnyVal {
    def toProperty: ReadableProperty[A] = new ImmutableProperty[A](value)
  }

  class Any2ModelProperty[A] private[properties](private val value: A) extends AnyVal {
    def toModelProperty: ReadableModelProperty[A] = new ImmutableProperty[A](value)
  }

  class Any2SeqProperty[A] private[properties](private val value: Seq[A]) extends AnyVal {
    def toSeqProperty: ReadableSeqProperty[A] = new ImmutableSeqProperty[A](value)
  }

  class PropertySeq2SeqProperty[A] private[properties](private val value: ISeq[Property[A]]) extends AnyVal {
    def combineToSeqProperty: ReadableSeqProperty[A] = new PropertySeqCombinedReadableSeqProperty[A](value)
  }
}