package io.udash.web.homepage.views

import com.avsystem.commons._
import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.Footer
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.homepage.RootState
import io.udash.web.homepage.components.Header
import io.udash.web.homepage.styles.partials.HomepageStyles
import scalatags.JsDom.tags2._

import scala.scalajs.js

object RootViewFactory extends StaticViewFactory[RootState.type](() => new RootView)

class RootView extends ContainerView with CssView {
  import scalatags.JsDom.all._

  private val content = div(
    Header.getTemplate,
    main(GlobalStyles.main)(
      childViewContainer
    ),
    Footer.getTemplate(HomepageStyles.body.opt)
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: Option[View]): Unit = {
    super.renderChild(view)
    js.Dynamic.global.svg4everybody().discard
  }
}