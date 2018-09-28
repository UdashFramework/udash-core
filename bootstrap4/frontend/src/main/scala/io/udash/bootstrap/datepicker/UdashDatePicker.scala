package io.udash.bootstrap.datepicker

import java.{util => ju}

import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.misc.AbstractCase
import io.udash._
import io.udash.bootstrap.utils.UdashIcons.FontAwesome
import io.udash.bootstrap.utils._
import io.udash.css.{CssStyle, CssStyleName}
import io.udash.logging.CrossLogging
import io.udash.wrappers.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.|
import scala.util.Try

/** Wrapper for the <a href="https://github.com/tempusdominus/bootstrap-4/">Bootstrap 4 Datepicker</a>. */
final class UdashDatePicker private[datepicker](
  val date: Property[Option[ju.Date]],
  options: ReadableProperty[UdashDatePicker.DatePickerOptions],
  override val componentId: ComponentId
) extends UdashBootstrapComponent with Listenable[UdashDatePicker, UdashDatePicker.DatePickerEvent] with CrossLogging {

  import UdashDatePicker._
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  private val inp = input(
    id := componentId.id, tpe := "text",
    BootstrapStyles.Form.control, CssStyleName("datetimepicker-input"),
    BootstrapTags.dataToggle := "datetimepicker", BootstrapTags.dataTarget := s"#$componentId"
  ).render
  private val jQInput = jQ(inp).asInstanceOf[UdashDatePickerJQuery]

  /** Shows date picker widget. */
  def show(): Unit =
    jQInput.datetimepicker("show")

  /** Hides date picker widget. */
  def hide(): Unit =
    jQInput.datetimepicker("hide")

  /** Toggle date picker widget visibility. */
  def toggle(): Unit =
    jQInput.datetimepicker("toggle")

  /** Enables date input. */
  def enable(): Unit =
    jQInput.datetimepicker("enable")

  /** Disables date input. */
  def disable(): Unit =
    jQInput.datetimepicker("disable")

  val render: Element = {
    jQInput.datetimepicker(optionsToJsDict(options.get))

    propertyListeners += options.listen { opts =>
      optionsToJsDict(opts).foreach { case (key, value) =>
        jQInput.datetimepicker(key, value)
      }
    }

    date.get.foreach(d => jQInput.datetimepicker("date", dateToMoment(d)))
    propertyListeners += date.listen(op => op.foreach(d => jQInput.datetimepicker("date", dateToMoment(d))))

    jQInput.on("change.datetimepicker", (_: Element, ev: JQueryEvent) => {
      val event = ev.asInstanceOf[DatePickerChangeJQEvent]
      val dateOption = event.option.flatMap(ev => sanitizeDate(ev.date)).map(momentToDate)
      val oldDateOption = date.get
      dateOption match {
        case Some(d) =>
          date.set(Option(d))
        case None =>
          date.set(None)
      }
      fire(UdashDatePicker.DatePickerEvent.Change(this, dateOption, oldDateOption))
    })
    jQInput.on("hide.datetimepicker", (_: Element, ev: JQueryEvent) => {
      fire(UdashDatePicker.DatePickerEvent.Hide(this, date.get))
    })
    jQInput.on("show.datetimepicker", (_: Element, ev: JQueryEvent) =>
      fire(UdashDatePicker.DatePickerEvent.Show(this))
    )
    jQInput.on("error.datetimepicker", (_: Element, ev: JQueryEvent) => {
      fire(UdashDatePicker.DatePickerEvent.Error(this, date.get))
    })

    inp
  }

  private def optionsToJsDict(options: UdashDatePicker.DatePickerOptions): js.Dictionary[js.Any] = {
    import scalajs.js.JSConverters._

    if (options.disabledDates.nonEmpty && options.enabledDates.nonEmpty)
      logger.warn("You should not use both `disabledDates` and `enabledDates` option!")

    js.Dictionary[js.Any](
      "format" -> options.format,
      "dayViewHeaderFormat" -> options.dayViewHeaderFormat,
      "extraFormats" -> (if (options.extraFormats.nonEmpty) options.extraFormats.toJSArray else false),
      "stepping" -> options.stepping,
      "useCurrent" -> options.useCurrent,
      "collapse" -> options.collapse,
      "disabledDates" -> (if (options.disabledDates.nonEmpty) options.disabledDates.map(dateToMoment).toJSArray else false),
      "enabledDates" -> (if (options.enabledDates.nonEmpty) options.enabledDates.map(dateToMoment).toJSArray else false),
      "icons" -> iconsOptionToJSDict(options.icons),
      "useStrict" -> options.useStrict,
      "sideBySide" -> options.sideBySide,
      "daysOfWeekDisabled" -> (if (options.daysOfWeekDisabled.nonEmpty) options.daysOfWeekDisabled.map(_.id).toJSArray else false),
      "calendarWeeks" -> options.calendarWeeks,
      "viewMode" -> options.viewMode.id,
      "keepOpen" -> options.keepOpen,
      "inline" -> options.inline,
      "keepInvalid" -> options.keepInvalid,
      "ignoreReadonly" -> options.ignoreReadonly,
      "allowInputToggle" -> options.allowInputToggle,
      "focusOnShow" -> options.focusOnShow,
      "enabledHours" -> (if (options.enabledHours.nonEmpty) options.enabledHours.toJSArray else false),
      "disabledHours" -> (if (options.disabledHours.nonEmpty) options.disabledHours.toJSArray else false),
      "viewDate" -> options.viewDate,
      "tooltips" -> tooltipsOptionToJSDict(options.tooltips),
      "locale" -> (if (options.locale.nonEmpty) options.locale.get else false),
      "widgetParent" -> (if (options.widgetParent.nonEmpty) options.widgetParent.get else null),
      "minDate" -> (if (options.minDate.nonEmpty) dateToMoment(options.minDate.get) else false),
      "maxDate" -> (if (options.maxDate.nonEmpty) dateToMoment(options.maxDate.get) else false),
      "defaultDate" -> (if (options.defaultDate.nonEmpty) dateToMoment(options.defaultDate.get) else false),
      "toolbarPlacement" -> (if (options.toolbarPlacement.nonEmpty) options.toolbarPlacement.get.name else UdashDatePicker.Placement.DefaultPlacement.name),
      "widgetPositioning" -> js.Dictionary(
        "horizontal" -> options.widgetPositioning.map(_._1).getOrElse(UdashDatePicker.Placement.AutoPlacement).name,
        "vertical" -> options.widgetPositioning.map(_._2).getOrElse(UdashDatePicker.Placement.AutoPlacement).name
      ),
      "buttons" -> js.Dictionary(
        "showTodayButton" -> options.showTodayButton,
        "showClear" -> options.showClear,
        "showClose" -> options.showClose,
      ),
    )
  }

  private def iconsOptionToJSDict(icons: UdashDatePicker.DatePickerIcons): js.Dictionary[js.Any] = {
    import scalajs.js.JSConverters._
    val dict = js.Dictionary[js.Any]()
    Seq(
      ("time", icons.time),
      ("date", icons.date),
      ("up", icons.up),
      ("down", icons.down),
      ("previous", icons.previous),
      ("next", icons.next),
      ("today", icons.today),
      ("clear", icons.clear),
      ("close", icons.close)
    ).filter(_._2.nonEmpty).foreach(item =>
      dict.update(item._1, item._2.flatMap(_.classNames).distinct.toJSArray)
    )
    dict
  }

  private def tooltipsOptionToJSDict(tooltips: UdashDatePicker.DatePickerTooltips): js.Dictionary[js.Any] = {
    js.Dictionary[js.Any](
      "today" -> tooltips.today,
      "clear" -> tooltips.clear,
      "close" -> tooltips.close,
      "selectMonth" -> tooltips.selectMonth,
      "prevMonth" -> tooltips.prevMonth,
      "nextMonth" -> tooltips.nextMonth,
      "selectYear" -> tooltips.selectYear,
      "prevYear" -> tooltips.prevYear,
      "nextYear" -> tooltips.nextYear,
      "selectDecade" -> tooltips.selectDecade,
      "prevDecade" -> tooltips.prevDecade,
      "nextDecade" -> tooltips.nextDecade,
      "prevCentury" -> tooltips.prevCentury,
      "nextCentury" -> tooltips.nextCentury
    )
  }

  private def internalFormat = options.get.format
  private def internalLocale = options.get.locale.getOrElse("en")

  private def dateToMoment(date: ju.Date): MomentFormatWrapper = {
    Try {
      val fullDate = moment(internalLocale, date.getTime, "x")
      // removes date part which is not present in format string; it prevents multiple updates of date from one user interaction
      moment(internalLocale, fullDate.format(internalFormat), internalFormat)
    }.getOrElse(null)
  }

  private def momentToDate(date: MomentFormatWrapper): ju.Date =
    Try {
      date.valueOf() match {
        case t if t.isNaN || t.isInfinity => null
        case t => new ju.Date(t.toLong)
      }
    }.getOrElse(null)
}

