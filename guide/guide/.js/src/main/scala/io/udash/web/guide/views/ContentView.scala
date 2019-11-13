package io.udash.web.guide.views

import io.udash._
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.ContentState
import io.udash.web.guide.components.GuideMenu
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom.tags2._

object ContentViewFactory extends StaticViewFactory[ContentState.type](() => new ContentView)

class ContentView extends ViewContainer {

  import scalatags.JsDom.all._

  protected val child = main(GuideStyles.contentWrapper).render

  private val content = main(GuideStyles.main)(
    div(GlobalStyles.body)(
      div(GuideStyles.menuWrapper)(
        GuideMenu().getTemplate
      ),
      child
    )
  )

  override def getTemplate: Modifier = content
}