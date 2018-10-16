package io.udash.component

import scalatags.JsDom.GenericAttr

trait Components {
  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}

object Components extends Components
