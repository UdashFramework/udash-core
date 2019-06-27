package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.views.frontend.demos.IntroFormDemo
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object FrontendIntroViewFactory extends StaticViewFactory[FrontendIntroState.type](() => new FrontendIntroView)


class FrontendIntroView extends FinalView with CssView {
  import Context._
  import JsDom.all._

  private val (introFormDemo, introFormSnippet) = IntroFormDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h2("Introduction"),
    p(
      "At present JavaScript is an undisputed market leader of frontend development. With frameworks like AngularJS, ReactJS ",
      "or jQuery development of small, modern and responsive websites is quite easy and fast. On the other hand, ",
      "JavaScript is untyped and not so easy to master. This leads to huge maintenance costs of JavaScript based projects ",
      "and tears of developers working on such codebase. "
    ),
    h3("A new hope"),
    p(
      "The ", a(href := References.ScalaJsHomepage)("Scala.js"), " project tries to make developers lives easier. It brings the ",
      "power of the ", a(href := References.ScalaHomepage)("Scala language"), " and compiles it to JavaScript. Thanks to this, ",
      "we can develop in a type-safe, modern, developer friendly language and publish a project as a website like with JavaScript. "
    ),
    p(
      "Udash framework provides tools to make web applications development with ",
      a(href := References.ScalaJsHomepage)("Scala.js"), " fast and easy. You might have already read about Udash ",
      a(href := RpcIntroState.url)("RPC"), " system. In this part of the guide you will read about: "
    ),
    ul(GuideStyles.defaultList)(
      li("Routing in Udash based applications."),
      li("The Properties mechanism for application modelling."),
      li(
        a(href := References.ScalatagsHomepage)("ScalaTags"), " and ", a(href := References.ScalaCssHomepage)("ScalaCSS"),
        " usage as HTML and CSS replacement."
      ),
      li("The property bindings for ", a(href := References.ScalatagsHomepage)("ScalaTags"), ".")
    ),
    p("All these features will make your life as a frontend developer pleasant."),
    p("To start development import Udash classes as follows:"),
    CodeBlock("""import io.udash._""".stripMargin)(GuideStyles),
    h4("Example"),
    p(
      "Take a look at this simple form with a validation. We will not discuss the implementation here, because ",
      "it is quite self-descriptive. All those elements will be described in detail in the following chapters. ",
    ),
    ForceBootstrap(introFormDemo),
    p(
      "Notice that this example follows the recommended code structure with separated model, view, presenter and ",
      "factory. It also assumes that you have created ", i("IntroFormDemoState"), " somewhere in your states hierarchy. "
    ),
    introFormSnippet,
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendRoutingState(None).url)("Routing in Udash"),
      " chapter to learn more about selecting a view based on a URL."
    )
  )
}