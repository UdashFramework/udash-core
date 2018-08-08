package io.udash.bootstrap

import org.scalajs.dom

import scalatags.JsDom.all._

object UdashBootstrap {
  private var cid = -1

  case class ComponentId(id: String) extends AnyVal {
    override def toString: String = id
  }

  /** Generates unique element ID */
  def newId(): ComponentId = {
    cid += 1
    ComponentId(s"bs-auto-$cid")
  }

  /** Loads FontAwesome styles. */
  def loadFontAwesome(): dom.Element =
    link(rel := "stylesheet", href := "https://use.fontawesome.com/releases/v5.2.0/css/all.css").render

  /** Loads Bootstrap styles. */
  def loadBootstrapStyles(): dom.Element =
    link(rel := "stylesheet", href := "https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css").render
}
