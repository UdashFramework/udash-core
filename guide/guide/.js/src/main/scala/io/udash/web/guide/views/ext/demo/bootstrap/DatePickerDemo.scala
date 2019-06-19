package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object DatePickerDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import java.{util => ju}

    import io.udash._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.datepicker.UdashDatePicker
    import io.udash.bootstrap.form.UdashForm.ValidationTrigger
    import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    val date = Property[Option[ju.Date]](Some(new ju.Date()))

    val pickerOptions = ModelProperty(
      new UdashDatePicker.DatePickerOptions(
        format = "MMMM Do YYYY, hh:mm a",
        locale = Some("en_GB"),
        showClear = true
      )
    )

    val disableWeekends = Property(false)
    disableWeekends.streamTo(pickerOptions.subSeq(_.daysOfWeekDisabled)) {
      case true => Seq(
        UdashDatePicker.DayOfWeek.Saturday,
        UdashDatePicker.DayOfWeek.Sunday
      )
      case false => Seq.empty
    }

    val picker = UdashDatePicker(date, pickerOptions)()

    val events = SeqProperty[String](Seq.empty)
    picker.listen {
      case UdashDatePicker.DatePickerEvent.Show(_) =>
        events.append("Widget shown")
      case UdashDatePicker.DatePickerEvent.Hide(_, date) =>
        events.append(s"Widget hidden with date: $date")
      case UdashDatePicker.DatePickerEvent.Change(_, date, oldDate) =>
        events.append(s"Widget change from $oldDate to $date")
    }

    val showButton = UdashButton()("Show")
    val hideButton = UdashButton()("Hide")
    val enableButton = UdashButton()("Enable")
    val disableButton = UdashButton()("Disable")
    showButton.listen { case _ => picker.show() }
    hideButton.listen { case _ => picker.hide() }
    enableButton.listen { case _ => picker.enable() }
    disableButton.listen { case _ => picker.disable() }

    div(
      div(
        UdashDatePicker.loadBootstrapDatePickerStyles(),
        UdashInputGroup()(
          UdashInputGroup.input(picker.render),
          UdashInputGroup.appendText(bind(date.transform(_.toString)))
        ),
      ).render,
      hr,
      UdashForm() { factory =>
        Seq[Modifier](
          factory.input.formGroup()(
            input = _ => factory.input.textInput(
              pickerOptions.subProp(_.format)
            )().render,
            labelContent = Some(_ => "Date format")
          ),
          factory.input.formGroup()(
            input = _ => factory.input.select(
              pickerOptions.subProp(_.locale).transform[String](
                (_: Option[String]).get, Some(_: String)
              ),
              Seq("en_GB", "pl", "ru", "af").toSeqProperty
            )(span(_)).render,
            labelContent = Some(_ => "Locale")
          ),
          factory.input.checkbox(disableWeekends, ValidationTrigger.None)(
            labelContent = Some(_ => "Disable weekends"),
          ),
          factory.input.checkbox(pickerOptions.subProp(_.showToday), ValidationTrigger.None)(
            labelContent = Some(_ => "Show `today` button")
          ),
          factory.input.checkbox(pickerOptions.subProp(_.showClose), ValidationTrigger.None)(
            labelContent = Some(_ => "Show `close` button")
          ),
          UdashButtonGroup()(
            factory.externalBinding(showButton).render,
            factory.externalBinding(hideButton).render,
            factory.externalBinding(enableButton).render,
            factory.externalBinding(disableButton).render
          ).render
        )
      },
      hr,
      div(Card.card, Card.body, Background.color(Color.Light))(
        repeat(events)(ev => Seq(i(ev.get).render, br.render))
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

