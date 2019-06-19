package io.udash.web.guide.views.ext.demo.bootstrap


import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object DateRangePickerDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import java.util.concurrent.TimeUnit
    import java.{util => ju}

    import io.udash._
    import io.udash.bootstrap.datepicker.UdashDatePicker
    import io.udash.bootstrap.form.UdashInputGroup
    import scalatags.JsDom.all._

    val now = new ju.Date().getTime
    val sevenDays = TimeUnit.DAYS.toMillis(7)
    val from = Property[Option[ju.Date]](Some(new ju.Date(now - sevenDays)))
    val to = Property[Option[ju.Date]](Some(new ju.Date(now + sevenDays)))

    val fromPickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "MMMM Do YYYY",
      locale = Some("en_GB")
    ))

    val toPickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "D MMMM YYYY",
      locale = Some("pl")
    ))

    val fromPicker: UdashDatePicker = UdashDatePicker(from, fromPickerOptions)()
    val toPicker: UdashDatePicker = UdashDatePicker(to, toPickerOptions)()

    UdashDatePicker.dateRange(fromPicker, toPicker)(fromPickerOptions, toPickerOptions)

    div(
      UdashDatePicker.loadBootstrapDatePickerStyles(),
      UdashInputGroup()(
        UdashInputGroup.prependText("From"),
        UdashInputGroup.input(fromPicker.render),
        UdashInputGroup.appendText("to"),
        UdashInputGroup.input(toPicker.render)
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator)
  }
}

