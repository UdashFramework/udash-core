package io.udash.bootstrap.form

import io.udash._
import io.udash.bootstrap.UdashBootstrap
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

import scala.concurrent.Future

class UdashFormTest extends AsyncUdashFrontendTest {
  import UdashForm._

  "UdashForm component" should {
    jQ("body").append(UdashBootstrap.loadBootstrapStyles())

    "apply validation on inputs" in {
      val name = Property("")
      name.addValidator { value =>
        if (value.length > 3) Valid
        else Invalid("Name is too short.")
      }
      val form = UdashForm() { factory => Seq(
        factory.input.formGroup()(
          nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.OnBlur)().render,
          labelContent = Some(nested => span("Name: ", nested(bind(name)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.passwordInput(name, validationTrigger = ValidationTrigger.Instant)().render,
          labelContent = Some(nested => span("Name: ", nested(bind(name)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.textArea(name, validationTrigger = ValidationTrigger.OnSubmit)().render,
          labelContent = Some(nested => span("Name: ", nested(bind(name)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.None)().render,
          labelContent = Some(nested => span("Name: ", nested(bind(name)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.OnChange)().render,
          labelContent = Some(nested => span("Name: ", nested(bind(name)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("Name is too short."))
        )
      )}
      val element: Element = form.render
      jQ("body").append(element)

      val inOnBlur = element.getElementsByTagName("input")(0)
      val validFeedbackOnBlur = jQ(element.getElementsByClassName("valid-feedback")(0))
      val invalidFeedbackOnBlur = jQ(element.getElementsByClassName("invalid-feedback")(0))

      val inInstant = element.getElementsByTagName("input")(1)
      val validFeedbackInstant = jQ(element.getElementsByClassName("valid-feedback")(1))
      val invalidFeedbackInstant = jQ(element.getElementsByClassName("invalid-feedback")(1))

      val inOnSubmit = element.getElementsByTagName("textarea")(0)
      val validFeedbackOnSubmit = jQ(element.getElementsByClassName("valid-feedback")(2))
      val invalidFeedbackOnSubmit = jQ(element.getElementsByClassName("invalid-feedback")(2))

      val inNone = element.getElementsByTagName("input")(2)
      val validFeedbackNone = jQ(element.getElementsByClassName("valid-feedback")(3))
      val invalidFeedbackNone = jQ(element.getElementsByClassName("invalid-feedback")(3))

      val inOnChange = element.getElementsByTagName("input")(3)
      val validFeedbackOnChange = jQ(element.getElementsByClassName("valid-feedback")(4))
      val invalidFeedbackOnChange = jQ(element.getElementsByClassName("invalid-feedback")(4))

      for {
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(true)
          invalidFeedbackOnBlur.is(":hidden") should be(true)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(true)
          invalidFeedbackInstant.is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList shouldNot contain("is-valid")
          inOnChange.classList shouldNot contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(true)
          invalidFeedbackOnChange.is(":hidden") should be(true)
          element.textContent should include("Name: ")
        }

        _ <- Future {
          jQ(inOnBlur).value("Test")
          jQ(inOnBlur).trigger(EventName.change)
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(true)
          invalidFeedbackOnBlur.is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(false)
          invalidFeedbackInstant.is(":hidden") should be(true)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList should contain("is-valid")
          inOnChange.classList shouldNot contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(false)
          invalidFeedbackOnChange.is(":hidden") should be(true)
          element.textContent should include("Name: Test")
        }

        _ <- Future {
          jQ(inOnBlur).trigger(EventName.blur)
        }
        _ <- retrying {
          inOnBlur.classList should contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(false)
          invalidFeedbackOnBlur.is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(false)
          invalidFeedbackInstant.is(":hidden") should be(true)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList should contain("is-valid")
          inOnChange.classList shouldNot contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(false)
          invalidFeedbackOnChange.is(":hidden") should be(true)
          element.textContent should include("Name: Test")
        }

        _ <- Future {
          name.set("Te")
          jQ(inOnBlur).trigger(EventName.blur)
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList should contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(true)
          invalidFeedbackOnBlur.is(":hidden") should be(false)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(true)
          invalidFeedbackInstant.is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList shouldNot contain("is-valid")
          inOnChange.classList should contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(true)
          invalidFeedbackOnChange.is(":hidden") should be(false)
          element.textContent should include("Name: Te")
        }

        _ <- Future {
          jQ(element).trigger(EventName.submit)
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList should contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(true)
          invalidFeedbackOnBlur.is(":hidden") should be(false)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(true)
          invalidFeedbackInstant.is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList should contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(false)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList shouldNot contain("is-valid")
          inOnChange.classList should contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(true)
          invalidFeedbackOnChange.is(":hidden") should be(false)
          element.textContent should include("Name: Te")
        }

        _ <- Future {
          name.set("Test")
          jQ(inOnBlur).trigger(EventName.blur)
          jQ(element).trigger(EventName.submit)
        }
        _ <- retrying {
          inOnBlur.classList should contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(false)
          invalidFeedbackOnBlur.is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(false)
          invalidFeedbackInstant.is(":hidden") should be(true)
          inOnSubmit.classList should contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(false)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList should contain("is-valid")
          inOnChange.classList shouldNot contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(false)
          invalidFeedbackOnChange.is(":hidden") should be(true)
          element.textContent should include("Name: Te")
        }

        _ <- Future {
          form.clearValidationResults()
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          validFeedbackOnBlur.is(":hidden") should be(true)
          invalidFeedbackOnBlur.is(":hidden") should be(true)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          validFeedbackInstant.is(":hidden") should be(true)
          invalidFeedbackInstant.is(":hidden") should be(true)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          validFeedbackOnSubmit.is(":hidden") should be(true)
          invalidFeedbackOnSubmit.is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          validFeedbackNone.is(":hidden") should be(true)
          invalidFeedbackNone.is(":hidden") should be(true)
          inOnChange.classList shouldNot contain("is-valid")
          inOnChange.classList shouldNot contain("is-invalid")
          validFeedbackOnChange.is(":hidden") should be(true)
          invalidFeedbackOnChange.is(":hidden") should be(true)
        }

        _ <- Future {
          form.kill()
        }
        r <- Future {
          name.listenersCount() should be(0)
          name.valid.listenersCount() should be(0)
          form.listenersCount() should be(0)
          form.validationProperties.size should be(0)
        }
      } yield r
    }

    "apply validation on checkboxes and radios" in {
      val inline = Property(true)
      val radioSelection = Property(1)
      radioSelection.addValidator { x =>
        if (x % 2 == 0)  Valid
        else Invalid("The number is not even")
      }
      val checkboxesSelection = SeqProperty(1)
      checkboxesSelection.addValidator { s =>
        if (s.exists(_ % 2 == 1)) Invalid("One of the numbers is not even")
        else Valid
      }

      val form = UdashForm() { factory => Seq(
        factory.input.radioButtons(radioSelection, Seq(1,2,3,4,5).toSeqProperty, inline)(
          labelContent = (v, _, _) => Some(span(v)),
          validFeedback = (_, idx, _) => if (idx == 4) Some(span("Looks good.")) else None,
          invalidFeedback = (_, idx, _) => if (idx == 4) Some(span("The number is not even.")) else None
        ),
        factory.input.checkButtons(checkboxesSelection, Seq(1,2,3,4,5).toSeqProperty, inline)(
          labelContent = (v, _, _) => Some(span(v)),
          validFeedback = (_, idx, _) => if (idx == 4) Some(span("Looks good.")) else None,
          invalidFeedback = (_, idx, _) => if (idx == 4) Some(span("One of the numbers is not even")) else None
        )
      )}

      val element = form.render
      jQ("body").append(element)

      val inputs = element.getElementsByTagName("input")

      for {
        _ <- retrying {
          for (i <- 0 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          jQ(inputs(1)).trigger(EventName.change)
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList should contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          jQ(inputs(2)).trigger(EventName.change)
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          jQ(inputs(6)).trigger(EventName.click)
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
        }

        _ <- Future {
          jQ(inputs(5)).trigger(EventName.click)
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList should contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          jQ(inputs(7)).trigger(EventName.click)
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
        }

        _ <- Future {
          radioSelection.set(4)
          checkboxesSelection.set(Seq(2,4))
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList should contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList should contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          radioSelection.set(5)
          checkboxesSelection.set(Seq(1,2,3))
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList should contain("is-invalid")
          }
        }

        _ <- Future {
          form.clearValidationResults()
        }
        _ <- retrying {
          for (i <- 0 until 5) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
          for (i <- 5 until inputs.length) {
            val input = inputs(i)
            input.classList shouldNot contain("is-valid")
            input.classList shouldNot contain("is-invalid")
          }
        }

        _ <- Future {
          form.kill()
        }
        r <- retrying {
          radioSelection.listenersCount() should be(0)
          checkboxesSelection.listenersCount() should be(0)
          inline.listenersCount() should be(0)
          form.validationProperties.size should be(0)
        }
      } yield r
    }

    "apply validation on select menu" in {
      import org.scalajs.dom.html.{Option => JSOption}
      val singleSelection = Property(1)
      singleSelection.addValidator { x =>
        if (x % 2 == 0)  Valid
        else Invalid("The number is not even")
      }
      val multiSelection = SeqProperty(1)
      multiSelection.addValidator { s =>
        if (s.exists(_ % 2 == 1)) Invalid("One of the numbers is not even")
        else Valid
      }

      val sizeProperty: ReadableProperty[Option[BootstrapStyles.Size]] = Property(Some(BootstrapStyles.Size.Small))
      val form = UdashForm() { factory => Seq(
        factory.input.formGroup()(
          nested => factory.input.select(singleSelection, Seq(1,2,3,4,5).toSeqProperty, sizeProperty)(span(_)).render,
          labelContent = Some(nested => span("Single select: ", nested(bind(singleSelection)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("The number is not even"))
        ),
        factory.input.formGroup()(
          nested => factory.input.multiSelect(multiSelection, Seq(1,2,3,4,5).toSeqProperty, sizeProperty)(span(_)).render,
          labelContent = Some(nested => span("Multi select: ", nested(bind(multiSelection)))),
          validFeedback = Some(_ => span("Looks good.")),
          invalidFeedback = Some(_ => span("One of the numbers is not even"))
        )
      )}

      val element = form.render
      jQ("body").append(element)

      val singleSelect = element.getElementsByTagName("select")(0)
      val multiSelect = element.getElementsByTagName("select")(1)

      for {
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList shouldNot contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          jQ(singleSelect).value("1")
          jQ(singleSelect).trigger(EventName.change)
        }
        _ <- retrying {
          singleSelect.classList should contain("is-valid")
          singleSelect.classList shouldNot contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          jQ(singleSelect).value("2")
          jQ(singleSelect).trigger(EventName.change)
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList should contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          multiSelect.getElementsByTagName("option")(1).asInstanceOf[JSOption].selected = true
          jQ(multiSelect).trigger(EventName.change)
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList should contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList should contain("is-invalid")
        }

        _ <- Future {
          multiSelect.getElementsByTagName("option")(0).asInstanceOf[JSOption].selected = false
          jQ(multiSelect).trigger(EventName.change)
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList should contain("is-invalid")
          multiSelect.classList should contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          multiSelect.getElementsByTagName("option")(2).asInstanceOf[JSOption].selected = true
          jQ(multiSelect).trigger(EventName.change)
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList should contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList should contain("is-invalid")
        }

        _ <- Future {
          singleSelection.set(4)
          multiSelection.set(Seq(2,4))
        }
        _ <- retrying {
          singleSelect.classList should contain("is-valid")
          singleSelect.classList shouldNot contain("is-invalid")
          multiSelect.classList should contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          singleSelection.set(5)
          multiSelection.set(Seq(1,2,3))
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList should contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList should contain("is-invalid")
        }

        _ <- Future {
          form.clearValidationResults()
        }
        _ <- retrying {
          singleSelect.classList shouldNot contain("is-valid")
          singleSelect.classList shouldNot contain("is-invalid")
          multiSelect.classList shouldNot contain("is-valid")
          multiSelect.classList shouldNot contain("is-invalid")
        }

        _ <- Future {
          form.kill()
        }
        r <- retrying {
          singleSelection.listenersCount() should be(0)
          multiSelection.listenersCount() should be(0)
          form.validationProperties.size should be(0)
          sizeProperty.listenersCount() should be(0)
        }
      } yield r
    }
  }
}