object UdashDatePicker {
  import scalatags.JsDom.all._

  /** Creates a date picker component.
    * More: <a href="https://tempusdominus.github.io/bootstrap-4/">Bootstrap 4 Datepicker Docs</a>.
    *
    * @param date        A date selected in the input.
    * @param options     A date picker's behaviour options.
    * @param componentId The arousel DOM element id.
    * @return A `UdashDatePicker` component, call `render` to create a DOM element representing this button.
    */
  def apply(
    date: Property[Option[ju.Date]],
    options: ReadableProperty[DatePickerOptions],
    componentId: ComponentId = ComponentId.newId()
  )(): UdashDatePicker =
    new UdashDatePicker(date, options, componentId)

  /** Combines two date pickers into a date range selector.
    * More: <a href="https://tempusdominus.github.io/bootstrap-4/">Bootstrap 4 Datepicker Docs</a>.
    *
    * @param fromOptions   Options of the `from` picker.
    * @param toOptions     Options of the `to` picker.
    * @return Registration cancelling the range selector.
    */
  def dateRange(from: UdashDatePicker, to: UdashDatePicker)(
    fromOptions: Property[DatePickerOptions],
    toOptions: Property[DatePickerOptions]
  ): Registration = {
    val r1 = from.date.streamTo(toOptions)(d => toOptions.get.copy(minDate = d))
    val r2 = to.date.streamTo(fromOptions)(d => fromOptions.get.copy(maxDate = d))
    new Registration {
      override def cancel(): Unit = {
        r1.cancel()
        r2.cancel()
      }

      override def restart(): Unit = {
        r1.restart()
        r2.restart()
      }

      override def isActive: Boolean =
        r1.isActive && r2.isActive
    }
  }

