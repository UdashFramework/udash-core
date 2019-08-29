package io.udash.web.homepage.components.demo

import io.udash._
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.views.{Component, Image}
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.DemoStyles
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import scalatags.JsDom.all._
import scalatags.generic.Attr

/**
  * Created by malchik on 2016-04-04.
  */

class DemoComponent(url: Property[IndexState]) extends Component {

  private val fiddleContainer = div(DemoStyles.demoFiddle).render
  private val jqFiddleContainer = jQ(fiddleContainer)

  private val template = div(DemoStyles.demoComponent)(
    Image("laptop.png", "", DemoStyles.laptopImage),
    div(DemoStyles.demoBody)(
      ul(DemoStyles.demoTabs)(
        DemoComponent.demoEntries.map(entry =>
          li(DemoStyles.demoTabsItem)(
            a(DemoStyles.demoTabsLink, href := entry.targetState.url)(
              entry.name
            )
          )
        )
      ),
      fiddleContainer
    )
  ).render

  url.listen(onUrlChange, initUpdate = true)

  private def onUrlChange(update: IndexState) = {
    val entryOption = DemoComponent.demoEntries.find(_.targetState == update)
    val entry = entryOption.getOrElse(DemoComponent.demoEntries.head)
    val urlString = s""""${entry.targetState.url}""""
    val tab = jQ(template).find(s".${DemoStyles.demoTabsLink.className}[href=$urlString]")

    jQ(template).not(tab).find(s".${DemoStyles.demoTabsLink.className}").attr(Attributes.data(Attributes.Active), "false")
    tab.attr(Attributes.data(Attributes.Active), "true")

    jqFiddleContainer
      .animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing,
        (_: Element) => {
          jqFiddleContainer
            .html(entry.fiddle)
            .animate(Map[String, Any]("opacity" -> 1), 200)
        })
  }

  override def getTemplate: Modifier = template
}

object DemoComponent {
  def fiddle(fiddleId: String): Element =
    iframe(
      Attr("frameborder") := "0",
      style := "width: 100%; height: 100%; overflow: hidden;",
      src := s"https://embed.scalafiddle.io/embed?sfid=$fiddleId&theme=dark"
    ).render

  def demoEntries: Seq[DemoEntry] = Seq(
    DemoEntry("Hello World", IndexState(Option("hello")), fiddle("13Wn0gZ/1")),
    DemoEntry("Properties", IndexState(Option("properties")), fiddle("OZe6XBJ/3")),
    DemoEntry("Validation", IndexState(Option("validation")), fiddle("Yiz0JO2/1")),
    DemoEntry("i18n", IndexState(Option("i18n")), fiddle("ll4AVYz/1")),
  )
}


