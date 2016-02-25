package io.udash.view

import io.udash.wrappers.jquery.jQ
import org.scalajs.dom
import org.scalajs.dom._

import scalatags.generic.Modifier

/** Reusable DOM elements generator. */
trait Component extends Modifier[dom.Element] {
  def getTemplate: Element

  def apply(): Element = getTemplate

  override def applyTo(t: Element): Unit = jQ(t).append(getTemplate)
}
