package io.udash.bootstrap
package form

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import org.scalajs.dom
import org.scalajs.dom._

import scala.util.{Failure, Success}
import scalatags.JsDom.all._

class UdashForm private(formStyle: Option[BootstrapStyles.BootstrapClass], override val componentId: ComponentId)
                       (content: Modifier*) extends UdashBootstrapComponent {
  override lazy val render =
    form(if (formStyle.isDefined) formStyle.get else ())(
      content
    ).render
}

object UdashForm {
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  /** Binds provided `property` validation result to element Bootstrap validation style. */
  def validation(property: Property[_]): Modifier =
    property.reactiveApply((el, _) => {
      import BootstrapStyles.Form._
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
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def apply(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(None, componentId)(content)

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
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def inline(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formInline), componentId)(content)

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
    * @param content Form content
    * @return `UdashForm` component, call render to create DOM element.
    */
  def horizontal(componentId: ComponentId, content: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formHorizontal), componentId)(content)

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
      label(`for` := inputId)(labelContent),
      UdashForm.input(input),
      validation
    )

  /** Creates text input group. */
  def textInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)(labelContent: Modifier*)
               (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextInput.debounced(property, id := inputId, input).render)

  /** Creates password input group. */
  def passwordInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)(labelContent: Modifier*)
                   (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(PasswordInput.debounced(property, id := inputId, input).render)

  /** Creates number input group. */
  def numberInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)(labelContent: Modifier*)
                 (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(NumberInput.debounced(property, id := inputId, input).render)

  /** Creates text area input group. */
  def textArea(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)(labelContent: Modifier*)
              (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextArea.debounced(property, id := inputId, input).render)

  /** Creates file input input group. */
  def fileInput(inputId: ComponentId = UdashBootstrap.newId(), validation: Option[Modifier] = None)(labelContent: Modifier*)
               (name: String, acceptMultipleFiles: Property[Boolean], selectedFiles: SeqProperty[File], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(FileInput(name, acceptMultipleFiles, selectedFiles)((id := inputId) +: input))

  /** Creates checkbox. */
  def checkbox(validation: Option[Modifier] = None)(labelContent: Modifier*)(property: Property[Boolean], input: Modifier*): Modifier =
    div(BootstrapStyles.Form.checkbox)(
      label(
        Checkbox(property, input).render
      )(labelContent),
      validation
    )

  private def defaultDecorator(checkboxStyle: BootstrapStyles.BootstrapClass) =
    (input: dom.html.Input, id: String) => label(checkboxStyle)(input, id).render

  /** Creates checkboxes for provided elements. `selected` property contains values from selected checkboxes. */
  def checkboxes(checkboxStyle: BootstrapStyles.BootstrapClass = BootstrapStyles.Form.checkbox)
                (selected: SeqProperty[String], options: Seq[String],
                 decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(checkboxStyle)): Modifier =
    CheckButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup)(
        items.map {
          case (input, id) => decorator(input, id)
        }
      )
    )

  /** Creates checkboxes for provided `options`. `selected` property has the value of selected radio button. */
  def radio(radioStyle: BootstrapStyles.BootstrapClass = BootstrapStyles.Form.radio)
           (selected: Property[String], options: Seq[String],
            decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(radioStyle)): Modifier =
    RadioButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup)(
        items.map {
          case (input, id) => decorator(input, id)
        }
      )
    )

  /** Creates selection input for provided `options`. `selected` property has the value of selected item. */
  def select(selected: Property[String], options: Seq[String], label: String => Modifier = Select.defaultLabel): Modifier =
    Select(selected, options, label)(BootstrapStyles.Form.formControl)

  /** Creates multiple selection input for provided `options`. `selected` property contains values of selected items. */
  def multiselect(selected: SeqProperty[String], options: Seq[String], label: String => Modifier = Select.defaultLabel): Modifier =
    Select(selected, options, label)(BootstrapStyles.Form.formControl)

  /** Creates static control element. */
  def staticControl(content: Modifier*): Modifier =
    p(BootstrapStyles.Form.formControlStatic)(content)

  /** Wrapper for disabled elements.
    *
    * @param disabled Property indicating if elements are disabled. You can change it anytime.
    */
  def disabled(disabled: Property[Boolean] = Property(true))(content: Modifier*): Modifier =
    fieldset((scalatags.JsDom.attrs.disabled := "disabled").attrIf(disabled))(content)
}