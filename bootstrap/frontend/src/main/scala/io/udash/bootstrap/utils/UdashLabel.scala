package io.udash.bootstrap
package utils

import io.udash._
import org.scalajs.dom
import scalatags.JsDom.all._

class UdashLabel(style: LabelStyle)(mds: Modifier*) extends UdashBootstrapComponent {
  override lazy val render: dom.Element =
    span(style)(mds:_*).render
}

object UdashLabel {
  def apply(content: Property[_]): UdashLabel =
    new UdashLabel(LabelStyle.Default)(bind(content))

  def apply(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Default)(mds)

  def primary(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Primary)(mds)

  def success(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Success)(mds)

  def info(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Info)(mds)

  def warning(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Warning)(mds)

  def danger(mds: Modifier*): UdashLabel =
    new UdashLabel(LabelStyle.Danger)(mds)
}