package io.udash.web.guide.views

import io.udash._
import io.udash.core.DomWindow
import io.udash.css.CssView
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.guide.Context._
import io.udash.web.guide.styles.partials.{GuideStyles, HeaderStyles}
import io.udash.web.guide.{ErrorState, IntroState}
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._

object ErrorViewFactory extends StaticViewFactory[ErrorState.type](() => new ErrorView)

class ErrorView extends FinalView with CssView {
  import scalatags.JsDom.all._

  private val content = section(GuideStyles.sectionError)(
    div(GlobalStyles.body)(
      div(GuideStyles.errorInner)(
        p(GuideStyles.errorHead)(
          span(GlobalStyles.red)("Oops! 404"), br(),
          span("The link doesn't work. Or maybe it never has? You can still ", a(GlobalStyles.grey, href := "assets/pdf/origami_crane_printok.pdf", target := "_blank")("make origami")), br(),
          span("... or go "), a(GlobalStyles.red, href := IntroState.url)("home"), span("")
        )
      )
    )
  ).render

  private lazy val jqTemplate = jQ(content)

  val window = jQ(DomWindow)
  window.resize((element: Element, _: JQueryEvent) => onResize())
  onResize()

  private def onResize(): Unit = {
    if (window.width <= StyleConstants.MediaQueriesBounds.TabletLandscapeMax) {
      val h = window.height - jQ(s".${FooterStyles.footer.className}").outerHeight() - jQ(s".${HeaderStyles.header.className}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Modifier = content
}