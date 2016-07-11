package io.udash.web.guide.views

import io.udash._
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.ContentState
import io.udash.web.guide.components.GuideMenu
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._

object ContentViewPresenter extends DefaultViewPresenterFactory[ContentState.type](() => new ContentView)

class ContentView extends ViewContainer {
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._

  protected val child = div(GuideStyles.get.contentWrapper).render

  private val content = main(GuideStyles.get.main)(
    div(GlobalStyles.get.body)(
      div(GuideStyles.get.menuWrapper)(
        GuideMenu().getTemplate
      ),
      child
    )
  ).render

  override def getTemplate: Element = content
}