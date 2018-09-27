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
import io.udash.properties.{PropertyCreator, seq}
import org.scalajs.dom._
import org.scalajs.dom.html.{Form, Input => JSInput}
import org.scalajs.dom.raw.Event
import scalatags.JsDom.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationLong}
import scala.util.{Failure, Success}

final class UdashForm private(
  formStyle: Option[CssStyle],
  inputValidationTrigger: ValidationTrigger,
  selectValidationTrigger: ValidationTrigger,
  override val componentId: ComponentId
)(content: UdashForm#FormElementsFactory => Modifier)
  extends UdashBootstrapComponent with Listenable[UdashForm, UdashForm.Event] with CrossLogging {

  import io.udash.css.CssView._

  class FormElementsFactory {
    /** Use this method to bond the external binging's lifecycle with the lifecycle of this form. */
    def externalBinding[T <: Binding](binding: T): T = UdashForm.this.nestedInterceptor(binding)

    /**
      * Wrapper for disabled elements.
      * @param disabled Property indicating if elements are disabled. You can change it anytime.
      */
    def disabled(disabled: ReadableProperty[Boolean] = UdashBootstrap.True)(
      content: Binding.NestedInterceptor => Modifier
    ): Modifier = {
      fieldset(
        nestedInterceptor((scalatags.JsDom.attrs.disabled := "disabled").attrIf(disabled))
      )(content(nestedInterceptor))
    }

    /** Provides input elements factory methods. */
    object input {
      /**
        * Creates a standard form group for the provided input. It contains a label and validation feedback elements.
        * The group layout can be organized vertically or horizontally based on the provided options.
        * You can wrap the result into grid elements in order to create more complex form layout.
        *
        * @param horizontal      Optional settings for a horizontal layout. If `None`, the layout will be organized vertically.
        * @param inputId         Id of the `input` form control. This value is used to properly set `for` attribute of the label.
        * @param groupId         Id of the root element.
        * @param input           The input element. It can be wrapped into `UdashInputGroup` or any other decoration.
        *                        Use the provided interceptor to properly clean up bindings inside the content.
        * @param labelContent    Optional label content.
        *                        It will be wrapped into `label` element with properly set `for` attribute.
        *                        Use the provided interceptor to properly clean up bindings inside the content.
        * @param validFeedback   Optional content of positive validation feedback.
        *                        It will be wrapped into `div` element with `valid-feedback` style.
        *                        Use the provided interceptor to properly clean up bindings inside the content.
        * @param invalidFeedback Optional content of negative validation feedback.
        *                        It will be wrapped into `div` element with `invalid-feedback` style.
        *                        Use the provided interceptor to properly clean up bindings inside the content.
        * @param helpText        Optional content of help text block.
        *                        It will be wrapped into `div` element with `form-text text-muted` style.
        *                        Use the provided interceptor to properly clean up bindings inside the content.
        */
      def formGroup(
        horizontal: Option[(Int, Int, ResponsiveBreakpoint, ReadableProperty[Option[BootstrapStyles.Size]])] = None, // TODO horizontal options case class
        inputId: ComponentId = ComponentId.newId(),
        groupId: ComponentId = ComponentId.newId()
      )(
        input: Binding.NestedInterceptor => Modifier,
        labelContent: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        validFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        invalidFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        helpText: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          override val render: Element = horizontal match {
            case None =>
              div(BootstrapStyles.Form.group, id := groupId)(
                labelContent(externalBinding).map(label(`for` := inputId)(_)),
                input(externalBinding),
                validFeedback(externalBinding).map(div(BootstrapStyles.Form.validFeedback)(_)),
                invalidFeedback(externalBinding).map(div(BootstrapStyles.Form.invalidFeedback)(_)),
                helpText(externalBinding).map(div(BootstrapStyles.Form.text, BootstrapStyles.Text.muted)(_)),
              ).render
            case Some((labelWidth, inputWidth, breakpoint, labelSize)) =>
              div(BootstrapStyles.Form.group, BootstrapStyles.Grid.row, id := groupId)(
                div(BootstrapStyles.Grid.col(labelWidth, breakpoint))(
                  labelContent(externalBinding).map(
                    label(`for` := inputId, (BootstrapStyles.Form.colFormLabelSize _).reactiveOptionApply(labelSize))(_)
                  )
                ),
                div(BootstrapStyles.Grid.col(inputWidth, breakpoint))(
                  input(externalBinding),
                  validFeedback(externalBinding).map(div(BootstrapStyles.Form.validFeedback)(_)),
                  invalidFeedback(externalBinding).map(div(BootstrapStyles.Form.invalidFeedback)(_))
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
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def textInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(property, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
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
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def passwordInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            PasswordInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(property, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
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
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def numberInput(
        property: Property[String],
        debounce: Duration = 20 millis,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            NumberInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(property, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
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
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def textArea(
        property: Property[String],
        debounce: Duration = 20 millis,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextArea(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(property, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
            ), inputId
          )
        )
      }

      /**
        * Creates a file input with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * It creates the whole input compoennt including a label and validation feedback.
        *
        * @param selectedFiles       This property contains information about files selected by a user.
        * @param acceptMultipleFiles Accepts more than one file if true.
        * @param size                Size of the input.
        * @param validationTrigger   Selects the event updating validation state of the input.
        * @param inputId             Id of the input DOM element.
        * @param inputName           Input element name.
        * @param inputModifier       Modifiers applied directly to the `input` element.
        *                            Use the provided interceptor to properly clean up bindings inside the content.
        * @param labelContent        Required label content.
        *                            It will be wrapped into `label` element with properly set `for` attribute.
        *                            Use the provided interceptor to properly clean up bindings inside the content.
        * @param validFeedback       Optional content of positive validation feedback.
        *                            It will be wrapped into `div` element with `valid-feedback` style.
        *                            Use the provided interceptor to properly clean up bindings inside the content.
        * @param invalidFeedback     Optional content of negative validation feedback.
        *                            It will be wrapped into `div` element with `invalid-feedback` style.
        *                            Use the provided interceptor to properly clean up bindings inside the content.
        */
      def fileInput(
        selectedFiles: SeqProperty[File],
        acceptMultipleFiles: ReadableProperty[Boolean] = UdashBootstrap.False,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        inputName: String,
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        labelContent: Binding.NestedInterceptor => Modifier = _ => "",
        validFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        invalidFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          private val input = FileInput(selectedFiles, acceptMultipleFiles)(
            inputName,
            id := inputId,
            BootstrapStyles.Form.customFileInput,
            inputModifier(nestedInterceptor),
            validationModifier(selectedFiles, validationTrigger, nestedInterceptor),
            (BootstrapStyles.Form.size _).reactiveOptionApply(size)
          )

          override val componentId: ComponentId = inputId

          override val render: Element = div(BootstrapStyles.Form.customFile)(
            input.render,
            label(`for` := inputId, BootstrapStyles.Form.customFileLabel)(labelContent(nestedInterceptor)),
            validFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.validFeedback)(_)),
            invalidFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.invalidFeedback)(_))
          ).render
        })
      }

      /**
        * Creates a select menu with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param selectedItem      Property containing selected element.
        * @param options           SeqProperty of available options.
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param itemLabel         Provides options' labels.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def select[T : PropertyCreator](
        selectedItem: Property[T],
        options: ReadableSeqProperty[T],
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        itemLabel: T => Modifier,
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            Select(selectedItem, options)(
              itemLabel,
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(selectedItem, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
            ), inputId
          )
        )
      }

      /**
        * Creates a multiple select menu with a default bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param selectedItems     Property containing selected elements.
        * @param options           SeqProperty of available options.
        * @param size              Size of the input.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param itemLabel         Provides options' labels.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def multiSelect[T: PropertyCreator, ElemType <: Property[T]](
        selectedItems: seq.SeqProperty[T, ElemType],
        options: ReadableSeqProperty[T],
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        inputId: ComponentId = ComponentId.newId()
      )(
        itemLabel: T => Modifier,
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            Select(selectedItems, options)(
              itemLabel,
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier(externalBinding),
              validationModifier(selectedItems, validationTrigger, externalBinding),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
            ), inputId
          )
        )
      }

      /**
        * Creates a checkbox with a custom bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * It also creates a label and validation feedback elements for the checkbox.
        *
        * @param property          Property which will be synchronised with the checkbox state and validated.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param groupId           Id of the root element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param labelContent      Optional label content.
        *                          It will be wrapped into `label` element with properly set `for` attribute.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param validFeedback     Optional content of positive validation feedback.
        *                          It will be wrapped into `div` element with `valid-feedback` style.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param invalidFeedback   Optional content of negative validation feedback.
        *                          It will be wrapped into `div` element with `invalid-feedback` style.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def checkbox(
        property: Property[Boolean],
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        inline: ReadableProperty[Boolean] = UdashBootstrap.False,
        inputId: ComponentId = ComponentId.newId(),
        groupId: ComponentId = ComponentId.newId()
      )(
        inputModifier: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        labelContent: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        validFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None,
        invalidFeedback: Binding.NestedInterceptor => Option[Modifier] = _ => None
      ): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          private val input = nestedInterceptor(Checkbox(property)(
            id := inputId,
            BootstrapStyles.Form.control,
            inputModifier(nestedInterceptor),
            validationModifier(property, validationTrigger, nestedInterceptor)
          ))

          override val componentId: ComponentId = groupId

          override val render: Element = div(
            id := groupId, BootstrapStyles.Form.customControl, BootstrapStyles.Form.customCheckbox,
            input.render.styles(BootstrapStyles.Form.customControlInput),
            nestedInterceptor(BootstrapStyles.Form.customControlInline.styleIf(inline)),
            label(`for` := inputId, BootstrapStyles.Form.customControlLabel)(
              labelContent(nestedInterceptor).getOrElse(span("\u00a0"))
            ),
            validFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.validFeedback)(_)),
            invalidFeedback(nestedInterceptor).map(div(BootstrapStyles.Form.invalidFeedback)(_))
          ).render
        })
      }

      /**
        * Creates a checkboxes with a custom bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * It also creates a label and validation feedback elements for each checkbox.
        *
        * @param selectedItems     Property which will be synchronised with the checkbox state and validated.
        * @param options           Seq of available options, one checkbox will be created for each option.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param groupId           Id of the root element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param labelContent      Optional label content.
        *                          It will be wrapped into `label` element with properly set `for` attribute.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param validFeedback     Optional content of positive validation feedback.
        *                          It will be wrapped into `div` element with `valid-feedback` style.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param invalidFeedback   Optional content of negative validation feedback.
        *                          It will be wrapped into `div` element with `invalid-feedback` style.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def checkButtons[T : PropertyCreator](
        selectedItems: seq.SeqProperty[T, _ <: ReadableProperty[T]],
        options: ReadableSeqProperty[T],
        inline: ReadableProperty[Boolean] = UdashBootstrap.False,
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        groupId: ComponentId = ComponentId.newId()
      )(
        inputModifier: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        labelContent: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        validFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        invalidFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None
      ): UdashBootstrapComponent = {
        externalBinding(new ButtonsComponent[T](
          selectedItems, decorator => CheckButtons(selectedItems, options)(decorator),
          BootstrapStyles.Form.customCheckbox, inline, validationTrigger, groupId
        )(inputModifier, labelContent, validFeedback, invalidFeedback))
      }

      /**
        * Creates radio buttons with a custom bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * It also creates a label and validation feedback elements for each radio button.
        *
        * @param selectedItem      Property which will be synchronised with the radio state and validated.
        * @param options           Seq of available options, one button will be created for each option.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param groupId           Id of the root element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param labelContent      Optional label content.
        *                          It will be wrapped into `label` element with properly set `for` attribute.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param validFeedback     Optional content of positive validation feedback.
        *                          It will be wrapped into `div` element with `valid-feedback` style.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        * @param invalidFeedback   Optional content of negative validation feedback.
        *                          It will be wrapped into `div` element with `invalid-feedback` style.
        *                          The factory takes item value and index as the arguments.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def radioButtons[T : PropertyCreator](
        selectedItem: Property[T],
        options: ReadableSeqProperty[T],
        inline: ReadableProperty[Boolean] = UdashBootstrap.False,
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        groupId: ComponentId = ComponentId.newId()
      )(
        inputModifier: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        labelContent: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        validFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None,
        invalidFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier] = (_: T, _: Int, _: Binding.NestedInterceptor) => None
      ): UdashBootstrapComponent = {
        externalBinding(new ButtonsComponent[T](
          selectedItem, decorator => RadioButtons(selectedItem, options)(decorator),
          BootstrapStyles.Form.customRadio, inline, validationTrigger, groupId
        )(inputModifier, labelContent, validFeedback, invalidFeedback))
      }

      private def validationModifier(
        property: ReadableProperty[_], validationTrigger: ValidationTrigger, nested: Binding.NestedInterceptor,
        groupValidationTrigger: Option[Property[Int]] = None // value change on this property should trigger validation
      ): Modifier = {
        def groupTrigger(startValidation: () => Any) = new Binding {
          override def applyTo(t: Element): Unit = {
            groupValidationTrigger.foreach { p =>
              propertyListeners += p.listen { _ =>
                startValidation()
              }
            }
          }
        }

        def startValidation(validationResult: Property[Option[ValidationResult]], triggerGroup: Boolean): Unit = {
          if (triggerGroup) groupValidationTrigger.foreach { p => p.set(p.get + 1) }
          validationResult.set(None)
          property.isValid onComplete {
            case Success(r) => validationResult.set(Some(r))
            case Failure(ex) =>
              logger.error("Validation failed.", ex)
              validationResult.set(None)
          }
        }

        def eventBasedModifiers(validationResult: Property[Option[ValidationResult]]): Modifier = Seq(
          nested(BootstrapStyles.Form.isValid.styleIf(validationResult.transform(_.contains(Valid)))),
          nested(BootstrapStyles.Form.isInvalid.styleIf(validationResult.transform(v => v.isDefined && !v.contains(Valid)))),
          nested(groupTrigger(() => startValidation(validationResult, triggerGroup = false)))
        )

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
              eventBasedModifiers(validationResult),
              onblur :+= { _: Event =>
                startValidation(validationResult, triggerGroup = true)
                false
              }
            )
          case ValidationTrigger.OnChange =>
            val validationResult = Property[Option[ValidationResult]](None)
            Seq(
              eventBasedModifiers(validationResult),
              nested(new Binding {
                override def applyTo(t: Element): Unit = {
                  propertyListeners += property.listen { _ =>
                      startValidation(validationResult, triggerGroup = true)
                  }
                }
              })
            )
          case ValidationTrigger.OnSubmit =>
            val validationResult = Property[Option[ValidationResult]](None)
            Seq(
              eventBasedModifiers(validationResult),
              nested(new Binding {
                override def applyTo(t: Element): Unit = {
                  propertyListeners += listen {
                    case ev: UdashForm.Event if ev.tpe == UdashForm.Event.EventType.Submit =>
                      startValidation(validationResult, triggerGroup = true)
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

      private class ButtonsComponent[T : PropertyCreator](
        selected: Property[_],
        input: (Seq[(JSInput, T)] => Seq[Node]) => InputBinding[_ <: Element],
        inputDecorationClass: CssStyle,
        inline: ReadableProperty[Boolean],
        validationTrigger: ValidationTrigger,
        groupId: ComponentId
      )(
        inputModifier: (T, Int, Binding.NestedInterceptor) => Option[Modifier],
        labelContent: (T, Int, Binding.NestedInterceptor) => Option[Modifier],
        validFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier],
        invalidFeedback: (T, Int, Binding.NestedInterceptor) => Option[Modifier]
      ) extends UdashBootstrapComponent {
        private val inputs = nestedInterceptor(input { inputs =>
          val groupValidationTrigger = Some(Property(0))
          inputs.zipWithIndex.map { case ((singleInput, item), idx) =>
            Seq[Modifier](
              BootstrapStyles.Form.customControlInput,
              inputModifier(item, idx, nestedInterceptor),
              validationModifier(selected, validationTrigger, nestedInterceptor, groupValidationTrigger)
            ).applyTo(singleInput)
            div(
              singleInput,
              BootstrapStyles.Form.customControl, inputDecorationClass,
              nestedInterceptor(BootstrapStyles.Form.customControlInline.styleIf(inline)),
              labelContent(item, idx, nestedInterceptor).map(label(`for` := singleInput.id, BootstrapStyles.Form.customControlLabel)(_)),
              validFeedback(item, idx, nestedInterceptor).map(div(BootstrapStyles.Form.validFeedback)(_)),
              invalidFeedback(item, idx, nestedInterceptor).map(div(BootstrapStyles.Form.invalidFeedback)(_))
            ).render
          }
        })

        override val componentId: ComponentId = groupId

        override val render: Element = div(id := groupId, inputs.render).render
      }
    }

    /** Provides grid elements factory methods. */
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
    // TODO describe these options and other AbstractValueEnums too
    final val None, Instant, OnChange, OnBlur, OnSubmit: Value = new ValidationTrigger
  }

  /**
    * Creates a standard form with a provided content. <br/>
    * Example: <br/>
    * <pre>
    * UdashForm() { factory => Seq(
    *   factory.input.formGroup()(
    *     nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.OnBlur)().render,
    *     labelContent = nested => Some(span("Name: ", nested(bind(name)))),
    *     validFeedback = _ => Some(span("Looks good.")),
    *     invalidFeedback = _ => Some(span("Name is too short."))
    *   ),
    *   factory.input.formGroup()(
    *     nested => factory.input.passwordInput(name, validationTrigger = ValidationTrigger.Instant)().render,
    *     labelContent = nested => Some(span("Password: ", nested(bind(name)))),
    *     validFeedback = _ => Some(span("Looks good.")),
    *     invalidFeedback = _ => Some(span("Name is too short."))
    *   )
    * )}.render
    * </pre>
    * More: <a href="http://getbootstrap.com/docs/4.1/components/forms/">Bootstrap Docs</a>.
    *
    * @param componentId An id of the root DOM node.
    * @param content     A factory of the form elements. All elements created with the factory will be cleaned up on the form cleanup.
    * @return A `UdashForm` component, call `render` to create a DOM element.
    */
  def apply(
    inputValidationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
    selectValidationTrigger: ValidationTrigger = ValidationTrigger.OnChange,
    componentId: ComponentId = ComponentId.newId()
  )(content: UdashForm#FormElementsFactory => Modifier): UdashForm =
    new UdashForm(None, inputValidationTrigger, selectValidationTrigger, componentId)(content)

  /**
    * Creates an inline form with a provided content. <br/>
    * Example: <br/>
    * <pre>
    * UdashForm.inline() { factory => Seq(
    *   factory.input.formGroup()(
    *     nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.OnBlur)().render,
    *     labelContent = nested => Some(span("Name: ", nested(bind(name)))),
    *     validFeedback = _ => Some(span("Looks good.")),
    *     invalidFeedback = _ => Some(span("Name is too short."))
    *   ),
    *   factory.input.formGroup()(
    *     nested => factory.input.passwordInput(name, validationTrigger = ValidationTrigger.Instant)().render,
    *     labelContent = nested => Some(span("Password: ", nested(bind(name)))),
    *     validFeedback = _ => Some(span("Looks good.")),
    *     invalidFeedback = _ => Some(span("Name is too short."))
    *   )
    * )}.render
    * </pre>
    * More: <a href="http://getbootstrap.com/docs/4.1/components/forms/#inline-forms">Bootstrap Docs</a>.
    *
    * @param componentId An id of the root DOM node.
    * @param content     A factory of the form elements. All elements created with the factory will be cleaned up on the form cleanup.
    * @return A `UdashForm` component, call `render` to create a DOM element.
    */
  def inline(
    inputValidationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
    selectValidationTrigger: ValidationTrigger = ValidationTrigger.OnChange,
    componentId: ComponentId = ComponentId.newId()
  )(content: UdashForm#FormElementsFactory => Modifier): UdashForm =
    new UdashForm(Some(BootstrapStyles.Form.inline), inputValidationTrigger, selectValidationTrigger, componentId)(content)
}