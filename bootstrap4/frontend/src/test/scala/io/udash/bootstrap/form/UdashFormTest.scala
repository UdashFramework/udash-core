package io.udash.bootstrap.form

import io.udash._
import io.udash.bootstrap.UdashBootstrap
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element
import scalatags.JsDom.all._

import scala.concurrent.Future

class UdashFormTest extends AsyncUdashFrontendTest {
  import UdashForm._

  "UdashForm component" should {
    "apply validation on inputs" in {
      val name = Property("")
      name.addValidator { value =>
        if (value.length > 3) Valid
        else Invalid("Name is too short.")
      }
      val form = UdashForm() { factory => Seq(
        factory.input.formGroup()(
          nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.OnBlur)().render,
          labelContent = nested => Some(span("Name: ", nested(bind(name)))),
          validFeedback = _ => Some(span("Looks good.")),
          invalidFeedback = _ => Some(span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.passwordInput(name, validationTrigger = ValidationTrigger.Instant)().render,
          labelContent = nested => Some(span("Name: ", nested(bind(name)))),
          validFeedback = _ => Some(span("Looks good.")),
          invalidFeedback = _ => Some(span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.textArea(name, validationTrigger = ValidationTrigger.OnSubmit)().render,
          labelContent = nested => Some(span("Name: ", nested(bind(name)))),
          validFeedback = _ => Some(span("Looks good.")),
          invalidFeedback = _ => Some(span("Name is too short."))
        ),
        factory.input.formGroup()(
          nested => factory.input.textInput(name, validationTrigger = ValidationTrigger.None)().render,
          labelContent = nested => Some(span("Name: ", nested(bind(name)))),
          validFeedback = _ => Some(span("Looks good.")),
          invalidFeedback = _ => Some(span("Name is too short."))
        )
      )}
      val element: Element = form.render
      jQ("body").append(UdashBootstrap.loadBootstrapStyles())
      jQ("body").append(element)

      val inOnBlur = element.getElementsByTagName("input")(0)
      val validFeedbackOnBlur = element.getElementsByClassName("valid-feedback")(0)
      val invalidFeedbackOnBlur = element.getElementsByClassName("invalid-feedback")(0)

      val inInstant = element.getElementsByTagName("input")(1)
      val validFeedbackInstant = element.getElementsByClassName("valid-feedback")(1)
      val invalidFeedbackInstant = element.getElementsByClassName("invalid-feedback")(1)

      val inOnSubmit = element.getElementsByTagName("textarea")(0)
      val validFeedbackOnSubmit = element.getElementsByClassName("valid-feedback")(2)
      val invalidFeedbackOnSubmit = element.getElementsByClassName("invalid-feedback")(2)

      val inNone = element.getElementsByTagName("input")(2)
      val validFeedbackNone = element.getElementsByClassName("valid-feedback")(3)
      val invalidFeedbackNone = element.getElementsByClassName("invalid-feedback")(3)

      for {
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnBlur).is(":hidden") should be(true)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(true)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(true)
          jQ(invalidFeedbackInstant).is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(true)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
          element.textContent should include("Name: ")
        }

        _ <- Future {
          name.set("Test")
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnBlur).is(":hidden") should be(true)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(false)
          jQ(invalidFeedbackInstant).is(":hidden") should be(true)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(true)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
          element.textContent should include("Name: Test")
        }

        _ <- Future {
          jQ(inOnBlur).trigger(EventName.blur)
        }
        _ <- retrying {
          inOnBlur.classList should contain("is-valid")
          inOnBlur.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnBlur).is(":hidden") should be(false)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(false)
          jQ(invalidFeedbackInstant).is(":hidden") should be(true)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(true)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
          element.textContent should include("Name: Test")
        }

        _ <- Future {
          name.set("Te")
          jQ(inOnBlur).trigger(EventName.blur)
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList should contain("is-invalid")
          jQ(validFeedbackOnBlur).is(":hidden") should be(true)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(false)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(true)
          jQ(invalidFeedbackInstant).is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(true)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
          element.textContent should include("Name: Te")
        }

        _ <- Future {
          jQ(element).trigger(EventName.submit)
        }
        _ <- retrying {
          inOnBlur.classList shouldNot contain("is-valid")
          inOnBlur.classList should contain("is-invalid")
          jQ(validFeedbackOnBlur).is(":hidden") should be(true)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(false)
          inInstant.classList shouldNot contain("is-valid")
          inInstant.classList should contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(true)
          jQ(invalidFeedbackInstant).is(":hidden") should be(false)
          inOnSubmit.classList shouldNot contain("is-valid")
          inOnSubmit.classList should contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(true)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(false)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
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
          jQ(validFeedbackOnBlur).is(":hidden") should be(false)
          jQ(invalidFeedbackOnBlur).is(":hidden") should be(true)
          inInstant.classList should contain("is-valid")
          inInstant.classList shouldNot contain("is-invalid")
          jQ(validFeedbackInstant).is(":hidden") should be(false)
          jQ(invalidFeedbackInstant).is(":hidden") should be(true)
          inOnSubmit.classList should contain("is-valid")
          inOnSubmit.classList shouldNot contain("is-invalid")
          jQ(validFeedbackOnSubmit).is(":hidden") should be(false)
          jQ(invalidFeedbackOnSubmit).is(":hidden") should be(true)
          inNone.classList shouldNot contain("is-valid")
          inNone.classList shouldNot contain("is-invalid")
          jQ(validFeedbackNone).is(":hidden") should be(true)
          jQ(invalidFeedbackNone).is(":hidden") should be(true)
          element.textContent should include("Name: Te")
        }

        _ <- Future {
          form.kill()
        }
        r <- Future {
          name.listenersCount() should be(1) // TODO this is listener from `valid`, it should be 0
          name.valid.listenersCount() should be(0)
          form.listenersCount() should be(0)
        }
      } yield r
    }
  }
}
