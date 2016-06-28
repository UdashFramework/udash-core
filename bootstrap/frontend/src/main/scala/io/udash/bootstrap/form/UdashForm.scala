package io.udash.bootstrap
package form

import io.udash._
import org.scalajs.dom

import scala.util.{Failure, Success}
import scalatags.JsDom.all._

class UdashForm(formStyle: Option[BootstrapStyles.BootstrapClass])(mds: Modifier*) extends UdashBootstrapComponent {
  override lazy val render =
    form(if (formStyle.isDefined) formStyle.get else ())(mds).render
}

object UdashForm {
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def validation(property: Property[_]): Modifier =
    bindAttribute(property)((_, el) => {
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

  def apply(mds: Modifier*): UdashForm =
    new UdashForm(None)(mds)

  def inline(mds: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formInline))(mds)

  def horizontal(mds: Modifier*): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.formHorizontal))(mds)

  def group(mds: Modifier*): Modifier =
    div(BootstrapStyles.Form.formGroup)(mds)

  def input(el: dom.Element): dom.Element = {
    BootstrapStyles.Form.formControl.addTo(el)
    el
  }

  private def inputGroup(inputId: String, validation: Option[Modifier])
                        (labelContent: Modifier*)(input: dom.Element): Modifier =
    group(
      label(`for` := inputId)(labelContent),
      UdashForm.input(input),
      validation
    )

  def textInput(inputId: String = UdashBootstrap.newId().id, validation: Option[Modifier] = None)(labelContent: Modifier*)
               (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextInput.debounced(property, id := inputId, input).render)

  def passwordInput(inputId: String = UdashBootstrap.newId().id, validation: Option[Modifier] = None)(labelContent: Modifier*)
                   (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(PasswordInput.debounced(property, id := inputId, input).render)

  def numberInput(inputId: String = UdashBootstrap.newId().id, validation: Option[Modifier] = None)(labelContent: Modifier*)
                 (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(NumberInput.debounced(property, id := inputId, input).render)

  def textArea(inputId: String = UdashBootstrap.newId().id, validation: Option[Modifier] = None)(labelContent: Modifier*)
              (property: Property[String], input: Modifier*): Modifier =
    inputGroup(inputId, validation)(labelContent)(TextArea.debounced(property, id := inputId, input).render)

  def checkbox(validation: Option[Modifier] = None)(labelContent: Modifier*)(property: Property[Boolean], input: Modifier*): Modifier =
    div(BootstrapStyles.Form.checkbox)(
      label(
        UdashForm.input(Checkbox(property, input).render)
      )(labelContent),
      validation
    )

  def checkboxes(validation: Option[Modifier] = None)(selected: SeqProperty[String], options: Seq[String],
                                                      checkboxStyle: BootstrapStyles.BootstrapClass = BootstrapStyles.Form.checkbox): Modifier =
    CheckButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup)(
        items.map(item => {
          val (input, desc) = item
          label(checkboxStyle)(input, desc)
        }):_*
      )
    )

  def radio(selected: Property[String], options: Seq[String],
            radioStyle: BootstrapStyles.BootstrapClass = BootstrapStyles.Form.radio): Modifier =
    RadioButtons(
      selected, options,
      items => div(BootstrapStyles.Form.formGroup)(
        items.map(item => {
          val (input, desc) = item
          label(radioStyle)(input, desc)
        }):_*
      )
    )

  def select(selected: Property[String], options: Seq[String]): Modifier =
    Select(selected, options, BootstrapStyles.Form.formControl)

  def multiselect(selected: SeqProperty[String], options: Seq[String]): Modifier =
    Select(selected, options, BootstrapStyles.Form.formControl)

  def staticControl(mds: Modifier*): Modifier =
    p(BootstrapStyles.Form.formControlStatic)(mds)

  def disabled(disabled: Property[Boolean] = Property(true))(mds: Modifier*): Modifier =
    fieldset(bindAttribute(disabled)((v, el) => {
      if (v) el.setAttribute("disabled", "disabled")
      else el.removeAttribute("disabled")
    }))(mds)
}