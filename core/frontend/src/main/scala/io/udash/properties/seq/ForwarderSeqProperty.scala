package io.udash.properties.seq

import io.udash.properties.single.{ForwarderProperty, ForwarderReadableProperty, Property, ReadableProperty}

trait ForwarderReadableSeqProperty[A, +ElemType <: ReadableProperty[A]]
  extends ForwarderReadableProperty[Seq[A]] with ReadableSeqProperty[A, ElemType] {

  protected def origin: ReadableSeqProperty[_, _]

}

trait ForwarderSeqProperty[A, +ElemType <: Property[A]]
  extends ForwarderReadableSeqProperty[A, ElemType] with ForwarderProperty[Seq[A]] with SeqProperty[A, ElemType] {

  protected def origin: SeqProperty[_, _]

}
