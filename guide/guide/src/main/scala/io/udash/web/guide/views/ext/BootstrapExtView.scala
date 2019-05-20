package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.BootstrapDemos
import io.udash.web.guide.views.ext.demo.bootstrap.{DatePickerDemo, DateRangePickerDemo, IconsDemo, StaticsDemo}
import io.udash.web.guide.views.{References, Versions}
import scalatags.JsDom

case object BootstrapExtViewFactory extends StaticViewFactory[BootstrapExtState.type](() => new BootstrapExtView)


class BootstrapExtView extends FinalView {
  import JsDom.all._

  private val (staticsDemo, staticsSnippet) = StaticsDemo.demoWithSnippet()
  private val (iconsDemo, iconsSnippet) = IconsDemo.demoWithSnippet()
  private val (datePickerDemo, datePickerSnippet) = DatePickerDemo.demoWithSnippet()
  private val (dateRangePickerDemo, dateRangePickerSnippet) = DateRangePickerDemo.demoWithSnippet()

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
    CodeBlock(
      s"""val responsive = Property[Option[ResponsiveBreakpoint]](Some(ResponsiveBreakpoint.All))
         |val dark = Property(false)
         |val striped = Property(true)
         |val bordered = Property(true)
         |val hover = Property(true)
         |val small = Property(false)
         |
         |val darkButton = UdashButton.toggle(active = dark)("Dark theme")
         |val stripedButton = UdashButton.toggle(active = striped)("Striped")
         |val borderedButton = UdashButton.toggle(active = bordered)("Bordered")
         |val hoverButton = UdashButton.toggle(active = hover)("Hover")
         |val smallButton = UdashButton.toggle(active = small)("Small")
         |
         |val items = SeqProperty(
         |  Seq.fill(7)((Random.nextDouble(), Random.nextDouble(), Random.nextDouble()))
         |)
         |
         |val table = UdashTable(
         |  items, responsive, dark,
         |  striped = striped,
         |  bordered = bordered,
         |  hover = hover,
         |  small = small
         |)(
         |  headerFactory = Some(_ => tr(Seq("x", "y", "z").map(header => th(b(header)))).render),
         |  rowFactory = (el, nested) => tr(
         |    nested(produce(el)(v => Seq(v._1, v._2, v._3).map(td(_).render)))
         |  ).render
         |)
         |
         |div(
         |  UdashButtonGroup(justified = true.toProperty)(
         |    darkButton.render,
         |    stripedButton.render,
         |    borderedButton.render,
         |    hoverButton.render,
         |    smallButton.render
         |  ).render,
         |  table.render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.tables()
    ),
    h3("Dropdowns"),
    p("You can create dynamic dropdowns using ", i("SeqProperty"), "-based ", i("UdashDropdown"),
      ". It allows listening on item selection and using custom item renderers."),
    p("The example below shows a simple dropup using default renderer and item styles. A new item is added every 5 seconds, ",
      "item selections are recorded and displayed underneath."),
    CodeBlock(
      s"""|val items = SeqProperty[UdashDropdown.DefaultDropdownItem](Seq(
          |  UdashDropdown.DropdownHeader("Start"),
          |  UdashDropdown.DropdownLink("Intro", Url(IntroState.url)),
          |  UdashDropdown.DropdownDisabled(UdashDropdown.DropdownLink("Test Disabled", url)),
          |  UdashDropdown.DropdownDivider,
          |  UdashDropdown.DropdownHeader("Dynamic")
          |))
          |
          |val clicks = SeqProperty[String](Seq.empty)
          |var i = 1
          |val appendHandler = window.setInterval(() => {
          |  items.append(UdashDropdown.DropdownLink(s"Test $i", url))
          |  i += 1
          |}, 5000)
          |window.setTimeout(() => window.clearInterval(appendHandler), 60000)
          |
          |val dropdown = UdashDropdown(items)(
          |  UdashDropdown.defaultItemFactory,
          |  _ => Seq[Modifier]("Dropdown ", BootstrapStyles.Button.color(Color.Primary))
          |)
          |val dropup = UdashDropdown(items, UdashDropdown.Direction.Up.toProperty)(
          |  UdashDropdown.defaultItemFactory, _ => "Dropup "
          |)
          |val listener = {
          |  case UdashDropdown.DropdownEvent.SelectionEvent(_, item) =>
          |    clicks.append(item.toString)
          |  case ev: DropdownEvent[_, _] =>
          |    logger.info(ev.toString)
          |}
          |dropdown.listen(listener)
          |dropup.listen(listener)
          |
          |div(
          |  div(BootstrapStyles.Grid.colXs6)(dropdown.render),
          |  div(BootstrapStyles.Grid.colXs6)(dropup.render)
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")( //force Boostrap styles
      BootstrapDemos.dropdown()
    ),
    h3("Button"),
    p("Bootstrap buttons are easy to use as ", i("UdashButton"), "s. They support click listening, ",
      "provide type-safe style & size classes and a ", i("Property"), "-based mechanism for activation and disabling."),
    p("This example shows a variety of available button options. Small button indicators register their clicks and are ",
      "randomly set as active or disabled by the block button action, which also clears the click history."),
    CodeBlock(
      s"""|val smallBtn = Some(Size.Small).toProperty[Option[Size]]
          |val disabledButtons = Property(Set.empty[Int])
          |def disabled(idx: Int): ReadableProperty[Boolean] = disabledButtons.transform(_.contains(idx))
          |
          |val buttons = Color.values.map(color =>
          |  UdashButton(color.toProperty, smallBtn, disabled = disabled(color.ordinal))(_ => Seq[Modifier](color.name, GlobalStyles.smallMargin))
          |)
          |
          |val clicks = SeqProperty[String](Seq.empty)
          |buttons.foreach(_.listen {
          |  case UdashButton.ButtonClickEvent(source, _) => clicks.append(source.render.textContent)
          |})
          |
          |val push = UdashButton(size = Some(Size.Large).toProperty[Option[Size]], block = true.toProperty)(
          |  "Disable random buttons!"
          |)
          |push.listen { case UdashButton.ButtonClickEvent(_, _) =>
          |  clicks.set(Seq.empty)
          |  val disabledCount = Random.nextInt(buttons.size + 1)
          |  disabledButtons.set(Seq.fill(disabledCount)(Random.nextInt(buttons.size)).toSet)
          |}
          |
          |div(
          |  push.render,
          |  buttons.map(_.render)
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.buttonsDemo()
    ),
    p("The example below presents helper method for creating toggle buttons."),
    CodeBlock(
      s"""|val buttons = Color.values.map { color =>
          |  color.name -> {
          |    val active = Property(false)
          |    val btn = UdashButton.toggle(active, color.toProperty[Color])(_ =>
          |      Seq[Modifier](color.name, GlobalStyles.smallMargin)
          |    )
          |    (active, btn)
          |  }
          |}
          |
          |div(
          |  buttons.map(_.render)
          |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.toggleButton()
    ),
    h3("Button groups"),
    p("There are many ways of creating a button group. The first example presents static API usage:"),
    CodeBlock(
      s"""div(
         |  UdashButtonGroup(vertical = true.toProperty)(
         |    UdashButton(buttonStyle = Color.Primary.toProperty)("Button 1").render,
         |    UdashButton()("Button 2").render,
         |    UdashButton()("Button 3").render
         |  ).render
         |).render
         |""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.staticButtonsGroup()
    ),
    p("It is also possible to create reactive groups and toolbars:"),
    CodeBlock(
      s"""val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))
         |UdashButtonToolbar.reactive(groups)((p: CastableProperty[Seq[Int]], nested) => {
         |  val range = p.asSeq[Int]
         |  val group = UdashButtonGroup.reactive(range, size = Some(Size.Large).toProperty[Option[Size]]) {
         |    case (element, nested) =>
         |      val btn = UdashButton()(_ => nested(bind(element)))
         |      nested(btn)
         |      btn.render
         |  }
         |  nested(group)
         |  group.render
         |}).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.buttonToolbar()
    ),
    p("Use ", i("checkboxes"), " method in order to create a group of buttons behaving as checkboxes:"),
    CodeBlock(
      s"""import UdashButtonGroup._
         |val options = SeqProperty[String]("Checkbox 1", "Checkbox 2", "Checkbox 3")
         |val selected = SeqProperty[String](options.get.head)
         |div(
         |  div(BootstrapStyles.Spacing.margin(side = Side.Bottom, size = SpacingSize.Normal))(
         |    UdashButtonGroup.checkboxes(selected, options)().render
         |  ),
         |  h4("Is active: "),
         |  div(wellStyles)(
         |    repeatWithNested(options) { (option, nested) =>
         |      val checked = selected.transform((_: Seq[String]).contains(option.get))
         |      div(nested(bind(option)), ": ", nested(bind(checked))).render
         |    }
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.checkboxButtons()
    ),
    p("The following example presents a group of buttons behaving as radio buttons:"),
    CodeBlock(
      s"""import UdashButtonGroup._
         |val options = SeqProperty[String]("Radio 1", "Radio 2", "Radio 3")
         |val selected = Property[String](options.get.head)
         |div(
         |  div(BootstrapStyles.Spacing.margin(side = Side.Bottom, size = SpacingSize.Normal))(
         |    UdashButtonGroup.radio(selected, options)().render
         |  ),
         |  h4("Is active: "),
         |  div(wellStyles)(
         |    repeatWithNested(options) { (option, nested) =>
         |      val checked = selected.transform(_ == option.get)
         |      div(nested(bind(option)), ": ", nested(bind(checked))).render
         |    }
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.radioButtons()
    ),
    h3("Button dropdowns"),
    p("The ", i("UdashDropdown"), " component can be used as part of a button group."),
    CodeBlock(
      s"""import UdashButtonGroup._
         |val items = SeqProperty[DefaultDropdownItem](
         |  UdashDropdown.DefaultDropdownItem.Header("Start"),
         |  UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#")),
         |  UdashDropdown.DefaultDropdownItem.Disabled(
         |    UdashDropdown.DefaultDropdownItem.Link("Test Disabled", Url("#"))
         |  ),
         |  UdashDropdown.DefaultDropdownItem.Divider,
         |  UdashDropdown.DefaultDropdownItem.Header("End"),
         |  UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#"))
         |)
         |div(
         |  UdashButtonToolbar()(
         |    UdashButtonGroup()(
         |      UdashButton()("Button").render,
         |      UdashDropdown(items)(UdashDropdown.defaultItemFactory, _ => "").render,
         |      UdashDropdown(items, dropDirection = UdashDropdown.Direction.Up.toProperty)(
         |        UdashDropdown.defaultItemFactory, _ => ""
         |      ).render
         |    ).render,
         |    UdashDropdown(items)(UdashDropdown.defaultItemFactory, _ => "Dropdown ").render
         |  ).render
         |).render
       """.stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.buttonDropdown()
    ),
    h3("Input groups"),
    p(
      i("UdashInputGroup"), " groups input elements into one component. It also provides convinient methods for creating the elements structure: ",
      i("input"), " for wrapping input elements, ", i("addon"), " for text elements and ", i("buttons"), " buttons."
    ),
    CodeBlock(
      s"""val vanityUrl = Property.blank[String]
         |val buttonDisabled = Property(true)
         |vanityUrl.listen(v => buttonDisabled.set(v.isEmpty))
         |val button = UdashButton()("Clear")
         |button.listen { case _ => vanityUrl.set("") }
         |div(
         |  label("Your URL"),
         |  UdashInputGroup(groupSize = Some(BootstrapStyles.Size.Large).toProperty)(
         |    UdashInputGroup.prependText("https://example.com/users/", bind(vanityUrl)),
         |    UdashInputGroup.input(TextInput(vanityUrl)().render),
         |    UdashInputGroup.append(
         |      UdashButton(disabled = buttonDisabled)("Go!").render,
         |      button.render
         |    )
         |  ).render
         |).render
         |""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.inputGroups()
    ),
    h3("Forms"),
    p(i("UdashForm"), " provides a lot of convenience methods for creating forms."),
    CodeBlock(
      s"""/** Omitting: ShirtSize, shirtSizeToLabel, labelToShirtSize */
         |trait UserModel {
         |  def name: String
         |  def age: Int
         |  def shirtSize: ShirtSize
         |}
         |object UserModel extends HasModelPropertyCreator[UserModel] {
         |  implicit val blank: Blank[UserModel] = Blank.Simple(new UserModel {
         |    override def name: String = ""
         |    override def age: Int = 25
         |    override def shirtSize: ShirtSize = Medium
         |  })
         |}
         |
          |val user = ModelProperty.blank[UserModel]
         |user.subProp(_.age).addValidator(new Validator[Int] {
         |  override def apply(element: Int): Future[ValidationResult] =
         |    Future {
         |      if (element < 0) Invalid("Age should be a non-negative integer!")
         |      else Valid
         |    }
         |})
         |
          |div(
         |  UdashForm()(factory => Seq(
         |    factory.input.formGroup()(
         |      input = _ => factory.input.textInput(user.subProp(_.name))().render,
         |      labelContent = Some(_ => "User name": Modifier)
         |    ),
         |    factory.input.formGroup()(
         |      input = _ => factory.input.numberInput(
         |        user.subProp(_.age).transform(_.toString, _.toInt),
         |      )().render,
         |      labelContent = Some(_ => "Age": Modifier),
         |      invalidFeedback = Some(_ => "Age should be a non-negative integer!")
         |    ),
         |    factory.input.radioButtons(
         |      user.subProp(_.shirtSize),
         |      Seq[ShirtSize](Small, Medium, Large).toSeqProperty,
         |      inline = true.toProperty,
         |      validationTrigger = UdashForm.ValidationTrigger.None
         |    )(labelContent = (item, _, _) => Some(label(shirtSizeToLabel(item)))),
         |    factory.disabled()(_ => UdashButton()("Send").render)
         |  )).render
         |).render""".stripMargin
    )(GuideStyles),
    ForceBootstrap(
      BootstrapDemos.simpleForm()
    ),
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