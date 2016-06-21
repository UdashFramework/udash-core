package io.udash.bootstrap.utils

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class UdashPageHeader(mds: Modifier*) {
  lazy val render: dom.Element =
    span(BootstrapStyles.Typography.pageHeader)(mds).render
}

object UdashPageHeader {
  def apply(content: Property[_]): UdashPageHeader =
    new UdashPageHeader(bind(content))

  def apply(mds: Modifier*): UdashPageHeader =
    new UdashPageHeader(mds)
}
