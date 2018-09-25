package io.udash.bootstrap

import org.scalajs.dom.Element
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
  def loadFontAwesome(): Element =
    link(rel := "stylesheet", href := "https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css").render

  /** Loads Bootstrap styles. */
  def loadBootstrapStyles(): Element =
    link(rel := "stylesheet", href := "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css").render
}