  /** Loads Bootstrap Date Picker styles. */
  def loadBootstrapDatePickerStyles(): Element =
    link(rel := "stylesheet", href := "https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.42/css/bootstrap-datetimepicker.min.css").render

  sealed trait DatePickerEvent extends AbstractCase with ListenableEvent[UdashDatePicker]
  object DatePickerEvent {
    final case class Show(source: UdashDatePicker) extends DatePickerEvent
    final case class Hide(source: UdashDatePicker, date: Option[ju.Date]) extends DatePickerEvent
    final case class Change(source: UdashDatePicker, date: Option[ju.Date], oldDate: Option[ju.Date]) extends DatePickerEvent
    final case class Error(source: UdashDatePicker, date: Option[ju.Date]) extends DatePickerEvent
  }

  /**
    * Full docs: <a href="http://eonasdan.github.io/bootstrap-datetimepicker/Options/">here</a>.
    *
    * @param format              See <a href="http://momentjs.com/docs/#/displaying/format/">momentjs'</a> docs for valid formats.
    *                            Format also dictates what components are shown, e.g. MM/dd/YYYY will not display the time picker.
    * @param dayViewHeaderFormat Changes the heading of the datepicker when in "days" view.
    * @param extraFormats        Allows for several input formats to be valid.
    * @param stepping            Number of minutes the up/down arrow's will move the minutes value in the time picker.
    * @param minDate             Prevents date/time selections before this date.
    * @param maxDate             Prevents date/time selections after this date.
    * @param useCurrent          On show, will set the picker to the current date/time.
    * @param collapse            Using a Bootstraps collapse to switch between date/time pickers.
    * @param locale              See <a href="http://momentjs.com/docs/#/displaying/format/">momentjs'</a> docs for valid locales.
    * @param defaultDate         Sets the picker default date/time. Overrides `useCurrent`.
    * @param disabledDates       Disables selection of dates in the array, e.g. holidays.
    * @param enabledDates        Disables selection of dates NOT in the array, e.g. holidays.
    * @param icons               Change the default icons for the pickers functions.
    * @param useStrict           Defines if moment should use strict date parsing when considering a date to be valid.
    * @param sideBySide          Shows the picker side by side when using the time and date together.
    * @param daysOfWeekDisabled  Disables the section of days of the week, e.g. weekends.
    * @param calendarWeeks       Shows the week of the year to the left of first day of the week.
    * @param viewMode            The default view to display when the picker is shown.
    *                            Note: To limit the picker to selecting, for instance the year and month, use format: `MM/YYYY`
    * @param toolbarPlacement    Changes the placement of the icon toolbar.
    * @param showTodayButton     Show the "Today" button in the icon toolbar.
    * @param showClear           Show the "Clear" button in the icon toolbar.
    * @param showClose           Show the "Close" button in the icon toolbar.
    * @param widgetPositioning   Position of datepicker widget.
    * @param widgetParent        On picker show, places the widget at the identifier object if the element has css position: 'relative'.
    * @param keepOpen            Will cause the date picker to stay open after selecting a date if no time components are being used.
    * @param inline              Will display the picker inline without the need of a input field. This will also hide borders and shadows.
    * @param keepInvalid         Will cause the date picker to not revert or overwrite invalid dates.
    * @param ignoreReadonly      Allow date picker show event to fire even when the associated input element has the `readonly="readonly"` property.
    * @param allowInputToggle    If `true`, the picker will show on textbox focus and icon click when used in a button group.
    * @param focusOnShow         If `false`, the textbox will not be given focus when the picker is shown
    * @param enabledHours        Will allow or disallow hour selections.
    * @param disabledHours       Will allow or disallow hour selections.
    * @param viewDate            This will change the viewDate without changing or setting the selected date.
    * @param tooltips            This will change the tooltips over each icon to a custom string.
    */
  class DatePickerOptions(
    val format: String,
    val dayViewHeaderFormat: String = "MMMM YYYY",
    val extraFormats: Seq[String] = Seq.empty,
    val stepping: Int = 1,
    val minDate: Option[ju.Date] = None,
    val maxDate: Option[ju.Date] = None,
    val useCurrent: Boolean = true,
    val collapse: Boolean = true,
    val locale: Option[String] = None,
    val defaultDate: Option[ju.Date] = None,
    val disabledDates: Seq[ju.Date] = Seq.empty,
    val enabledDates: Seq[ju.Date] = Seq.empty,
    val icons: DatePickerIcons = new DatePickerIcons(),
    val useStrict: Boolean = false,
    val sideBySide: Boolean = false,
    val daysOfWeekDisabled: Seq[DayOfWeek] = Seq.empty,
    val calendarWeeks: Boolean = false,
    val viewMode: ViewMode = ViewMode.Days,
    val toolbarPlacement: Option[UdashDatePicker.Placement.VerticalPlacement] = None,
    val showTodayButton: Boolean = false,
    val showClear: Boolean = false,
    val showClose: Boolean = false,
    val widgetPositioning: Option[(UdashDatePicker.Placement.HorizontalPlacement, UdashDatePicker.Placement.VerticalPlacement)] = None,
    val widgetParent: Option[String] = None,
    val keepOpen: Boolean = false,
    val inline: Boolean = false,
    val keepInvalid: Boolean = false,
    val ignoreReadonly: Boolean = false,
    val allowInputToggle: Boolean = false,
    val focusOnShow: Boolean = true,
    val enabledHours: Seq[Int] = Seq.empty,
    val disabledHours: Seq[Int] = Seq.empty,
    val viewDate: Boolean = false,
    val tooltips: DatePickerTooltips = new DatePickerTooltips()
  ) {
    private[udash] def copy(minDate: Option[ju.Date] = minDate, maxDate: Option[ju.Date] = maxDate): DatePickerOptions = {
      new DatePickerOptions(
        format, dayViewHeaderFormat, extraFormats, stepping, minDate, maxDate, useCurrent, collapse, locale,
        defaultDate, disabledDates, enabledDates, icons, useStrict, sideBySide, daysOfWeekDisabled, calendarWeeks,
        viewMode, toolbarPlacement, showTodayButton, showClear, showClose, widgetPositioning, widgetParent, keepOpen,
        inline, keepInvalid, ignoreReadonly, allowInputToggle, focusOnShow, enabledHours, disabledHours, viewDate, tooltips
      )
    }
  }

