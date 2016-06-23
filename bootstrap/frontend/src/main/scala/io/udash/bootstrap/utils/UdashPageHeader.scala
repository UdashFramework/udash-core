package io.udash.bootstrap
package utils

import io.udash._
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

class UdashPageHeader(mds: Modifier*) extends UdashBootstrapComponent {
  override lazy val render: dom.Element =
    span(BootstrapStyles.Typography.pageHeader)(mds).render
}

object UdashPageHeader {
  def apply(content: Property[_]): UdashPageHeader =
    new UdashPageHeader(bind(content))

  def apply(mds: Modifier*): UdashPageHeader =
    new UdashPageHeader(mds)
}
