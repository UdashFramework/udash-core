package io.udash.web.homepage.views

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.components.FooterStyles
import io.udash.web.commons.styles.utils.StyleConstants
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.HomepageStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._

object ErrorViewFactory extends StaticViewFactory[IndexState](() => new ErrorView)

class ErrorView extends FinalView with CssView {
  import scalatags.JsDom.all._

  private val content = section(HomepageStyles.sectionError)(
    div(GlobalStyles.body)(
      div(HomepageStyles.errorInner)(
        p(HomepageStyles.errorHead)(
          span(GlobalStyles.red)("Oops! 404"), br(),
          span(
            "The link doesn't work. Or maybe it never did? You can still ",
            a(GlobalStyles.grey, href := "/assets/pdf/origami_crane_printok.pdf", target := "_blank")("make origami")
          ), br(),
          span("... or go "), a(GlobalStyles.red, href := IndexState(None).url)("home"), span(".")
        )
      )
    )
  ).render

  private lazy val jqTemplate = jQ(content)

  val window = jQ(org.scalajs.dom.window)
  window.on(EventName.resize, (element: Element, _: JQueryEvent) => onResize())
  onResize()

  private def onResize(): Unit = {
    if (window.width <= StyleConstants.MediaQueriesBounds.TabletLandscapeMax) {
      val h = window.height - jQ(s".${FooterStyles.footer.className}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Modifier = content
}