  object DatePickerOptions extends HasModelPropertyCreator[DatePickerOptions]

  class DatePickerIcons(
    val time: Seq[CssStyle] = Seq(FontAwesome.Regular.clock),
    val date: Seq[CssStyle] = Seq(FontAwesome.Regular.calendar),
    val up: Seq[CssStyle] = Seq(FontAwesome.Solid.angleUp),
    val down: Seq[CssStyle] = Seq(FontAwesome.Solid.angleDown),
    val previous: Seq[CssStyle] = Seq(FontAwesome.Solid.angleLeft),
    val next: Seq[CssStyle] = Seq(FontAwesome.Solid.angleRight),
    val today: Seq[CssStyle] = Seq(FontAwesome.Regular.calendarCheck),
    val clear: Seq[CssStyle] = Seq(FontAwesome.Regular.trashAlt),
    val close: Seq[CssStyle] = Seq(FontAwesome.Solid.times)
  )

  class DatePickerTooltips(
    val today: String = "Go to today",
    val clear: String = "Clear selection",
    val close: String = "Close the picker",
    val selectMonth: String = "Select Month",
    val prevMonth: String = "Previous Month",
    val nextMonth: String = "Next Month",
    val selectYear: String = "Select Year",
    val prevYear: String = "Previous Year",
    val nextYear: String = "Next Year",
    val selectDecade: String = "Select Decade",
    val prevDecade: String = "Previous Decade",
    val nextDecade: String = "Next Decade",
    val prevCentury: String = "Previous Century",
    val nextCentury: String = "Next Century"
  )

