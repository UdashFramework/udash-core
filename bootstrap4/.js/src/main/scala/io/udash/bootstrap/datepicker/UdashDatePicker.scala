package io.udash.bootstrap
package datepicker

import java.{util => ju}

import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.misc.{AbstractCase, AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import io.udash.bootstrap.utils.UdashIcons.FontAwesome
import io.udash.bootstrap.utils.{BootstrapStyles, BootstrapTags, UdashBootstrapComponent}
import io.udash.css.{CssStyle, CssStyleName}
import io.udash.logging.CrossLogging
import io.udash.wrappers.jquery._
import org.scalajs.dom.{Element, document}

import scala.scalajs.js
import scala.scalajs.js.|
import scala.util.Try

/** Wrapper for the <a href="https://github.com/tempusdominus/bootstrap-4/">Bootstrap 4 Datepicker</a>. */
final class UdashDatePicker private[datepicker](
  val date: Property[Option[ju.Date]],
  options: ReadableProperty[UdashDatePicker.DatePickerOptions],
  override val componentId: ComponentId
) extends UdashBootstrapComponent with Listenable with CrossLogging {

  import UdashDatePicker._
  import io.udash.css.CssView._
  import scalatags.JsDom.all._

  override type EventType = UdashDatePicker.DatePickerEvent

  private val inp = input(
    componentId, tpe := "text",
    BootstrapStyles.Form.control, CssStyleName("datetimepicker-input"),
    BootstrapTags.dataToggle := "datetimepicker", BootstrapTags.dataTarget := s"#$componentId"
  ).render
  private val jQInput = jQ(inp).asInstanceOf[UdashDatePickerJQuery]

  private val changeCallback = (_: Element, event: JQueryEvent) => {
    val dateOption = event.asInstanceOf[DatePickerChangeJQEvent].option
      .flatMap(ev => sanitizeDate(ev.date))
      .map(momentToDate)
    val oldDateOption = date.get
    if (dateOption != oldDateOption) {
      dateOption match {
        case Some(null) | None => date.set(None)
        case _ => date.set(dateOption)
      }
      fire(DatePickerEvent.Change(this, dateOption, oldDateOption))
    }
  }
  private val hideCallback = (_: Element, _: JQueryEvent) => {
    fire(DatePickerEvent.Hide(this, date.get))
  }
  private val showCallback = (_: Element, _: JQueryEvent) => {
    fire(DatePickerEvent.Show(this))
  }
  private val errorCallback = (_: Element, _: JQueryEvent) => {
    fire(DatePickerEvent.Error(this, date.get))
  }

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

  override val render: Element = {
    propertyListeners += options.listen(opts =>
      optionsToJsDict(opts).foreach { case (optionKey, optionValue) => jQInput.datetimepicker(optionKey, optionValue) }
    )
    propertyListeners += date.listen(optionalDate => optionalDate.foreach(date => jQInput.datetimepicker("date", dateToMoment(date))))
    inp
  }

  override def kill(): Unit = {
    deregisterMutationCallbacks(render)
    jQInput.datetimepicker("destroy")
    super.kill()
  }

  locally {
    registerMutationCallbacks(
      render,
      () => {
        // initialization
        date.get
          .forEmpty(jQInput.datetimepicker())
          .foreach(date => jQInput.datetimepicker(js.Dictionary[js.Any]("date" -> dateToMoment(date))))

        // options propagation
        optionsToJsDict(options.get)
          .setup(optionsDict => date.get.foreach(date => optionsDict.update("date", dateToMoment(date))))
          .foreach { case (optionKey, optionValue) => jQInput.datetimepicker(optionKey, optionValue) }

        jQInput.on(ChangeEvent, changeCallback)
        jQInput.on(HideEvent, hideCallback)
        jQInput.on(ShowEvent, showCallback)
        jQInput.on(ErrorEvent, errorCallback)
      },
      () => {
        jQInput.off(ChangeEvent, changeCallback)
        jQInput.off(HideEvent, hideCallback)
        jQInput.off(ShowEvent, showCallback)
        jQInput.off(ErrorEvent, errorCallback)
      }
    )
  }

  private def optionsToJsDict(options: DatePickerOptions): js.Dictionary[js.Any] = {
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
      "icons" -> options.icons.jsDictionary,
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
      "toolbarPlacement" -> (if (options.toolbarPlacement.nonEmpty) options.toolbarPlacement.get.name else Placement.DefaultPlacement.name),
      "widgetPositioning" -> options.widgetPositioning.map {
        case (horizontal, vertical) => js.Dictionary("horizontal" -> horizontal.name, "vertical" -> vertical.name)
      }.getOrElse(js.Dictionary("horizontal" -> Placement.AutoPlacement.name, "vertical" -> Placement.AutoPlacement.name)),
      "buttons" -> js.Dictionary(
        "showToday" -> options.showToday,
        "showClear" -> options.showClear,
        "showClose" -> options.showClose
      )
    )
  }

  private def tooltipsOptionToJSDict(tooltips: DatePickerTooltips): js.Dictionary[js.Any] = {
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

  private val ChangeEvent = "change.datetimepicker"
  private val HideEvent = "hide.datetimepicker"
  private val ShowEvent = "show.datetimepicker"
  private val ErrorEvent = "error.datetimepicker"

  /** Creates a date picker component.
   * More: <a href="https://tempusdominus.github.io/bootstrap-4/">Bootstrap 4 Datepicker Docs</a>.
   *
   * @param date        A date selected in the input.
   * @param options     A date picker's behaviour options.
   * @param componentId The DOM element id.
   * @return A `UdashDatePicker` component, call `render` to create a DOM element representing this button.
   */
  def apply(
    date: Property[Option[ju.Date]],
    options: ReadableProperty[DatePickerOptions],
    componentId: ComponentId = ComponentId.generate()
  )(): UdashDatePicker = {
    new UdashDatePicker(date, options, componentId)
  }

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
    link(rel := "stylesheet", href := "https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.1.2/css/tempusdominus-bootstrap-4.min.css").render

  sealed trait DatePickerEvent extends AbstractCase with ListenableEvent
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
    * @param locale             See <a href="http://momentjs.com/docs/#/i18n/">momentjs'</a> docs for valid locales.
    * @param defaultDate        Sets the picker default date/time. Overrides `useCurrent`.
    * @param disabledDates      Disables selection of dates in the array, e.g. holidays.
    * @param enabledDates       Disables selection of dates NOT in the array, e.g. holidays.
    * @param icons              Change the default icons for the pickers functions.
    * @param useStrict          Defines if moment should use strict date parsing when considering a date to be valid.
    * @param sideBySide         Shows the picker side by side when using the time and date together.
    * @param daysOfWeekDisabled Disables the section of days of the week, e.g. weekends.
    * @param calendarWeeks      Shows the week of the year to the left of first day of the week.
    * @param viewMode           The default view to display when the picker is shown.
    *                           Note: To limit the picker to selecting, for instance the year and month, use format: `MM/YYYY`
    * @param toolbarPlacement   Changes the placement of the icon toolbar.
    * @param showToday          Show the "Today" button in the icon toolbar.
    * @param showClear          Show the "Clear" button in the icon toolbar.
    * @param showClose          Show the "Close" button in the icon toolbar.
    * @param widgetPositioning  Position of datepicker widget.
    * @param widgetParent       On picker show, places the widget at the identifier object if the element has css position: 'relative'.
    * @param keepOpen           Will cause the date picker to stay open after selecting a date if no time components are being used.
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
  final case class DatePickerOptions(
    format: String,
    dayViewHeaderFormat: String = "MMMM YYYY",
    extraFormats: Seq[String] = Seq.empty,
    stepping: Int = 1,
    minDate: Option[ju.Date] = None,
    maxDate: Option[ju.Date] = None,
    useCurrent: Boolean = true,
    collapse: Boolean = true,
    locale: Option[String] = None,
    defaultDate: Option[ju.Date] = None,
    disabledDates: Seq[ju.Date] = Seq.empty,
    enabledDates: Seq[ju.Date] = Seq.empty,
    icons: DatePickerIcons = DefaultDatePickerIcons,
    useStrict: Boolean = false,
    sideBySide: Boolean = false,
    daysOfWeekDisabled: Seq[DayOfWeek] = Seq.empty,
    calendarWeeks: Boolean = false,
    viewMode: ViewMode = ViewMode.Days,
    toolbarPlacement: Option[UdashDatePicker.Placement.VerticalPlacement] = None,
    showToday: Boolean = false,
    showClear: Boolean = false,
    showClose: Boolean = false,
    widgetPositioning: Option[(UdashDatePicker.Placement.HorizontalPlacement, UdashDatePicker.Placement.VerticalPlacement)] = None,
    widgetParent: Option[String] = None,
    keepOpen: Boolean = false,
    inline: Boolean = false,
    keepInvalid: Boolean = false,
    ignoreReadonly: Boolean = false,
    allowInputToggle: Boolean = false,
    focusOnShow: Boolean = true,
    enabledHours: Seq[Int] = Seq.empty,
    disabledHours: Seq[Int] = Seq.empty,
    viewDate: Boolean = false,
    tooltips: DatePickerTooltips = DatePickerTooltips(
      today = "Go to today",
      clear = "Clear selection",
      close = "Close the picker",
      selectMonth = "Select month",
      prevMonth = "Previous month",
      nextMonth = "Next month",
      selectYear = "Select year",
      prevYear = "Previous year",
      nextYear = "Next year",
      selectDecade = "Select decade",
      prevDecade = "Previous decade",
      nextDecade = "Next decade",
      prevCentury = "Previous century",
      nextCentury = "Next century"
    )
  ) extends AbstractCase

  object DatePickerOptions extends HasModelPropertyCreator[DatePickerOptions]

  sealed trait DatePickerIcons {
    def jsDictionary: js.Dictionary[js.Any]
  }

  final class CustomDatePickerIcons(
      val time: Option[CssStyle] = Option.empty,
      val date: Option[CssStyle] = Option.empty,
      val up: Option[CssStyle] = Option.empty,
      val down: Option[CssStyle] = Option.empty,
      val previous: Option[CssStyle] = Option.empty,
      val next: Option[CssStyle] = Option.empty,
      val today: Option[CssStyle] = Option.empty,
      val clear: Option[CssStyle] = Option.empty,
      val close: Option[CssStyle] = Option.empty
  ) extends DatePickerIcons {
    import scala.scalajs.js.JSConverters._

    override val jsDictionary: js.Dictionary[js.Any] = js.Dictionary(
      DefaultDatePickerIcon.values.iterator.map(_.name.toLowerCase())
        .zip(Iterator(time, date, up, down, previous, next, today, clear, close))
        .flatMap { case (key, valueOpt) => valueOpt.map(key -> _.classNames.toJSArray) }
        .toSeq: _*
    )
  }

  object DefaultDatePickerIcons extends DatePickerIcons {
    override val jsDictionary: js.Dictionary[js.Any] = js.Dictionary(DefaultDatePickerIcon.values.map(_.jsDictionaryItem): _*)
  }

  final class DefaultDatePickerIcon(style: CssStyle)(implicit enumCtx: EnumCtx) extends AbstractValueEnum {
    import scala.scalajs.js.JSConverters._

    val jsDictionaryItem: (String, js.Array[String]) = name.toLowerCase() -> style.classNames.toJSArray
  }
  object DefaultDatePickerIcon extends AbstractValueEnumCompanion[DefaultDatePickerIcon] {
    final val Time: Value = new DefaultDatePickerIcon(FontAwesome.Regular.clock)
    final val Date: Value = new DefaultDatePickerIcon(FontAwesome.Regular.calendar)
    final val Up: Value = new DefaultDatePickerIcon(FontAwesome.Solid.angleUp)
    final val Down: Value = new DefaultDatePickerIcon(FontAwesome.Solid.angleDown)
    final val Previous: Value = new DefaultDatePickerIcon(FontAwesome.Solid.angleLeft)
    final val Next: Value = new DefaultDatePickerIcon(FontAwesome.Solid.angleRight)
    final val Today: Value = new DefaultDatePickerIcon(FontAwesome.Regular.calendarCheck)
    final val Clear: Value = new DefaultDatePickerIcon(FontAwesome.Regular.trashAlt)
    final val Close: Value = new DefaultDatePickerIcon(FontAwesome.Solid.times)
  }

  final case class DatePickerTooltips(
    today: String,
    clear: String,
    close: String,
    selectMonth: String,
    prevMonth: String,
    nextMonth: String,
    selectYear: String,
    prevYear: String,
    nextYear: String,
    selectDecade: String,
    prevDecade: String,
    nextDecade: String,
    prevCentury: String,
    nextCentury: String
  ) extends AbstractCase

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
    def datetimepicker(): UdashDatePickerJQuery = js.native
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

  import org.scalajs.dom.{MutationObserver, MutationObserverInit, MutationRecord, Node, NodeList}
  import scala.collection.Map
  import scala.collection.mutable.{Map => MMap}

  private val datePickerSetupCallbacks = MMap.empty[Node, () => Unit]
  private val datePickerDetachCallbacks = MMap.empty[Node, () => Unit]

  // When a date picker gets appended to a DOM, its options and listeners should be initialized once again. Thus, all
  // nodes with mutated children are examined whether there is a date picker defined within a corresponding DOM
  // subtree (note the `Node#contains` call). Added/removed nodes reported by a `MutationObserver` within single
  // `MutationRecord` are roots of recently added/removed DOM subtrees (not all Nodes listed recursively), hence each
  // registered picker is assured to be embedded within at most one of such subtrees.
  //
  // Similarly, Tempus Dominus custom jQuery event listeners get deregistered each time a date picker is detached from
  // a DOM.
  //
  // This mutation observer is turned on just before the first callback registration and off when there are no components
  // registered to be watched.
  private val datePickerMutationObserver = {
    new MutationObserver((records: js.Array[MutationRecord], _: MutationObserver) => {
      def mutationHandler(nodesExtractor: MutationRecord => NodeList, callbacks: Map[Node, () => Unit]): Unit = {
        records
          .flatMap(nodesExtractor(_) |> (recordNodes => for {i <- 0 until recordNodes.length} yield recordNodes(i)))
          .foreach(node =>
            callbacks.iterator
              .filter { case (pickerNode, _) => node.contains(pickerNode) }
              .foreach { case (_, callback) => callback() }
          )
      }

      mutationHandler(_.removedNodes, datePickerDetachCallbacks)
      mutationHandler(_.addedNodes, datePickerSetupCallbacks)
    })
  }

  def registerMutationCallbacks(pickerNode: Node, setupCallback: () => Unit, detachCallback: () => Unit): Unit = {
    if (datePickerSetupCallbacks.isEmpty && datePickerDetachCallbacks.isEmpty)
      datePickerMutationObserver.observe(document.body, MutationObserverInit(childList = true, subtree = true))
    datePickerSetupCallbacks += (pickerNode -> setupCallback)
    datePickerDetachCallbacks += (pickerNode -> detachCallback)
  }

  def deregisterMutationCallbacks(pickerNode: Node): Unit = {
    datePickerSetupCallbacks -= pickerNode
    datePickerDetachCallbacks -= pickerNode
    if (datePickerSetupCallbacks.isEmpty && datePickerDetachCallbacks.isEmpty)
      datePickerMutationObserver.disconnect()
  }
}
