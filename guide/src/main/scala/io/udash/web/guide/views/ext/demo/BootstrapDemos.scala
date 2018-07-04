package io.udash.web.guide.views.ext.demo

import java.util.concurrent.TimeUnit

import io.udash._
import io.udash.bootstrap.alert.{DismissibleUdashAlert, UdashAlert}
import io.udash.bootstrap.button._
import io.udash.bootstrap.carousel.UdashCarousel.AnimationOptions
import io.udash.bootstrap.carousel.{UdashCarousel, UdashCarouselSlide}
import io.udash.bootstrap.collapse.{UdashAccordion, UdashCollapse}
import io.udash.bootstrap.datepicker.UdashDatePicker
import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.dropdown.UdashDropdown.{DefaultDropdownItem, DropdownEvent}
import io.udash.bootstrap.form.{InputGroupSize, UdashForm, UdashInputGroup}
import io.udash.bootstrap.label.UdashLabel
import io.udash.bootstrap.modal.{ModalSize, UdashModal}
import io.udash.bootstrap.navs.{UdashNav, UdashNavbar}
import io.udash.bootstrap.pagination.UdashPagination
import io.udash.bootstrap.panel.{PanelStyle, UdashPanel}
import io.udash.bootstrap.progressbar.ProgressBarStyle.{Danger, Striped, Success}
import io.udash.bootstrap.progressbar.UdashProgressBar
import io.udash.bootstrap.table.UdashTable
import io.udash.bootstrap.tooltip.{UdashPopover, UdashTooltip}
import io.udash.bootstrap.utils._
import io.udash.bootstrap.{BootstrapStyles, UdashBootstrap}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{BootstrapExtState, Context, IntroState}
import org.scalajs.dom
import scalatags.JsDom

import scala.collection.mutable
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

