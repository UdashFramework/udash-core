package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.seq.PropertySeqCombinedReadableSeqProperty

trait Properties {
  final val Property = single.Property
  final val ModelProperty = model.ModelProperty
  final val SeqProperty = seq.SeqProperty
  type CallbackSequencer = io.udash.properties.CallbackSequencer
  final val CallbackSequencer = io.udash.properties.CallbackSequencer

  type CastableReadableProperty[A] = single.CastableReadableProperty[A]
  type CastableProperty[A] = single.CastableProperty[A]
  type ReadableProperty[A] = single.ReadableProperty[A]
  type Property[A] = single.Property[A]
  type ReadableModelProperty[A] = model.ReadableModelProperty[A]
  type ModelProperty[A] = model.ModelProperty[A]
  type ReadableSeqProperty[A] = seq.ReadableSeqProperty[A, _ <: ReadableProperty[A]]
  type SeqProperty[A] = seq.SeqProperty[A, _ <: Property[A]]

  type Patch[+P <: ReadableProperty[_]] = seq.Patch[P]
  type Blank[T] = io.udash.properties.Blank[T]
  final val Blank = io.udash.properties.Blank

  import Properties._
  implicit def any2Property[A](value: A): Any2Property[A] = new Any2Property(value)
  implicit def any2SeqProperty[A](value: Seq[A]): Any2SeqProperty[A] = new Any2SeqProperty(value)
  implicit def propertySeq2SeqProperty[A](value: ISeq[ReadableProperty[A]]): PropertySeq2SeqProperty[A] = new PropertySeq2SeqProperty(value)
  implicit def booleanProp2BooleanOpsProperty(value: Property[Boolean]): BooleanPropertyOps = new BooleanPropertyOps(value)
}

object Properties extends Properties {
  class Any2Property[A] private[properties](private val value: A) extends AnyVal {
    def toProperty[B >: A : PropertyCreator]: ReadableProperty[B] = PropertyCreator[B].newImmutableProperty(value)
    def toModelProperty[B >: A : ModelPropertyCreator]: ReadableModelProperty[B] = ModelPropertyCreator[B].newImmutableProperty(value)
  }

  class Any2SeqProperty[A] private[properties](private val value: Seq[A]) extends AnyVal {
    def toSeqProperty: ReadableSeqProperty[A] = new ImmutableSeqProperty[A, Seq](value)
  }

  class PropertySeq2SeqProperty[A] private[properties](private val value: ISeq[ReadableProperty[A]]) extends AnyVal {
    def combineToSeqProperty: ReadableSeqProperty[A] = new PropertySeqCombinedReadableSeqProperty[A](value)
  }

  class BooleanPropertyOps private[properties](private val underlying: Property[Boolean]) extends AnyVal {
    /** Toggles the value of the underlying boolean-backed property.
      * @param force If true, the value change listeners will be fired even if value didn't change.
      * */
    def toggle(force: Boolean = true): Unit = underlying.set(!underlying.get, force)
  }
}
