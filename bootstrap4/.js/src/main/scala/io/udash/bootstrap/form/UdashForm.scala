package io.udash.bootstrap
package form

import com.avsystem.commons._
import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bindings.inputs.InputBinding
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.form.UdashForm.{HorizontalLayoutSettings, ValidationTrigger}
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.bootstrap.utils._
import io.udash.component.{ComponentId, Listenable, ListenableEvent}
import io.udash.css.CssStyle
import io.udash.logging.CrossLogging
import io.udash.properties.{PropertyCreator, seq}
import org.scalajs.dom._
import org.scalajs.dom.html.{Form, Input => JSInput}
import org.scalajs.dom.raw.Event
import scalatags.JsDom.all._

import scala.concurrent.duration.{Duration, DurationLong}

final class UdashForm private(
  formStyle: Option[CssStyle],
  inputValidationTrigger: ValidationTrigger,
  selectValidationTrigger: ValidationTrigger,
  override val componentId: ComponentId
)(content: UdashForm#FormElementsFactory => Modifier)
  extends UdashBootstrapComponent with Listenable[UdashForm, UdashForm.FormEvent] with CrossLogging {

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
        horizontal: Option[HorizontalLayoutSettings] = None,
        groupId: ComponentId = ComponentId.newId()
      )(
        input: Binding.NestedInterceptor => Element,
        labelContent: Option[Binding.NestedInterceptor => Modifier] = None,
        validFeedback: Option[Binding.NestedInterceptor => Modifier] = None,
        invalidFeedback: Option[Binding.NestedInterceptor => Modifier] = None,
        helpText: Option[Binding.NestedInterceptor => Modifier] = None
      ): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          override val render: Element = horizontal match {
            case None =>
              val inputEl = input(externalBinding)
              div(BootstrapStyles.Form.group, id := groupId)(
                labelContent.map(content => label(`for` := inputEl.id)(content(externalBinding))),
                inputEl,
                validFeedback.map(content => div(BootstrapStyles.Form.validFeedback)(content(externalBinding))),
                invalidFeedback.map(content => div(BootstrapStyles.Form.invalidFeedback)(content(externalBinding))),
                helpText.map(content => div(BootstrapStyles.Form.text, BootstrapStyles.Text.muted)(content(externalBinding)))
              ).render
            case Some(HorizontalLayoutSettings(labelWidth, inputWidth, breakpoint, labelSize)) =>
              val inputEl = input(externalBinding)
              div(BootstrapStyles.Form.group, BootstrapStyles.Grid.row, id := groupId)(
                div(BootstrapStyles.Grid.col(labelWidth, breakpoint))(
                  labelContent.map { content =>
                    label(`for` := inputEl.id, (BootstrapStyles.Form.colFormLabelSize _).reactiveOptionApply(labelSize))(
                      content(externalBinding)
                    )
                  }
                ),
                div(BootstrapStyles.Grid.col(inputWidth, breakpoint))(
                  inputEl,
                  validFeedback.map(content => div(BootstrapStyles.Form.validFeedback)(content(externalBinding))),
                  invalidFeedback.map(content => div(BootstrapStyles.Form.invalidFeedback)(content(externalBinding)))
                )
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
        inputId: ComponentId = ComponentId.newId())(inputModifier: Option[Binding.NestedInterceptor => Modifier] = None)
        (validator: Validator[String] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(property, validationTrigger, externalBinding)(validator),
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
        inputId: ComponentId = ComponentId.newId())(inputModifier: Option[Binding.NestedInterceptor => Modifier] = None)
        (validator: Validator[String] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            PasswordInput(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(property, validationTrigger, externalBinding)(validator),
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
        property: Property[Double],
        debounce: Duration = 20 millis,
        size: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None,
        validationTrigger: ValidationTrigger = inputValidationTrigger,
        inputId: ComponentId = ComponentId.newId())(inputModifier: Option[Binding.NestedInterceptor => Modifier] = None)
        (validator: Validator[Double] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            NumberInput(property.transform(_.toString, _.toDouble), debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(property, validationTrigger, externalBinding)(validator),
              (BootstrapStyles.Form.size _).reactiveOptionApply(size)
            ), inputId
          )
        )
      }

      /**
        * Creates a range input with a custom bootstrap styling and an optional validation callback which sets
        * proper bootstrap classes: `is-valid` and `is-invalid`.
        * Use `formGroup` if you want to create an input with a label and validation feedback elements.
        *
        * @param value             Current value synchronised with the input.
        *                          The value should be between `minValue` and `maxValue`. It should be also divisible by `valueStep`.
        * @param minValue          The minimum value for this input, which must not be greater than its maximum (`maxValue` attribute) value.
        * @param maxValue          The maximum value for the input. Must not be less than its minimum (`minValue` attribute) value.
        * @param valueStep         Limit the increments at which a numeric value can be set.
        * @param validationTrigger Selects the event updating validation state of the input.
        * @param inputId           Id of the input DOM element.
        * @param inputModifier     Modifiers applied directly to the `input` element.
        *                          Use the provided interceptor to properly clean up bindings inside the content.
        */
      def rangeInput(
        value: Property[Double],
        minValue: ReadableProperty[Double] = 0d.toProperty,
        maxValue: ReadableProperty[Double] = 100d.toProperty,
        valueStep: ReadableProperty[Double] = 1d.toProperty,
        validationTrigger: ValidationTrigger = selectValidationTrigger,
        inputId: ComponentId = ComponentId.newId())(inputModifier: Option[Binding.NestedInterceptor => Modifier] = None)
        (validator: Validator[Double] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            RangeInput(value, minValue, maxValue, valueStep)(
              id := inputId,
              BootstrapStyles.Form.controlRange,
              BootstrapStyles.Form.customRange,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(value, validationTrigger, externalBinding)(validator)
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
        inputId: ComponentId = ComponentId.newId())(inputModifier: Option[Binding.NestedInterceptor => Modifier] = None)
        (validator: Validator[String] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            TextArea(property, debounce)(
              id := inputId,
              BootstrapStyles.Form.control,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(property, validationTrigger, externalBinding)(validator),
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
        inputModifier: Option[Binding.NestedInterceptor => Modifier] = None,
        labelContent: Binding.NestedInterceptor => Modifier = _ => "",
        validFeedback: Option[Binding.NestedInterceptor => Modifier] = None,
        invalidFeedback: Option[Binding.NestedInterceptor => Modifier] = None
      )(validator: Validator[Seq[File]] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          private val input = FileInput(selectedFiles, acceptMultipleFiles)(
            inputName,
            id := inputId,
            BootstrapStyles.Form.customFileInput,
            inputModifier.map(_.apply(nestedInterceptor)),
            validationModifier(selectedFiles, validationTrigger, nestedInterceptor)(validator),
            (BootstrapStyles.Form.size _).reactiveOptionApply(size)
          )

          override val componentId: ComponentId = inputId

          override val render: Element = div(BootstrapStyles.Form.customFile)(
            input.render,
            label(`for` := inputId, BootstrapStyles.Form.customFileLabel)(labelContent(nestedInterceptor)),
            validFeedback.map(content => div(BootstrapStyles.Form.validFeedback)(content(nestedInterceptor))),
            invalidFeedback.map(content => div(BootstrapStyles.Form.invalidFeedback)(content(nestedInterceptor)))
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
        inputModifier: Option[Binding.NestedInterceptor => Modifier] = None
      )(validator: Validator[T] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            Select(selectedItem, options)(
              itemLabel,
              id := inputId,
              BootstrapStyles.Form.customSelect,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(selectedItem, validationTrigger, externalBinding)(validator),
              nestedInterceptor((BootstrapStyles.Form.size _).reactiveOptionApply(size))
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
        inputModifier: Option[Binding.NestedInterceptor => Modifier] = None
      )(validator: Validator[Seq[T]] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(
          new InputComponent(
            Select(selectedItems, options)(
              itemLabel,
              id := inputId,
              BootstrapStyles.Form.customSelect,
              inputModifier.map(_.apply(externalBinding)),
              validationModifier(selectedItems, validationTrigger, externalBinding)(validator),
              nestedInterceptor((BootstrapStyles.Form.size _).reactiveOptionApply(size))
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
        inputModifier: Option[Binding.NestedInterceptor => Modifier] = None,
        labelContent: Option[Binding.NestedInterceptor => Modifier] = None,
        validFeedback: Option[Binding.NestedInterceptor => Modifier] = None,
        invalidFeedback: Option[Binding.NestedInterceptor => Modifier] = None
      )(validator: Validator[Boolean] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(new UdashBootstrapComponent {
          private val input = nestedInterceptor(Checkbox(property)(
            id := inputId,
            BootstrapStyles.Form.control,
            inputModifier.map(_.apply(nestedInterceptor)),
            validationModifier(property, validationTrigger, nestedInterceptor)(validator)
          ))

          override val componentId: ComponentId = groupId

          override val render: Element = div(
            id := groupId, BootstrapStyles.Form.customControl, BootstrapStyles.Form.customCheckbox,
            input.render.styles(BootstrapStyles.Form.customControlInput),
            nestedInterceptor(BootstrapStyles.Form.customControlInline.styleIf(inline)),
            label(`for` := inputId, BootstrapStyles.Form.customControlLabel)(
              labelContent.map(_.apply(nestedInterceptor)).getOrElse(span("\u00a0"))
            ),
            validFeedback.map(content => div(BootstrapStyles.Form.validFeedback)(content(nestedInterceptor))),
            invalidFeedback.map(content => div(BootstrapStyles.Form.invalidFeedback)(content(nestedInterceptor)))
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
      )(validator: Validator[Seq[T]] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(new ButtonsComponent(
          selectedItems, CheckButtons(selectedItems, options)(_: Seq[(JSInput, T)] => Seq[Node]),
          BootstrapStyles.Form.customCheckbox, inline, validationTrigger, groupId
        )(inputModifier, labelContent, validFeedback, invalidFeedback)(validator))
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
      )(validator: Validator[T] = Validator.Default): UdashBootstrapComponent = {
        externalBinding(new ButtonsComponent(
          selectedItem, RadioButtons(selectedItem, options)(_: Seq[(JSInput, T)] => Seq[Node]),
          BootstrapStyles.Form.customRadio, inline, validationTrigger, groupId
        )(inputModifier, labelContent, validFeedback, invalidFeedback)(validator))
      }

      private def validationModifier[ArgumentType](
        property: ReadableProperty[ArgumentType], validationTrigger: ValidationTrigger,
        nested: Binding.NestedInterceptor, groupValidationTrigger: Option[Property[Int]] = None // value change on this property should trigger validation
      )(validator: Validator[ArgumentType]): Modifier = {
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
          validator(property.get).onCompleteNow {
            case Success(r) => validationResult.set(Some(r))
            case Failure(ex) =>
              logger.error("Validation failed.", ex)
              validationResult.set(None)
          }
        }

        def eventBasedModifiers(validationResult: Property[Option[ValidationResult]]): Modifier = Seq(
          nested(BootstrapStyles.Form.isValid.styleIf(validationResult.transform(_.contains(Valid)))),
          nested(BootstrapStyles.Form.isInvalid.styleIf(validationResult.transform(v => v.isDefined && !v.contains(Valid)))),
          nested(groupTrigger(() => startValidation(validationResult, triggerGroup = false))),
          nested(new Binding {
            override def applyTo(t: Element): Unit =
              validationProperties += validationResult

            override def kill(): Unit = {
              super.kill()
              validationProperties -= validationResult
            }
          })
        )

        validationTrigger match {
          case ValidationTrigger.None => Seq.empty[Modifier]
          case ValidationTrigger.Instant =>
            val validationResult = Property[Option[ValidationResult]](None)
            Seq(
              nested(new Binding {
                override def applyTo(t: Element): Unit = {
                  propertyListeners += property.listen({ _ =>
                    startValidation(validationResult, triggerGroup = true)
                  }, initUpdate = true)
                  validationProperties += validationResult
                }
                override def kill(): Unit = {
                  super.kill()
                  validationProperties -= validationResult
                }
              }),
              nested(BootstrapStyles.Form.isValid.styleIf(validationResult.transform(_.contains(Valid)))),
              nested(BootstrapStyles.Form.isInvalid.styleIf(validationResult.transform(v => v.isDefined && !v.contains(Valid))))
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
                    case ev: UdashForm.FormEvent if ev.tpe == UdashForm.FormEvent.EventType.Submit =>
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

      private class ButtonsComponent[T: PropertyCreator, SelectedType](
        selected: Property[SelectedType],
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
      )(validator: Validator[SelectedType]) extends UdashBootstrapComponent {
        private val inputs = nestedInterceptor(input { inputs =>
          val groupValidationTrigger = Some(Property(0))
          inputs.zipWithIndex.map { case ((singleInput, item), idx) =>
            Seq[Modifier](
              BootstrapStyles.Form.customControlInput,
              inputModifier(item, idx, nestedInterceptor),
              validationModifier(selected, validationTrigger, nestedInterceptor, groupValidationTrigger)(validator)
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

  private[form] val validationProperties: MSet[Property[Option[ValidationResult]]] = MSet.empty

  def clearValidationResults(): Unit = {
    validationProperties.foreach(_.set(None))
  }

  override val render: Form =
    form(formStyle)(
      onsubmit :+= { _: Event => fire(new UdashForm.FormEvent(this, UdashForm.FormEvent.EventType.Submit)); true },
      content(new FormElementsFactory)
    ).render
}

object UdashForm {
  final case class FormEvent(override val source: UdashForm, tpe: FormEvent.EventType)
    extends AbstractCase with ListenableEvent[UdashForm]
  object FormEvent {
    final class EventType(implicit enumCtx: EnumCtx) extends AbstractValueEnum
    object EventType extends AbstractValueEnumCompanion[EventType] {
      /** Fired on the form submit. */
      final val Submit: Value = new EventType
    }
  }

  /** Decides when the provided property will be validated in order to highlight the input. */
  final class ValidationTrigger(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ValidationTrigger extends AbstractValueEnumCompanion[ValidationTrigger] {
    /** The validation is disabled. */
    final val None: Value = new ValidationTrigger
    /** The validation will be triggered on initialization and will be retriggered on each property value change. */
    final val Instant: Value = new ValidationTrigger
    /** The validation will be triggered on each value change. */
    final val OnChange: Value = new ValidationTrigger
    /** The validation will be triggered on `blur` event on the input. */
    final val OnBlur: Value = new ValidationTrigger
    /** The validation will be triggered on the form `submit` event.
      * Notice that the validation won't block the form submit. */
    final val OnSubmit: Value = new ValidationTrigger
  }

  /** Settings for the horizontal form layout.
    * More: <a href="http://getbootstrap.com/docs/4.1/components/forms/#horizontal-form">Bootstrap Docs</a>.
    *
    * @param labelWidth Width of the label column.
    * @param inputWidth Width of the input column.
    * @param breakpoint Breakpoint of the form's grid.
    * @param labelSize Size of the label text.
    */
  case class HorizontalLayoutSettings(
    labelWidth: Int = 4,
    inputWidth: Int = 8,
    breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.Medium,
    labelSize: ReadableProperty[Option[BootstrapStyles.Size]] = UdashBootstrap.None
  )

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
    * @param inline                  If true, creates an inline form.
    * @param inputValidationTrigger  Default validation trigger for text inputs in this form.
    * @param selectValidationTrigger Default validation trigger for selectors like checkboxes or select inputs.
    * @param componentId             An id of the root DOM node.
    * @param content                 A factory of the form elements. All elements created with the factory will be cleaned up on the form cleanup.
    * @return A `UdashForm` component, call `render` to create a DOM element.
    */
  def apply(
    inline: Boolean = false,
    inputValidationTrigger: ValidationTrigger = ValidationTrigger.OnBlur,
    selectValidationTrigger: ValidationTrigger = ValidationTrigger.OnChange,
    componentId: ComponentId = ComponentId.newId()
  )(content: UdashForm#FormElementsFactory => Modifier): UdashForm = {
    new UdashForm(
      Some(BootstrapStyles.Form.inline).filter(_ => inline),
      inputValidationTrigger, selectValidationTrigger, componentId
    )(content)
  }
}