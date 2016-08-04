package io.udash.web.homepage.views

import io.udash._
import io.udash.core.DomWindow
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.HomepageStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.tags2._

object ErrorViewPresenter extends DefaultViewPresenterFactory[IndexState](() => new ErrorView)

class ErrorView extends FinalView {
  import scalatags.JsDom.all._

  private val content = section(HomepageStyles.get.sectionError)(
    div(GlobalStyles.get.body)(
      div(HomepageStyles.get.errorInner)(
        p(HomepageStyles.get.errorHead)(
          span(GlobalStyles.get.red)("Oops! 404"), br(),
          span("The link doesn't work. Or maybe it never has? You can still ", a(GlobalStyles.get.grey, href := "assets/pdf/origami_crane_printok.pdf", target := "_blank")("make origami")), br(),
          span("... or go "), a(GlobalStyles.get.red, href := IndexState(None).url)("home"), span(".")
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
      val h = window.height - jQ(s".${FooterStyles.get.footer.htmlClass}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Modifier = content
}