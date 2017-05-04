package io.udash.bootstrap.datepicker

import java.{util => ju}

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.testing.AsyncUdashFrontendTest
import io.udash.wrappers.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

class UdashDatePickerTest extends AsyncUdashFrontendTest {

  "UdashDatePicker component" should {
    "show/hide on method call and emit events" in {
      val contentId = "datepicker-test-content"
      val date = Property(new ju.Date)
      val options = Property(UdashDatePicker.DatePickerOptions(
        format = "YYYY MM DD"
      ))
      val picker = UdashDatePicker(ComponentId(contentId))(date, options)

      jQ("body").append(UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.addon("test"),
        style := "position: relative;"
      ).render)

      var showCounter = 0
      var hideCounter = 0
      var changeCounter = 0
      picker.listen {
        case UdashDatePicker.DatePickerEvent.Show(_) => showCounter += 1
        case UdashDatePicker.DatePickerEvent.Hide(_, _) => hideCounter += 1
        case UdashDatePicker.DatePickerEvent.Change(_, _, _) => changeCounter += 1
      }

      picker.hide()
      eventually {
        (showCounter, hideCounter, changeCounter) should be(0, 0, 0) // it was hidden already
      } flatMap { _ =>
        picker.show()
        eventually {
          (showCounter, hideCounter, changeCounter) should be(1, 0, 0)
        } flatMap { _ =>
          picker.hide()
          eventually {
            (showCounter, hideCounter, changeCounter) should be(1, 1, 0)
          } flatMap { _ =>
            picker.toggle()
            eventually {
              (showCounter, hideCounter, changeCounter) should be(2, 1, 0)
            } flatMap { _ =>
              picker.toggle()
              eventually {
                (showCounter, hideCounter, changeCounter) should be(2, 2, 0)
              } flatMap { _ =>
                date.set(new ju.Date(123123123))
                eventually {
                  (showCounter, hideCounter, changeCounter) should be(2, 2, 1)
                }
              }
            }
          }
        }
      }
    }

    "not fail on null input value" in {
      val date = Property[ju.Date](new ju.Date())
      val pickerOptions = ModelProperty(UdashDatePicker.DatePickerOptions(
        format = "MMMM Do YYYY, hh:mm a",
        locale = Some("en_GB")
      ))
      val picker: UdashDatePicker = UdashDatePicker()(date, pickerOptions)
      jQ("body").append(
        div(
          UdashDatePicker.loadBootstrapDatePickerStyles(),
          UdashInputGroup()(
            UdashInputGroup.input(picker.render)
          ).render
        ).render
      )
      noException shouldBe thrownBy {
        jQ("#" + picker.componentId.id).asDatePicker().date(null)
      }
    }



    "emit error events" in {
      val contentId = "datepicker-test-content"
      val date = Property(new ju.Date)
      val options = Property(UdashDatePicker.DatePickerOptions(
        format = "YYYY MM DD",
        minDate = Some(new ju.Date(1000000000)),
        maxDate = Some(new ju.Date(5000000000L))
      ))
      val picker = UdashDatePicker(ComponentId(contentId))(date, options)

      jQ("body").append(UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.addon("test"),
        style := "position: relative;"
      ).render)

      var errorCounter = 0
      var changeCounter = 0
      picker.listen {
        case UdashDatePicker.DatePickerEvent.Error(_, _) => errorCounter += 1
        case UdashDatePicker.DatePickerEvent.Change(_, _, _) => changeCounter += 1
      }

      date.set(new ju.Date(3000000000L))
      eventually {
        (errorCounter, changeCounter) should be(0, 1)
      } flatMap { _ =>
        date.set(new ju.Date(300000))
        eventually {
          (errorCounter, changeCounter) should be(1, 1)
        } flatMap { _ =>
          date.set(new ju.Date(2000000000L))
          eventually {
            (errorCounter, changeCounter) should be(1, 2)
          } flatMap { _ =>
            date.set(new ju.Date(8000000000L))
            eventually {
              (errorCounter, changeCounter) should be(2, 2)
            } flatMap { _ =>
              date.set(new ju.Date(3000000000L))
              eventually {
                (errorCounter, changeCounter) should be(2, 3)
              } flatMap { _ =>
                date.set(new ju.Date(4000000000L))
                eventually {
                  (errorCounter, changeCounter) should be(2, 4)
                }
              }
            }
          }
        }
      }
    }
  }

  private implicit class JQueryDatePickerDataExt(jQ: JQuery) {
    def asDatePicker(): UdashDatePickerData =
      jQ.data("DateTimePicker").get.asInstanceOf[UdashDatePickerData]
  }

}

@js.native
private[datepicker] trait UdashDatePickerData extends js.Object {
  def date(formattedDate: String): Unit = js.native
}


