package io.udash.bootstrap
package progressbar

import io.udash.bootstrap.BootstrapStyles.ProgressBar._

import scalacss.StyleA

sealed abstract class ProgressBarStyle(style: Option[StyleA]) extends ClassModifier(Seq(Some(progressBar), style).flatten: _*) {
  def this(style: StyleA) = this(Some(style))
}

object ProgressBarStyle {

  case object Default extends ProgressBarStyle(None)

  case object Striped extends ProgressBarStyle(progressBarStriped)

  case object Success extends ProgressBarStyle(progressBarSuccess)

  case object Info extends ProgressBarStyle(progressBarInfo)

  case object Warning extends ProgressBarStyle(progressBarWarning)

  case object Danger extends ProgressBarStyle(progressBarDanger)

}
