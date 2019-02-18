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
  * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works".
  */
trait CastableProperty[A] extends CastableReadableProperty[A] with Property[A] {
  /** Safely casts DirectProperty[A] to ModelProperty[A] */
  override def asModel(implicit ev: ModelPropertyCreator[A]): ModelProperty[A] =
    this.asInstanceOf[ModelProperty[A]]

  /** Safely casts DirectProperty[Seq[A]] to DirectSeqProperty[A] */
  override def asSeq[B](implicit sev: A <:< Seq[B], ev: SeqPropertyCreator[B]): SeqProperty[B, CastableProperty[B]] =
    this.asInstanceOf[SeqProperty[B, CastableProperty[B]]]
}
