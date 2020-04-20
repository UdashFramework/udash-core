package io.udash.bootstrap
package datepicker

import java.{util => ju}

import com.avsystem.commons._
import io.udash._
import io.udash.bootstrap.datepicker.UdashDatePickerTest.JQueryDatePickerExt
import io.udash.bootstrap.form.UdashInputGroup
import io.udash.bootstrap.utils.UdashIcons.FontAwesome
import io.udash.i18n.{Bundle, BundleHash, Lang, LocalTranslationProvider, TranslationKey}
import io.udash.testing.AsyncUdashCoreFrontendTest
import io.udash.wrappers.jquery._
import org.scalajs.dom
import scalatags.JsDom.all._

import scala.concurrent.Future
import scala.scalajs.js

class UdashDatePickerTest extends AsyncUdashCoreFrontendTest {

  "UdashDatePicker component" should {
    "show/hide on method call and emit events" ignore {
      val contentId = "datepicker-test-content"
      val date = Property[Option[ju.Date]](None)
      val options = Property(new UdashDatePicker.DatePickerOptions(
        format = "YYYY MM DD a",
        useCurrent = false
      ))
      val picker = UdashDatePicker(date, options, ComponentId(contentId))()

      jQ("body").append(UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.appendText("test"),
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

      for {
        _ <- {
          picker.hide()
          retrying {
            (showCounter, hideCounter, changeCounter) should be((0, 0, 0)) // it was hidden already
          }
        }
        _ <- {
          picker.show()
          retrying {
            (showCounter, hideCounter, changeCounter) should be((1, 0, 0))
          }
        }
        _ <- {
          date.set(Some(new ju.Date(123123123)))
          retrying {
            (showCounter, hideCounter, changeCounter) should be((1, 0, 1))
          }
        }
        _ <- {
          picker.hide()
          retrying {
            (showCounter, hideCounter, changeCounter) should be((1, 1, 1))
          }
        }
        _ <- {
          picker.toggle()
          retrying {
            (showCounter, hideCounter, changeCounter) should be((2, 1, 1))
          }
        }
        _ <- {
          picker.toggle()
          retrying {
            (showCounter, hideCounter, changeCounter) should be((2, 2, 1))
          }
        }
        r <- {
          date.set(Some(new ju.Date(333123123123L)))
          retrying {
            (showCounter, hideCounter, changeCounter) should be((2, 2, 2))
          }
        }
      } yield r
    }

    "not fail on null input value" in {
      val date = Property[Option[ju.Date]](Some(new ju.Date()))
      val pickerOptions = Property(new UdashDatePicker.DatePickerOptions(
        format = "MMMM Do YYYY, hh:mm a",
        locale = Some("en_GB")
      ))
      val picker: UdashDatePicker = UdashDatePicker(date, pickerOptions)()
      jQ("body").append(
        div(
          UdashDatePicker.loadBootstrapDatePickerStyles(),
          UdashInputGroup()(
            UdashInputGroup.input(picker.render)
          ).render
        ).render
      )
      noException shouldBe thrownBy {
        jQ(() => jQ("#" + picker.componentId.value).asInstanceOf[JQueryDatePickerExt].datetimepicker("date", null))
      }
    }

    "sync with property" in {
      val date = Property[Option[ju.Date]](Some(new ju.Date()))
      val pickerOptions = Property(new UdashDatePicker.DatePickerOptions(
        format = "MMMM Do YYYY, hh:mm a",
        locale = Some("en_GB")
      ))
      val picker: UdashDatePicker = UdashDatePicker(date, pickerOptions)()
      val r = div(
        UdashDatePicker.loadBootstrapDatePickerStyles(),
        UdashInputGroup()(
          UdashInputGroup.input(picker.render)
        ).render
      ).render
      jQ("body").append(r)

      val pickerJQ = jQ("#" + picker.componentId.value).asInstanceOf[JQueryDatePickerExt]

      for {
        _ <- {
          jQ(() => pickerJQ.datetimepicker("date", "May 15th 2017, 10:59 am"))
          retrying {
            // ignore time zone
            date.get.get.getTime should be > 1494763200000L
            date.get.get.getTime should be < 1494936000000L
          }
        }
        r <- {
          jQ(() => pickerJQ.datetimepicker("date", null))
          retrying {
            date.get should be(None)
          }
        }
      } yield r
    }

    "emit error events" in {
      val contentId = "datepicker-test-content"
      val date = Property[Option[ju.Date]](Some(new ju.Date()))
      val options = Property(new UdashDatePicker.DatePickerOptions(
        format = "YYYY MM DD",
        minDate = Some(new ju.Date(1000000000)),
        maxDate = Some(new ju.Date(5000000000L))
      ))
      val picker = UdashDatePicker(date, options, ComponentId(contentId))()

      jQ("body").append(UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.appendText("test"),
        style := "position: relative;"
      ).render)

      var errorCounter = 0
      var changeCounter = 0
      picker.listen {
        case UdashDatePicker.DatePickerEvent.Error(_, _) => errorCounter += 1
        case UdashDatePicker.DatePickerEvent.Change(_, _, _) => changeCounter += 1
      }

      for {
        _ <- {
          jQ(() => date.set(Some(new ju.Date(3000000000L))))
          retrying {
            (errorCounter, changeCounter) should be((0, 1))
          }
        }
        _ <- {
          jQ(() => date.set(Some(new ju.Date(300000))))
          retrying {
            (errorCounter, changeCounter) should be((1, 1))
          }
        }
        _ <- {
          jQ(() => date.set(Some(new ju.Date(2000000000L))))
          retrying {
            (errorCounter, changeCounter) should be((1, 2))
          }
        }
        _ <- {
          jQ(() => date.set(Some(new ju.Date(8000000000L))))
          retrying {
            (errorCounter, changeCounter) should be((2, 2))
          }
        }
        _ <- {
          jQ(() => date.set(Some(new ju.Date(3000000000L))))
          retrying {
            (errorCounter, changeCounter) should be((2, 3))
          }
        }
        r <- {
          jQ(() => date.set(Some(new ju.Date(4000000000L))))
          retrying {
            (errorCounter, changeCounter) should be((2, 4))
          }
        }
      } yield r
    }

    "translate tooltips in options" in {
      implicit val tp = new LocalTranslationProvider(
        Map(
          Lang("test") -> Bundle(BundleHash("h"), Map("today" -> "Dzisiaj", "clear" -> "Wyczyść")),
          Lang("test2") -> Bundle(BundleHash("h"), Map("today" -> "Today", "clear" -> "Clear"))
        )
      )
      val lang = Property(Lang("test"))

      val date = Property[Option[ju.Date]](Some(new ju.Date()))
      val pickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
        format = "MMMM Do YYYY, hh:mm a",
        locale = Some("en_GB")
      ))
      val emptyTk = TranslationKey.untranslatable("")

