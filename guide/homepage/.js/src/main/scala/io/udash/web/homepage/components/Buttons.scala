package io.udash.web.homepage.components

import io.udash.web.homepage.styles.partials.ButtonsStyle

import scalatags.JsDom.all._

/**
  * Created by malchik on 2016-04-04.
  */
object Buttons {
  import io.udash.css.CssView._
  def whiteBorderButton(link: String, label: String, xs: Modifier*): Modifier =
    a(href := link, target := "_blank", ButtonsStyle.btnDefault, xs: Modifier)(
      div(ButtonsStyle.btnDefaultInner)(label)
    )

  def blackBorderButton(link: String, label: String, xs: Modifier*): Modifier =
    a(href := link, target := "_blank", ButtonsStyle.btnDefault, ButtonsStyle.btnDefaultBlack, xs: Modifier)(
      div(ButtonsStyle.btnDefaultInner, ButtonsStyle.btnDefaultInnerBlack)(label)
    )
}
