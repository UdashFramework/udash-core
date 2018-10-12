package io.udash.properties.model

import io.udash.properties.seq.ReadableSeqProperty
import io.udash.properties.single.{ReadableProperty, ReadableWrapper => SingleReadableWrapper}

private[properties] class ReadableWrapper[T](private val p: ReadableModelProperty[T] with ModelPropertyMacroApi[T])
  extends SingleReadableWrapper[T](p) with ModelPropertyMacroApi[T] {

  override def getSubProperty[R](getter: T => R, key: String): ReadableProperty[R] =
    p.getSubProperty(getter, key)

  override def getSubModel[R](getter: T => R, key: String): ReadableModelProperty[R] =
    p.getSubModel(getter, key)

  override def getSubSeq[R](getter: T => Seq[R], key: String): ReadableSeqProperty[R, ReadableProperty[R]] =
    p.getSubSeq(getter, key)

  override def readable: ReadableModelProperty[T] = this
}