object BootstrapDemos extends CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.Context._
  import org.scalajs.dom._

  def statics(): dom.Element =
    div(BootstrapStyles.row, GuideStyles.frame)(
      div(BootstrapStyles.Grid.colXs9, BootstrapStyles.Well.well)(
        ".col-xs-9"
      ),
      div(BootstrapStyles.Grid.colXs4, BootstrapStyles.Well.well)(
        ".col-xs-4", br,
        "Since 9 + 4 = 13 > 12, this 4-column-wide div",
        "gets wrapped onto a new line as one contiguous unit."
      ),
      div(BootstrapStyles.Grid.colXs6, BootstrapStyles.Well.well)(
        ".col-xs-6", br,
        "Subsequent columns continue along the new line."
      )
    ).render

  def icons(): dom.Element =
    div(GuideStyles.frame)(
      UdashButtonToolbar(
        UdashButtonGroup()(
          UdashButton()(i(UdashIcons.Glyphicon.alignLeft)).render,
          UdashButton()(i(UdashIcons.Glyphicon.alignCenter)).render,
          UdashButton()(i(UdashIcons.Glyphicon.alignRight)).render,
          UdashButton()(i(UdashIcons.Glyphicon.alignJustify)).render
        ).render,
        UdashButtonGroup()(
          UdashButton()(i(UdashIcons.FontAwesome.bitcoin)).render,
          UdashButton()(i(UdashIcons.FontAwesome.euro)).render,
          UdashButton()(i(UdashIcons.FontAwesome.dollar)).render,
          UdashButton()(i(UdashIcons.FontAwesome.superpowers)).render,
        ).render
      ).render
    ).render

  def datePicker(): dom.Element = {
    import java.{util => ju}
    val date = Property[Option[ju.Date]](Some(new ju.Date()))

    val pickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "MMMM Do YYYY, hh:mm a",
      locale = Some("en_GB"),
      showClear = true
    ))

    val disableWeekends = Property(false)
    disableWeekends.streamTo(pickerOptions.subSeq(_.daysOfWeekDisabled)) {
      case true => Seq(UdashDatePicker.DayOfWeek.Saturday, UdashDatePicker.DayOfWeek.Sunday)
      case false => Seq.empty
    }

    val picker: UdashDatePicker = UdashDatePicker()(date, pickerOptions)

    val showButton = UdashButton()("Show")
    val hideButton = UdashButton()("Hide")
    val enableButton = UdashButton()("Enable")
    val disableButton = UdashButton()("Disable")
    showButton.listen { case _ => picker.show() }
    hideButton.listen { case _ => picker.hide() }
    enableButton.listen { case _ => picker.enable() }
    disableButton.listen { case _ => picker.disable() }

    val events = SeqProperty[String](Seq.empty)
    picker.listen {
      case UdashDatePicker.DatePickerEvent.Show(_) => events.append("Widget shown")
      case UdashDatePicker.DatePickerEvent.Hide(_, date) => events.append(s"Widget hidden with date: $date")
      case UdashDatePicker.DatePickerEvent.Change(_, date, oldDate) => events.append(s"Widget change from $oldDate to $date")
    }

    div(GuideStyles.frame)(
      UdashDatePicker.loadBootstrapDatePickerStyles(),
      UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.addon(bind(date.transform(_.toString)))
      ).render,
      hr,
      UdashForm(
        UdashForm.textInput()("Date format")(pickerOptions.subProp(_.format)),
        UdashForm.group(
          label("Locale"),
          UdashForm.select(pickerOptions.subProp(_.locale).transform(_.get, Some(_)), Seq("en_GB", "pl", "ru", "af"))
        ),
        UdashForm.checkbox()("Disable weekends")(disableWeekends),
        UdashForm.checkbox()("Show `today` button")(pickerOptions.subProp(_.showTodayButton)),
        UdashForm.checkbox()("Show `close` button")(pickerOptions.subProp(_.showClose)),
        UdashButtonGroup()(
          showButton.render,
          hideButton.render,
          enableButton.render,
          disableButton.render
        ).render
      ).render,
      hr,
      div(BootstrapStyles.Well.well)(
        repeat(events)(ev => Seq(i(ev.get).render, br.render))
      )
    ).render
  }

  def datePickerRange(): dom.Element = {
    import java.{util => ju}
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

    val fromPicker: UdashDatePicker = UdashDatePicker()(from, fromPickerOptions)
    val toPicker: UdashDatePicker = UdashDatePicker()(to, toPickerOptions)

    UdashDatePicker.dateRange(fromPicker, toPicker)(fromPickerOptions, toPickerOptions)

    div(GuideStyles.frame)(
      UdashDatePicker.loadBootstrapDatePickerStyles(),
      UdashInputGroup()(
        UdashInputGroup.addon("From"),
        UdashInputGroup.input(fromPicker.render),
        UdashInputGroup.addon("to"),
        UdashInputGroup.input(toPicker.render)
      ).render
    ).render
  }

  def tables(): dom.Element = {
    val striped = Property(true)
    val bordered = Property(true)
    val hover = Property(true)
    val condensed = Property(false)

    val stripedButton = UdashButton.toggle(active = striped)("Striped")
    val borderedButton = UdashButton.toggle(active = bordered)("Bordered")
    val hoverButton = UdashButton.toggle(active = hover)("Hover")
    val condensedButton = UdashButton.toggle(active = condensed)("Condensed")

    val items = SeqProperty(
      Seq.fill(7)((Random.nextDouble(), Random.nextDouble(), Random.nextDouble()))
    )
    val table = UdashTable(striped, bordered, hover, condensed)(items)(
      headerFactory = Some(() => tr(th(b("x")), th(b("y")), th(b("z"))).render),
      rowFactory = (el) => tr(
        td(produce(el)(v => span(v._1).render)),
        td(produce(el)(v => span(v._2).render)),
        td(produce(el)(v => span(v._3).render))
      ).render
    )

    div(GuideStyles.frame)(
      UdashButtonGroup(justified = true)(
        stripedButton.render,
        borderedButton.render,
        hoverButton.render,
        condensedButton.render
      ).render,
      table.render
    ).render
  }

  def dropdown(): dom.Element = {
    val url = Url(BootstrapExtState.url)
    val items = SeqProperty[UdashDropdown.DefaultDropdownItem](Seq(
      UdashDropdown.DropdownHeader("Start"),
      UdashDropdown.DropdownLink("Intro", Url(IntroState.url)),
      UdashDropdown.DropdownDisabled(UdashDropdown.DropdownLink("Test Disabled", url)),
      UdashDropdown.DropdownDivider,
      UdashDropdown.DropdownHeader("Dynamic")
    ))

    val clicks = SeqProperty[String](Seq.empty)
    var i = 1
    val appendHandler = window.setInterval(() => {
      items.append(UdashDropdown.DropdownLink(s"Test $i", url))
      i += 1
    }, 5000)
    window.setTimeout(() => window.clearInterval(appendHandler), 60000)

    val dropdown = UdashDropdown(items)(UdashDropdown.defaultItemFactory)("Dropdown ", BootstrapStyles.Button.btnPrimary)
    val dropup = UdashDropdown.dropup(items)(UdashDropdown.defaultItemFactory)("Dropup ")
    val listener: PartialFunction[UdashDropdown.DropdownEvent[DefaultDropdownItem, CastableProperty[DefaultDropdownItem]], Unit] = {
      case UdashDropdown.SelectionEvent(_, item) => clicks.append(item.toString)
      case ev: DropdownEvent[_, _] => logger.info(ev.toString)
    }

    dropdown.listen(listener)
    dropup.listen(listener)

    div(GuideStyles.frame)(
      div(BootstrapStyles.row)(
        div(BootstrapStyles.Grid.colXs6)(dropdown.render),
        div(BootstrapStyles.Grid.colXs6)(dropup.render)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(BootstrapStyles.Well.well)(seq.map(click =>
          li(click)
        ): _*).render
      )
    ).render
  }

  def button(): dom.Element = {
    val buttons = Seq(
      UdashButton(size = ButtonSize.Small)("Default", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Primary, ButtonSize.Small)("Primary", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Success, ButtonSize.Small)("Success", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Info, ButtonSize.Small)("Info", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Warning, ButtonSize.Small)("Warning", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Danger, ButtonSize.Small)("Danger", GlobalStyles.smallMargin),
      UdashButton(ButtonStyle.Link, ButtonSize.Small)("Link", GlobalStyles.smallMargin)
    )

    val clicks = SeqProperty[String](Seq.empty)
    buttons.foreach(_.listen {
      case ev => clicks.append(ev.source.render.textContent)
    })

    val push = UdashButton(size = ButtonSize.Large, block = true)("Push the button!")
    push.listen { case _ =>
      clicks.set(Seq.empty)
      buttons.foreach { button =>
        button.disabled.set(Random.nextBoolean())
      }
    }

    div(GuideStyles.frame)(
      push.render,
      div(GlobalStyles.centerBlock)(
        buttons.map(b => b.render)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(BootstrapStyles.Well.well)(seq.map(click =>
          li(click)
        ): _*).render
      )
    ).render
  }

  def toggleButton(): dom.Element = {
    val buttons = mutable.LinkedHashMap[String, UdashButton](
      "Default" -> UdashButton.toggle()("Default", GlobalStyles.smallMargin),
      "Primary" -> UdashButton.toggle(ButtonStyle.Primary)("Primary", GlobalStyles.smallMargin),
      "Success" -> UdashButton.toggle(ButtonStyle.Success)("Success", GlobalStyles.smallMargin),
      "Info" -> UdashButton.toggle(ButtonStyle.Info)("Info", GlobalStyles.smallMargin),
      "Warning" -> UdashButton.toggle(ButtonStyle.Warning)("Warning", GlobalStyles.smallMargin),
      "Danger" -> UdashButton.toggle(ButtonStyle.Danger)("Danger", GlobalStyles.smallMargin),
      "Link" -> UdashButton.toggle(ButtonStyle.Link)("Link", GlobalStyles.smallMargin)
    )

    div(GuideStyles.frame)(
      div(GlobalStyles.centerBlock)(
        buttons.values.map(_.render).toSeq
      ),
      h4("Is active: "),
      div(BootstrapStyles.Well.well)(
        buttons.map({ case (name, button) =>
          span(s"$name: ", bind(button.active), br)
        }).toSeq
      )
    ).render
  }

  def staticButtonsGroup(): dom.Element = {
    div(GuideStyles.frame)(
      UdashButtonGroup(vertical = true)(
        UdashButton(buttonStyle = ButtonStyle.Primary)("Button 1").render,
        UdashButton()("Button 2").render,
        UdashButton()("Button 3").render
      ).render
    ).render
  }

  def buttonToolbar(): dom.Element = {
    val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))
    div(GuideStyles.frame)(
      UdashButtonToolbar.reactive(groups, (p: CastableProperty[Seq[Int]]) => {
        val range = p.asSeq[Int]
        UdashButtonGroup.reactive(range, size = ButtonSize.Large)(element =>
          UdashButton()(element.get).render
        ).render
      }).render
    ).render
  }

  def checkboxButtons(): dom.Element = {
    import UdashButtonGroup._
    val options = SeqProperty[CheckboxModel](
      DefaultCheckboxModel("Checkbox 1 (pre-checked)", true),
      DefaultCheckboxModel("Checkbox 2", false),
      DefaultCheckboxModel("Checkbox 3", false)
    )
    div(GuideStyles.frame)(
      UdashButtonGroup.checkboxes(options).render,
      h4("Is active: "),
      div(BootstrapStyles.Well.well)(
        repeat(options)(option => {
          val model = option.asModel
          val name = model.subProp(_.text)
          val checked = model.subProp(_.checked)
          div(bind(name), ": ", bind(checked)).render
        })
      )
    ).render
  }

  def radioButtons(): dom.Element = {
    import UdashButtonGroup._
    val options = SeqProperty[CheckboxModel](
      DefaultCheckboxModel("Radio 1 (preselected)", true),
      DefaultCheckboxModel("Radio 2", false),
      DefaultCheckboxModel("Radio 3", false)
    )
    div(GuideStyles.frame)(
      UdashButtonGroup.radio(options, justified = true).render,
      h4("Is active: "),
      div(BootstrapStyles.Well.well)(
        repeat(options)(option => {
          val model = option.asModel
          val name = model.subProp(_.text)
          val checked = model.subProp(_.checked)
          div(bind(name), ": ", bind(checked)).render
        })
      )
    ).render
  }

  def buttonDropdown(): dom.Element = {
    val items = SeqProperty[DefaultDropdownItem](
      UdashDropdown.DropdownHeader("Start"),
      UdashDropdown.DropdownLink("Intro", Url("#")),
      UdashDropdown.DropdownDisabled(UdashDropdown.DropdownLink("Test Disabled", Url("#"))),
      UdashDropdown.DropdownDivider,
      UdashDropdown.DropdownHeader("End"),
      UdashDropdown.DropdownLink("Intro", Url("#"))
    )
    div(GuideStyles.frame)(
      UdashButtonToolbar(
        UdashButtonGroup()(
          UdashButton()("Button").render,
          UdashDropdown(items)(UdashDropdown.defaultItemFactory)().render,
          UdashDropdown.dropup(items)(UdashDropdown.defaultItemFactory)().render
        ).render,
        UdashDropdown(items)(UdashDropdown.defaultItemFactory)("Dropdown ").render
      ).render
    ).render
  }

  def inputGroups(): dom.Element = {
    val vanityUrl = Property.blank[String]
    val buttonDisabled = Property(true)
    vanityUrl.listen(v => buttonDisabled.set(v.isEmpty))
    val button = UdashButton()("Clear")
    button.listen { case _ => vanityUrl.set("") }
    div(GuideStyles.frame)(
      label("Your URL"),
      UdashInputGroup(InputGroupSize.Large)(
        UdashInputGroup.addon("https://example.com/users/", bind(vanityUrl)),
        UdashInputGroup.input(TextInput(vanityUrl)().render),
        UdashInputGroup.buttons(
          UdashButton(
            disabled = buttonDisabled
          )("Go!").render,
          button.render
        )
      ).render
    ).render
  }

  def simpleForm(): dom.Element = {
    sealed trait ShirtSize
    case object Small extends ShirtSize
    case object Medium extends ShirtSize
    case object Large extends ShirtSize

    def shirtSizeToLabel(size: ShirtSize): String = size match {
      case Small => "S"
      case Medium => "M"
      case Large => "L"
    }

    def labelToShirtSize(label: String): ShirtSize = label match {
      case "S" => Small
      case "M" => Medium
      case "L" => Large
    }

    trait UserModel {
      def name: String
      def age: Int
      def shirtSize: ShirtSize
    }
    object UserModel extends HasModelPropertyCreator[UserModel] {
      implicit val blank: Blank[UserModel] = Blank.Simple(new UserModel {
        override def name: String = ""
        override def age: Int = 25
        override def shirtSize: ShirtSize = Medium
      })
    }

    val user = ModelProperty.blank[UserModel]
    user.subProp(_.age).addValidator(new Validator[Int] {
      override def apply(element: Int): Future[ValidationResult] =
        Future {
          if (element < 0) Invalid("Age should be a non-negative integer!")
          else Valid
        }
    })

    div(GuideStyles.frame)(
      UdashForm(
        UdashForm.textInput()("User name")(user.subProp(_.name)),
        UdashForm.numberInput(
          validation = Some(UdashForm.validation(user.subProp(_.age)))
        )("Age")(user.subProp(_.age).transform(_.toString, _.toInt)),
        UdashForm.group(
          label("Shirt size"),
          UdashForm.radio(radioStyle = BootstrapStyles.Form.radioInline)(
            user.subProp(_.shirtSize)
              .transform(shirtSizeToLabel, labelToShirtSize),
            Seq(Small, Medium, Large).map(shirtSizeToLabel)
          )
        ),
        UdashForm.disabled()(UdashButton()("Send").render)
      ).render
    ).render
  }

  def inlineForm(): dom.Element = {
    val search = Property.blank[String]
    val something = Property.blank[String]
    div(GuideStyles.frame)(
      UdashForm.inline(
        UdashForm.group(
          UdashInputGroup()(
            UdashInputGroup.addon("Search: "),
            UdashInputGroup.input(TextInput(search)().render)
          ).render
        ),
        UdashForm.group(
          UdashInputGroup()(
            UdashInputGroup.addon("Something: "),
            UdashInputGroup.input(TextInput(something)().render)
          ).render
        )
      ).render
    ).render
  }

  def navs(): dom.Element = {
    trait Panel {
      def title: String
      def content: String
    }
    object Panel extends HasModelPropertyCreator[Panel]
    case class DefaultPanel(override val title: String, override val content: String) extends Panel

    val panels = SeqProperty[Panel](
      DefaultPanel("Title 1", "Content of panel 1..."),
      DefaultPanel("Title 2", "Content of panel 2..."),
      DefaultPanel("Title 3", "Content of panel 3..."),
      DefaultPanel("Title 4", "Content of panel 4...")
    )
    val selected = Property[Panel](panels.elemProperties.head.get)
    panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
    div(GuideStyles.frame)(
      UdashNav.tabs(justified = true)(panels)(
        elemFactory = (panel) => a(
          href := "",
          onclick :+= ((ev: Event) => selected.set(panel.get), true)
        )(bind(panel.asModel.subProp(_.title))).render,
        isActive = (panel) => panel.combine(selected)((panel, selected) => panel.title == selected.title)
      ).render,
      div(BootstrapStyles.Well.well)(
        bind(selected.asModel.subProp(_.content))
      )
    ).render
  }

  def navbars(): dom.Element = {
    trait Panel {
      def title: String
      def content: String
    }
    object Panel extends HasModelPropertyCreator[Panel]

    case class DefaultPanel(override val title: String, override val content: String) extends Panel

    val panels = SeqProperty[Panel](
      DefaultPanel("Title 1", "Content of panel 1..."),
      DefaultPanel("Title 2", "Content of panel 2..."),
      DefaultPanel("Title 3", "Content of panel 3..."),
      DefaultPanel("Title 4", "Content of panel 4...")
    )
    panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
    div(GuideStyles.frame)(
      UdashNavbar(
        div(BootstrapStyles.Navigation.navbarBrand)("Udash").render,
        UdashNav.navbar(panels)(
          elemFactory = (panel) => a(href := "", onclick :+= ((ev: Event) => true))(
            bind(panel.asModel.subProp(_.title))
          ).render,
          isActive = (el) => el.transform(_.title.endsWith("1")),
          isDisabled = (el) => el.transform(_.title.endsWith("5"))
        )
      ).render
    ).render
  }

  def udashNavigation(): dom.Element = {
    def linkFactory(l: MenuLink) =
      a(href := l.state.url)(span(l.name)).render

    val panels = SeqProperty[MenuEntry](mainMenuEntries.slice(0, 4))
    div(GuideStyles.frame)(
      UdashNavbar.inverted(
        div(BootstrapStyles.Navigation.navbarBrand)("Udash").render,
        UdashNav.navbar(panels)(
          elemFactory = (panel) => panel.get match {
            case MenuContainer(name, children) =>
              val childrenProperty = SeqProperty(children)
              UdashDropdown(childrenProperty)(
                (item: Property[MenuLink]) => li(linkFactory(item.get)).render)(
                name, " "
              ).linkRender
            case link: MenuLink =>
              linkFactory(link)
          },
          isDropdown = (panel) => panel.transform {
            case MenuContainer(name, children) => true
            case MenuLink(name, state) => false
          }
        )
      ).render
    ).render
  }

  def breadcrumbs(): dom.Element = {
    import io.udash.bootstrap.utils.UdashBreadcrumbs._

    val pages = SeqProperty[Breadcrumb](
      DefaultBreadcrumb("Udash", Url("http://udash.io/")),
      DefaultBreadcrumb("Dev's Guide", Url("http://guide.udash.io/")),
      DefaultBreadcrumb("Extensions", Url("http://guide.udash.io/")),
      DefaultBreadcrumb("Bootstrap wrapper", Url("http://guide.udash.io/ext/bootstrap"))
    )

    val breadcrumbs = UdashBreadcrumbs(pages)(
      defaultPageFactory,
      (item) => pages.get.last == item
    )
    div(GuideStyles.frame)(
      breadcrumbs.render
    ).render
  }

  def pagination(): dom.Element = {
    import Context._
    import UdashPagination._

    val showArrows = Property(true)
    val highlightActive = Property(true)
    val toggleArrows = UdashButton.toggle(active = showArrows)("Toggle arrows")
    val toggleHighlight = UdashButton.toggle(active = highlightActive)("Toggle highlight")

    val pages = SeqProperty(Seq.tabulate[Page](7)(idx =>
      DefaultPage((idx + 1).toString, Url(BootstrapExtState.url))
    ))
    val selected = Property(0)
    val pagination = UdashPagination(
      showArrows = showArrows, highlightActive = highlightActive
    )(pages, selected)(defaultPageFactory)
    val pager = UdashPagination.pager()(pages, selected)(defaultPageFactory)
    div(GuideStyles.frame)(
      div(
        UdashButtonGroup()(
          toggleArrows.render,
          toggleHighlight.render
        ).render
      ),
      div("Selected page index: ", bind(selected)),
      div(
        div(GlobalStyles.centerBlock)(pagination.render),
        pager.render
      )
    ).render
  }

  def labels(): dom.Element = {
    div(GuideStyles.frame)(
      div(
        UdashLabel(UdashBootstrap.newId(), "Default", GlobalStyles.smallMargin).render,
        UdashLabel.primary(UdashBootstrap.newId(), "Primary", GlobalStyles.smallMargin).render,
        UdashLabel.success(UdashBootstrap.newId(), "Success", GlobalStyles.smallMargin).render,
        UdashLabel.info(UdashBootstrap.newId(), "Info", GlobalStyles.smallMargin).render,
        UdashLabel.warning(UdashBootstrap.newId(), "Warning", GlobalStyles.smallMargin).render,
        UdashLabel.danger(UdashBootstrap.newId(), "Danger", GlobalStyles.smallMargin).render
      )
    ).render
  }

  def badges(): dom.Element = {
    val counter = Property(0)
    window.setInterval(() => counter.set(counter.get + 1), 3000)
    div(GuideStyles.frame)(
      div(
        UdashButton(buttonStyle = ButtonStyle.Primary, size = ButtonSize.Large)("Button ", UdashBadge(counter).render).render
      )
    ).render
  }

  def alerts(): dom.Element = {
    val styles = Seq[(String) => DismissibleUdashAlert](
      (title) => DismissibleUdashAlert.info(title),
      (title) => DismissibleUdashAlert.danger(title),
      (title) => DismissibleUdashAlert.success(title),
      (title) => DismissibleUdashAlert.warning(title)
    )
    val dismissed = SeqProperty[String](Seq.empty)
    def randomDismissible(): dom.Element = {
      val title = randomString()
      val alert = styles(Random.nextInt(styles.size))(title)
      alert.dismissed.listen(_ => dismissed.append(title))
      alert.render
    }
    val alerts = div(BootstrapStyles.Well.well, GlobalStyles.centerBlock)(
      UdashAlert.info("info").render,
      UdashAlert.success("success").render,
      UdashAlert.warning("warning").render,
      UdashAlert.danger("danger").render
    ).render
    val create = UdashButton(
      size = ButtonSize.Large,
      block = true
    )("Create dismissible alert")
    create.listen { case _ => alerts.appendChild(randomDismissible()) }
    div(GuideStyles.frame)(
      create.render,
      alerts,
      h4("Dismissed: "),
      produce(dismissed)(seq =>
        ul(BootstrapStyles.Well.well)(seq.map(click =>
          li(click)
        ): _*).render
      )
    ).render
  }

  def listGroup(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
    val listGroup = UdashListGroup(news)((news) =>
      li(
        BootstrapStyles.active.styleIf(news.transform(_.endsWith("1"))),
        BootstrapStyles.disabled.styleIf(news.transform(_.endsWith("2"))),
        BootstrapStyles.List.listItemSuccess.styleIf(news.transform(_.endsWith("3"))),
        BootstrapStyles.List.listItemDanger.styleIf(news.transform(_.endsWith("4"))),
        BootstrapStyles.List.listItemInfo.styleIf(news.transform(_.endsWith("5"))),
        BootstrapStyles.List.listItemWarning.styleIf(news.transform(_.endsWith("6")))
      )(bind(news)).render
    )

    var i = 1
    val appendHandler = window.setInterval(() => {
      news.append(s"Dynamic $i")
      i += 1
    }, 2000)
    window.setTimeout(() => window.clearInterval(appendHandler), 20000)

    div(GuideStyles.frame)(
        listGroup.render
    ).render
  }

  def panels(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
    div(GuideStyles.frame)(
      UdashPanel(PanelStyle.Success)(
        UdashPanel.heading("Panel heading"),
        UdashPanel.body("Some default panel content here. Nulla vitae elit libero, a pharetra augue. Aenean lacinia bibendum nulla sed consectetur. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Nullam id dolor id nibh ultricies vehicula ut id elit."),
        UdashListGroup(news)((news) =>
          li(bind(news)).render
        ).render,
        UdashPanel.footer("Panel footer")
      ).render
    ).render
  }

  def responsiveEmbed(): dom.Element = {
    div(GuideStyles.frame)(
      div(BootstrapStyles.EmbedResponsive.embed, BootstrapStyles.EmbedResponsive.embed16by9, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      ),
      div(BootstrapStyles.EmbedResponsive.embed, BootstrapStyles.EmbedResponsive.embed4by3, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      )
    ).render
  }

  def wells(): dom.Element = {
    div(GuideStyles.frame)(
      div(BootstrapStyles.Well.well, BootstrapStyles.Well.wellSm)("Small well..."),
      div(BootstrapStyles.Well.well)("Standard well..."),
      div(BootstrapStyles.Well.well, BootstrapStyles.Well.wellLg)("Large well...")
    ).render
  }

  def simpleModal(): dom.Element = {
    val events = SeqProperty.blank[UdashModal.ModalEvent]
    val header = () => div(
      "Modal events",
      UdashButton()(UdashModal.CloseButtonAttr, BootstrapStyles.close, "×").render
    ).render
    val body = () => div(
      div(BootstrapStyles.Well.well)(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    ).render
    val footer = () => div(
      UdashButton()(UdashModal.CloseButtonAttr, "Close").render,
      UdashButton(buttonStyle = ButtonStyle.Primary)("Something...").render
    ).render

    val modal = UdashModal(modalSize = ModalSize.Large)(
      headerFactory = Some(header),
      bodyFactory = Some(body),
      footerFactory = Some(footer)
    )
    modal.listen { case ev => events.append(ev) }

    val openModalButton = UdashButton(buttonStyle = ButtonStyle.Primary)(modal.openButtonAttrs(), "Show modal...")
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case _ =>
      modal.show()
      window.setTimeout(() => modal.hide(), 2000)
    }
    div(GuideStyles.frame)(
      modal.render,
      UdashButtonGroup()(
        openModalButton.render,
        openAndCloseButton.render
      ).render
    ).render
  }

  def progressBar(): dom.Element = {
    val showPercentage = Property(true)
    val animate = Property(true)
    val value = Property(50)
    div(GuideStyles.frame)(
      div(
        UdashButtonGroup()(
          UdashButton.toggle(active = showPercentage)("Show percentage").render,
          UdashButton.toggle(active = animate)("Animate").render
        ).render
      ), br,
      UdashProgressBar(value, showPercentage, Success)().render,
      UdashProgressBar(value, showPercentage, Striped)(value => value + " percent").render,
      UdashProgressBar.animated(value, showPercentage, animate, Danger)().render,
      NumberInput(value.transform(_.toString, Integer.parseInt))(
        BootstrapStyles.Form.formControl, placeholder := "Percentage"
      )
    ).render
  }

  def tooltips(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val label1 = UdashLabel(UdashBootstrap.newId(), "Tooltip on hover with delay", GlobalStyles.smallMargin).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.HoverTrigger),
      delay = UdashTooltip.Delay(500 millis, 250 millis),
      title = (_) => "Tooltip..."
    )(label1)

    val label2 = UdashLabel(UdashBootstrap.newId(), "Tooltip on click", GlobalStyles.smallMargin).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.ClickTrigger),
      delay = UdashTooltip.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashTooltip.BottomPlacement),
      title = (_) => "Tooltip 2..."
    )(label2)

    val label3 = UdashLabel(UdashBootstrap.newId(), "Tooltip with JS toggler", GlobalStyles.smallMargin).render
    val label3Tooltip = UdashTooltip(
      trigger = Seq(UdashTooltip.ManualTrigger),
      placement = (_, _) => Seq(UdashTooltip.RightPlacement),
      title = (_) => "Tooltip 3..."
    )(label3)

    val button = UdashButton()("Toggle tooltip")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame)(
      label1, label2, label3, button.render
    ).render
  }

  def popovers(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val label1 = UdashLabel(UdashBootstrap.newId(), "Popover on hover with delay", GlobalStyles.smallMargin).render
    UdashPopover(
      trigger = Seq(UdashPopover.HoverTrigger),
      delay = UdashPopover.Delay(500 millis, 250 millis),
      title = (_) => "Popover...",
      content = (_) => "Content..."
    )(label1)

    val label2 = UdashLabel(UdashBootstrap.newId(), "Popover on click", GlobalStyles.smallMargin).render
    UdashPopover(
      trigger = Seq(UdashPopover.ClickTrigger),
      delay = UdashPopover.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashPopover.BottomPlacement),
      title = (_) => "Popover 2...",
      content = (_) => "Content..."
    )(label2)

    val label3 = UdashLabel(UdashBootstrap.newId(), "Popover with JS toggler", GlobalStyles.smallMargin).render
    val label3Tooltip = UdashPopover(
      trigger = Seq(UdashPopover.ManualTrigger),
      placement = (_, _) => Seq(UdashPopover.LeftPlacement),
      html = true,
      title = (_) => "Popover 3...",
      content = (_) => {
        import scalatags.Text.all._
        Seq(
          p("HTML content..."),
          ul(li("Item 1"), li("Item 2"), li("Item 3"))
        ).render
      }
    )(label3)

    val button = UdashButton()("Toggle popover")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame)(
      label1, label2, label3, button.render
    ).render
  }

  def simpleCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val collapse = UdashCollapse()(
      div(BootstrapStyles.Well.well)(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    )
    collapse.listen { case ev => events.append(ev) }

    val toggleButton = UdashButton(buttonStyle = ButtonStyle.Primary)(collapse.toggleButtonAttrs(), "Toggle...")
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case _ =>
      collapse.show()
      window.setTimeout(() => collapse.hide(), 2000)
    }
    div(GuideStyles.frame)(
      UdashButtonGroup(justified = true)(
        toggleButton.render,
        openAndCloseButton.render
      ).render,
      collapse.render
    ).render
  }

  def accordionCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val news = SeqProperty[String](
      "Title 1", "Title 2", "Title 3"
    )

    val accordion = UdashAccordion(news)(
      (news) => span(news.get).render,
      (_) => div(BootstrapStyles.Panel.panelBody)(
        div(BootstrapStyles.Well.well)(
          ul(repeat(events)(event => li(event.get.toString).render))
        )
      ).render
    )

    val accordionElement = accordion.render
    news.elemProperties.map(news => {
      accordion.collapseOf(news)
    }).filter(_.isDefined)
      .foreach(_.get.listen { case ev => events.append(ev) })

    div(GuideStyles.frame)(
        accordionElement
    ).render
  }

  def carousel(): dom.Element = {
    def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
      Url("/assets/images/ext/bootstrap/carousel.jpg")
    )(
      h3(randomString()),
      p(randomString())
    )
    val slides = SeqProperty[UdashCarouselSlide]((1 to 5).map(_ => newSlide()))
    val active = Property(true)
    import scala.concurrent.duration._
    val carousel = UdashCarousel(slides, activeSlide = 1,
      animationOptions = AnimationOptions(interval = 2 seconds, keyboard = false, active = active.get)
    )
    val prevButton = UdashButton()("Prev")
    val nextButton = UdashButton()("Next")
    val prependButton = UdashButton()("Prepend")
    val appendButton = UdashButton()("Append")
    prevButton.listen { case _ => carousel.previousSlide() }
    nextButton.listen { case _ => carousel.nextSlide() }
    prependButton.listen { case _ => slides.prepend(newSlide()) }
    appendButton.listen { case _ => slides.append(newSlide()) }
    active.listen(b => if (b) carousel.cycle() else carousel.pause())
    div(
      div(GuideStyles.frame)(
        UdashButtonToolbar(
          UdashButton.toggle(active = active)("Run animation").render,
          UdashButtonGroup()(
            prevButton.render,
            nextButton.render
          ).render,
          UdashButtonGroup()(
            prependButton.render,
            appendButton.render
          ).render
        ).render
      ),
      div(
        carousel.render
      )
    ).render
  }

  def jumbotron(): dom.Element =
    UdashJumbotron(
      h1("Jumbo poem!"),
      p("One component to rule them all, one component to find them, one component to bring them all and in the darkness bind them."),
      UdashButton(ButtonStyle.Info, size = ButtonSize.Large)("Click").render
    ).render

  private def randomString(): String = Random.nextLong().toString
}
