package io.udash.bootstrap
package label

import io.udash.css.CssStyle

final class LabelStyle(style: CssStyle) extends ClassModifier(BootstrapStyles.Label.label, style)

object LabelStyle {
  import BootstrapStyles.Label._

  val Default = new LabelStyle(labelDefault)
  val Primary = new LabelStyle(labelPrimary)
  val Success = new LabelStyle(labelSuccess)
  val Info = new LabelStyle(labelInfo)
  val Warning = new LabelStyle(labelWarning)
  val Danger = new LabelStyle(labelDanger)
}
