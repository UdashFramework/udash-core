package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.{Context, _}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object FrontendBindingsViewPresenter extends DefaultViewPresenterFactory[FrontendBindingsState.type](() => new FrontendBindingsView)

class FrontendBindingsView extends FinalView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Property Bindings"),
    p(
      "As every modern frontend framework Udash provides model-view template bindings. ",
      "What really distinguishes Udash from other frameworks is the fact that it is type safe."
    ),
    p("Udash provides many ways to bind properties to Scalatags templates. To use them you have to add this import in your code:"),
    CodeBlock(
      """import io.udash._""".stripMargin
    )(GuideStyles),
    p("Let's briefly introduce all these methods:"),
    ul(GuideStyles.get.defaultList)(
      li(i("bind"), " - the simplest way to bind a property to a template, it uses the ", i(".toString"), " method to get the string which should be displayed."),
      li(i("produce"), " - similar to ", i("bind"), ", but takes a builder method which is called on every change of the property - its result is inserted into DOM."),
      li(i("repeat"), " - draws all elements of a ", i("SeqProperty"), " and updates the view on every sequence change."),
      li(i("showIf"), " - shows and hides elements depending on provided property value."),
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
        |  ), br,
        |  "Integers (patching): ",
        |  produce(integers,
        |    (seq: Seq[Property[Int]]) =>
        |      seq.map(p => span(id := p.hashCode())(s"${p.get}, ").render),
        |    (patch: Patch[Property[Int]], el: Seq[Element]) => {
        |      val insertBefore = jQ(el:_*).children().at(patch.idx)
        |      patch.added.foreach(p =>
        |        jQ(span(id := p.hashCode())(s"${p.get}, ").render)
        |          .insertBefore(insertBefore)
        |      )
        |      patch.removed.foreach(p => jQ(s"#${p.hashCode()}").remove())
        |    }
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new ProduceDemoComponent,
    p(
      "The above example presents three variants of the ", i("produce"), " method. This is very similar to the ", i("bind"),
      " method, but you can provide a custom DOM element builder. Notice that the first version of ", i("produce"), " for ", i("SeqProperty"),  ", ",
      "redraws the whole sequence every time - it is ok when the sequence is small. The second version updates only changed ",
      "elements of the sequence. To make it easier to notice, every added element is highlighted."
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
    p("This method is similar to the patching version of produce, but it takes care about replacing elements internally."),
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
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendFormsState.url)("Two-way Forms Binding"), " chapter to read about properties bindings to HTML form."
    )
  )
}
