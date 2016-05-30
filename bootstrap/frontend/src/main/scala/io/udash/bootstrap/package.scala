package io.udash

import scalatags.JsDom.all._

package object bootstrap extends BootstrapImplicits {

  @inline def optionalModifier(flag: Boolean, modifier: Modifier): Option[Modifier] = {
    if (flag) Some(modifier) else None
  }
}
