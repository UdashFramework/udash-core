package io.udash.bootstrap.utils

import scalatags.JsDom.GenericAttr


trait BootstrapImplicits {
  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}

object BootstrapImplicits extends BootstrapImplicits