  final class DayOfWeek(val id: Int)
  object DayOfWeek {
    val Sunday = new DayOfWeek(0)
    val Monday = new DayOfWeek(1)
    val Tuesday = new DayOfWeek(2)
    val Wednesday = new DayOfWeek(3)
    val Thursday = new DayOfWeek(4)
    val Friday = new DayOfWeek(5)
    val Saturday = new DayOfWeek(6)
  }

  final class ViewMode(val id: String)
  object ViewMode {
    val Days = new ViewMode("days")
    val Months = new ViewMode("months")
    val Years = new ViewMode("years")
    val Decades = new ViewMode("decades")
  }

  sealed class Placement(val name: String)
  object Placement {
    val DefaultPlacement = new Placement("default")
    val AutoPlacement = new Placement("auto")

    final class VerticalPlacement(name: String) extends Placement(name)
    val TopPlacement = new VerticalPlacement("top")
    val BottomPlacement = new VerticalPlacement("bottom")

    final class HorizontalPlacement(name: String) extends Placement(name)
    val LeftPlacement = new HorizontalPlacement("left")
    val RightPlacement = new HorizontalPlacement("right")

  }

  @js.native
  private trait UdashDatePickerJQuery extends JQuery {
    def datetimepicker(settings: js.Dictionary[js.Any]): UdashDatePickerJQuery = js.native
    def datetimepicker(function: String): UdashDatePickerJQuery = js.native
    def datetimepicker(option: String, value: js.Any): UdashDatePickerJQuery = js.native
  }

  private def sanitizeDate(maybeDate: MomentFormatWrapper | Boolean): Option[MomentFormatWrapper] =
    maybeDate.option.filterNot(_.isInstanceOf[Boolean]).asInstanceOf[Option[MomentFormatWrapper]]

  @js.native
  private trait DateJQEvent extends JQueryEvent {
    def date: MomentFormatWrapper | Boolean = js.native
  }

  @js.native
  private trait DatePickerChangeJQEvent extends DateJQEvent {
    def oldDate: MomentFormatWrapper | Boolean = js.native
  }

  private def moment(locale: String, time: js.Any, format: String): MomentFormatWrapper =
    js.Dynamic.global.moment(time, format, locale).asInstanceOf[MomentFormatWrapper]

  @js.native
  private trait MomentFormatWrapper extends js.Any {
    def format(dateFormat: String): String = js.native
    def valueOf(): Double = js.native
  }

}
