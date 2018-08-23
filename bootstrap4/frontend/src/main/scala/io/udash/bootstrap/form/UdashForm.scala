package io.udash.bootstrap
package form

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.inputs.InputBinding
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.form.UdashForm.ValidationTrigger
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.bootstrap.utils._
import io.udash.css.CssStyle
import io.udash.logging.CrossLogging
import org.scalajs.dom._
import org.scalajs.dom.html.Form
import org.scalajs.dom.raw.Event
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationLong}
import scala.util.{Failure, Success}

final class UdashForm private(
  formStyle: Option[CssStyle],
  override val componentId: ComponentId
)(content: UdashForm#FormElementsFactory => Modifier)
  extends UdashBootstrapComponent with Listenable[UdashForm, UdashForm.Event] with CrossLogging {

  import io.udash.css.CssView._

  class FormElementsFactory {
    /** Use this method to bond the external binging's lifecycle with the lifecycle of this form. */
    def externalBinding[T <: Binding](binding: T): T = UdashForm.this.nestedInterceptor(binding)

    object input {
      /**
        * Creates a standard form group for the provided input. It contains a label and validation feedback elements.
        * The group layout can be organized vertically or horizontally based on the provided options.
        * You can wrap the result into grid elements in order to create more complex form layout.
        *
        * @param horizontal      Optional settings for a horizontal layout. If `None`, the layout will be organized vertically.
        * @param inputId         Id of the `input` form control. This value is used to properly set `for` attribute of the label.
        * @param groupId         Id of the root element.
        * @param input           The input element. IT can be wrapped into `UdashInputGroup` or any other decoration.
        * @param labelContent    Optional label content.
        *                        It will be wrapped into `label` element with properly set `for` attribute.
        * @param validFeedback   Optional content of positive validation feedback.
        *                        It will be wrapped into `div` element with `valid-feedback` style.
        * @param invalidFeedback Optional content of negative validation feedback.
        *                        It will be wrapped into `div` element with `invalid-feedback` style.
        */
      def formGroup(
        horizontal: Option[(Int, Int, ResponsiveBreakpoint)] = None, // TODO horizontal options case class
        inputId: ComponentId = ComponentId.newId(),
        groupId: ComponentId = ComponentId.newId()
      )(
        input: Binding.NestedInterceptor => Modifier,
        labelContent: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        validFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        invalidFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          override val render: Element = horizontal match {
            case None =>
              div(BootstrapStyles.Form.group, id := groupId)(
                labelContent(nestedInterceptor).map(label(`for` := inputId)(_)),
                input(nestedInterceptor),
                validFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.validFeedback)(_)),
                invalidFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.invalidFeedback)(_))
              ).render
            case Some((labelWidth, inputWidth, breakpoint)) =>
              div(BootstrapStyles.Form.group, BootstrapStyles.Grid.row, id := groupId)(
                div(BootstrapStyles.Grid.col(labelWidth, breakpoint))(
                  labelContent(nestedInterceptor).map(label(`for` := inputId)(_))
                ),
                div(BootstrapStyles.Grid.col(inputWidth, breakpoint))(
                  input(nestedInterceptor),
                  validFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.validFeedback)(_)),
                  invalidFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.invalidFeedback)(_))
                ),
              ).render
          }

          override val componentId: ComponentId = groupId
        })
      }

      /**
        * Creates a text input with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param property          Property which will be synchronised with the input content and validated.
        * @param debounce          Property update timeout after input changes.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        */
      def textInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        validationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(nestedInterceptor),
              validationModifier(property, validationTrigger, nestedInterceptor)
            ), inputId
          )
        )
      }

      /**
        * Creates a password input with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param property          Property which will be synchronised with the input content and validated.
        * @param debounce          Property update timeout after input changes.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        */
      def passwordInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        validationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            PasswordInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(nestedInterceptor),
              validationModifier(property, validationTrigger, nestedInterceptor)
            ), inputId
          )
        )
      }

      /**
        * Creates a number input with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param property          Property which will be synchronised with the input content and validated.
        * @param debounce          Property update timeout after input changes.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        */
      def numberInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        validationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            NumberInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(nestedInterceptor),
              validationModifier(property, validationTrigger, nestedInterceptor)
            ), inputId
          )
        )
      }

      /**
        * Creates a text area with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param property          Property which will be synchronised with the input content and validated.
        * @param debounce          Property update timeout after input changes.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        */
      def textArea(
        property: Property[String],
        debounce: Duration = 20 millis,
        validationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextArea(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(nestedInterceptor),
              validationModifier(property, validationTrigger, nestedInterceptor)
            ), inputId
          )
        )
      }

      private def validationModifier(
        property: ReadableProperty[_], validationTrigger: ValidationTrigger, nested: Binding.NestedInterceptor
      ): Modifier = {
        validationTrigger match {
          case ValidationTrigger.None => Seq.empty[Modifier]
          case ValidationTrigger.Instant => Seq(
            // TODO valid does not clear listener on the original property when it's no longer needed
            nested(BootstrapStyles.Form.isValid.styleIf(property.valid.transform(_ == Valid))),
            nested(BootstrapStyles.Form.isInvalid.styleIf(property.valid.transform(_ != Valid))),
          )
          case ValidationTrigger.OnBlur =>
            val validationResult = Property[Option[ValidationResult]](None)
            Seq(
              nested(BootstrapStyles.Form.isValid.styleIf(validationResult.transform(_.contains(Valid)))),
              nested(BootstrapStyles.Form.isInvalid.styleIf(validationResult.transform(v => v.isDefined && !v.contains(Valid)))),
              onblur :+= { _: Event =>
                validationResult.set(None)
                property.isValid onComplete {
                  case Success(r) => validationResult.set(Some(r))
                  case Failure(ex) =>
                    logger.error("Validation failed.", ex)
                    validationResult.set(None)
                }
                false
              }
            )
          case ValidationTrigger.OnSubmit =>
            val validationResult = Property[Option[ValidationResult]](None)
            Seq(
              nested(BootstrapStyles.Form.isValid.styleIf(validationResult.transform(_.contains(Valid)))),
              nested(BootstrapStyles.Form.isInvalid.styleIf(validationResult.transform(v => v.isDefined && !v.contains(Valid)))),
              nested(new Binding {
                override def applyTo(t: Element): Unit = {
                  propertyListeners += listen {
                    case ev: UdashForm.Event if ev.tpe == UdashForm.Event.EventType.Submit =>
                      validationResult.set(None)
                      property.isValid onComplete {
                        case Success(r) => validationResult.set(Some(r))
                        case Failure(ex) =>
                          logger.error("Validation failed.", ex)
                          validationResult.set(None)
                      }
                  }
                }
              })
            )
        }
      }

      private class InputComponent(in: InputBinding[_ <: Element], inputId: ComponentId) extends UdashBootstrapComponent {
        private val input: InputBinding[_ <: Element] = nestedInterceptor(in)
        override val componentId: ComponentId = inputId
        override val render: Element = input.render
      }
    }

    object grid {
      def row(content: Modifier*): Modifier =
        div(BootstrapStyles.Grid.row)(content)

      def formRow(content: Modifier*): Modifier =
        div(BootstrapStyles.Grid.formRow)(content)

      def col(content: Modifier*): Modifier =
        div(BootstrapStyles.Grid.col)(content)

      def col(size: Int, breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All)(content: Modifier*): Modifier =
        div(BootstrapStyles.Grid.col(size, breakpoint))(content)
    }
  }

  override val render: Form =
    form(formStyle)(
      onsubmit :+= { _: Event => fire(new UdashForm.Event(this, UdashForm.Event.EventType.Submit)); true },
      content(new FormElementsFactory)
    ).render
}

