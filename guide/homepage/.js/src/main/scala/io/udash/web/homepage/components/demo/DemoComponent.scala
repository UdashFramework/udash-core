package io.udash.web.homepage.components.demo

import com.avsystem.commons.SharedExtensions
import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.components.CodeBlock.Prism
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.views.{Component, Image}
import io.udash.web.homepage.Context._
import io.udash.web.homepage.IndexState
import io.udash.web.homepage.styles.partials.{DemoStyles, HomepageStyles}
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

class DemoComponent(url: Property[IndexState]) extends Component {

  private val fiddleContainer = div(DemoStyles.demoFiddle).render
  private val jqFiddleContainer = jQ(fiddleContainer)

  private val template = div(DemoStyles.demoComponent)(
    Image("laptop.png", "", DemoStyles.laptopImage),
    div(DemoStyles.demoBody)(
      ul(DemoStyles.demoTabs)(
        DemoComponent.demoEntries.map(entry =>
          li(DemoStyles.demoTabsItem)(
            a(
              DemoStyles.demoTabsLink,
              href := entry.targetState.url,
              (attr(Attributes.data(Attributes.Active)) := "true").attrIf(url.transform(_ == entry.targetState))
            )(entry.name)
          )
        )
      ),
      fiddleContainer
    )
  ).render

  url.listen(onUrlChange, initUpdate = true)

  private def onUrlChange(update: IndexState): Unit = {
    val entryOption = DemoComponent.demoEntries.find(_.targetState == update)
    val entry = entryOption.getOrElse(DemoComponent.demoEntries.head)

    jqFiddleContainer.html(entry.fiddle)
    Prism.highlightAllUnder(fiddleContainer)
  }

  override def getTemplate: Modifier = template
}

object DemoComponent extends SharedExtensions {

  def code(): Element = {
    div(
      style := "display: grid; grid-template-columns: 60% 40%",
      div(textAlign.left, backgroundColor := "#f5f2f0")(
        CodeBlock.lines(HelloDemo.source.linesIterator.drop(1).map(_.drop(2)).toSeq.dropRight(1).iterator)(HomepageStyles)
      ),
      div(backgroundColor := "white")(HelloDemo.rendered),
    ).render
  }

  def demoEntries: Seq[DemoEntry] = Seq(
    DemoEntry("Hello World", IndexState(Option("hello")), code()),
    DemoEntry("Properties", IndexState(Option("properties")), div().render),
    DemoEntry("Validation", IndexState(Option("validation")), div().render),
    DemoEntry("i18n", IndexState(Option("i18n")), div().render),
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


