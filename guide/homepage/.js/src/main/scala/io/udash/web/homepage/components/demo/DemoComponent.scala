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
import io.udash.css.CssView._
import io.udash.web.homepage.components.demo.DemoComponent.CodeDemo

class DemoComponent(url: Property[IndexState]) extends Component {

  private def code(demo: CodeDemo): Element =
    div(DemoStyles.demoFiddle)(
      style := "display: grid; grid-template-columns: 60% 40%",
      height := 100.pct,
      div(textAlign.left, backgroundColor := "#f5f2f0")(
        CodeBlock.lines(demo.source.linesIterator.drop(1).map(_.drop(2)).toSeq.dropRight(1).iterator)(HomepageStyles)
      ),
      div(backgroundColor := "white")(demo.rendered),
    ).render

  private val template = div(DemoStyles.demoComponent)(
    Image("laptop.png", "", DemoStyles.laptopImage),
    div(DemoStyles.demoBody)(
      ul(DemoStyles.demoTabs)(
        IndexState.values.iterator.map { state =>
          li(DemoStyles.demoTabsItem)(
            a(
              DemoStyles.demoTabsLink,
              href := state.url,
              (attr(Attributes.data(Attributes.Active)) := "true").attrIf(url.transform(_ == state))
            )(state.name)
          )
        }.toSeq
      ),
      produce(url) { state =>
        code(state.codeDemo).setup(Prism.highlightAllUnder)
      }
    )
  ).render

  override def getTemplate: Modifier = template
}

object DemoComponent {

  //  def demoEntries: Map[IndexState, DemoEntry] = Map(
  //    IndexState("hello") -> DemoEntry("Hello World", code(HelloDemo)),
  //    IndexState("properties") -> DemoEntry("Properties", div().render),
  //  )

  trait CodeDemo {
    def rendered: Modifier
    def source: String
  }

  object HelloDemo extends CodeDemo {
    val (rendered, source) = {
      val name = Property.blank[String]
      div(
        TextInput(name)(),
        p("Hello, ", bind(name), "!"),
      )
    }.withSourceCode
  }
}


