package io.udash.web.guide.views.frontend

import java.util.concurrent.TimeUnit

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.wrappers.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLStyleElement

import scala.concurrent.duration.FiniteDuration
import scalacss.{AV, Keyframes}
import scalacss.Defaults._
import scalatags.JsDom
import scalatags.JsDom.TypedTag

case object FrontendTemplatesViewPresenter extends DefaultViewPresenterFactory[FrontendTemplatesState.type](() => new FrontendTemplatesView)

class FrontendTemplatesView extends View {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  override def getTemplate: dom.Element = div(
    ExampleStyles.render[TypedTag[HTMLStyleElement]],
    ExampleKeyframes.render[TypedTag[HTMLStyleElement]],
    h2("Scalatags & ScalaCSS"),
    p(
      "Using ", a(href := References.ScalatagsHomepage)("Scalatags"), " and ", a(href := References.ScalaCssHomepage)("ScalaCSS"), " ",
      "is the recommended way of creating and styling view templates. This part of the guide presents the most interesting parts ",
      "of these libraries. For more details refer to projects documentation."
    ),
    h2("Scalatags"),
    p(
      "Scalatags is a fast and small library for creating an XML/HTML/CSS structure directly in Scala code. ",
      "The important advantage of Scalatags is the ability to maintain views and logic in one place."
    ),
    p("For example, this piece of code:"),
    CodeBlock(
      """html(
        |  head(
        |    meta(charset := "UTF-8"),
        |    meta(name := "viewport", content := "width=device-width, initial-scala=1"),
        |    script(src := "scripts/fastopt.js")
        |  ),
        |  body(
        |    header(cls := "site-header")(
        |      "Hello, World!"
        |    ),
        |    main(
        |      h1("I'm a title."),
        |      div(id := "link-wrapper")(
        |        a(href := "#", target := "_blank")(
        |          "I'm a link."
        |        )
        |      )
        |    )
        |  )
        |)""".stripMargin
    )(GuideStyles),
    p("Will be compiled to this HTML:"),
    CodeBlock(
      """<html>
        |  <head>
        |    <meta charset="UTF-8">
        |    <meta name="viewport" content="width=device-width, initial-scala=1">
        |    <script src="scripts/fastopt.js"></script>
        |  </head>
        |  <body>
        |    <header class="site-header">Hello, World!</header>
        |    <main>
        |      <h1>I'm a title.</h1>
        |      <div id="link-wrapper">
        |        <a href="#" target="_blank">I'm a link.</a>
        |      </div>
        |    </main>
        |  </body>
        |</html>""".stripMargin
    )(GuideStyles),
    p("With Scalatags you can also bind callbacks, just like in HTML."),
    CodeBlock(
      """a(
        |  cls := "btn btn-default",
        |  id := "example-button",
        |  onclick := { () => jQ("#example-button").toggleClass("btn-success") }
        |)("Click me")""".stripMargin
    )(GuideStyles),
    h2("ScalaCSS"),
    p(
      "ScalaCSS is a library for creating CSS stylesheets with the Scala language. Using inline stylesheets is type safe ",
      "and there is no need to manually manage class names. You can create your styles like in SASS/LESS, but you have the power ",
      "of Scala and your stylesheets can be generated dynamically (that is, you can change property values at runtime)."
    ),
    p("Look at a simple button example with ScalaCSS based styles:"),
    CodeBlock(
      """object ExampleStyles extends StyleSheet.Inline {
        |  import scala.language.postfixOps
        |  import dsl._
        |
        |  val btn = style(
        |    display.inlineBlock,
        |    padding(6 px, 12 px),
        |    fontSize(14 px),
        |    fontWeight._400,
        |    textAlign.center,
        |    whiteSpace.nowrap,
        |    verticalAlign.middle,
        |    cursor.pointer,
        |    borderWidth(1 px),
        |    borderStyle.solid,
        |    borderColor.transparent,
        |    borderRadius(4 px),
        |    userSelect := "none"
        |  )
        |
        |  val btnDefault = style(
        |    color(c"#000000"),
        |    backgroundColor(c"#FFFFFF"),
        |    borderColor(c"#CCCCCC"),
        |
        |    &.hover (
        |      color(c"#333333"),
        |      backgroundColor(c"#E6E6E6"),
        |      borderColor(c"#ADADAD"),
        |      textDecoration := "none"
        |    )
        |  )
        |
        |  val btnSuccess = style(
        |    color(c"#FFFFFF"),
        |    backgroundColor(c"#5CB85C"),
        |    borderColor(c"#4CAE4C"),
        |
        |    &.hover (
        |      color(c"#FFFFFF"),
        |      backgroundColor(c"#449D44"),
        |      borderColor(c"#398439")
        |    )
        |  )
        |}""".stripMargin
    )(GuideStyles),
    p("Using Scalatags:"),
    CodeBlock(
      """div(
        |  ExampleStyles.render[TypedTag[HTMLStyleElement]],
        |  a(
        |    ExampleStyles.btn  + ExampleStyles.btnDefault, id := "example-button",
        |    onclick := { () =>
        |      jQ("#example-button").toggleClass(ExampleStyles.btnSuccess.htmlClass)
        |    }
        |  )("Click me")
        |)""".stripMargin
    )(GuideStyles),
    div(GuideStyles.frame)(
      a(
        ExampleStyles.btn, ExampleStyles.btnDefault, id := "example-button",
        onclick := { () => jQ("#example-button").toggleClass(ExampleStyles.btnSuccess.htmlClass)}
      )("Click me")
    ),
    h3("Nested styles"),
    p("If you need styles nesting, you can use unsafeChild():"),
    CodeBlock(
      """object ExampleStyles extends StyleSheet.Inline {
        |  import scala.language.postfixOps
        |  import dsl._
        |
        |  val innerOff = style(
        |    padding(6 px, 12 px),
        |    borderBottomWidth(1 px),
        |    borderBottomStyle.solid,
        |    borderBottomColor(c"#CCCCCC")
        |  )
        |
        |  val innerOn = style(
        |    padding(6 px, 12 px),
        |    color(c"#FFFFFF"),
        |    backgroundColor(c"#5CB85C"),
        |    borderTopWidth(1 px),
        |    borderTopStyle.solid,
        |    borderTopColor(c"#4CAE4C")
        |  )
        |
        |  val swither = style(
        |    display.inlineBlock,
        |    borderWidth(1 px),
        |    borderStyle.solid,
        |    borderRadius(4 px),
        |    borderColor(c"#CCCCCC"),
        |    cursor.pointer,
        |    userSelect := "none",
        |
        |    &.hover (
        |      textDecoration := "none"
        |    ),
        |
        |    &.attr("data-state", "on") (
        |      unsafeChild(s".${innerOff.htmlClass}") (
        |        visibility.hidden
        |      ),
        |      unsafeChild(s".${innerOn.htmlClass}") (
        |        visibility.visible
        |      )
        |    ),
        |    &.attr("data-state", "off") (
        |      unsafeChild(s".${innerOff.htmlClass}") (
        |        visibility.visible
        |      ),
        |      unsafeChild(s".${innerOn.htmlClass}") (
        |        visibility.hidden
        |      )
        |    )
        |  )
        |}""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """a(
        |  ExampleStyles.swither, id := "example-switcher", data("state") := "off",
        |  onclick := { () =>
        |    val jqSwitcher = jQ("#example-switcher")
        |
        |    if (jqSwitcher.attr("data-state").get == "on")
        |      jqSwitcher.attr("data-state", "off")
        |    else
        |      jqSwitcher.attr("data-state", "on")
        |  }
        |)(
        |  div(ExampleStyles.innerOff)("Off"),
        |  div(ExampleStyles.innerOn)("On")
        |)""".stripMargin
    )(GuideStyles),
    div(GuideStyles.frame)(
      a(ExampleStyles.swither, id := "example-switcher", data("state") := "off", onclick := { () =>
        val jqSwitcher = jQ("#example-switcher")
        if (jqSwitcher.attr("data-state").get == "on") jqSwitcher.attr("data-state", "off") else jqSwitcher.attr("data-state", "on")
      })(
        div(ExampleStyles.innerOff)("Off"),
        div(ExampleStyles.innerOn)("On")
      )
    ),
    h3("Keyframe animation"),
    p("You can use DSL methods for keyframe animations."),
    CodeBlock(
      """object ExampleKeyframes extends StyleSheet.Inline {
        |  import scala.language.postfixOps
        |  import dsl._
        |
        |  val colorPulse = keyframes(
        |    (0 %%) -> keyframe(
        |      color(c"#000000"),
        |      backgroundColor(c"#FFFFFF")
        |    ),
        |
        |    (50 %%) -> keyframe(
        |      color(c"#FFFFFF"),
        |      backgroundColor(c"#5CB85C")
        |    ),
        |
        |    (100 %%) -> keyframe(
        |      color(c"#000000"),
        |      backgroundColor(c"#FFFFFF")
        |    )
        |  )
        |
        |  val animated = style(
        |    animationName(colorPulse),
        |    animationIterationCount.count(1),
        |    animationDuration(2 seconds)
        |  )
        |}""".stripMargin
    )(GuideStyles),
    h3("Mixins"),
    p("If you need some mixins, you can define methods which return a StyleA typed object:"),
    CodeBlock(
      """object ExampleMixins extends StyleSheet.Inline{
        |  import dsl._
        |
        |  def animation(name: String, duration: FiniteDuration,
        |                iterationCount: AV = animationIterationCount.infinite,
        |                easing: AV = animationTimingFunction.easeInOut): StyleA = style(
        |    animationName := name,
        |    iterationCount,
        |    animationDuration(duration),
        |    easing
        |  )
        |}""".stripMargin
    )(GuideStyles),
    p("Using keyframes and animation mixins, you can create a button with a simple animation when you hover over it, for example:"),
    CodeBlock(
      """object ExampleStyles extends StyleSheet.Inline {
        |  import scala.language.postfixOps
        |  import dsl._
        |
        |  val btnAnimated = style(
        |    &.hover {
        |      ExampleMixins.animation(
        |        ExampleKeyframes.colorPulse.name.value,
        |        FiniteDuration(750, TimeUnit.MILLISECONDS)
        |      )
        |    }
        |  )
        |}""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """a(
        |  ExampleStyles.btn + ExampleStyles.btnDefault + ExampleStyles.btnAnimated
        |)("Hover over me")""".stripMargin
    )(GuideStyles),
    div(GuideStyles.frame)(
      a(ExampleStyles.btn, ExampleStyles.btnDefault, ExampleStyles.btnAnimated)( "Hover over me" )
    ),
    h3("Media queries"),
    p("It is also possible to create styles for responsive designs:"),
    CodeBlock(
      """object ExampleStyles extends StyleSheet.Inline {
        |  import scala.language.postfixOps
        |  import dsl._

        |  val mediaContainer = style(
        |    position.relative,
        |    fontSize(28 px),
        |    textAlign.center,
        |    padding(40 px, 0 px),
        |    borderWidth(2 px),
        |    borderStyle.solid,
        |    borderColor(c"#000000")
        |  )
        |
        |  val mediaDesktop = style(
        |    media.maxWidth(769 px) (
        |      display.none
        |    )
        |  )
        |
        |  val mediaTablet = style(
        |    display.none,
        |
        |    media.maxWidth(768 px) (
        |      display.block
        |    )
        |  )
        |}""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """div(
        |  ExampleStyles.mediaContainer + ExampleStyles.mediaDesktop
        |)("Reduce the browser width"),
        |div(
        |  ExampleStyles.mediaContainer + ExampleStyles.mediaTablet
        |)("Increase the browser width")""".stripMargin
    )(GuideStyles),
    div(
      div(ExampleStyles.mediaContainer, ExampleStyles.mediaDesktop)( "Reduce the browser width" ),
      div(ExampleStyles.mediaContainer, ExampleStyles.mediaTablet)( "Increase the browser width" )
    ),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendPropertiesState.url)("Properties"),
      " chapter to read about a data model in the Udash applications."
    )
  ).render

  override def renderChild(view: View): Unit = {}
}

object ExampleStyles extends StyleSheet.Inline {
  import dsl._

  import scala.language.postfixOps

  val btn = style(
    display.inlineBlock,
    padding(6 px, 12 px),
    fontSize(14 px),
    fontWeight._400,
    textAlign.center,
    whiteSpace.nowrap,
    verticalAlign.middle,
    cursor.pointer,
    borderWidth(1 px),
    borderStyle.solid,
    borderColor.transparent,
    borderRadius(4 px),
    userSelect := "none",
    overflow.hidden
  )

  val btnDefault = style(
    color(c"#000000"),
    backgroundColor(c"#FFFFFF"),
    borderColor(c"#CCCCCC"),

    &.hover (
      color(c"#333333"),
      backgroundColor(c"#E6E6E6"),
      borderColor(c"#ADADAD"),
      textDecoration := "none"
    )
  )

  val btnSuccess = style(
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderColor(c"#4CAE4C"),

    &.hover (
      color(c"#FFFFFF"),
      backgroundColor(c"#449D44"),
      borderColor(c"#398439")
    )
  )

  val btnAnimated = style(
    &.hover {
      ExampleMixins.animation(ExampleKeyframes.colorPulse, FiniteDuration(750, TimeUnit.MILLISECONDS))
    }
  )

  val innerOff = style(
    padding(6 px, 12 px),
    borderBottomWidth(1 px),
    borderBottomStyle.solid,
    borderBottomColor(c"#CCCCCC")
  )

  val innerOn = style(
    padding(6 px, 12 px),
    color(c"#FFFFFF"),
    backgroundColor(c"#5CB85C"),
    borderTopWidth(1 px),
    borderTopStyle.solid,
    borderTopColor(c"#4CAE4C")
  )

  val swither = style(
    display.inlineBlock,
    borderWidth(1 px),
    borderStyle.solid,
    borderRadius(4 px),
    borderColor(c"#CCCCCC"),
    cursor.pointer,
    userSelect := "none",

    &.hover (
      textDecoration := "none"
    ),

    &.attr("data-state", "on") (
      unsafeChild(s".${innerOff.htmlClass}") (
        visibility.hidden
      ),
      unsafeChild(s".${innerOn.htmlClass}") (
        visibility.visible
      )
    ),
    &.attr("data-state", "off") (
      unsafeChild(s".${innerOff.htmlClass}") (
        visibility.visible
      ),
      unsafeChild(s".${innerOn.htmlClass}") (
        visibility.hidden
      )
    )
  )

  val mediaDesktop = style(
    backgroundColor(c"#E6E6E6"),
    media.maxWidth(769 px) (
      display.none
    )
  )

  val mediaTablet = style(
    display.none,
    backgroundColor(c"#5CB85C"),

    media.maxWidth(768 px) (
      display.block
    )
  )

  val mediaContainer = style(
    position.relative,
    fontSize(28 px),
    textAlign.center,
    padding(40 px, 0 px),
    borderWidth(2 px),
    borderStyle.solid,
    borderColor(c"#000000")
  )
}

object ExampleMixins extends StyleSheet.Inline {
  import dsl._

  def animation(keyframes: Keyframes, duration: FiniteDuration, iterationCount: AV = animationIterationCount.infinite, easing: AV = animationTimingFunction.easeInOut): StyleA = style(
    animationName(keyframes),
    iterationCount,
    animationDuration(duration),
    easing
  )
}

object ExampleKeyframes extends StyleSheet.Inline {
  import dsl._

  import scala.language.postfixOps

  val colorPulse = keyframes(
    (0 %%) -> keyframe(
      color(c"#000000"),
      backgroundColor(c"#FFFFFF")
    ),

    (50 %%) -> keyframe(
      color(c"#FFFFFF"),
      backgroundColor(c"#D9534F")
    ),

    (100 %%) -> keyframe(
      color(c"#000000"),
      backgroundColor(c"#FFFFFF")
    )
  )
}

