package io.udash.web.homepage.views

import com.avsystem.commons.*
import io.udash.*
import io.udash.css.CssView
import io.udash.web.commons.components.Footer
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.homepage.RootState
import io.udash.web.homepage.components.Header
import io.udash.web.homepage.styles.partials.HomepageStyles
import scalatags.JsDom.tags2.*

object RootViewFactory extends StaticViewFactory[RootState.type](() => new RootView)

class RootView extends ContainerView with CssView {
  import scalatags.JsDom.all.*

  private val content = div(
    Header.getTemplate,
    main(GlobalStyles.main)(
      childViewContainer
    ),
    Footer.getTemplate(HomepageStyles.body.opt)
  )

  override def getTemplate: Modifier = content
}