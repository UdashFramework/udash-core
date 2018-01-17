package io.udash.bootstrap
package form

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.css.CssStyle
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.Event

import scala.util.{Failure, Success}
import scalatags.JsDom.all._

final class UdashForm private(formStyle: Option[CssStyle], override val componentId: ComponentId)
                             (content: Modifier*) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render =
    form(if (formStyle.isDefined) formStyle.get else ())(
      content
    ).render
}

object UdashForm {
  import io.udash.css.CssView._

  /** Binds provided `property` validation result to element Bootstrap validation style. */
  def validation(property: ReadableProperty[_]): Modifier =
    property.reactiveApply((el, _) => {
      import BootstrapStyles.Form._
      import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

      Seq(hasSuccess, hasError, hasWarning).foreach(_.removeFrom(el))

      property.isValid onComplete {
        case Success(Valid) =>
          hasSuccess.addTo(el)
        case Success(Invalid(_)) =>
          hasError.addTo(el)
        case Failure(ex) =>
          hasWarning.addTo(el)
      }
    })

  /**
    * Creates standard form with provided content. <br/>
    * Example: `UdashForm(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms">Bootstrap Docs</a>.
    *
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def apply(content: Modifier*): UdashForm =
    new UdashForm(None, UdashBootstrap.newId())(content)

  /**
    * Creates standard form with provided content. <br/>
    * Example: `UdashForm(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param onSubmit Callback executed when a form is submitted
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def apply(onSubmit: Event => Any)(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(None, componentId)(content, onsubmit :+= { (ev: Event) => onSubmit(ev); true })

  /**
    * Creates inline form with provided content. <br/>
    * Example: `UdashForm.inline(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms-inline">Bootstrap Docs</a>.
    *
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def inline(content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formInline), UdashBootstrap.newId())(content)

  /**
    * Creates inline form with provided content. <br/>
    * Example: `UdashForm.inline(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms-inline">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param onSubmit Callback executed when a form is submitted
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def inline(onSubmit: Event => Any)(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formInline), componentId)(content, onsubmit :+= { (ev: Event) => onSubmit(ev); true })

  /**
    * Creates horizontal form with provided content. <br/>
    * Example: `UdashForm.horizontal(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms-horizontal">Bootstrap Docs</a>.
    *
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def horizontal(content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formHorizontal), UdashBootstrap.newId())(content)

  /**
    * Creates horizontal form with provided content. <br/>
    * Example: `UdashForm.horizontal(UdashForm.textInput()("User name")(user.subProp(_.name))).render` <br/>
    * More: <a href="http://getbootstrap.com/css/#forms-horizontal">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param onSubmit Callback executed when a form is submitted
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def horizontal(onSubmit: Event => Any)(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formHorizontal), componentId)(content, onsubmit :+= { (ev: Event) => onSubmit(ev); true })

  /** Creates from group with provided content. You can put it into `UdashForm`. <br/>
    * Example: `UdashForm(UdashForm.group(...)).render` */
  def group(content: Modifier*): Modifier =
    div(BootstrapStyles.Form.formGroup)(content)

  /** Wrapper for inputs in form. */
  def input(el: dom.Element): dom.Element = {
    BootstrapStyles.Form.formControl.addTo(el)
    el
  }

  private def inputGroup(inputId: ComponentId, validation: Option[Modifier])
                        (labelContent: Modifier*)(input: dom.Element): Modifier =
    group(
      if (labelContent.nonEmpty) label(`for` := inputId)(labelContent) else (),
      UdashForm.input(input),
      validation
    )

