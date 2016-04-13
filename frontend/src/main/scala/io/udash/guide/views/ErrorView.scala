package io.udash.guide.views

import io.udash._
import io.udash.wrappers.jquery._
import io.udash.core.DomWindow
import io.udash.guide.{ErrorState, IntroState}
import io.udash.guide.styles.GlobalStyles
import io.udash.guide.styles.partials.{FooterStyles, GuideStyles, HeaderStyles}
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._
import scalacss.ScalatagsCss._
import io.udash.guide.Context._
import io.udash.guide.styles.constant.StyleConstants
import io.udash.wrappers.jquery._

object ErrorViewPresenter extends DefaultViewPresenterFactory[ErrorState.type](() => new ErrorView)

class ErrorView extends View {
  import scalatags.JsDom.all._

  private val content = section(GuideStyles.sectionError)(
    div(GlobalStyles.body)(
      div(GuideStyles.errorInner)(
        p(GuideStyles.errorHead)(
          span(GlobalStyles.red)("Oops! 404"), br(),
          span("The link doesn’t work. Or maybe it never has? You can still ", a(GlobalStyles.grey, href := "assets/pdf/origami_crane_printok.pdf", target := "_blank")("make origami")), br(),
          span("... or go "), a(GlobalStyles.red, href := IntroState.url)("home"), span(".")
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
      val h = window.height - jQ(s".${FooterStyles.footer.htmlClass}").outerHeight() - jQ(s".${HeaderStyles.header.htmlClass}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}