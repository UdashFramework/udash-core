package io.udash.bootstrap.utils

import io.udash.bindings.modifiers.Binding
import scalatags.JsDom.all._

trait BootstrapImplicits {
  implicit def withoutNested(modifier: Modifier): Binding.NestedInterceptor => Modifier = _ => modifier
  implicit def stringWithoutNested(modifier: String): Binding.NestedInterceptor => Modifier = _ => modifier
}

object BootstrapImplicits extends BootstrapImplicits