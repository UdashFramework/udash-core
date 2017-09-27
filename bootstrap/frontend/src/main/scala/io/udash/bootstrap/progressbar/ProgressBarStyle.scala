package io.udash.bootstrap
package progressbar

import io.udash.css.CssStyle

final class ProgressBarStyle(style: Option[CssStyle])
  extends ClassModifier(Seq(Some(BootstrapStyles.ProgressBar.progressBar), style).flatten: _*) {

  def this(style: CssStyle) = this(Some(style))
}

object ProgressBarStyle {
  import BootstrapStyles.ProgressBar._

  val Default = new ProgressBarStyle(None)
  val Striped = new ProgressBarStyle(progressBarStriped)
  val Success = new ProgressBarStyle(progressBarSuccess)
  val Info = new ProgressBarStyle(progressBarInfo)
  val Warning = new ProgressBarStyle(progressBarWarning)
  val Danger = new ProgressBarStyle(progressBarDanger)
}
