package io.udash.web.homepage.components.demo

import com.avsystem.commons.SharedExtensions._
import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.components.CodeBlock.Prism
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.views.{Component, Image}
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.{DemoStyles, HomepageStyles}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

class DemoComponent(url: Property[IndexState]) extends Component {

  private val template = div(DemoStyles.demoComponent)(
    Image("laptop.png", "", DemoStyles.laptopImage),
    div(DemoStyles.demoBody)(
      ul(DemoStyles.demoTabs)(
        DemoComponent.demoEntries.iterator.map { case (state, entry) =>
          li(DemoStyles.demoTabsItem)(
            a(
              DemoStyles.demoTabsLink,
              href := state.url,
              (attr(Attributes.data(Attributes.Active)) := "true").attrIf(url.transform(_ == state))
            )(entry.name)
          )
        }.toSeq
      ),
      div(DemoStyles.demoFiddle)(
        produce(url) { state =>
          DemoComponent.demoEntries(state).fiddle.setup(Prism.highlightAllUnder)
        }
      )
    )
  ).render

  override def getTemplate: Modifier = template
}

object DemoComponent {

  def code(): Element = {
    div(
      style := "display: grid; grid-template-columns: 60% 40%",
      height := 100.pct,
      div(textAlign.left, backgroundColor := "#f5f2f0")(
        CodeBlock.lines(HelloDemo.source.linesIterator.drop(1).map(_.drop(2)).toSeq.dropRight(1).iterator)(HomepageStyles)
      ),
      div(backgroundColor := "white")(HelloDemo.rendered),
    ).render
  }

  def demoEntries: Map[IndexState, DemoEntry] = Map(
    IndexState(Option("hello")) -> DemoEntry("Hello World", code()),
    IndexState(Option("properties")) -> DemoEntry("Properties", div().render),
    IndexState(Option("validation")) -> DemoEntry("Validation", div().render),
    IndexState(Option("i18n")) -> DemoEntry("i18n", div().render),
  )

  private object HelloDemo {
    val (rendered, source) = {
      val name = Property.blank[String]
      div(
        TextInput(name)(),
        p("Hello, ", bind(name), "!"),
      )
    }.withSourceCode
  }
}


