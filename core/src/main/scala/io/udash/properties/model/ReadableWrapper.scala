package io.udash.properties.model

import io.udash.properties.single.{ReadableProperty, ReadableWrapper => SingleReadableWrapper}
import io.udash.properties.{ModelPropertyCreator, PropertyCreator}

private[properties] class ReadableWrapper[T](private val p: ReadableModelProperty[T] with ModelPropertyMacroApi[T])
  extends SingleReadableWrapper[T](p) with ModelPropertyMacroApi[T] {

  override def getSubProperty[R: PropertyCreator](getter: T => R, key: String): ReadableProperty[R] =
    p.getSubProperty(getter, key)

  override def getSubModel[R: ModelPropertyCreator](getter: T => R, key: String): ReadableModelProperty[R] =
    p.getSubModel(getter, key)

  override def readable: ReadableModelProperty[T] = this
}
