package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object FrontendFormsViewPresenter extends DefaultViewPresenterFactory[FrontendFormsState.type](() => new FrontendFormsView)

class FrontendFormsView extends View {
  import io.udash.web.guide.Context._

  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h2("Two-way Form Bindings"),
    p(
      "In the ", a(href := FrontendBindingsState.url)("previous"), " chapter you could read about one way properties to Scalatags templates bindings. ",
      "In this part of the guide you will learn means of binding properties to form elements."
    ),
    p("Let's briefly introduce all bindable form elements:"),
    ul(GuideStyles.defaultList)(
      li(i("Checkbox"), " - a single checkbox bound to ", i("Property[Boolean]"), ""),
      li(i("CheckButtons"), " - a group of checkboxes bound to ", i("SeqProperty[String]"), ""),
      li(i("NumberInput"), " - input accepting only numbers, bound to ", i("Property[String]"), ""),
      li(i("PasswordInput"), " - password input bound to ", i("Property[String]"), ""),
      li(i("RadioButtons"), " - a group of radio buttons bound to ", i("Property[String]"), ""),
      li(i("Select"), " - a select element bound to ", i("Property[String]"), ""),
      li(i("TextArea"), " - multiline input bound to ", i("Property[String]"), ""),
      li(i("TextInput"), " - standard input bound to ", i("Property[String]"), "")
    ),
    h3("TextInput & NumberInput & PasswordInput"),
    p(
      "Let's start with simple input fields. ",
      "The below example presents how easily you can bind your properties to HTML input elements. ", i("TextInput"), " takes ",
      "a property which should be bound to an input and takes care of updating a field and property after every change."
    ),
    CodeBlock(
      """val name: Property[String] = Property("")
        |val password: Property[String] = Property("")
        |val age: Property[Int] = Property(1)
        |
        |form(
        |  div(
        |    "Name: ",
        |    TextInput(name)(placeholder := "Input your name..."),
        |    span(bind(name))
        |  ),
        |  div(
        |    "Password: ",
        |    PasswordInput(password)(placeholder := "Input your password..."),
        |    span(bind(password))
        |  ),
        |  div(
        |    "Age: ",
        |    NumberInput(
        |      age.transform(_.toString, Integer.parseInt)
        |    )(placeholder := "Input your age..."),
        |    span(bind(age))
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new TextInputDemoComponent,
    h3("TextArea"),
    p("Below you can find a similar example, this time with text areas."),
    CodeBlock(
      """val text: Property[String] = Property("")
        |
        |form(
        |  TextArea(text),
        |  TextArea(text),
        |  TextArea(text)
        |)""".stripMargin
    )(GuideStyles),
    new TextAreaDemoComponent,
    h3("Checkbox"),
    p(
      "Below you can find the example of creating a single checkbox. Notice that the third property contains String, so it uses ",
      "property transformation for checkbox binding. "
    ),
    CodeBlock(
      """val propA: Property[Boolean] = Property(true)
        |val propB: Property[Boolean] = Property(false)
        |val propC: Property[String] = Property("Yes")
        |val propCAsBoolean = propC.transform(
        |  (s: String) => if (s.equalsIgnoreCase("yes")) true else false,
        |  (b: Boolean) => if (b) "Yes" else "No"
        |)
        |
        |form(
        |  Checkbox(propA), " A -> ", bind(propA),
        |  Checkbox(propB), " B -> ", bind(propB),
        |  Checkbox(propCAsBoolean), " C -> ", bind(propC)
        |)""".stripMargin
    )(GuideStyles),
    new CheckboxDemoComponent,
    h3("CheckButtons"),
    p(
      "The below example shows how to create a sequence of checkboxes for a provided sequence of possible values and bind them ",
      "with a SeqProperty. The CheckButtons constructor gets ", i("SeqProperty[String]"), ", ", i("Seq[String]"), " with possible values and ",
      "a decorator method. The decorator gets ", i("Seq[(Input, String)]"), ", where the Input generates a checkbox and the String ",
      "is the bound value. This generates a Scalatags template containing the checkboxes."
    ),
    CodeBlock(
      """sealed trait Fruit
        |case object Apple extends Fruit
        |case object Orange extends Fruit
        |case object Banana extends Fruit
        |
        |val favoriteFruits: SeqProperty[Fruit] = SeqProperty[Fruit](Apple, Banana)
        |val favoriteFruitsStrings = favoriteFruits.transform(
        |  (f: Fruit) => f.toString,
        |  (s: String) => s match {
        |    case "Apple" => Apple
        |    case "Orange" => Orange
        |    case "Banana" => Banana
        |  }
        |)
        |
        |form(
        |  CheckButtons(
        |    favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString),
        |    (els: Seq[(Input, String)]) => span(
        |      els.map { case (i: Input, l: String) => label(i, l) }
        |    )
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new CheckButtonsDemoComponent,
    h3("RadioButtons"),
    p(
      "RadioButtons work very similarly to CheckButtons. The only difference is that they work with a ", i("Property"), ", ",
      "not with a ", i("SeqProperty"), ", so only one value can be selected. "
    ),
    CodeBlock(
      """sealed trait Fruit
        |case object Apple extends Fruit
        |case object Orange extends Fruit
        |case object Banana extends Fruit
        |
        |val favoriteFruit: Property[Fruit] = Property[Fruit](Apple)
        |val favoriteFruitString = favoriteFruit.transform(
        |  (f: Fruit) => f.toString,
        |  (s: String) => s match {
        |    case "Apple" => Apple
        |    case "Orange" => Orange
        |    case "Banana" => Banana
        |  }
        |)
        |
        |form(
        |  RadioButtons(
        |    favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString),
        |    (els: Seq[(Input, String)]) => span(
        |      els.map { case (i: Input, l: String) => label(i, l) }
        |    )
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new RadioButtonsDemoComponent,
    h3("Select"),
    p("The HTML select element might be used in two ways: with or without multi selection. Below you can find examples of both usages."),
    CodeBlock(
      """sealed trait Fruit
        |case object Apple extends Fruit
        |case object Orange extends Fruit
        |case object Banana extends Fruit
        |
        |val favoriteFruit: Property[Fruit] = Property[Fruit](Apple)
        |val favoriteFruitString = favoriteFruit.transform(
        |  (f: Fruit) => f.toString,
        |  (s: String) => s match {
        |    case "Apple" => Apple
        |    case "Orange" => Orange
        |    case "Banana" => Banana
        |  }
        |)
        |
        |form(
        |  Select(
        |    favoriteFruitString, Seq(Apple, Orange, Banana).map(_.toString)
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new SelectDemoComponent,
    h4("Select with multiple selected values"),
    p("Notice that the only difference is the type of the used property."),
    CodeBlock(
      """sealed trait Fruit
        |case object Apple extends Fruit
        |case object Orange extends Fruit
        |case object Banana extends Fruit
        |
        |val favoriteFruits: SeqProperty[Fruit] = SeqProperty[Fruit](Apple, Banana)
        |val favoriteFruitsStrings = favoriteFruits.transform(
        |  (f: Fruit) => f.toString,
        |  (s: String) => s match {
        |    case "Apple" => Apple
        |    case "Orange" => Orange
        |    case "Banana" => Banana
        |  }
        |)
        |
        |form(
        |  Select(
        |    favoriteFruitsStrings, Seq(Apple, Orange, Banana).map(_.toString))
        |  )
        |)""".stripMargin
    )(GuideStyles),
    new MultiSelectDemoComponent,
    h2("What's next?"),
    p(
      "Now you know everything you need to start frontend development using Udash. ",
      "If you want to learn more about client-server communication, check the ",
      a(href := RpcIntroState.url)("RPC"), " chapter."
    )
  ).render

  override def renderChild(view: View): Unit = {}
}