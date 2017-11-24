package io.udash.bootstrap
package progressbar

import io.udash.css.CssStyle

final class ProgressBarStyle(style: Option[CssStyle])
  extends ClassModifier(Seq(Some(BootstrapStyles.ProgressBar.progressBar), style).flatten: _*) {

  def this(style: CssStyle) = this(Some(style))
}

object ProgressBarStyle {
  import BootstrapStyles.ProgressBar._

  final val Default = new ProgressBarStyle(None)
  final val Striped = new ProgressBarStyle(progressBarStriped)
  final val Success = new ProgressBarStyle(progressBarSuccess)
  final val Info = new ProgressBarStyle(progressBarInfo)
  final val Warning = new ProgressBarStyle(progressBarWarning)
  final val Danger = new ProgressBarStyle(progressBarDanger)
}