  /**
    * Creates text input group.
    *
    * @param inputId        Id of the input DOM element.
    * @param validation     Modifier applied to the created form group.
    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent   The content of a label. If empty, the `label` won't be created.
    * @param property       Property which will be synchronised with the input content.
    * @param inputModifiers Modifiers applied directly to the `input` element.
    */
  def textInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)
               (labelContent: Modifier*)
               (property: Property[String], inputModifiers: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextInput.debounced(property, id := inputId, inputModifiers).render)

  /**
    * Creates password input group.
    *
    * @param inputId        Id of the input DOM element.
    * @param validation     Modifier applied to the created form group.
    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent   The content of a label. If empty, the `label` won't be created.
    * @param property       Property which will be synchronised with the input content.
    * @param inputModifiers Modifiers applied directly to the `input` element.
    */
  def passwordInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)
                   (labelContent: Modifier*)
                   (property: Property[String], inputModifiers: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(PasswordInput.debounced(property, id := inputId, inputModifiers).render)

  /**
    * Creates number input group.
    *
    * @param inputId        Id of the input DOM element.
    * @param validation     Modifier applied to the created form group.
    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent   The content of a label. If empty, the `label` won't be created.
    * @param property       Property which will be synchronised with the input content.
    * @param inputModifiers Modifiers applied directly to the `input` element.
    */
  def numberInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)
                 (labelContent: Modifier*)
                 (property: Property[String], inputModifiers: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(NumberInput.debounced(property, id := inputId, inputModifiers).render)

  /**
    * Creates text area input group.
    *
    * @param inputId        Id of the input DOM element.
    * @param validation     Modifier applied to the created form group.
    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent   The content of a label. If empty, the `label` won't be created.
    * @param property       Property which will be synchronised with the input content.
    * @param inputModifiers Modifiers applied directly to the `input` element.
    */
  def textArea(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)
              (labelContent: Modifier*)
              (property: Property[String], inputModifiers: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextArea.debounced(property, id := inputId, inputModifiers).render)

  /**
    * Creates file input group.
    *
    * @param inputId             Id of the input DOM element.
    * @param validation          Modifier applied to the created form group.
    *                            Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent        The content of a label. If empty, the `label` won't be created.
    * @param name                Name of the input. This value will be assigned to the `name` attribute of the input.
    * @param acceptMultipleFiles If true, input will accept multiple files.
    * @param selectedFiles       Property which will be synchronised with the input content.
    * @param inputModifiers      Modifiers applied directly to the `input` element.
    */
  def fileInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)
               (labelContent: Modifier*)
               (name: String, acceptMultipleFiles: ReadableProperty[Boolean],
                selectedFiles: SeqProperty[File], inputModifiers: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(
      FileInput(name, acceptMultipleFiles, selectedFiles)(id := inputId, inputModifiers)
    )

  /**
    * Creates checkbox.
    *
    * @param inputId        Id of the input DOM element.
    * @param validation     Modifier applied to the created form group.
    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
    * @param labelContent   The content of a label. If empty, the `label` won't be created.
    * @param property       Property which will be synchronised with the input content.
    * @param inputModifiers Modifiers applied directly to the `input` element.
    */
  def checkbox(validation: Option[Modifier] = None, inputId: ComponentId = UdashBootstrap.newId())
              (labelContent: Modifier*)(property: Property[Boolean], inputModifiers: Modifier*): Modifier =
    div(BootstrapStyles.Form.checkbox)(
      label(
        Checkbox(property, id := inputId, inputModifiers).render
      )(labelContent),
      validation
    )

  private def defaultDecorator(checkboxStyle: CssStyle) =
    (input: dom.html.Input, id: String) => label(checkboxStyle)(input, id).render

  /**
    * Creates checkboxes for provided options.
    *
    * @param checkboxStyle  Style applied to each checkbox by the default decorator.
    * @param groupId        Id of created form group.
    * @param selected       Property which will be synchronised with the selected elements.
    * @param options        List of possible values. Each options has one checkbox.
    * @param decorator      This methods allows you to customize DOM structure around each checkbox.
    *                       By default it creates a `label` around input with option value as its content.
    */
  def checkboxes(checkboxStyle: CssStyle = BootstrapStyles.Form.checkbox, groupId: ComponentId = UdashBootstrap.newId())
                (selected: SeqProperty[String], options: Seq[String],
                 decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(checkboxStyle)): Modifier =
    CheckButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup, id := groupId.id)(
        items.map {
          case (input, id) => decorator(input, id)
        }
      )
    )

  /**
    * Creates radio buttons for provided options.
    *
    * @param radioStyle Style applied to each radio button by the default decorator.
    * @param groupId    Id of created form group.
    * @param selected   Property which will be synchronised with the selected elements.
    * @param options    List of possible values. Each options has one checkbox.
    * @param decorator  This methods allows you to customize DOM structure around each checkbox.
    *                   By default it creates a `label` around input with option value as its content.
    */
  def radio(radioStyle: CssStyle = BootstrapStyles.Form.radio, groupId: ComponentId = UdashBootstrap.newId())
           (selected: Property[String], options: Seq[String],
            decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(radioStyle)): Modifier =
    RadioButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup, id := groupId.id)(
        items.map {
          case (input, id) => decorator(input, id)
        }
      )
    )

  /**
    * Creates selection input for provided `options`.
    *
    * @param selected Property which will be synchronised with the selected element.
    * @param options  List of possible values. Each options has one checkbox.
    * @param label    This methods allows you to customize label of each option.
    *                 By default it creates a `label` around input with option value as its content.
    * @param inputId  Id of the select DOM element.
    */
  def select(selected: Property[String], options: Seq[String],
             label: String => Modifier = Select.defaultLabel,
             inputId: ComponentId = UdashBootstrap.newId()): Modifier =
    Select(selected, options, label)(BootstrapStyles.Form.formControl, id := inputId.id)

  /**
    * Creates multiple selection input for provided `options`.
    *
    * @param selected Property which will be synchronised with the selected elements.
    * @param options  List of possible values. Each options has one checkbox.
    * @param label    This methods allows you to customize label of each option.
    *                 By default it creates a `label` around input with option value as its content.
    * @param inputId  Id of the select DOM element.
    */
  def multiselect(selected: SeqProperty[String], options: Seq[String],
                  label: String => Modifier = Select.defaultLabel,
                  inputId: ComponentId = UdashBootstrap.newId()): Modifier =
    Select(selected, options, label)(BootstrapStyles.Form.formControl, id := inputId.id)

  /** Creates static control element. */
  def staticControl(content: Modifier*): Modifier =
    p(BootstrapStyles.Form.formControlStatic)(content)

  /**
    * Wrapper for disabled elements.
    * @param disabled Property indicating if elements are disabled. You can change it anytime.
    */
  def disabled(disabled: ReadableProperty[Boolean] = Property(true))(content: Modifier*): Modifier =
    fieldset((scalatags.JsDom.attrs.disabled := "disabled").attrIf(disabled))(content)
}