object UdashForm {
  final class Event(override val source: UdashForm, val tpe: Event.EventType) extends ListenableEvent[UdashForm]
  object Event {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      final val Submit: Value = new EventType
    }
  }

  final class ValidationTrigger(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ValidationTrigger extends AbstractValueEnumCompanion[ValidationTrigger] {
    // TODO describe these options
    final val None, Instant, OnBlur, OnSubmit: Value = new ValidationTrigger
  }

  // TODO verify these examples
  /**
    * Creates standard form with provided content. <br/>
    * Example: <br/>
    * <pre>
    * UdashForm(
    *   UdashForm.textInput()("Email")(user.subProp(_.email)),
    *   UdashForm.passwordInput()("Password")(user.subProp(_.password))
    * ).render
    * </pre>
    * Grid example: <br/>
    * <pre>
    * UdashForm(
    *   UdashForm.Grid.formRow(
    *     UdashForm.Grid.col(UdashForm.textInput()("Email")(user.subProp(_.email))),
    *     UdashForm.Grid.col(UdashForm.passwordInput()("Password")(user.subProp(_.password)))
    *   )
    * ).render
    * </pre>
    * More: <a href="http://getbootstrap.com/css/#forms">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content     Form content - usually you should pass controls (automatically wrapped into form groups) here.
    *                    You can also use the grid system to prepare more complex layout.
    * @return `UdashForm` component, call render to create DOM element.
    */
  def apply(componentId: ComponentId = ComponentId.newId())(content: UdashForm#FormElementsFactory => Modifier): UdashForm =
    new UdashForm(None, componentId)(content)

  /**
    * Creates inline form with provided content. <br/>
    * Example: <br/>
    * <pre>
    * UdashForm.inline(
    *   UdashForm.textInput()("Email")(user.subProp(_.email)),
    *   UdashForm.passwordInput()("Password")(user.subProp(_.password))
    * ).render
    * </pre>
    * More: <a href="http://getbootstrap.com/css/#forms-inline">Bootstrap Docs</a>.
    *
    * @param componentId Id of the root DOM node.
    * @param content     Form content - usually you should pass controls (automatically wrapped into form groups) here.
    *                    You can also use the grid system to prepare more complex layout.
    * @return `UdashForm` component, call render to create DOM element.
    */
  def inline(componentId: ComponentId = ComponentId.newId())(content: UdashForm#FormElementsFactory => Modifier): UdashForm =
    new UdashForm(None, componentId)(content)

//  /** Creates from group with provided content. You can put it into `UdashForm`. <br/>
//    * Example: `UdashForm(UdashForm.group(...)).render` */
//  def group(content: Modifier*): Modifier =
//    div(BootstrapStyles.Form.group)(content)
//
//  /** Wrapper for inputs in form. */
//  def input(el: dom.Element): dom.Element = {
//    BootstrapStyles.Form.control.addTo(el)
//    el
//  }
//
//  /**
//    * Creates file input group.
//    *
//    * @param inputId             Id of the input DOM element.
//    * @param validation          Modifier applied to the created form group.
//    *                            Take a look at `UdashForm.validation` - an example field validation implementation.
//    * @param labelContent        The content of a label. If empty, the `label` won't be created.
//    * @param name                Name of the input. This value will be assigned to the `name` attribute of the input.
//    * @param acceptMultipleFiles If true, input will accept multiple files.
//    * @param selectedFiles       Property which will be synchronised with the input content.
//    * @param inputModifiers      Modifiers applied directly to the `input` element.
//    */
//  def fileInput(inputId: ComponentId = ComponentId.newId(), validation: Option[Modifier] = None)
//               (labelContent: Modifier*)
//               (name: String, acceptMultipleFiles: ReadableProperty[Boolean],
//                selectedFiles: SeqProperty[File], inputModifiers: Modifier*): Modifier =
//    inputGroup(inputId, validation, None)(
//      FileInput(selectedFiles, acceptMultipleFiles)(name, id := inputId, inputModifiers).render, labelContent
//    )
//
//  /**
//    * Creates checkbox.
//    *
//    * @param inputId        Id of the input DOM element.
//    * @param validation     Modifier applied to the created form group.
//    *                       Take a look at `UdashForm.validation` - an example field validation implementation.
//    * @param labelContent   The content of a label. If empty, the `label` won't be created.
//    * @param property       Property which will be synchronised with the input content.
//    * @param inputModifiers Modifiers applied directly to the `input` element.
//    */
//  def checkbox(validation: Option[Modifier] = None, inputId: ComponentId = ComponentId.newId())
//              (labelContent: Modifier*)(property: Property[Boolean], inputModifiers: Modifier*): Modifier =
//    div(BootstrapStyles.Form.check)(
//      Checkbox(property)(id := inputId, inputModifiers).render,
//      label()(labelContent),
//      validation
//    )
//
//  private def defaultDecorator(checkboxStyle: CssStyle) =
//    (input: dom.html.Input, id: String) => label(checkboxStyle)(input, id).render
//
//  /**
//    * Creates checkboxes for provided options.
//    *
//    * @param checkboxStyle  Style applied to each checkbox by the default decorator.
//    * @param groupId        Id of created form group.
//    * @param selected       Property which will be synchronised with the selected elements.
//    * @param options        List of possible values. Each options has one checkbox.
//    * @param decorator      This methods allows you to customize DOM structure around each checkbox.
//    *                       By default it creates a `label` around input with option value as its content.
//    */
//  def checkboxes(checkboxStyle: CssStyle = BootstrapStyles.Form.check, groupId: ComponentId = ComponentId.newId())
//                (selected: SeqProperty[String], options: Seq[String],
//                 decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(checkboxStyle)): Modifier =
//    CheckButtons(selected, options.toSeqProperty)(
//      (items: Seq[(dom.html.Input, String)]) => div(BootstrapStyles.Form.group, id := groupId.id)(
//        items.map {
//          case (input, id) => decorator(input, id)
//        }
//      ).render
//    )
//
//  /**
//    * Creates radio buttons for provided options.
//    *
//    * @param radioStyle Style applied to each radio button by the default decorator.
//    * @param groupId    Id of created form group.
//    * @param selected   Property which will be synchronised with the selected elements.
//    * @param options    List of possible values. Each options has one checkbox.
//    * @param decorator  This methods allows you to customize DOM structure around each checkbox.
//    *                   By default it creates a `label` around input with option value as its content.
//    */
//  // TODO radio style BootstrapStyles.Form.radio?
//  def radio(radioStyle: CssStyle = null, groupId: ComponentId = ComponentId.newId())
//           (selected: Property[String], options: Seq[String],
//            decorator: (dom.html.Input, String) => dom.Element = defaultDecorator(radioStyle)): Modifier =
//    RadioButtons(selected, options.toSeqProperty)(
//      (items: Seq[(dom.html.Input, String)]) => div(BootstrapStyles.Form.group, id := groupId.id)(
//        items.map {
//          case (input, id) => decorator(input, id)
//        }
//      ).render
//    )
//
//  /**
//    * Creates selection input for provided `options`.
//    *
//    * @param selected Property which will be synchronised with the selected element.
//    * @param options  List of possible values. Each options has one checkbox.
//    * @param label    This methods allows you to customize label of each option.
//    *                 By default it creates a `label` around input with option value as its content.
//    * @param inputId  Id of the select DOM element.
//    */
//  def select(selected: Property[String], options: Seq[String],
//             label: String => Modifier = Select.defaultLabel,
//             inputId: ComponentId = ComponentId.newId()): Modifier =
//    Select(selected, options.toSeqProperty)(label, BootstrapStyles.Form.control, id := inputId.id).render
//
//  /**
//    * Creates multiple selection input for provided `options`.
//    *
//    * @param selected Property which will be synchronised with the selected elements.
//    * @param options  List of possible values. Each options has one checkbox.
//    * @param label    This methods allows you to customize label of each option.
//    *                 By default it creates a `label` around input with option value as its content.
//    * @param inputId  Id of the select DOM element.
//    */
//  def multiselect(selected: SeqProperty[String], options: Seq[String],
//                  label: String => Modifier = Select.defaultLabel,
//                  inputId: ComponentId = ComponentId.newId()): Modifier =
//    Select(selected, options.toSeqProperty)(label, BootstrapStyles.Form.control, id := inputId.id)
//
//  /** Creates static control element. */
//  def staticControl(content: Modifier*): Modifier =
//    p(BootstrapStyles.Form.controlPlaintext)(content)
//
//  /**
//    * Wrapper for disabled elements.
//    * @param disabled Property indicating if elements are disabled. You can change it anytime.
//    */
//  def disabled(disabled: ReadableProperty[Boolean] = Property(true))(content: Modifier*): Modifier =
//    fieldset((scalatags.JsDom.attrs.disabled := "disabled").attrIf(disabled))(content)
}