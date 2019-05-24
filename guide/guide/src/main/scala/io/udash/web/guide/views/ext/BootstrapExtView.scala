package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.BootstrapDemos
import io.udash.web.guide.views.ext.demo.bootstrap._
import io.udash.web.guide.views.{References, Versions}
import scalatags.JsDom

case object BootstrapExtViewFactory extends StaticViewFactory[BootstrapExtState.type](() => new BootstrapExtView)


class BootstrapExtView extends FinalView {
  import JsDom.all._

  private val (staticsDemo, staticsSnippet) = StaticsDemo.demoWithSnippet()
  private val (iconsDemo, iconsSnippet) = IconsDemo.demoWithSnippet()
  private val (datePickerDemo, datePickerSnippet) = DatePickerDemo.demoWithSnippet()
  private val (dateRangePickerDemo, dateRangePickerSnippet) = DateRangePickerDemo.demoWithSnippet()
  private val (tableDemo, tableSnippet) = TableDemo.demoWithSnippet()
  private val (dropdownsDemo, dropdownsSnippet) = DropdownsDemo.demoWithSnippet()
  private val (buttonsDemo, buttonsSnippet) = ButtonsDemo.demoWithSnippet()
  private val (toggleButtonsDemo, toggleButtonsSnippet) = ToggleButtonsDemo.demoWithSnippet()
  private val (staticButtonsGroupDemo, staticButtonsGroupSnippet) = StaticButtonsGroupDemo.demoWithSnippet()
  private val (buttonToolbarDemo, buttonToolbarSnippet) = ButtonToolbarDemo.demoWithSnippet()
  private val (checkboxButtonsDemo, checkboxButtonsSnippet) = CheckboxButtonsDemo.demoWithSnippet()
  private val (radioButtonsDemo, radioButtonsSnippet) = RadioButtonsDemo.demoWithSnippet()
  private val (buttonDropdownDemo, buttonDropdownSnippet) = ButtonDropdownDemo.demoWithSnippet()
  private val (inputGroupDemo, inputGroupSnippet) = InputGroupDemo.demoWithSnippet()
  private val (simpleFormDemo, simpleFormSnippet) = SimpleFormDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h1("Udash Bootstrap Components"),
    h2("First steps"),
    p("To start development with the Bootstrap wrapper add the following line in you frontend module dependencies: "),
    CodeBlock(
      s""""io.udash" %%% "udash-bootstrap" % "${Versions.udashVersion}"""".stripMargin
    )(GuideStyles),
    p("The wrapper provides a typed equivalent of the ", a(href := References.BootstrapHomepage)("Twitter Bootstrap"), " API."),
    h2("Statics"),
    p(
      "All Bootstrap tags and styles are available as UdashCSS styles. If you want to use ",
      i("BootstrapStyles"), " import ", i("io.udash.css.CssView._"), ". It enables implicit conversion ",
      "from these styles into Scalatags modifiers."
    ),
    staticsSnippet,
    ForceBootstrap(staticsDemo),
    h2("Components"),
    p("The ", i("UdashBootstrapComponent"), " hierarchy enables you to seamlessly use Bootstrap components and integrate ",
      "them with your Udash app, both in a completely type-safe way."),
    h3("Glyphicons & FontAwesome"),
    p("The icons from ", i("Glyphicons"), " and ", i("FontAwesome"), " packages are accessible in ", i("Icons"), " object."),
    iconsSnippet,
    ForceBootstrap(iconsDemo),
    h3("Date Picker"),
    datePickerSnippet,
    ForceBootstrap(datePickerDemo),
    p("It is possible to create a date range selector from two pickers."),
    dateRangePickerSnippet,
    ForceBootstrap(dateRangePickerDemo),
    h3("Tables"),
    tableSnippet,
    ForceBootstrap(tableDemo),
    h3("Dropdowns"),
    p("You can create dynamic dropdowns using ", i("SeqProperty"), "-based ", i("UdashDropdown"),
      ". It allows listening on item selection and using custom item renderers."),
    p("The example below shows a simple dropup using default renderer and item styles. A new item is added every 5 seconds, ",
      "item selections are recorded and displayed underneath."),
    dropdownsSnippet,
    ForceBootstrap(dropdownsDemo),
    h3("Button"),
    p("Bootstrap buttons are easy to use as ", i("UdashButton"), "s. They support click listening, ",
      "provide type-safe style & size classes and a ", i("Property"), "-based mechanism for activation and disabling."),
    p("This example shows a variety of available button options. Small button indicators register their clicks and are ",
      "randomly set as active or disabled by the block button action, which also clears the click history."),
    buttonsSnippet,
    ForceBootstrap(buttonsDemo),
    p("The example below presents helper method for creating toggle buttons."),
    toggleButtonsSnippet,
    ForceBootstrap(toggleButtonsDemo),
    h3("Button groups"),
    p("There are many ways of creating a button group. The first example presents static API usage:"),
    staticButtonsGroupSnippet,
    ForceBootstrap(staticButtonsGroupDemo),
    p("It is also possible to create reactive groups and toolbars:"),
    buttonToolbarSnippet,
    ForceBootstrap(buttonToolbarDemo),
    p("Use ", i("checkboxes"), " method in order to create a group of buttons behaving as checkboxes:"),
    checkboxButtonsSnippet,
    ForceBootstrap(checkboxButtonsDemo),
    p("The following example presents a group of buttons behaving as radio buttons:"),
    radioButtonsSnippet,
    ForceBootstrap(radioButtonsDemo),
    h3("Button dropdowns"),
    p("The ", i("UdashDropdown"), " component can be used as part of a button group."),
    buttonDropdownSnippet,
    ForceBootstrap(buttonDropdownDemo),
    h3("Input groups"),
    p(
      i("UdashInputGroup"), " groups input elements into one component. It also provides convinient methods for creating the elements structure: ",
      i("input"), " for wrapping input elements, ", i("addon"), " for text elements and ", i("buttons"), " buttons."
    ),
    inputGroupSnippet,
    ForceBootstrap(inputGroupDemo),
    h3("Forms"),
    p(i("UdashForm"), " provides a lot of convenience methods for creating forms."),
    simpleFormSnippet,
    ForceBootstrap(simpleFormDemo),
    p("It is also possible to create an ", i("inline"), " or ", i("horizontal"), " form."),
    CodeBlock(
      s"""val search = Property.blank[String]
         |val something = Property.blank[String]
         |
         |UdashForm(inline = true)(factory => Seq(
         |  UdashInputGroup()(
         |    UdashInputGroup.prependText("Search: "),
         |    UdashInputGroup.input(factory.input.textInput(search)().render)
         |  ).render,
         |  UdashInputGroup()(
         |    UdashInputGroup.prependText("Something: "),
         |    UdashInputGroup.input(factory.input.textInput(something)().render)
         |  ).render,
         |)).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.inlineForm()
    ),
    h3("Navs"),
    CodeBlock(
      s"""trait Panel {
         |  def title: String
         |  def content: String
         |}
         |object Panel extends HasModelPropertyCreator[Panel]
         |final case class DefaultPanel(override val title: String, override val content: String) extends Panel
         |val panels = SeqProperty[Panel](
         |  DefaultPanel("Title 1", "Content of panel 1..."),
         |  DefaultPanel("Title 2", "Content of panel 2..."),
         |  DefaultPanel("Title 3", "Content of panel 3..."),
         |  DefaultPanel("Title 4", "Content of panel 4...")
         |)
         |val selected = Property[Panel](panels.elemProperties.head.get)
         |panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
         |div(
         |  UdashNav(panels, justified = true.toProperty, tabs = true.toProperty)(
         |    elemFactory = (panel, nested) => a(
         |      BootstrapStyles.Navigation.link,
         |      href := "",
         |      onclick :+= ((_: Event) => selected.set(panel.get), true)
         |    )(nested(bind(panel.asModel.subProp(_.title)))).render,
         |    isActive = panel => panel.combine(selected)((panel, selected) => panel.title == selected.title)
         |  ).render,
         |  div(wellStyles)(
         |    bind(selected.asModel.subProp(_.content))
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.navs()
    ),
    h3("Navbar"),
    CodeBlock(
      s"""
         |trait Panel {
         |  def title: String
         |  def content: String
         |}
         |object Panel extends HasModelPropertyCreator[Panel]
         |final case class DefaultPanel(override val title: String, override val content: String) extends Panel
         |val panels = SeqProperty[Panel](
         |  DefaultPanel("Title 1", "Content of panel 1..."),
         |  DefaultPanel("Title 2", "Content of panel 2..."),
         |  DefaultPanel("Title 3", "Content of panel 3..."),
         |  DefaultPanel("Title 4", "Content of panel 4...")
         |)
         |panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
         |div(
         |  UdashNavbar()(
         |    _ => UdashNav(panels)(
         |      elemFactory = (panel, nested) => a(
         |        BootstrapStyles.Navigation.link,
         |        href := "",
         |        onclick :+= ((_: Event) => true)
         |      )(
         |        nested(bind(panel.asModel.subProp(_.title)))
         |      ).render,
         |      isActive = el => el.transform(_.title.endsWith("1")),
         |      isDisabled = el => el.transform(_.title.endsWith("5"))
         |    ),
         |    span("Udash"),
         |  ).render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.navbars()
    ),
    p("The following example presents a navbar with a dropdown item. It uses menu of this guide."),
    CodeBlock(
      s"""def linkFactory(l: MenuLink, dropdown: Boolean = true) =
         |  a(
         |    href := l.state.url,
         |    BootstrapStyles.Dropdown.item.styleIf(dropdown),
         |    BootstrapStyles.Navigation.link.styleIf(!dropdown)
         |  )(span(l.name)).render
         |
          |val panels = SeqProperty[MenuEntry](mainMenuEntries.slice(0, 4))
         |div(
         |  UdashNavbar(darkStyle = true.toProperty, backgroundStyle = BootstrapStyles.Color.Dark.toProperty)(
         |    _ => UdashNav(panels)(
         |      elemFactory = (panel, nested) => div(nested(produce(panel) {
         |        case MenuContainer(name, children) =>
         |          val childrenProperty = SeqProperty(children)
         |          UdashDropdown(childrenProperty, buttonToggle = false.toProperty)(
         |            (item: Property[MenuLink], _) => linkFactory(item.get),
         |            _ => span(name, " ")
         |          ).render.setup(_.firstElementChild.applyTags(BootstrapStyles.Navigation.link))
         |        case link: MenuLink => linkFactory(link, dropdown = false)
         |      })).render,
         |      isDropdown = _.transform {
         |        case MenuContainer(_, _) => true
         |        case MenuLink(_, _) => false
         |      }
         |    ),
         |    span("Udash"),
         |  ).render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.udashNavigation()
    ),
    h3("Breadcrumbs"),
    CodeBlock(
      s"""
         |import UdashBreadcrumbs._
         |val pages = SeqProperty[Breadcrumb](
         |  new Breadcrumb("Udash", Url("http://udash.io/")),
         |  new Breadcrumb("Dev's Guide", Url("http://guide.udash.io/")),
         |  new Breadcrumb("Extensions", Url("http://guide.udash.io/")),
         |  new Breadcrumb("Bootstrap wrapper", Url("http://guide.udash.io/ext/bootstrap"))
         |).readable
         |val breadcrumbs = UdashBreadcrumbs(pages)(
         |  (pageProperty, nested) => nested(produce(pageProperty) { page =>
         |    if (pages.get.last == page) JsDom.StringFrag(page.name).render
         |    else a(href := page.link)(page.name).render
         |  }),
         |  pages.get.last == _
         |)
         |div(
         |  breadcrumbs.render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.breadcrumbs()
    ),
    h3("Pagination"),
    CodeBlock(
      s"""import UdashPagination._
         |
          |val showArrows = Property(true)
         |val highlightActive = Property(true)
         |val toggleArrows = UdashButton.toggle(active = showArrows)("Toggle arrows")
         |val toggleHighlight = UdashButton.toggle(active = highlightActive)("Toggle highlight")
         |
          |val pages = SeqProperty(0 to 7)
         |val selected = Property(0)
         |val pagination = UdashPagination(
         |  pages, selected,
         |  showArrows = showArrows, highlightActive = highlightActive
         |)(defaultPageFactory).render.setup(_.firstElementChild.applyTags(
         |  BootstrapStyles.Flex.justifyContent(
         |    BootstrapStyles.FlexContentJustification.Center
         |  )
         |))
         |div(
         |  div(BootstrapStyles.Spacing.margin(
         |    side = Side.Bottom, size = SpacingSize.Normal
         |  ))(
         |    UdashButtonGroup()(
         |      toggleArrows.render,
         |      toggleHighlight.render
         |    ).render
         |  ),
         |  div(BootstrapStyles.Spacing.margin(
         |    side = Side.Bottom, size = SpacingSize.Normal
         |  ))("Selected page index: ", bind(selected)),
         |  div(pagination)
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.pagination()
    ),
    h3("Labels"),
    CodeBlock(
      s"""UdashBadge(badgeStyle = BootstrapStyles.Color.Primary.toProperty)(_ => "Primary").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Secondary.toProperty, pillStyle = true.toProperty)(_ => "Secondary Pill").render,
         |UdashBadge.link(Property("https://udash.io/"), badgeStyle = BootstrapStyles.Color.Success.toProperty)(_ =>"Success Link").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Danger.toProperty)(_ => "Danger").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Warning.toProperty)(_ => "Warning").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Info.toProperty)(_ => "Info").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Light.toProperty)(_ => "Light").render,
         |UdashBadge(badgeStyle = BootstrapStyles.Color.Dark.toProperty)(_ => "Dark").render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.labels()
    ),
    h3("Badges"),
    CodeBlock(
      s"""
         |val counter = Property(0)
         |window.setInterval(() => counter.set(counter.get + 1), 3000)
         |div(GuideStyles.frame)(
         |  div(
         |    UdashButton(
         |      buttonStyle = BootstrapStyles.Color.Primary.toProperty,
         |      size = Some(BootstrapStyles.Size.Large).toProperty
         |    )(_ => Seq[Modifier]("Button ", UdashBadge()(nested => nested(bind(counter))).render)
              ).render
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.badges()
    ),
    h3("Jumbotron"),
    p("A lightweight, flexible component that can optionally extend the entire viewport to showcase key content on your site."),
    CodeBlock(
      s"""UdashJumbotron()( _ => Seq[Modifier](
         |  h1("Jumbo poem!"),
         |  p("One component to rule them all, one component to find them, one component to bring them all and in the darkness bind them."),
         |  UdashButton(buttonStyle = Color.Info.toProperty, size = Some(Size.Large).toProperty[Option[Size]])(_ => "Click").render
         |)).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(
      BootstrapDemos.jumbotron()
    ),
    h3("Alerts"),
    p("The ", i("UdashAlert")," component supports both regular and dismissible Bootstrap alerts with type-safe styling and ",
      i("Property"), "-based dismissal mechanism."),
    CodeBlock(
      s"""|val dismissed = SeqProperty[String](Seq.empty)
          |def randomDismissible(): dom.Element = {
          |  val title = randomString()
          |  val alert = DismissibleUdashAlert(
          |    alertStyle = BootstrapStyles.Color.values(Random.nextInt(BootstrapStyles.Color.values.size)).toProperty
          |  )(title)
          |  alert.dismissed.listen(_ => dismissed.append(title))
          |  alert.render
          |}
          |val alerts = div(GlobalStyles.centerBlock)(
          |  UdashAlert(alertStyle = BootstrapStyles.Color.Info.toProperty)("info").render,
          |  UdashAlert(alertStyle = BootstrapStyles.Color.Success.toProperty)("success").render,
          |  UdashAlert(alertStyle = BootstrapStyles.Color.Warning.toProperty)("warning").render,
          |  UdashAlert(alertStyle = BootstrapStyles.Color.Danger.toProperty)("danger").render
          |).render
          |val create = UdashButton()("Create dismissible alert")
          |create.listen { case _ => alerts.appendChild(randomDismissible()) }
          |div(
          |  alerts,
          |  create.render,
          |  div(BootstrapStyles.Spacing.margin(
          |    side = Side.Top, size = SpacingSize.Normal
          |  ))(
          |    h4("Dismissed: "),
          |    div(wellStyles)(produce(dismissed)(seq =>
          |      ul(seq.map(click => li(click))).render
          |    ))
          |  )
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.alerts()
    ),
    h3("Progress bars"),
    p("The ", i("UdashProgressBar"), " component provides a simple way to use built-in Bootstrap progress bars ",
      "with custom stringifiers and ", i("Property"), "-controlled value, percentage showing and animation."),
    CodeBlock(
      s"""|val showPercentage = Property(true)
          |val animate = Property(true)
          |val value = Property(50)
          |div(
          |  div(
          |    UdashButtonGroup()(
          |      UdashButton.toggle(active = showPercentage)("Show percentage").render,
          |      UdashButton.toggle(active = animate)("Animate").render
          |    ).render
          |  ), br,
          |  UdashProgressBar(value, showPercentage, barStyle = Some(BootstrapStyles.Color.Success).toProperty)().render,
          |  UdashProgressBar(value, showPercentage, stripped = true.toProperty)(
          |    (value, min, max, nested) => Seq[Modifier](
          |      nested(bind(value.combine(min)(_ - _).combine(max.combine(min)(_ - _))(_ * 100 / _))),
          |      " percent"
          |    )
          |  ).render,
          |  UdashProgressBar(value, showPercentage, stripped = true.toProperty, animated = animate,
          |    barStyle = Some(BootstrapStyles.Color.Danger).toProperty
          |  )().render,
          |  NumberInput(value.transform(_.toString, Integer.parseInt))(
          |    BootstrapStyles.Form.control, placeholder := "Percentage"
          |  )
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.progressBar()
    ),
    h3("List group"),
    CodeBlock(
      s"""import io.udash.bootstrap.BootstrapImplicits._
         |val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
         |
         |def newsStyle(newsProperty: Property[String]): ReadableProperty[String] = {
         |  newsProperty.transform(_.last match {
         |    case '1' => BootstrapStyles.active.className
         |    case '2' => BootstrapStyles.disabled.className
         |    case '3' => BootstrapStyles.List.color(BootstrapStyles.Color.Success).className
         |    case '4' => BootstrapStyles.List.color(BootstrapStyles.Color.Danger).className
         |    case '5' => BootstrapStyles.List.color(BootstrapStyles.Color.Info).className
         |    case '6' => BootstrapStyles.List.color(BootstrapStyles.Color.Warning).className
         |  })
         |}
         |val listGroup = UdashListGroup(news)((news, nested) =>
         |  li(nested(cls.bind(newsStyle(news))))(nested(bind(news))).render
         |)
         |
         |var i = 1
         |val appendHandler = window.setInterval(() => {
         |  news.append(s"Dynamic $i")
         |  i += 1
         |}, 2000)
         |window.setTimeout(() => window.clearInterval(appendHandler), 20000)
         |div(
         |  listGroup.render
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")( //force Boostrap styles
      BootstrapDemos.listGroup()
    ),
    h3("Card"),
    CodeBlock(
      s"""val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
         |div(
         |  UdashCard(
         |    borderColor = Some(BootstrapStyles.Color.Success).toProperty,
         |    textColor = Some(BootstrapStyles.Color.Primary).toProperty,
         |  )(factory => Seq(
         |    factory.header("Card heading"),
         |    factory.body("Content..."),
         |    factory.listGroup(nested => {
         |      val group = UdashListGroup(news)((news, nested) => li(nested(bind(news))).render)
         |      nested(group)
         |      group
         |    }),
         |    factory.footer("Card footer")
         |  )).render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.cards()
    ),
    h3("Responsive embed"),
    CodeBlock(
      s"""div(
         |  div(BootstrapStyles.EmbedResponsive.responsive,
         |      BootstrapStyles.EmbedResponsive.embed16by9)(
         |    iframe(BootstrapStyles.EmbedResponsive.item, src := "...")
         |  ),
         |  div(BootstrapStyles.EmbedResponsive.responsive,
         |      BootstrapStyles.EmbedResponsive.embed4by3)(
         |    iframe(BootstrapStyles.EmbedResponsive.item, src := "...")
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.responsiveEmbed()
    ),
    h3("Modals"),
    p(
      "The modal window constructor takes three optional methods as the arguments. The first one is used to create ",
      "a modal window's header, the second creates a body and the last produces a window's footer."
    ),
    p(
      "The ", i("UdashModal"), " class exposes methods for opening/hiding window. It is also possible to listen on window's events."
    ),
    CodeBlock(
      s"""|val events = SeqProperty.blank[UdashModal.ModalEvent]
          |val header = (_: Binding.NestedInterceptor) => div("Modal events").render
          |val body = (nested: Binding.NestedInterceptor) => div(BootstrapStyles.Spacing.margin())(
          |  ul(nested(repeat(events)(event => li(event.get.toString).render)))
          |).render
          |val footer = (_: Binding.NestedInterceptor) => div(
          |  UdashButton()(_ => Seq[Modifier](UdashModal.CloseButtonAttr, "Close")).render,
          |  UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Something...").render
          |).render
          |
          |val modal = UdashModal(modalSize = Some(BootstrapStyles.Size.Large).toProperty)(
          |  headerFactory = Some(header),
          |  bodyFactory = Some(body),
          |  footerFactory = Some(footer)
          |)
          |modal.listen { case ev => events.append(ev) }
          |
          |val openModalButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Show modal...")
          |openModalButton.listen { case UdashButton.ButtonClickEvent(_, _) =>
          |  modal.show()
          |}
          |val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
          |openAndCloseButton.listen { case UdashButton.ButtonClickEvent(_, _) =>
          |  modal.show()
          |  window.setTimeout(() => modal.hide(), 2000)
          |}
          |
          |div(
          |  modal.render,
          |  UdashButtonGroup()(
          |    openModalButton.render,
          |    openAndCloseButton.render
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.simpleModal()
    ),
    h3("Tooltips"),
    CodeBlock(
      s"""|import scala.concurrent.duration.DurationInt
          |val tooltipContainerId = ComponentId("tooltip-container")
          |val label1 = UdashBadge()(_ => Seq[Modifier]("Tooltip on hover with delay", GlobalStyles.smallMargin)).render
          |UdashTooltip(
          |  trigger = Seq(UdashTooltip.Trigger.Hover),
          |  delay = UdashTooltip.Delay(500 millis, 250 millis),
          |  title = (_) => "Tooltip...",
          |  container = Option("#" + tooltipContainerId)
          |)(label1)
          |
          |val label2 = UdashBadge()(_ => Seq[Modifier]("Tooltip on click", GlobalStyles.smallMargin)).render
          |UdashTooltip(
          |  trigger = Seq(UdashTooltip.Trigger.Click),
          |  delay = UdashTooltip.Delay(0 millis, 250 millis),
          |  placement = (_, _) => Seq(UdashTooltip.Placement.Bottom),
          |  title = (_) => "Tooltip 2...",
          |  container = Option("#" + tooltipContainerId)
          |)(label2)
          |
          |val label3 = UdashBadge()(_ => Seq[Modifier]("Tooltip with JS toggler", GlobalStyles.smallMargin)).render
          |val label3Tooltip = UdashTooltip(
          |  trigger = Seq(UdashTooltip.Trigger.Manual),
          |  placement = (_, _) => Seq(UdashTooltip.Placement.Right),
          |  title = (_) => "Tooltip 3...",
          |  container = Option("#" + tooltipContainerId)
          |)(label3)
          |
          |val button = UdashButton()("Toggle tooltip")
          |button.listen { case _ => label3Tooltip.toggle() }
          |
          |div(id := tooltipContainerId)(
          |  label1, label2, label3, button.render
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.tooltips()
    ),
    h3("Popovers"),
    CodeBlock(
      s"""
         |import scala.concurrent.duration.DurationInt
         |val popoverContainerId = ComponentId("popover-container")
         |val label1 = UdashBadge()(_ => Seq[Modifier]("Popover on hover with delay", GlobalStyles.smallMargin)).render
         |UdashPopover(
         |  trigger = Seq(UdashPopover.Trigger.Hover),
         |  delay = UdashPopover.Delay(500 millis, 250 millis),
         |  title = (_) => "Popover...",
         |  content = (_) => "Content...",
         |  container = Option("#" + popoverContainerId)
         |)(label1)
         |val label2 = UdashBadge()(_ => Seq[Modifier]("Popover on click", GlobalStyles.smallMargin)).render
         |UdashPopover(
         |  trigger = Seq(UdashPopover.Trigger.Click),
         |  delay = UdashPopover.Delay(0 millis, 250 millis),
         |  placement = (_, _) => Seq(UdashPopover.Placement.Bottom),
         |  title = (_) => "Popover 2...",
         |  content = (_) => "Content...",
         |  container = Option("#" + popoverContainerId)
         |)(label2)
         |val label3 = UdashBadge()(_ => Seq[Modifier]("Popover with JS toggler", GlobalStyles.smallMargin)).render
         |val label3Tooltip = UdashPopover(
         |  trigger = Seq(UdashPopover.Trigger.Manual),
         |  placement = (_, _) => Seq(UdashPopover.Placement.Left),
         |  html = true,
         |  title = (_) => "Popover 3...",
         |  content = (_) => {
         |    import scalatags.Text.all._
         |    Seq(
         |      p("HTML content..."),
         |      ul(li("Item 1"), li("Item 2"), li("Item 3"))
         |    ).render
         |  },
         |  container = Option("#" + popoverContainerId)
         |)(label3)
         |
         |val button = UdashButton()("Toggle popover")
         |button.listen { case _ => label3Tooltip.toggle() }
         |
         |div(id := popoverContainerId)(
         |  label1, label2, label3, button.render
         |).render
       """.stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.popovers()
    ),
    h3("Collapse"),
    p(
      i("UdashCollapse"), " represents element with toggle behaviour. It provides methods ",
      i("toggle"), ", ", i("open"), " and ", i("close"), " for manual manipulation and ",
      i("toggleButtonAttrs"), " for easy creation of toggle button."
    ),
    CodeBlock(
      s"""|val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
          |val collapse = UdashCollapse()(
          |  div(wellStyles)(
          |    ul(repeat(events)(event => li(event.get.toString).render))
          |  )
          |)
          |collapse.listen { case ev => events.append(ev) }
          |val toggleButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)(
          |  _ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle...")
          |)
          |val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
          |openAndCloseButton.listen { case _ =>
          |  collapse.show()
          |  window.setTimeout(() => collapse.hide(), 2000)
          |}
          |
          |div(
          |  UdashButtonGroup(justified = true.toProperty)(
          |    toggleButton.render,
          |    openAndCloseButton.render
          |  ).render,
          |  collapse.render
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.simpleCollapse()
    ),
    p(
      i("UdashAccordion"), " internally uses ", i("UdashCollapse"), ". It provides ",
      i("collapseOf"), " method for obtaining ", i("UdashCollapse"), " created for selected element."
    ),
    CodeBlock(
      s"""val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
         |val news = SeqProperty[String](
         |  "Title 1", "Title 2", "Title 3"
         |)
         |
          |val accordion = UdashAccordion(news)(
         |  (news, _) => span(news.get).render,
         |  (_, _) => div(wellStyles)(ul(repeat(events)(event => li(event.get.toString).render))).render
         |)
         |
          |val accordionElement = accordion.render
         |news.elemProperties.map(accordion.collapseOf)
         |  .filter(_.isDefined)
         |  .foreach(_.get.listen { case ev => events.append(ev) })
         |div(accordionElement).render
       """.stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.accordionCollapse()
    ),
    h3("Carousel"),
    p(
      i("UdashCarousel"), " is a slideshow component. It exposes its state (slides, current view) through ", i("Properties"),
      " and can be cycled through programatically."
    ),
    CodeBlock(
      s"""def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
         |  Url("/assets/images/ext/bootstrap/carousel.jpg")
         |)(
         |  h3(randomString()),
         |  p(randomString())
         |)
         |val slides = SeqProperty[UdashCarouselSlide]((1 to 5).map(_ => newSlide()))
         |val active = Property(true)
         |import scala.concurrent.duration._
         |val carousel = UdashCarousel(
         |  slides = slides,
         |  activeSlide = Property(1),
         |  animationOptions = Property(AnimationOptions(interval = 2 seconds, keyboard = false, active = active.get))
         |) { case (slide, nested) => nested(produce(slide)(_.render)) }
         |val prevButton = UdashButton()("Prev")
         |val nextButton = UdashButton()("Next")
         |val prependButton = UdashButton()("Prepend")
         |val appendButton = UdashButton()("Append")
         |prevButton.listen { case _ => carousel.previousSlide() }
         |nextButton.listen { case _ => carousel.nextSlide() }
         |prependButton.listen { case _ => slides.prepend(newSlide()) }
         |appendButton.listen { case _ => slides.append(newSlide()) }
         |active.listen(b => if (b) carousel.cycle() else carousel.pause())
         |div(
         |  div(
         |    UdashButtonToolbar()(
         |      UdashButton.toggle(active = active)("Run animation").render,
         |      UdashButtonGroup()(
         |        prevButton.render,
         |        nextButton.render
         |      ).render,
         |      UdashButtonGroup()(
         |        prependButton.render,
         |        appendButton.render
         |      ).render
         |    ).render
         |  ),
         |  div(
         |    carousel.render
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.carousel()
    ),
    h2("What's next?"),
    p("You can check the code for this page on our ", a(href := References.UdashGuideRepo)("GitHub repository"),
      ". It contains all the examples above and more, since UdashBootstrap is heavily used in the Udash Guide.")
  )
}