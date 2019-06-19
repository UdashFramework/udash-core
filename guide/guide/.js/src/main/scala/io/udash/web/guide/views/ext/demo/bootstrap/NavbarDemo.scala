package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.properties.HasModelPropertyCreator
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object NavbarDemo extends AutoDemo {

  trait NavbarPanel {
    def title: String

    def content: String
  }

  object NavbarPanel extends HasModelPropertyCreator[NavbarPanel]

  final case class DefaultNavbarPanel(override val title: String, override val content: String) extends NavbarPanel

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.nav.{UdashNav, UdashNavbar}
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import org.scalajs.dom.Event
    import scalatags.JsDom.all._

    /*
    trait NavbarPanel {
      def title: String
      def content: String
    }

    object NavbarPanel extends HasModelPropertyCreator[NavbarPanel]

    final case class DefaultNavbarPanel(
      override val title: String,
      override val content: String
    ) extends NavbarPanel
    */

    val panels = SeqProperty[NavbarPanel](
      DefaultNavbarPanel("Title 1", "Content of panel 1..."),
      DefaultNavbarPanel("Title 2", "Content of panel 2..."),
      DefaultNavbarPanel("Title 3", "Content of panel 3..."),
      DefaultNavbarPanel("Title 4", "Content of panel 4...")
    )
    panels.append(
      DefaultNavbarPanel("Title 5", "Content of panel 5...")
    )

    div(
      UdashNavbar()(
        _ => UdashNav(panels)(
          elemFactory = (panel, nested) => a(
            Navigation.link,
            href := "",
            onclick :+= ((_: Event) => true)
          )(
            nested(bind(panel.asModel.subProp(_.title)))
          ).render,
          isActive = el => el.transform(_.title.endsWith("1")),
          isDisabled = el => el.transform(_.title.endsWith("5"))
        ),
        span("Udash"),
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

