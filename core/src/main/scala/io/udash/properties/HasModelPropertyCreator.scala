package io.udash.properties

import com.avsystem.commons.meta.MacroInstances

abstract class HasModelPropertyCreator[T](implicit instances: MacroInstances[Unit, () => ModelPropertyCreator[T]]) {
  implicit final lazy val modelPropertyCreator: ModelPropertyCreator[T] = instances((), this).apply()
}