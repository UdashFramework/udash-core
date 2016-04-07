package io.udash.homepage.components

import io.udash.homepage.styles.partials.ButtonsStyle

import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

/**
  * Created by malchik on 2016-04-04.
  */
object Buttons {
  def whiteBorderButton(link: String, label: String, xs: Modifier*) =
    a(href := link, target := "_blank", ButtonsStyle.btnDefault, xs: Modifier)(
      div(ButtonsStyle.btnDefaultInner)(label)
    )

  def blackBorderButton(link: String, label: String, xs: Modifier*) =
    a(href := link, target := "_blank", ButtonsStyle.btnDefault, ButtonsStyle.btnDefaultBlack, xs: Modifier)(
      div(ButtonsStyle.btnDefaultInner, ButtonsStyle.btnDefaultInnerBlack)(label)
    )
}
