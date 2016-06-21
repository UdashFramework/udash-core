package io.udash.bootstrap.utils

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class UdashBadge(mds: Modifier*) {
  lazy val render: dom.Element =
    span(BootstrapStyles.Label.badge)(mds).render
}

object UdashBadge {
  def apply(content: Property[_]): UdashBadge =
    new UdashBadge(bind(content))

  def apply(mds: Modifier*): UdashBadge =
    new UdashBadge(mds)
}
