package io.udash.web.homepage.views

import com.avsystem.commons.universalOps
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

class ErrorView extends View with CssView {
  import scalatags.JsDom.all._

  private val content = section(HomepageStyles.sectionError)(
    div(GlobalStyles.body, HomepageStyles.body)(
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

  private def jqTemplate = jQ(content)

  private def jqWindow = jQ(org.scalajs.dom.window)

  locally {
    jqWindow.on(EventName.resize, (_: Element, _: JQueryEvent) => onResize())
    onResize()
  }

  private def onResize(): Unit = {
    if (jqWindow.width() <= StyleConstants.MediaQueriesBounds.TabletLandscapeMax) {
      val h = jqWindow.height() - jQ(s".${FooterStyles.footer.className}").outerHeight().getOrElse(0d)
      jqTemplate.css("min-height", s"${h}px").discard
    }
  }

  override def getTemplate: Modifier = content
}