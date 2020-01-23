package io.udash.properties.single

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.seq.{SeqProperty, SeqPropertyFromSingleValue}
import io.udash.utils.Registration

object Property {
  /** Creates a blank `DirectProperty[T]`.  */
  def blank[T: PropertyCreator : Blank]: CastableProperty[T] =
    PropertyCreator[T].newProperty(null)

  /** Creates `DirectProperty[T]` with initial value. */
  def apply[T: PropertyCreator](init: T): CastableProperty[T] =
    PropertyCreator[T].newProperty(init, null)
}

/** Property which can be modified. */
trait Property[A] extends ReadableProperty[A] {
  /** Changes current property value. Fires value change listeners.
    * @param t Should not be null!
    * @param force If true, the value change listeners will be fired even if value didn't change. */
  def set(t: A, force: Boolean = false): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Fires value change listeners with current value. */
  def touch(): Unit

  /** Removes all listeners from property. */
  def clearListeners(): Unit

  /**
    * Creates Property[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return New Property[B], which will be synchronised with original Property[A].
    */
  def bitransform[B](transformer: A => B)(revert: B => A): Property[B]

  /**
    * Creates SeqProperty[B] linked to `this`. Changes will be synchronized with `this` in both directions.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @param revert Method transforming type Seq[B] to A.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original Property[A].
    */
  def bitransformToSeq[B](transformer: A => BSeq[B])(revert: BSeq[B] => A): SeqProperty[B, Property[B]]

  /**
    * Bidirectionally synchronizes Property[B] with `this`. The transformed value is synchronized from `this`
    * to Property[B] on initialization.
    *
    * @param p           Property to be synchronized with `this`.
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert      Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return Bidirectional registration between existing and new property.
    */
  def sync[B](p: Property[B])(transformer: A => B, revert: B => A): Registration
}

/** Property which can be modified. */
private[properties] trait AbstractProperty[A] extends AbstractReadableProperty[A] with Property[A] {

  override def clearListeners(): Unit = {
    listenersUpdate()
    listeners.clear()
    oneTimeListeners.clear()
  }

  override def bitransform[B](transformer: A => B)(revert: B => A): Property[B] =
    new TransformedProperty[A, B](this, transformer, revert)

  override def bitransformToSeq[B](transformer: A => BSeq[B])(revert: BSeq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert)

  override def sync[B](p: Property[B])(transformer: A => B, revert: B => A): Registration = {
    val transformerRegistration = this.streamTo(p)(transformer)
    val revertRegistration = p.streamTo(this, initUpdate = false)(revert)
    new Registration {
      override def cancel(): Unit = {
        transformerRegistration.cancel()
        revertRegistration.cancel()
      }

      override def isActive: Boolean = {
        transformerRegistration.isActive && revertRegistration.isActive
      }

      override def restart(): Unit = {
        transformerRegistration.restart()
        revertRegistration.restart()
        touch()
      }
    }
  }

  //def cmb[B, O](p: Property[B])(transformer: (A, B))
}
