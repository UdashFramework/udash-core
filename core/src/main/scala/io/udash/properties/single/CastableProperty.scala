package io.udash.properties
package single

import io.udash.properties.model.{ModelProperty, ReadableModelProperty}
import io.udash.properties.seq.{ReadableSeqProperty, SeqProperty}

/** Property which can be casted. <br/>
 * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works".
 */
trait CastableReadableProperty[A] extends ReadableProperty[A] {
  /** Safely casts DirectProperty[A] to ModelProperty[A] */
  def asModel(implicit ev: ModelPropertyCreator[A]): ReadableModelProperty[A] =
    this.asInstanceOf[ReadableModelProperty[A]]

  /** Safely casts DirectProperty[Seq[A]] to DirectSeqProperty[A] */
  def asSeq[B](implicit sev: A <:< Seq[B], ev: SeqPropertyCreator[B]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    this.asInstanceOf[ReadableSeqProperty[B, CastableReadableProperty[B]]]
}

/** Property which can be casted. <br/>
 * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works", as long as the property creators
 * were not explicitly passed and relevant implicit scope is equal for property creation and cast.
 */
trait CastableProperty[A] extends CastableReadableProperty[A] with Property[A] {
  /** Safely casts `DirectProperty[A]` to `ModelProperty[A]` */
  override def asModel(implicit ev: ModelPropertyCreator[A]): ModelProperty[A] = {
    if (!this.isInstanceOf[ModelProperty[A]])
      throw new IllegalStateException("Property was created without provided ModelPropertyCreator in scope. " +
        "Make sure it is uniformly available, e.g. in the companion object of the model class.")
    this.asInstanceOf[ModelProperty[A]]
  }

  /** Safely casts `DirectProperty[Seq[A]]` to `DirectSeqProperty[A]` */
  override def asSeq[B](implicit sev: A <:< Seq[B], ev: SeqPropertyCreator[B]): SeqProperty[B, CastableProperty[B]] = {
    if (!this.isInstanceOf[SeqProperty[_, _]])
      throw new IllegalStateException("Property was created without provided SeqPropertyCreator in scope. " +
        "Make sure not to explicitly pass creators on property creation.")
    this.asInstanceOf[SeqProperty[B, CastableProperty[B]]]
  }
}
