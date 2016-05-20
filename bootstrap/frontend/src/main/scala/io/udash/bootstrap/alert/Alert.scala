package io.udash.bootstrap.alert

import com.karasiq.bootstrap.alert.AlertStyle
import org.scalajs.dom

import scalatags.JsDom.all._

object Alert {
  def apply(style: AlertStyle, md: Modifier*): ConcreteHtmlTag[dom.html.Div] = com.karasiq.bootstrap.alert.Alert(style, md: _*)
}
