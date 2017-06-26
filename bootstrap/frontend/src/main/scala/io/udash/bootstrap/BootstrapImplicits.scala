package io.udash.bootstrap

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom.GenericAttr
import scalatags.JsDom.all._


trait BootstrapImplicits {
  implicit val idAttrValue: GenericAttr[ComponentId] = new GenericAttr[ComponentId]
}

object BootstrapImplicits extends BootstrapImplicits