package io.udash.bootstrap.utils

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class UdashJumbotron(mds: Modifier*) {
  lazy val render: dom.Element =
    div(BootstrapStyles.jumbotron)(mds).render
}

object UdashJumbotron {
  def apply(content: Property[_]): UdashJumbotron =
    new UdashJumbotron(bind(content))

  def apply(mds: Modifier*): UdashJumbotron =
    new UdashJumbotron(mds)
}
