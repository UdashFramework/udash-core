package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.properties.HasModelPropertyCreator
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object NavsDemo extends AutoDemo {

  trait NavPanel {
    def title: String

    def content: String
  }

  object NavPanel extends HasModelPropertyCreator[NavPanel]

  final case class DefaultNavPanel(override val title: String, override val content: String) extends NavPanel

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.nav.UdashNav
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.Event
    import scalatags.JsDom.all._

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
    val selected = Property(panels.elemProperties.head.get)
    panels.append(DefaultNavPanel("Title 5", "Content of panel 5..."))

    div(
      UdashNav(panels, justified = true.toProperty, tabs = true.toProperty)(
        elemFactory = (panel, nested) => a(
          Navigation.link,
          href := "",
          onclick :+= ((_: Event) => selected.set(panel.get).thenReturn(true))
        )(nested(bind(panel.asModel.subProp(_.title)))).render,
        isActive = panel => panel.combine(selected)((panel, selected) =>
          panel.title == selected.title
        )
      ),
      div(Card.card, Card.body, Background.color(Color.Light))(
        bind(selected.asModel.subProp(_.content))
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

