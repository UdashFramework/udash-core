package io.udash.web.commons.views

import io.udash.css.CssView
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic.Modifier

trait Component extends Modifier[dom.Element] with CssView {
  def getTemplate: Modifier[dom.Element]

  def apply(): Modifier[dom.Element] = getTemplate

  override def applyTo(t: Element): Unit =
    getTemplate.applyTo(t)
}