      val picker = UdashDatePicker(date, pickerOptions)().setup(_.addRegistration(lang.listen(implicit lang =>
        for {
          today <- TranslationKey.key("today")().mapNow(_.string)
          clear <- TranslationKey.key("clear")().mapNow(_.string)
          close <- TranslationKey.untranslatable("close 123")().mapNow(_.string)
          other <- emptyTk().mapNow(_.string)
        } yield {
          pickerOptions.subProp(_.tooltips).set(
            UdashDatePicker.DatePickerTooltips(
              today, clear, close, other, other, other, other, other, other, other, other, other, other, other
            )
          )
        }
        , initUpdate = true)))

      jQ("body").append(picker.render)

      val pickerJQ = jQ("#" + picker.componentId.value).asInstanceOf[JQueryDatePickerExt]

      for {
        _ <- retrying {
          val tooltips = pickerJQ.datetimepicker("tooltips").asInstanceOf[js.Dictionary[js.Any]]
          tooltips("today") should be("Dzisiaj")
          tooltips("clear") should be("Wyczyść")
          tooltips("close") should be("close 123")
        }
        _ <- Future {
          lang.set(Lang("test2"))
        }
        _ <- retrying {
          val tooltips = pickerJQ.datetimepicker("tooltips").asInstanceOf[js.Dictionary[js.Any]]
          tooltips("today") should be("Today")
          tooltips("clear") should be("Clear")
          tooltips("close") should be("close 123")
        }
        _ <- Future {
          picker.kill()
        }
        r <- {
          lang.listenersCount() should be(0)
          pickerOptions.listenersCount() should be(0)
        }
      } yield r
    }

    "invoke setup/detach callbacks when appending to/removing from the DOM" in {
      def defaultPicker = UdashDatePicker(
        Property(Option.empty),
        new UdashDatePicker.DatePickerOptions("YYYY MM DD").toProperty
      ).render

      var firstPickerSetupCount = 0
      var firstPickerDetachCount = 0
      val firstPicker = defaultPicker.setup(UdashDatePicker.registerMutationCallbacks(
        _, setupCallback = () => firstPickerSetupCount += 1, detachCallback = () => firstPickerDetachCount += 1))

      var secondPickerSetupCount = 0
      var secondPickerDetachCount = 0
      val secondPicker = defaultPicker.setup(UdashDatePicker.registerMutationCallbacks(
        _, setupCallback = () => secondPickerSetupCount += 1, detachCallback = () => secondPickerDetachCount += 1))

      def assertCounters(
        expectedFirstPickerSetupCount: Int,
        expectedSecondPickerSetupCount: Int
      )(
        expectedFirstPickerDetachCount: Int,
        expectedSecondPickerDetachCount: Int
      ) =
        (firstPickerSetupCount, secondPickerSetupCount, firstPickerDetachCount, secondPickerDetachCount) should be(
          (expectedFirstPickerSetupCount, expectedSecondPickerSetupCount, expectedFirstPickerDetachCount, expectedSecondPickerDetachCount))

      for {
        _ <- {
          dom.document.body.appendChild(firstPicker)
          retrying(assertCounters(1, 0)(0, 0))
        }
        _ <- {
          dom.document.body.appendChild(div(div("something"), span(width := "100%")(secondPicker)).render)
          retrying(assertCounters(1, 1)(0, 0))
        }
        _ <- {
          dom.document.body.removeChild(firstPicker)
          retrying(assertCounters(1, 1)(1, 0))
        }
        r <- {
          dom.document.body.appendChild(firstPicker)
          retrying(assertCounters(2, 1)(1, 0))
        }
      } yield r
    }

    "apply custom options if embedded within a dynamic view" in {
      val dateFormat = "MMMM Do YYYY, hh:mm a"
      val localeString = "pl_PL"
      val inlinePicker = true
      val todayButton = true

      val picker = UdashDatePicker(
        Property(Option.empty),
        new UdashDatePicker.DatePickerOptions(
          format = dateFormat,
          locale = Some(localeString),
          inline = inlinePicker,
          showToday = todayButton
        ).toProperty
      ).render

      def assertOptions() = {
        val jqPicker = jQ(picker).asInstanceOf[JQueryDatePickerExt]
        jqPicker.datetimepicker("format") should be(dateFormat)
        jqPicker.datetimepicker("locale") should be(localeString)
        jqPicker.datetimepicker("inline") shouldBe inlinePicker
        jqPicker.datetimepicker("buttons").asInstanceOf[js.Dictionary[Boolean]]("showToday") should be(todayButton)
      }

      for {
        _ <- {
          dom.document.body.appendChild(picker)
          retrying(assertOptions())
        }
        r <- {
          dom.document.body.removeChild(picker)
          dom.document.body.appendChild(picker)
          retrying(assertOptions())
        }
      } yield r
    }

    "initialize a picker with pre-populated input" in {
      import io.udash.bootstrap.datepicker.UdashDatePickerTest.MomentDate

      val formatString = "DD/MM/YYYY"
      val formattedDate = "01/01/1970"

      val picker = UdashDatePicker(
        date = Property(Some(new ju.Date(0L))),
        options = new UdashDatePicker.DatePickerOptions(
          format = formatString
        ).toProperty
      ).render
      val jqPicker = jQ(picker).asInstanceOf[JQueryDatePickerExt]

      // populate input value before appending the picker to the DOM
      picker.setAttribute("value", formattedDate)
      dom.document.body.appendChild(picker)

      retrying {
        jqPicker.datetimepicker("format") should be(formatString)
        jqPicker.datetimepicker("date").asInstanceOf[MomentDate].creationData()("input") should be(formattedDate)
      }
    }
  }

  "UdashDatePicker options" should {
    import io.udash.bootstrap.datepicker.UdashDatePickerTest.JsDictionaryOps
    import scala.scalajs.js.JSConverters._

    "apply selected icons config" in {
      new UdashDatePicker.CustomDatePickerIcons(
        date = FontAwesome.Solid.calendarAlt.option,
        next = FontAwesome.Regular.arrowAltCircleRight.option
      ).jsDictionary.json should be(js.Dictionary[js.Any](
        "date" -> FontAwesome.Solid.calendarAlt.classNames.toJSArray,
        "next" -> FontAwesome.Regular.arrowAltCircleRight.classNames.toJSArray
      ).json)
    }

    "apply full icons config" in {
      new UdashDatePicker.CustomDatePickerIcons(
        time = FontAwesome.Regular.clock.option,
        date = FontAwesome.Regular.calendar.option,
        up = FontAwesome.Solid.angleUp.option,
        down = FontAwesome.Solid.angleDown.option,
        previous = FontAwesome.Solid.angleLeft.option,
        next = FontAwesome.Solid.angleRight.option,
        today = FontAwesome.Regular.calendarCheck.option,
        clear = FontAwesome.Regular.trashAlt.option,
        close = FontAwesome.Solid.times.option
      ).jsDictionary.json should be(UdashDatePicker.DefaultDatePickerIcons.jsDictionary.json)
    }
  }
}

object UdashDatePickerTest {
  implicit final class JsDictionaryOps(private val dictionary: js.Dictionary[js.Any]) extends AnyVal {
    def json: String = js.JSON.stringify(dictionary)
  }

  @js.native
  private trait JQueryDatePickerExt extends JQuery {
    def datetimepicker(function: String): js.Any = js.native
    def datetimepicker(option: String, value: js.Any): JQueryDatePickerExt = js.native
  }

  @js.native
  private trait MomentDate extends js.Any {
    /**
      * https://momentjs.com/docs/#/parsing/creation-data/
      *
      * @return input data provided during the Moment object creation
      */
    def creationData(): js.Dictionary[Any] = js.native
  }
}
