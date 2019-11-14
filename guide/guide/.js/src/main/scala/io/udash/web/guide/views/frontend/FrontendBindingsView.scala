package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object FrontendBindingsViewFactory extends StaticViewFactory[FrontendBindingsState.type](() => new FrontendBindingsView)

class FrontendBindingsView extends View {
  import Context._
  import JsDom.all._

  private val (bindDemo, bindSnippet) = BindDemo.demoWithSnippet()
  private val (produceDemo, produceSnippet) = ProduceDemo.demoWithSnippet()
  private val (repeatDemo, repeatSnippet) = RepeatDemo.demoWithSnippet()
  private val (showIfDemo, showIfSnippet) = ShowIfDemo.demoWithSnippet()
  private val (bindAttributeDemo, bindAttributeSnippet) = BindAttributeDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h2("Property Bindings"),
    p(
      "As every modern frontend framework Udash provides model-view template bindings. ",
      "What really distinguishes Udash from other frameworks is the fact that it is type-safe."
    ),
    p("Udash provides many ways to bind properties to Scalatags templates. To use them you have to add this import in your code:"),
    CodeBlock(
      """import io.udash._""".stripMargin
    )(GuideStyles),
    p("Let's briefly introduce all these methods:"),
    ul(GuideStyles.defaultList)(
      li(i("bind"), " - the simplest way to bind a property to a template, it uses the ", i(".toString"), " method to get the string which should be displayed."),
      li(i("produce"), " - similar to ", i("bind"), ", but takes a builder method which is called on every change of the property - its result is inserted into DOM."),
      li(i("repeat"), " - draws all elements of a ", i("SeqProperty"), " and updates the view on every sequence change."),
      li(i("showIf/showIfElse"), " - shows and hides elements depending on provided property value."),
      li(i("Attribute bindings"), " - on every change of the property updates a HTML attribute state."),
    ),
    h3("bind"),
    bindSnippet,
    bindDemo,
    p("As you can see the ", i("bind"), " method automatically updates displayed name on every change of the property value."),
    h3("produce"),
    produceSnippet,
    produceDemo,
    p(
      "The above example presents two variants of the ", i("produce"), " method. This is very similar to the ", i("bind"),
      " method, but you can provide a custom DOM element builder. "
    ),
    h3("repeat"),
    repeatSnippet,
    repeatDemo,
    p("Notice that the version of ", i("produce"), " for ", i("SeqProperty"),  ", ",
      "redraws the whole sequence every time - it is ok when the sequence is small. The ", i("repeat"),
      " method updates only changed elements of the sequence. To make it easier to notice, every added element is highlighted. "
    ),
    h3("showIf"),
    p(
      "This binding method takes two arguments: ", i("Property[Boolean]"), " and ", i("Seq[dom.Element]"), ". ",
      "If value of the property is ", i("true"), " it shows all passed elements and hides them otherwise."
    ),
    showIfSnippet,
    showIfDemo,
    h3("Attribute bindings"),
    p(
      "Udash provides extension methods on Scalatags ", i("Attr"), " and ", i("AttrPair"), ". ",
      i("Attr.bind(ReadableProperty[String])"), " synchronises attribute value with the property. ",
      i("AttrPair.attrIf(ReadableProperty[Boolean])"), " adds attribute if the property value is ", i("true"), " and removes it otherwise. "
    ),
    bindAttributeSnippet,
    bindAttributeDemo,
    h2("Nested bindings"),
    p("Sometimes you want to create property binding inside another binding builder. For example:"),
    CodeBlock(
      """val p = Property("A")
        |val p2 = Property("a")
        |produce(p) { v =>
        |  div(v, bind(p2)).render
        |}""".stripMargin
    )(GuideStyles),
    p(
      "When you change ", i("p"), " value, the builder creates a new element with a new binding inside, but unfortunately ",
      "the old one is still working. There is no way to kill or reuse the old binding, so it will be working as long ",
      "as you keep the ", i("p2"), " reference."
    ),
    p(
      "Every binding creating DOM elements has a version supporting nested bindings. For example: ",
      i("produceWithNested"), " and ", i("repeatWithNested"), ". These bindings provide not only the property value, ",
      "but also the interceptor which prepares the nested binding to be removed when it's no longer needed."
    ),
    CodeBlock(
      """val p = Property("A")
        |val p2 = Property("a")
        |produceWithNested(p) { (v, nested) =>
        |  div(v, nested(bind(p2))).render
        |}""".stripMargin
    )(GuideStyles),
    p("Each binding method returns a ", i("Binding"), " which enables manual management of the binding lifecycle. "),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendFormsState.url)("Two-way Forms Binding"), " chapter to read about binding properties to HTML forms."
    )
  )
}
