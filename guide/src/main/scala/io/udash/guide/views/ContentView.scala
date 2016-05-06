package io.udash.guide.views

import io.udash._
import io.udash.guide.ContentState
import io.udash.guide.components.GuideMenu
import io.udash.guide.styles.GlobalStyles
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom.Element

import scalatags.JsDom.tags2._

object ContentViewPresenter extends DefaultViewPresenterFactory[ContentState.type](() => new ContentView)

class ContentView extends ViewContainer {
  import scalacss.ScalatagsCss._
  import scalatags.JsDom.all._

  protected val child = div(GuideStyles.contentWrapper).render

  private val content = main(GuideStyles.main)(
    div(GlobalStyles.body)(
      div(GuideStyles.menuWrapper)(
        GuideMenu().getTemplate
      ),
      child
    )
  ).render

  override def getTemplate: Element = content
}