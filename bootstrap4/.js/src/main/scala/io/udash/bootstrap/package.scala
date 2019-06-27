package io.udash

import io.udash.bootstrap.utils.BootstrapImplicits
import io.udash.component.Components

package object bootstrap extends BootstrapImplicits with Components {
  final val BootstrapStyles = io.udash.bootstrap.utils.BootstrapStyles
  final val BootstrapTags = io.udash.bootstrap.utils.BootstrapTags
}
