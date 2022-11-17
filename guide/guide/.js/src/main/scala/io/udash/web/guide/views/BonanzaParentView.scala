package io.udash.web.guide.views

import io.udash.core.StaticViewFactory
import io.udash.css.CssView
import io.udash.properties.Properties.Property
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{BonanzaParentState, RoutingState}
import io.udash.{Application, bind}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

class BonanzaParentViewFactory(implicit application: Application[RoutingState]) extends StaticViewFactory[BonanzaParentState.type](viewCreator = () => new BonanzaParentView)

class BonanzaParentView(implicit application: Application[RoutingState]) extends ViewContainer with CssView {
  override protected val child: Element = div().render

  private val numberOfReloads = Property(0)
  locally {
    application.getUrlChangeProvider.onFragmentChange { _ =>
      numberOfReloads.set(numberOfReloads.get + 1)
    }
  }

  override def getTemplate: Modifier = div(GlobalStyles.body)(
    div(GuideStyles.main)(
      span("Number of reloads: "),
      bind(numberOfReloads),
      br(),
      child,
    )
  )
}
