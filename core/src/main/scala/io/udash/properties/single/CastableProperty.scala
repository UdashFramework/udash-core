package io.udash.properties
package single

import com.avsystem.commons.BSeq
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
  def asSeq[B](implicit sev: A <:< BSeq[B], ev: SeqPropertyCreator[B, BSeq]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    this.asInstanceOf[ReadableSeqProperty[B, CastableReadableProperty[B]]]
}

/** Property which can be casted. <br/>
 * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works", as long as the property creators
 * were not explicitly passed and relevant implicit scope is equal for property creation and cast.
 */
trait CastableProperty[A] extends CastableReadableProperty[A] with Property[A] {
  /** Safely casts `DirectProperty[A]` to `ModelProperty[A]` */
  override def asModel(implicit ev: ModelPropertyCreator[A]): ModelProperty[A] = {
    this match {
      case mp: ModelProperty[A] => mp
      case _ => throw new IllegalStateException("Property was created without provided ModelPropertyCreator in scope. " +
        "Make sure it is uniformly available, e.g. in the companion object of the model class.")
    }
  }

  /** Safely casts `DirectProperty[Seq[A]]` to `DirectSeqProperty[A]` */
  override def asSeq[B](implicit sev: A <:< BSeq[B], ev: SeqPropertyCreator[B, BSeq]): SeqProperty[B, CastableProperty[B]] = {
    (this: Any) match {
      case sp: SeqProperty[_, _] => sp.asInstanceOf[SeqProperty[B, CastableProperty[B]]]
      case _ => throw new IllegalStateException("Property was created without provided SeqPropertyCreator in scope. " +
        "Make sure not to explicitly pass creators on property creation.")
    }
  }
}
