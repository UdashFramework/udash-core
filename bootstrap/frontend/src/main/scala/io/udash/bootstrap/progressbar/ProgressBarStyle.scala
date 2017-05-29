package io.udash.bootstrap
package progressbar

import io.udash.css.CssStyle

sealed abstract class ProgressBarStyle(style: Option[CssStyle])
  extends ClassModifier(Seq(Some(BootstrapStyles.ProgressBar.progressBar), style).flatten: _*) {

  def this(style: CssStyle) = this(Some(style))
}

object ProgressBarStyle {
  import BootstrapStyles.ProgressBar._

  case object Default extends ProgressBarStyle(None)
  case object Striped extends ProgressBarStyle(progressBarStriped)
  case object Success extends ProgressBarStyle(progressBarSuccess)
  case object Info extends ProgressBarStyle(progressBarInfo)
  case object Warning extends ProgressBarStyle(progressBarWarning)
  case object Danger extends ProgressBarStyle(progressBarDanger)
}
