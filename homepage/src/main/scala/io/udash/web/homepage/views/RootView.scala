package io.udash.web.homepage.views

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.Footer
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.homepage.RootState
import io.udash.web.homepage.components.Header
import org.scalajs.dom.Element

import scala.scalajs.js
import scalatags.JsDom.tags2._

object RootViewPresenter extends DefaultViewPresenterFactory[RootState.type](() => new RootView)

class RootView extends View with CssView {
  import scalatags.JsDom.all._

  private var child: Element =
    div().render

  private val content = div(
    Header.getTemplate,
    main(GlobalStyles.main)(
      child
    ),
    Footer.getTemplate
  )

  override def getTemplate: Modifier = content

  override def renderChild(view: View): Unit = {
    val newChild = view.getTemplate
    newChild.applyTo(child)

    js.Dynamic.global.svg4everybody()
  }
}