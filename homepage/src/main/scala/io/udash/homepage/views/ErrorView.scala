package io.udash.homepage.views

import io.udash._
import io.udash.homepage.IndexState
import io.udash.homepage.styles.GlobalStyles
import io.udash.homepage.styles.partials.{FooterStyles, HeaderStyles, HomepageStyles}
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._
import scalacss.ScalatagsCss._
import io.udash.homepage.Context._
import io.udash.homepage.styles.constant.StyleConstants
import io.udash.wrappers.jquery._

object ErrorViewPresenter extends DefaultViewPresenterFactory[IndexState](() => new ErrorView)

class ErrorView extends View {
  import scalatags.JsDom.all._

  private val content = section(HomepageStyles.sectionError)(
    div(GlobalStyles.body)(
      div(HomepageStyles.errorInner)(
        p(HomepageStyles.errorHead)(
          span(GlobalStyles.red)("Oops! 404"), br(),
          span("The link doesn’t work. Or maybe it never has? You can still ", a(GlobalStyles.grey, href := "assets/pdf/origami_crane_printok.pdf", target := "_blank")("make origami")), br(),
          span("... or go "), a(GlobalStyles.red, href := IndexState(None).url)("home"), span(".")
        )
      )
    )
  ).render

  private lazy val jqTemplate = jQ(content)

  Window.onResize(onResize)
  onResize()

  private def onResize(): Unit = {
    if (Window.width <= StyleConstants.MediaQueriesBounds.TabletLandscapeMax) {
      val h = Window.height - jQ(s".${FooterStyles.footer.htmlClass}").outerHeight()
      jqTemplate.css("min-height", s"${h}px")
    }
  }

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}