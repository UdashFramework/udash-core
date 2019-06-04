package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.nav.UdashNav
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom.Event
import scalatags.JsDom

object NavsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  trait NavPanel {
    def title: String

    def content: String
  }

  object NavPanel extends HasModelPropertyCreator[NavPanel]

  final case class DefaultNavPanel(override val title: String, override val content: String) extends NavPanel

  private val (rendered, source) = {
    /*
    trait NavPanel {
      def title: String
      def content: String
    }
    object NavPanel extends HasModelPropertyCreator[NavPanel]
    final case class DefaultNavPanel(
      override val title: String,
      override val content: String
    ) extends NavPanel
    */

    val panels = SeqProperty[NavPanel](
      DefaultNavPanel("Title 1", "Content of panel 1..."),
      DefaultNavPanel("Title 2", "Content of panel 2..."),
      DefaultNavPanel("Title 3", "Content of panel 3..."),
      DefaultNavPanel("Title 4", "Content of panel 4...")
    )
    val selected = Property[NavPanel](panels.elemProperties.head.get)
    panels.append(DefaultNavPanel("Title 5", "Content of panel 5..."))

    div(
      UdashNav(panels, justified = true.toProperty, tabs = true.toProperty)(
        elemFactory = (panel, nested) => a(
          BootstrapStyles.Navigation.link,
          href := "",
          onclick :+= ((_: Event) => selected.set(panel.get), true)
        )(nested(bind(panel.asModel.subProp(_.title)))).render,
        isActive = panel => panel.combine(selected)((panel, selected) =>
          panel.title == selected.title
        )
      ),
      div(wellStyles)(
        bind(selected.asModel.subProp(_.content))
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

