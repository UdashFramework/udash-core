package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.{JQueryCallbacksDemo, JQueryEventsDemo}
import io.udash.web.guide.views.{References, Versions}
import scalatags.JsDom

case object JQueryExtViewFactory extends StaticViewFactory[JQueryExtState.type](() => new JQueryExtView)


class JQueryExtView extends View {
  import JsDom.all._

  private val (jQueryEventsDemo, jQueryEventsSnippet) = JQueryEventsDemo.demoWithSnippet()
  private val (jQueryCallbacksDemo, jQueryCallbacksSnippet) = JQueryCallbacksDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h1("Udash jQuery wrapper"),
    p(
      "The jQuery library is a very popular tool in the web development. We have created a strongly typed wrapper for jQuery, ",
      "which allows you to use jQuery in the typed environment of Scala.js."
    ),
    h2("The first steps"),
    p("To start development with the jQuery wrapper add the following line in you frontend module dependencies: "),
    CodeBlock(
      s""""io.udash" %%% "udash-jquery" % "${Versions.udashJQueryVersion}"""".stripMargin
    )(GuideStyles),
    p("The wrapper provides a typed equivalent of the jQuery ", i("$()"), " operator: "),
    CodeBlock(
      s"""import io.udash.wrappers.jquery._
         |import scalatags.JsDom.all._
         |
         |val component = h1("Hello, jQuery!").render
         |
         |val paragraphs = jQ("p")
         |val hello = jQ(component)""".stripMargin
    )(GuideStyles),
    p("Now you can use any jQuery method on these values: "),
    CodeBlock(
      s"""paragraphs.show(1500, EasingFunction.swing)
         |hello.hide(AnimationOptions(
         |  duration = Some(3000),
         |  easing = Some(EasingFunction.linear)
         |))""".stripMargin
    )(GuideStyles),
    h2("jQuery event handlers"),
    p("The below example presents events handling with jQuery wrapper: "),
    jQueryEventsSnippet,
    p(
      "Notice that if you want to use the ", i("off()"), " method, then you have to pass exactly the same object ",
      "that you passed to the method ", i("on()"), " or ", i("one()"), ". Be careful with implicit conversions, ",
      "they create new object every time."
    ),
    ForceBootstrap(jQueryEventsDemo),
    h2("jQuery callbacks"),
    p("The wrapper provides also typed API for the jQuery callbacks mechanism: "),
    jQueryCallbacksSnippet,
    ForceBootstrap(jQueryCallbacksDemo),
    h2("What's next?"),
    p(
      "You can find more information on the wrapper ", a(href := References.UdashjQueryWrapperRepo, target := "_blank")("GitHub repository"), ". ",
      "It also contains an example application which presents more ways of working with this wrapper."
    )
  )
}
