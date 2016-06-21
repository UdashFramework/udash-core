package io.udash.bootstrap
package utils

import scalacss.StyleA

sealed abstract class LabelStyle(style: StyleA) extends ClassModifier(BootstrapStyles.Label.label, style)

object LabelStyle {
  import BootstrapStyles.Label._

  case object Default extends LabelStyle(labelDefault)
  case object Primary extends LabelStyle(labelPrimary)
  case object Success extends LabelStyle(labelSuccess)
  case object Info extends LabelStyle(labelInfo)
  case object Warning extends LabelStyle(labelWarning)
  case object Danger extends LabelStyle(labelDanger)
}
