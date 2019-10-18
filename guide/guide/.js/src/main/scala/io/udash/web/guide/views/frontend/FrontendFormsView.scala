package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.ForceBootstrap
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.frontend.demos._
import scalatags.JsDom

case object FrontendFormsViewFactory extends StaticViewFactory[FrontendFormsState.type](() => new FrontendFormsView)

class FrontendFormsView extends View with CssView {
  import JsDom.all._
  import io.udash.web.guide.Context._

  private val (textInputDemo, textInputSnippet) = TextInputDemo.demoWithSnippet()
  private val (textAreaDemo, textAreaSnippet) = TextAreaDemo.demoWithSnippet()
  private val (checkboxDemo, checkboxSnippet) = CheckboxDemo.demoWithSnippet()
  private val (checkButtonsDemo, checkButtonsSnippet) = CheckButtonsDemo.demoWithSnippet()
  private val (radioButtonsDemo, radioButtonsSnippet) = RadioButtonsDemo.demoWithSnippet()
  private val (selectDemo, selectSnippet) = SelectDemo.demoWithSnippet()
  private val (multiSelectDemo, multiSelectSnippet) = MultiSelectDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h2("Two-way Form Bindings"),
    p(
      "In the ", a(href := FrontendBindingsState.url)("previous"), " chapter you could read about one way properties to Scalatags templates bindings. ",
      "In this part of the guide you will learn means of binding properties to form elements."
    ),
    p("Let's briefly introduce all bindable form elements:"),
    ul(GuideStyles.defaultList)(
      li(i("Checkbox"), " - a single checkbox bound to ", i("Property[Boolean]"), "."),
      li(i("CheckButtons"), " - a group of checkboxes bound to ", i("SeqProperty[T]"), "."),
      li(i("NumberInput"), " - input accepting only numbers, bound to ", i("Property[String]"), "."),
      li(i("PasswordInput"), " - password input bound to ", i("Property[String]"), "."),
      li(i("RadioButtons"), " - a group of radio buttons bound to ", i("Property[T]"), "."),
      li(i("Select"), " - a select element bound to ", i("Property[T]"), "."),
      li(i("TextArea"), " - multiline input bound to ", i("Property[String]"), "."),
      li(i("TextInput"), " - standard input bound to ", i("Property[String]"), ".")
    ),
    h3("TextInput & NumberInput & PasswordInput"),
    p(
      "Let's start with simple input fields. ",
      "The below example presents how easily you can bind your properties to HTML input elements. ", i("TextInput"), " takes ",
      "a property which should be bound to an input and takes care of updating a field and property after every change."
    ),
    textInputSnippet,
    ForceBootstrap(textInputDemo),
    h3("TextArea"),
    p("Below you can find a similar example, this time with text areas."),
    textAreaSnippet,
    ForceBootstrap(textAreaDemo),
    h3("Checkbox"),
    p(
      "Below you can find the example of creating a single checkbox. Notice that the third property contains String, so it uses ",
      "property transformation for checkbox binding. "
    ),
    checkboxSnippet,
    ForceBootstrap(checkboxDemo),
    h3("CheckButtons"),
    p(
      "The below example shows how to create a sequence of checkboxes for a provided sequence of possible values and bind them ",
      "with a SeqProperty. The CheckButtons constructor gets ", i("SeqProperty[String]"), ", ", i("Seq[String]"), " with possible values and ",
      "a decorator method. The decorator gets ", i("Seq[(Input, String)]"), ", where the Input generates a checkbox and the String ",
      "is the bound value. This generates a Scalatags template containing the checkboxes."
    ),
    checkButtonsSnippet,
    ForceBootstrap(checkButtonsDemo),
    h3("RadioButtons"),
    p(
      "RadioButtons work very similarly to CheckButtons. The only difference is that they work with a ", i("Property"), ", ",
      "not with a ", i("SeqProperty"), ", so only one value can be selected. "
    ),
    radioButtonsSnippet,
    ForceBootstrap(radioButtonsDemo),
    h3("Select"),
    p("The HTML select element might be used in two ways: with or without multi selection. Below you can find examples of both usages."),
    selectSnippet,
    ForceBootstrap(selectDemo),
    h4("Select with multiple selected values"),
    p("Notice that the only difference is the type of the used property."),
    multiSelectSnippet,
    ForceBootstrap(multiSelectDemo),
    h2("What's next?"),
    p(
      "Now you know everything you need to start frontend development using Udash. ",
      "If you want to learn more about client-server communication, check the ",
      a(href := RpcIntroState.url)("RPC"), " chapter. ",
      "You might  find ", a(href := BootstrapExtState.url)("Bootstrap Components"), " and ",
      a(href := FrontendFilesState.url)("File upload"), " interesting later on."
    )
  )
}