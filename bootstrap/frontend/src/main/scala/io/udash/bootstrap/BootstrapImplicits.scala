package io.udash.bootstrap

import io.udash.bootstrap.UdashBootstrap.ComponentId
import scalatags.JsDom.GenericAttr


trait BootstrapImplicits {
  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}

object BootstrapImplicits extends BootstrapImplicits