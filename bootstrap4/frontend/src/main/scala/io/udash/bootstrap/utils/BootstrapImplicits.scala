package io.udash.bootstrap.utils

import io.udash.bindings.modifiers.Binding
import io.udash.component.Components
import scalatags.JsDom.all._


trait BootstrapImplicits extends Components {
  implicit def withoutNested(modifier: Modifier): Binding.NestedInterceptor => Modifier = _ => modifier
  implicit def stringWithoutNested(modifier: String): Binding.NestedInterceptor => Modifier = _ => modifier
}

object BootstrapImplicits extends BootstrapImplicits