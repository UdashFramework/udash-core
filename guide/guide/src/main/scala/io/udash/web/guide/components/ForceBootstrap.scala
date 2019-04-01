package io.udash.web.guide.components

object ForceBootstrap {
  import scalatags.JsDom.all._

  def apply(modifiers: Modifier*): Modifier =
    div(cls := "bootstrap")( //force Bootstrap styles
      modifiers:_*
    )
}
