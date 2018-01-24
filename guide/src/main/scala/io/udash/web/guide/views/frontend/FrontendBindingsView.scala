package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._
import io.udash.web.guide.{Context, _}

import scalatags.JsDom

case object FrontendBindingsViewFactory extends StaticViewFactory[FrontendBindingsState.type](() => new FrontendBindingsView)

class FrontendBindingsView extends FinalView with CssView {
  import Context._

  import JsDom.all._

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
      li(i("validation"), " - on every change of the property validates its value and calls the builder with the result.")
    ),
    h3("bind"),
    CodeBlock(
      """val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
        |
        |val name: Property[String] = Property[String](names.next())
        |div("Name: ", bind(name)).render
        |
        |dom.window.setInterval(() => name.set(names.next()), 500)""".stripMargin
    )(GuideStyles),
    new BindDemoComponent,
    p("As you can see the ", i("bind"), " method automatically updates displayed name on every change of the property value."),
    h3("produce"),
    CodeBlock(
      """val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
        |val name: Property[String] = Property[String](names.next())
        |val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
        |
        |div(
        |  "Name: ",
        |  produce(name)(value => b(value).render), br,
        |  "Integers: ",
        |  produce(integers)((seq: Seq[Int]) =>
        |    seq.map(p => span(s"$p, ").render)
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new ProduceDemoComponent,
    p(
      "The above example presents two variants of the ", i("produce"), " method. This is very similar to the ", i("bind"),
      " method, but you can provide a custom DOM element builder. "
    ),
    h3("repeat"),
    CodeBlock(
      """val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
        |
        |val template: Element = div(
        |  "Integers: ",
        |  repeat(integers)(p => span(s"${p.get}, ").render)
        |).render""".stripMargin
    )(GuideStyles),
    new RepeatDemoComponent,
    p("Notice that the version of ", i("produce"), " for ", i("SeqProperty"),  ", ",
      "redraws the whole sequence every time - it is ok when the sequence is small. The ", i("repeat"),
      " method updates only changed elements of the sequence. To make it easier to notice, every added element is highlighted. "
    ),
    h3("showIf"),
    p(
      "This binding method takes two arguments: ", i("Property[Boolean]"), " and ", i("Seq[dom.Element]"), ". ",
      "If value of the property is ", i("true"), " it shows all passed elements and hides them otherwise."
    ),
    CodeBlock("""val visible: Property[Boolean] = Property[Boolean](true)
                |dom.window.setInterval(() => visible.set(!visible.get), 1000)
                |
                |div(
                |  span("Visible: ", bind(visible), " -> "),
                |  showIf(visible)(span("Show/hide").render)
                |)""".stripMargin
    )(GuideStyles),
    new ShowIfDemoComponent,
    h3("Attribute bindings"),
    p(
      "Udash provides extension methods on Scalatags ", i("Attr"), " and ", i("AttrPair"), ". ",
      i("Attr.bind(ReadableProperty[String])"), " synchronises attribute value with the property. ",
      i("AttrPair.attrIf(ReadableProperty[Boolean])"), " adds attribute if the property value is ", i("true"), " and removes it otherwise. "
    ),
    CodeBlock("""val visible: Property[Boolean] = Property[Boolean](true)
                |dom.window.setInterval(() => visible.set(!visible.get), 1000)
                |
                |div(
                |  span("Visible: ", bind(visible), " -> "),
                |  span((style := "display: none;").attrIfNot(visible))("Show/hide")
                |)""".stripMargin
    )(GuideStyles),
    new BindAttributeDemoComponent,
    h3("Validation"),
    CodeBlock(
      """val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
        |integers.addValidator((element: Seq[Int]) => {
        |  val zipped = element.toStream
        |    .slice(0, element.size-1)
        |    .zip(element.toStream.drop(1))
        |  if (zipped.forall { case (x: Int, y: Int) => x <= y } ) Valid
        |  else Invalid("Sequence is not sorted!")
        |})
        |
        |div(
        |  "Integers: ",
        |  span((attr("data-valid") := true).attrIf(
        |    integers.valid.transform(_ == Valid)
        |  ))(repeat(integers)(p => span(s"${p.get}, ").render)), br,
        |  "Is sorted: ",
        |  valid(integers)(
        |    {
        |      case Valid => span(id := "validation-demo-result")("Yes").render
        |      case Invalid(_) => span(id := "validation-demo-result")("No").render
        |    },
        |    progressBuilder = _ => span("Validation in progress...").render,
        |    errorBuilder = _ => span("Validation error...").render
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new BindValidationDemoComponent,
    p(
      "The above example presents usage of validation result binding. On every change of the sequence content, validators are started ",
      "and the result is passed to provided callbacks. It also adds a ", i("data-valid"), " attribute if numbers are sorted."
    ),
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
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendFormsState.url)("Two-way Forms Binding"), " chapter to read about binding properties to HTML forms."
    )
  )
}
