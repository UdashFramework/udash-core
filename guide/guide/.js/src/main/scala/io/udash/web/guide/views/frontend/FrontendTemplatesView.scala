package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.styles.attributes.Attributes
import io.udash.web.guide._
import io.udash.web.guide.styles.demo.ExampleStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.wrappers.jquery._
import scalatags.JsDom

case object FrontendTemplatesViewFactory extends StaticViewFactory[FrontendTemplatesState.type](() => new FrontendTemplatesView)

class FrontendTemplatesView extends FinalView with CssView {
  import JsDom.all._
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = div(
    h2("Scalatags & UdashCSS"),
    p(
      "Using ", a(href := References.ScalatagsHomepage, target := "_blank")("ScalaTags"), " and ", i("UdashCSS"), " ",
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
      """import scalatags.JsDom.all._
        |
        |html(
        |  head(
        |    meta(charset := "UTF-8"),
        |    meta(name := "viewport", content := "width=device-width, initial-scale=1"),
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
        |    <meta name="viewport" content="width=device-width, initial-scale=1">
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
    h2("UdashCSS"),
    p(
      a(href := References.ScalaCssHomepage, target := "_blank")("ScalaCSS"),
      " is a library for creating CSS stylesheets with the Scala language. Using inline stylesheets is type-safe ",
      "and there is no need to manually manage class names. You can create your styles like in SASS/LESS, but you have the power ",
      "of Scala and your stylesheets can be generated dynamically (that is, you can change property values at runtime)."
    ),
    p(
      "Unfortunately ScalaCSS generates a lot of JavaScript code and significantly reduces application start-up performance. ",
      "Udash provides tools for server-side CSS rendering with type-safe class references. It allows to keep benefits of ScalaCSS DSL ",
      "and decrease JS size and initialization time. For reference, the Udash Homepage JS size decreased from ", i("766kB"), " to ", i("314kB"), "."
    ),
    p(
      "Udash CSS tooling tries to keep migration from ScalaCSS as easy as possible. It also reuses a huge part of ScalaCSS DSL. ",
      "Here is a list of all major differences: ",
      ul(GuideStyles.defaultList)(
        li(b("Styles in shared module"), " - you have to create your stylesheets in the cross-compiled module in order to render them on the JVM and refer to them in JS."),
        li(b(i("CssBase"), " instead of ", i("StyleSheet.Inline")), " - it provides ", i("import dsl._"), " like ScalaCSS, but with minor internal differences."),
        li(b(i("CssStyle"), " instead of ", i("StyleS")), "/", i("StylaA"), " - all your style fields use this type."),
        li(b("Names in JavaScript, definition in JVM"), " - all your styles are not compiled into JS, it contains only class names."),
        li(b("Render in JVM"), " - you can use ", i("CssFileRenderer"), " or ", i("CssStringRenderer"), " to render your stylesheets and provide them to the frontend app.")
      )
    ),
    p("Look at a simple button example:"),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleStyles extends CssBase {
        |  import dsl._
        |
        |  val btn: CssStyle = style(
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
        |  val btnDefault: CssStyle = style(
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
        |  val btnSuccess: CssStyle = style(
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
    p("Using with Scalatags:"),
    CodeBlock(
      """div(
        |  a(
        |    ExampleStyles.btn, ExampleStyles.btnDefault,
        |    id := "example-button",
        |    onclick := { () =>
        |      jQ("#example-button")
        |        .toggleClass(ExampleStyles.btnSuccess.className)
        |    }
        |  )("Click me")
        |)""".stripMargin
    )(GuideStyles),
    div(GuideStyles.frame)(
      a(
        ExampleStyles.btn, ExampleStyles.btnDefault, id := "example-button",
        onclick := { () => jQ("#example-button").toggleClass(ExampleStyles.btnSuccess.className)}
      )("Click me")
    ),
    h3("Nested styles"),
    p("If you need styles nesting, you can use unsafeChild():"),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleStyles extends CssBase {
        |  import dsl._
        |
        |  val innerOff: CssStyle = style(
        |    padding(6 px, 12 px),
        |    borderBottomWidth(1 px),
        |    borderBottomStyle.solid,
        |    borderBottomColor(c"#CCCCCC")
        |  )
        |
        |  val innerOn: CssStyle = style(
        |    padding(6 px, 12 px),
        |    color(c"#FFFFFF"),
        |    backgroundColor(c"#5CB85C"),
        |    borderTopWidth(1 px),
        |    borderTopStyle.solid,
        |    borderTopColor(c"#4CAE4C")
        |  )
        |
        |  val swither: CssStyle = style(
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
        |      unsafeChild(s".${innerOff.className}") (
        |        visibility.hidden
        |      ),
        |      unsafeChild(s".${innerOn.className}") (
        |        visibility.visible
        |      )
        |    ),
        |    &.attr("data-state", "off") (
        |      unsafeChild(s".${innerOff.className}") (
        |        visibility.visible
        |      ),
        |      unsafeChild(s".${innerOn.className}") (
        |        visibility.hidden
        |      )
        |    )
        |  )
        |}""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """a(
        |  ExampleStyles.swither,
        |  id := "example-switcher",
        |  data("state") := "off",
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
        if (jqSwitcher.attr(Attributes.data(Attributes.State)).get == "on") jqSwitcher.attr(Attributes.data(Attributes.State), "off")
        else jqSwitcher.attr(Attributes.data(Attributes.State), "on")
      })(
        div(ExampleStyles.innerOff)("Off"),
        div(ExampleStyles.innerOn)("On")
      )
    ),
    h3("Keyframe animation"),
    p("You can use DSL methods for keyframe animations."),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleKeyframes extends CssBase {
        |  import dsl._
        |
        |  val colorPulse = keyframes(
        |    0d -> keyframe(
        |      color(c"#000000"),
        |      backgroundColor(c"#FFFFFF")
        |    ),
        |
        |    50d -> keyframe(
        |      color(c"#FFFFFF"),
        |      backgroundColor(c"#5CB85C")
        |    ),
        |
        |    100d -> keyframe(
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
    p("If you need some mixins, you can define methods which return a CssStyle typed object:"),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleMixins extends CssBase {
        |  import dsl._
        |
        |  def animation(name: String, duration: FiniteDuration,
        |                iterationCount: AV = animationIterationCount.infinite,
        |                easing: AV = animationTimingFunction.easeInOut): CssStyle =
        |    mixin(
        |      animationName := name,
        |      iterationCount,
        |      animationDuration(duration),
        |      easing
        |    )
        |}""".stripMargin
    )(GuideStyles),
    p("Using keyframes and animation mixins, you can create a button with a simple animation when you hover over it, for example:"),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleStyles extends CssBase {
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
        |  ExampleStyles.btn, ExampleStyles.btnDefault, ExampleStyles.btnAnimated
        |)("Hover over me")""".stripMargin
    )(GuideStyles),
    div(GuideStyles.frame)(
      a(ExampleStyles.btn, ExampleStyles.btnDefault, ExampleStyles.btnAnimated)( "Hover over me" )
    ),
    h3("Media queries"),
    p("It is also possible to create styles for responsive designs:"),
    CodeBlock(
      """import io.udash.css._
        |
        |object ExampleStyles extends CssBase {
        |  import dsl._
        |
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
        |  ExampleStyles.mediaContainer, ExampleStyles.mediaDesktop
        |)("Reduce the browser width"),
        |div(
        |  ExampleStyles.mediaContainer, ExampleStyles.mediaTablet
        |)("Increase the browser width")""".stripMargin
    )(GuideStyles),
    div(
      div(ExampleStyles.mediaContainer, ExampleStyles.mediaDesktop)( "Reduce the browser width" ),
      div(ExampleStyles.mediaContainer, ExampleStyles.mediaTablet)( "Increase the browser width" )
    ),
    h3("Rendering"),
    p(
      "The JavaScript version of this cross-compiled code contains only the class names of CSS rules. ",
      "This chapter describes the CSS rendering process."
    ),
    p("First of all you have to create ", i("CssRenderer"), " class:"),
    CodeBlock(
      """import io.udash.css._
        |import scalacss.internal.{Renderer, StringRenderer}
        |
        |/** Renderer of styles based on UdashCSS. */
        |class CssRenderer(path: String, renderPretty: Boolean) {
        |  private val renderer: Renderer[String] =
        |    if (renderPretty) StringRenderer.defaultPretty
        |    else StringRenderer.formatTiny
        |
        |  def render(): Unit = {
        |    new CssFileRenderer(
        |      path,
        |      Seq(
        |        // you have to put here all your CssBase classes
        |        ExampleStyles
        |      ),
        |      // it allows you to include all styles with the single
        |      createMain = true
        |    ).render()(renderer)
        |  }
        |}
        |
        |object CssRenderer {
        |  // we want to make it runnable without
        |  // starting the whole application
        |  def main(args: Array[String]): Unit = {
        |    require(args.length == 2)
        |    new CssRenderer(
        |      args(0), java.lang.Boolean.parseBoolean(args(1))
        |    ).render()
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p("Now you can configure a new sbt task to generate the CSS files: "),
    CodeBlock(
      """val cssDir = settingKey[File]("Target for `compileCss` task.")
        |val compileCss = taskKey[Unit]("Compiles CSS files.")
        |
        |// inside your frontend module
        |// lazy val frontend = project.in(file("frontend"))
        |//   .settings(
        |  cssDir := {
        |    (Compile / fastOptJS / target).value /
        |      "UdashStatics" / "WebContent" / "styles"
        |  },
        |  compileCss := Def.taskDyn {
        |    val dir = (Compile / cssDir).value
        |    val path = dir.absolutePath
        |    println(s"Generating CSS files in `$path`...")
        |    dir.mkdirs()
        |    // make sure you have configured the valid `CssRenderer` path
        |    // we assume that `CssRenderer` exists in the `backend` module
        |    (backend / Compile / runMain)
        |      .toTask(s" io.app.backend.css.CssRenderer $path false")
        |  }.value,
        |
        |// you can also add `compileCss` as a dependency to
        |// the `compileStatics` and `compileAndOptimizeStatics` tasks""".stripMargin
    )(GuideStyles),
    p(
      "With above configuration you can call ", i("sbt frontend/compileCss"), " to render your styles. ",
      "Now you just need to include them in the ", i("index.html"), " file."
    ),
    CodeBlock(
      """<link href="styles/main.css" rel="stylesheet" />""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendPropertiesState.url)("Properties"),
      " chapter to read about a data model in the Udash applications. ",
      "You might find ", a(href := BootstrapExtState.url)("Bootstrap Components"), " interesting later on."
    )
  )
}
