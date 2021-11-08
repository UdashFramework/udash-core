package io.udash.properties.single

import com.avsystem.commons._
import io.udash.properties._
import io.udash.properties.seq.{ReadableSeqProperty, ReadableSeqPropertyFromSingleValue}
import io.udash.utils.Registration

/** Base interface of every Property in Udash. */
trait ReadableProperty[+A] {

  /** @return Current property value. */
  def get: A

  /**
   * Registers listener which will be called on value change.
   *
   * @param initUpdate If `true`, listener will be instantly triggered with current value of property.
   */
  def listen(valueListener: A => Any, initUpdate: Boolean = false): Registration

  /** Registers listener which will be called on the next value change. This listener will be fired only once. */
  def listenOnce(valueListener: A => Any): Registration

  /** Returns listeners count. */
  def listenersCount(): Int

  /** This method should be called when the value has changed. */
  protected[properties] def valueChanged(): Unit

  /** This method should be called when the listener is registered or removed. */
  protected[properties] def listenersUpdate(): Unit

  /**
   * Creates ReadableProperty[B] linked to `this`. Changes will be synchronized with `this`.
   *
   * @param transformer Method transforming type A of existing Property to type B of new Property.
   * @tparam B Type of new Property.
   * @return New ReadableProperty[B], which will be synchronised with original ReadableProperty[A].
   */
  def transform[B](transformer: A => B): ReadableProperty[B]

  /**
   * Creates ReadableSeqProperty[B] linked to `this`. Changes will be synchronized with `this`.
   *
   * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
   * @tparam B Type of elements in new SeqProperty.
   * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableProperty[A].
   */
  def transformToSeq[B: PropertyCreator](transformer: A => BSeq[B]): ReadableSeqProperty[B, ReadableProperty[B]]

  /**
   * Streams value changes to the `target` property.
   * It is not as strong relation as `transform`, because `target` can change value independently.
   */
  def streamTo[B](target: Property[B], initUpdate: Boolean = true)(transformer: A => B): Registration

  /**
   * Creates a mutable copy of this property which follows the stream of updates from this property.
   * Similarly to [[streamTo]], the target can change value independently and origin value updates can be cancelled.
   */
  def mirror[B >: A : PropertyCreator](): MirrorProperty[B] =
    new MirrorProperty(this)

  /**
   * Combines two properties into a new one. Created property will be updated after any change in the origin ones.
   *
   * @param property `Property[B]` to combine with `this`.
   * @param combiner Method combining values A and B into O.
   * @tparam B Type of elements in provided property.
   * @tparam O Output property elements type.
   * @return Property[O] updated on any change in `this` or `property`.
   */
  def combine[B, O](property: ReadableProperty[B])(combiner: (A, B) => O): ReadableProperty[O] =
    new CombinedProperty[A, B, O](this, property, combiner)
}

final class MirrorProperty[A: PropertyCreator](origin: ReadableProperty[A]) {
  private val castable: CastableProperty[A] = PropertyCreator[A].newProperty(origin.get, null)
  private val registration = origin.streamTo(castable, initUpdate = false)(identity)
}
object MirrorProperty {
  implicit def castable[A](property: MirrorProperty[A]): CastableProperty[A] = property.castable
  implicit def registration(property: MirrorProperty[_]): Registration = property.registration
}

private[properties] trait AbstractReadableProperty[A] extends ReadableProperty[A] {
  protected[this] final val listeners: MArrayBuffer[A => Any] = MArrayBuffer.empty
  protected[this] final val oneTimeListeners: MArrayBuffer[Registration] = MArrayBuffer.empty

  protected def wrapListenerRegistration(reg: Registration): Registration = reg
  protected def wrapOneTimeListenerRegistration(reg: Registration): Registration = wrapListenerRegistration(reg)

  /** Parent property. `null` if this property has no parent. */
  protected def parent: ReadableProperty[_]

  override def listen(valueListener: A => Any, initUpdate: Boolean = false): Registration = {
    listeners += valueListener
    listenersUpdate()
    if (initUpdate) valueListener(this.get)
    wrapListenerRegistration(
      new MutableBufferRegistration(listeners, valueListener, Opt(listenersUpdate _))
    )
  }

  override def listenOnce(valueListener: A => Any): Registration = {
    val reg = wrapOneTimeListenerRegistration(
      new MutableBufferRegistration(listeners, valueListener, Opt(listenersUpdate _))
    )
    listeners += valueListener
    oneTimeListeners += reg
    listenersUpdate()
    reg
  }

  override def listenersCount(): Int =
    listeners.length

  override protected[properties] def listenersUpdate(): Unit = {
    if (parent != null) parent.listenersUpdate()
  }

  override def transform[B](transformer: A => B): ReadableProperty[B] =
    new TransformedReadableProperty[A, B](this, transformer)

  override def transformToSeq[B: PropertyCreator](transformer: A => BSeq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
    new ReadableSeqPropertyFromSingleValue(this, transformer)

  override def streamTo[B](target: Property[B], initUpdate: Boolean = true)(transformer: A => B): Registration = {
    @inline def update(v: A): Unit = target.set(transformer(v))
    if (initUpdate) update(get)
    val listenerRegistration = listen(update)
    new Registration {
      override def cancel(): Unit = listenerRegistration.cancel()
      override def isActive: Boolean = listenerRegistration.isActive
      override def restart(): Unit = {
        listenerRegistration.restart()
        update(get)
      }
    }
  }

  protected[properties] override def valueChanged(): Unit = {
    val originalListeners = listeners.toSet
    CallbackSequencer().queue(s"$hashCode:valueChanged", () => {
      val value = get
      listeners.toList.foreach { listener => if (originalListeners.contains(listener) && listeners.contains(listener)) listener(value) }
      oneTimeListeners.foreach(_.cancel())
      oneTimeListeners.clear()
    })
    if (parent != null) parent.valueChanged()
  }

}