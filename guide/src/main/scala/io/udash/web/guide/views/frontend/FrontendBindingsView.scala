package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.{Context, _}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object FrontendBindingsViewPresenter extends DefaultViewPresenterFactory[FrontendBindingsState.type](() => new FrontendBindingsView)

class FrontendBindingsView extends View {
  import Context._

  import JsDom.all._

  override def getTemplate: dom.Element = div(
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
    ul(GuideStyles.defaultList)(
      li(i("bind"), " - the simplest way to bind a property to a template, it uses the ", i(".toString"), " method to get the string which should be displayed."),
      li(i("produce"), " - similar to ", i("bind"), ", but takes a builder method witch is called on every change of the property - its result is inserted into DOM."),
      li(i("repeat"), " - draws all elements of a ", i("SeqProperty"), " and updates the view on every sequence change."),
      li(i("bindValidation"), " - on every change of the property validates its value and calls the builder with the result."),
      li(i("bindAttribute"), " - on every change of the property runs passed callback witch can modify the DOM element.")
    ),
    h3("bind"),
    CodeBlock(
      """val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
        |val name: Property[String] = Property[String](names.next())
        |
        |dom.window.setInterval(() => name.set(names.next()), 500)
        |
        |val template: Element = div(
        |  "Name: ", bind(name)
        |).render""".stripMargin
    )(GuideStyles),
    new BindDemoComponent,
    p("As you can see the ", i("bind"), " method automatically updates displayed name on every change of the property value."),
    h3("produce"),
    CodeBlock(
      """val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
        |val name: Property[String] = Property[String](names.next())
        |val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
        |
        |dom.window.setInterval(() => {
        |  name.set(names.next())
        |
        |  val s: Int = integers.get.size
        |  val idx = Random.nextInt(s)
        |  val amount = Random.nextInt(s - idx) + 1
        |  val count = Random.nextInt(5)
        |  integers.replace(
        |    idx, amount,
        |    Stream.range(idx, idx + amount * count + 1, amount).toSeq:_*
        |  )
        |}, 500)
        |
        |val template: Element = div(
        |  "Name: ",
        |  produce(name)(value => b(value).render), br,
        |  "Integers: ",
        |  produce(integers)((seq: Seq[Int]) =>
        |    span(seq.map(p => span(s"$p, ")):_*).render
        |  ), br,
        |  "Integers (patching): ",
        |  produce(integers,
        |    (seq: Seq[Property[Int]]) =>
        |      span(seq.map(p => span(id := p.hashCode())(s"${p.get}, ")):_*).render,
        |    (patch: Patch[Property[Int]], el: Element) => {
        |      val insertBefore = jQ(el).children().eq(patch.idx)
        |      patch.added.foreach(p =>
        |        jQ(span(id := p.hashCode())(s"${p.get}, ").render)
        |          .insertBefore(insertBefore)
        |      )
        |      patch.removed.foreach(p => jQ(s"#${p.hashCode()}").remove())
        |    }
        |  )
        |).render""".stripMargin
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
        |dom.window.setInterval(() => {
        |  val s: Int = integers.get.size
        |  val idx = Random.nextInt(s)
        |  val amount = Random.nextInt(s - idx) + 1
        |  val count = Random.nextInt(5)
        |  integers.replace(
        |    idx, amount,
        |    Stream.range(idx, idx + amount * count + 1, amount).toSeq:_*
        |  )
        |}, 500)
        |
        |val template: Element = div(
        |  "Integers: ",
        |  repeat(integers)(p => span(s"${p.get}, ").render)
        |).render""".stripMargin
    )(GuideStyles),
    new RepeatDemoComponent,
    p("This method is similar to the patching version of produce, but it takes care about replacing elements internally."),
    h3("bindValidation"),
    CodeBlock(
      """val integers: SeqProperty[Int] = SeqProperty[Int](1,2,3,4)
        |integers.addValidator(new Validator[Seq[Int]] {
        |  def apply(element: Seq[Int])
        |           (implicit ec: ExecutionContext): Future[ValidationResult] =
        |    Future {
        |      val zipped = element.toStream.slice(0, element.size-1).zip(element.toStream.drop(1))
        |      if (zipped.forall { case (x: Int, y: Int) => x <= y } ) Valid
        |      else Invalid(Seq("Sequence is not sorted!"))
        |    }
        |})
        |
        |dom.window.setInterval(() => {
        |  val s: Int = integers.get.size
        |  val idx = Random.nextInt(s)
        |  val amount = Random.nextInt(s - idx) + 1
        |  val count = Random.nextInt(5)
        |  integers.replace(
        |    idx, amount,
        |    Stream.range(idx, idx + amount * count + 1, amount).toSeq:_*
        |  )
        |}, 1000)
        |
        |val template: Element = div(
        |  "Integers: ",
        |  produce(integers)((seq: Seq[Int]) =>
        |    span(seq.map(p => span(s"$p, ")): _*).render
        |  ), br,
        |  "Is sorted: ",
        |  bindValidation(integers,
        |    _ => span("Validation in progress...").render,
        |    {
        |      case Valid => span("Yes").render
        |      case Invalid(_) => span("No").render
        |    },
        |    _ => span("Validation error...").render
        |  )
        |).render""".stripMargin
    )(GuideStyles),
    new BindValidationDemoComponent,
    p(
      "The above example presents usage of validation result binding. On every change of the sequence content, validators are started ",
      "and the result is passed to provided callbacks. "
    ),
    h3("bindAttribute"),
    CodeBlock("""val visible: Property[Boolean] = Property[Boolean](true)
                |
                |dom.window.setInterval(() => visible.set(!visible.get), 1000)
                |
                |val template: Element = div(
                |  span("Visible: ", bind(visible), " -> "),
                |  span(bindAttribute(visible)((show, el) => {
                |    if (show) el.setAttribute("style", "display: inline;")
                |    else el.setAttribute("style", "display: none;")
                |  }))("Show/hide")
                |).render""".stripMargin
    )(GuideStyles),
    new BindAttributeDemoComponent,
    p("On every change of the property value, passed callback is called and it can change the element attributes."),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendFormsState.url)("Two-way Forms Binding"), " chapter to read about properties bindings to HTML form."
    )
  ).render

  override def renderChild(view: View): Unit = {}
}