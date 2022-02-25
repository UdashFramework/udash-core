package io.udash.web.homepage.components.demo

import com.avsystem.commons.SharedExtensions._
import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.components.CodeBlock.Prism
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.commons.views.{Component, Image}
import io.udash.web.homepage.styles.partials.{DemoStyles, HomepageStyles}
import io.udash.web.homepage.{IndexState, RoutingState}
import org.scalajs.dom.Element
import scalatags.JsDom.all._

final class DemoComponent(implicit application: Application[RoutingState]) extends Component {

  private def code(demo: CodeDemo): Element =
    div(DemoStyles.demoFiddle)(
      div(textAlign.left, backgroundColor := "#f5f2f0", overflow.auto)(
        CodeBlock.lines(demo.source.linesIterator.drop(1).map(_.drop(2)).toSeq.dropRight(1).iterator)(HomepageStyles)
      ),
      div(backgroundColor := "white", paddingTop := 15.px)(demo.rendered),
    ).render

  override def getTemplate: Modifier =
    div(DemoStyles.demoComponent)(
      Image("laptop.png", "", DemoStyles.laptopImage),
      div(DemoStyles.demoBody)(
        ul(DemoStyles.demoTabs)(
          IndexState.values.iterator.map { state =>
            li(DemoStyles.demoTabsItem)(
              a(
                DemoStyles.demoTabsLink,
                href := state.url,
                (attr(Attributes.data(Attributes.Active)) := "true").attrIf(application.currentStateProperty.transform(_ == state))
              )(state.name)
            )
          }.toSeq
        ),
        produce(application.currentStateProperty) { state =>
          state.opt.collect { case state: IndexState => code(state.codeDemo).setup(Prism.highlightAllUnder) }.getOrElse[Element](div().render)
        }
      )
    ).render
}