package io.udash.web.guide.views

import io.udash._
import io.udash.core.DomWindow
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.guide.Context._
import io.udash.web.guide.styles.partials.{GuideStyles, HeaderStyles}
import io.udash.web.guide.{ErrorState, IntroState}
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.tags2._

object ErrorViewPresenter extends DefaultViewPresenterFactory[ErrorState.type](() => new ErrorView)

class ErrorView extends View {
  import scalatags.JsDom.all._

  private val content = section(GuideStyles.get.sectionError)(
    div(GlobalStyles.body)(
      div(GuideStyles.get.errorInner)(
        p(GuideStyles.get.errorHead)(
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
      val h = window.height - jQ(s".${FooterStyles.get.footer.htmlClass}").outerHeight() - jQ(s".${HeaderStyles.get.header.htmlClass}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}