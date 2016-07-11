package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.BootstrapDemos
import io.udash.web.guide.views.{References, Versions}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLStyleElement

import scalatags.JsDom
import scalatags.JsDom.TypedTag

case object BootstrapExtViewPresenter extends DefaultViewPresenterFactory[BootstrapExtState.type](() => new BootstrapExtView)


class BootstrapExtView extends View {
  import JsDom.all._
  import scalacss.ScalatagsCss._
  import scalacss.Defaults._

  override def getTemplate: dom.Element = div(
    h1("Udash Bootstrap Components"),
    h2("First steps"),
    p("To start development with the Bootstrap wrapper add the following line in you frontend module dependencies: "),
    CodeBlock(
      s""""io.udash" %%% "udash-bootstrap" % "${Versions.udashVersion}"""".stripMargin
    )(GuideStyles),
    p("The wrapper provides a typed equivalent of the ", a(href := References.BootstrapHomepage)("Twitter Bootstrap"), " API."),
    h2("Statics"),
    p(s"All Bootstrap tags and styles are available as ScalaCSS applicable styles (", i("StyleA"), ")."),
    CodeBlock(
      s"""|div(BootstrapStyles.row)(
          |  div(BootstrapStyles.Grid.colXs9, BootstrapStyles.Well.well)(
          |    ".col-xs-9"
          |  ),
          |  div(BootstrapStyles.Grid.colXs4, BootstrapStyles.Well.well)(
          |    ".col-xs-4",br,
          |    "Since 9 + 4 = 13 > 12, this 4-column-wide div",
          |    "gets wrapped onto a new line as one contiguous unit."
          |  ),
          |  div(BootstrapStyles.Grid.colXs6, BootstrapStyles.Well.well)(
          |    ".col-xs-6",br,
          |    "Subsequent columns continue along the new line."
          |  )
          |)""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.statics()
    ),
    h2("Components"),
    p("The ", i("UdashBootstrapComponent"), " hierarchy enables you to seamlessly use Bootstrap components and integrate ",
      "them with your Udash app, both in a completely typesafe way."),
    h3("Glyphicons & FontAwesome"),
    p("The icons from ", i("Glyphicons"), " and ", i("FontAwesome"), " packages are accessible in ", i("Icons"), " object."),
    CodeBlock(
      s"""UdashBootstrap.loadFontAwesome(),
         |UdashButtonToolbar(
         |  UdashButtonGroup()(
         |    UdashButton()(Icons.Glyphicon.alignLeft).render,
         |    UdashButton()(Icons.Glyphicon.alignCenter).render,
         |    UdashButton()(Icons.Glyphicon.alignRight).render,
         |    UdashButton()(Icons.Glyphicon.alignJustify).render
         |  ).render,
         |  UdashButtonGroup()(
         |    UdashButton()(Icons.FontAwesome.bitcoin).render,
         |    UdashButton()(Icons.FontAwesome.euro).render,
         |    UdashButton()(Icons.FontAwesome.dollar).render
         |  ).render
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.icons()
    ),
    h3("Tables"),
    CodeBlock(
      s"""val striped = Property(true)
         |val bordered = Property(true)
         |val hover = Property(true)
         |val condensed = Property(false)
         |
         |val stripedButton = UdashButton.toggle(active = striped)("Striped")
         |val borderedButton = UdashButton.toggle(active = bordered)("Bordered")
         |val hoverButton = UdashButton.toggle(active = hover)("Hover")
         |val condensedButton = UdashButton.toggle(active = condensed)("Condensed")
         |
         |val items = SeqProperty(
         |  Seq.fill(7)((Random.nextDouble(), Random.nextDouble(), Random.nextDouble()))
         |)
         |val table = UdashTable(striped, bordered, hover, condensed)(items)(
         |  headerFactory = Some(() => tr(th(b("x")), th(b("y")), th(b("z"))).render),
         |  rowFactory = (el) => tr(
         |    td(produce(el)(v => i(v._1).render)),
         |    td(produce(el)(v => i(v._2).render)),
         |    td(produce(el)(v => i(v._3).render))
         |  ).render
         |)
         |
         |div(
         |  UdashButtonGroup(justified = true)(
         |    stripedButton.render,
         |    borderedButton.render,
         |    hoverButton.render,
         |    condensedButton.render
         |  ).render,
         |  table.render
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
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
          |val dropdown = UdashDropdown(items)(UdashDropdown.defaultItemFactory)(
          |  "Dropdown ", BootstrapStyles.Button.btnPrimary
          |)
          |val dropup = UdashDropdown.dropup(items)(UdashDropdown.defaultItemFactory)(
          |  "Dropup "
          |)
          |val listener: dropdown.EventHandler = {
          |  case UdashDropdown.SelectionEvent(_, item) =>
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
      "provide typesafe style & size classes and a ", i("Property"), "-based mechanism for activation and disabling."),
    p("This example shows a variety of available button options. Small button indicators register their clicks and are ",
      "randomly set as active or disabled by the block button action, which also clears the click history."),
    CodeBlock(
      s"""|val buttons = Seq(
          |  UdashButton(size = ButtonSize.Small)("Default"),
          |  UdashButton(ButtonStyle.Primary, ButtonSize.Small)("Primary"),
          |  UdashButton(ButtonStyle.Success, ButtonSize.Small)("Success"),
          |  UdashButton(ButtonStyle.Info, ButtonSize.Small)("Info") ,
          |  UdashButton(ButtonStyle.Warning, ButtonSize.Small)("Warning") ,
          |  UdashButton(ButtonStyle.Danger, ButtonSize.Small)("Danger"),
          |  UdashButton(ButtonStyle.Link, ButtonSize.Small)("Link")
          |)
          |
          |val clicks = SeqProperty[String](Seq.empty)
          |buttons.foreach(_.listen {
          |  case ev => clicks.append(ev.source.render.textContent)
          |})
          |
          |val push = UdashButton(size = ButtonSize.Large, block = true)(
          |  "Push the button!"
          |)
          |push.listen {
          |  case _ =>
          |    clicks.set(Seq.empty)
          |    buttons.foreach(button => {
          |      val random = Random.nextBoolean()
          |      button.disabled.set(random)
          |    })
          |}
          |
          |div(
          |  push.render,
          |  buttons.map(_.render)
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.button()
    ),
    p("The example below presents helper method for creating toggle buttons."),
    CodeBlock(
      s"""|val buttons = Seq(
          |  UdashButton.toggle()("Default"),
          |  UdashButton.toggle(ButtonStyle.Primary)("Primary"),
          |  UdashButton.toggle(ButtonStyle.Success)("Success"),
          |  UdashButton.toggle(ButtonStyle.Info)("Info"),
          |  UdashButton.toggle(ButtonStyle.Warning)("Warning") ,
          |  UdashButton.toggle(ButtonStyle.Danger)("Danger"),
          |  UdashButton.toggle(ButtonStyle.Link)("Link")
          |)
          |
          |div(
          |  buttons.map(_.render)
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.toggleButton()
    ),
    h3("Button groups"),
    p("There are many ways of creating a button group. The first example presents static API usage:"),
    CodeBlock(
      s"""UdashButtonGroup(vertical = true)(
          |  UdashButton(buttonStyle = ButtonStyle.Primary)("Button 1").render,
          |  UdashButton()("Button 2").render,
          |  UdashButton()("Button 3").render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.staticButtonsGroup()
    ),
    p("It is also possible to create reactive groups and toolbars:"),
    CodeBlock(
      s"""val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))
         |UdashButtonToolbar.reactive(groups, (p: CastableProperty[Seq[Int]]) => {
         |  val range = p.asSeq[Int]
         |  UdashButtonGroup.reactive(range, size = ButtonSize.Large)(element =>
         |    UdashButton()(element.get).render
         |  ).render
         |}).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.buttonToolbar()
    ),
    p("Use ", i("checkboxes"), " method in order to create a group of buttons behaving as checkboxes:"),
    CodeBlock(
      s"""import UdashButtonGroup._
          |val options = SeqProperty[CheckboxModel](
          |  DefaultCheckboxModel("Checkbox 1 (pre-checked)", true),
          |  DefaultCheckboxModel("Checkbox 2", false),
          |  DefaultCheckboxModel("Checkbox 3", false)
          |)
          |div(
          |  UdashButtonGroup.checkboxes(options).render,
          |  h4("Is active: "),
          |  div(BootstrapStyles.Well.well)(
          |    repeat(options)(option => {
          |      val model = option.asModel
          |      val name = model.subProp(_.text)
          |      val checked = model.subProp(_.checked)
          |      div(bind(name), ": ", bind(checked)).render
          |    })
          |  )
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.checkboxButtons()
    ),
    p("The following example presents a group of buttons behaving as radio buttons:"),
    CodeBlock(
      s"""import UdashButtonGroup._
         |val options = SeqProperty[CheckboxModel](
         |  DefaultCheckboxModel("Radio 1 (preselected)", true),
         |  DefaultCheckboxModel("Radio 2", false),
         |  DefaultCheckboxModel("Radio 3", false)
         |)
         |div(
         |  UdashButtonGroup.radio(options, justified = true).render,
         |  h4("Is active: "),
         |  div(BootstrapStyles.Well.well)(
         |    repeat(options)(option => {
         |      val model = option.asModel
         |      val name = model.subProp(_.text)
         |      val checked = model.subProp(_.checked)
         |      div(bind(name), ": ", bind(checked)).render
         |    })
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.radioButtons()
    ),
    h3("Button dropdowns"),
    p("The ", i("UdashDropdown"), " component can be used as part of a button group."),
    CodeBlock(
      s"""|val items = SeqProperty[DefaultDropdownItem](
          |  UdashDropdown.DropdownHeader("Start"),
          |  UdashDropdown.DropdownLink("Intro", Url("#")),
          |  UdashDropdown.DropdownDisabled(UdashDropdown.DropdownLink("Test Disabled", Url("#"))),
          |  UdashDropdown.DropdownDivider,
          |  UdashDropdown.DropdownHeader("End"),
          |  UdashDropdown.DropdownLink("Intro", Url("#"))
          |)
          |UdashButtonToolbar(
          |  UdashButtonGroup()(
          |    UdashButton()("Button").render,
          |    UdashDropdown(items)(UdashDropdown.defaultItemFactory)().render,
          |    UdashDropdown.dropup(items)(UdashDropdown.defaultItemFactory)().render
          |  ).render,
          |  UdashDropdown(items)(UdashDropdown.defaultItemFactory)("Dropdown ").render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.buttonDropdown()
    ),
    h3("Input groups"),
    p(
      i("UdashInputGroup"), " groups input elements into one component. It also provides convinient methods for creating the elements structure: ",
      i("input"), " for wrapping input elements, ", i("addon"), " for text elements and ", i("buttons"), " buttons."
    ),
    CodeBlock(
      s"""val vanityUrl = Property[String]
          |val buttonDisabled = Property(true)
          |vanityUrl.listen(v => buttonDisabled.set(v.isEmpty))
          |val button = UdashButton()("Clear")
          |button.listen{ case _ => vanityUrl.set("")}
          |div(
          |  label("Your URL"),
          |  UdashInputGroup(InputGroupSize.Large)(
          |    UdashInputGroup.addon("https://example.com/users/", bind(vanityUrl)),
          |    UdashInputGroup.input(TextInput.debounced(vanityUrl).render),
          |    UdashInputGroup.buttons(
          |      UdashButton(
          |        disabled = buttonDisabled
          |      )("Go!").render,
          |      button.render
          |    )
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
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
          |
         |val user = ModelProperty[UserModel]
          |user.subProp(_.name).set("")
          |user.subProp(_.age).set(25)
          |user.subProp(_.shirtSize).set(Medium)
          |user.subProp(_.age).addValidator(new Validator[Int] {
          |  def apply(element: Int)(implicit ec: ExecutionContext) =
          |    Future {
          |      if (element < 0) Invalid(Seq("Age should be a non-negative integer!"))
          |      else Valid
          |    }
          |})
          |
         |div(
          |  UdashForm(
          |    UdashForm.textInput()("User name")(user.subProp(_.name)),
          |    UdashForm.numberInput(
          |      validation = Some(UdashForm.validation(user.subProp(_.age)))
          |    )("Age")(user.subProp(_.age).transform(_.toString, _.toInt)),
          |    UdashForm.group(
          |      label("Shirt size"),
          |      UdashForm.radio(radioStyle = BootstrapStyles.Form.radioInline)(
          |        user.subProp(_.shirtSize)
          |          .transform(shirtSizeToLabel, labelToShirtSize),
          |        Seq(Small, Medium, Large).map(shirtSizeToLabel)
          |      )
          |    ),
          |    UdashForm.disabled()(UdashButton()("Send").render)
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.simpleForm()
    ),
    p("It is also possible to create an ", i("inline"), " or ", i("horizontal"), " form."),
    CodeBlock(
      s"""|val search = Property[String]
          |val something = Property[String]
          |div(
          |  UdashForm.inline(
          |    UdashForm.group(
          |      UdashInputGroup()(
          |        UdashInputGroup.addon("Search: "),
          |        UdashInputGroup.input(TextInput.debounced(search).render)
          |      ).render
          |    ),
          |    UdashForm.group(
          |      UdashInputGroup()(
          |        UdashInputGroup.addon("Something: "),
          |        UdashInputGroup.input(TextInput.debounced(something).render)
          |      ).render
          |    )
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.inlineForm()
    ),
    h3("Navs"),
    CodeBlock(
      s"""trait Panel {
         |  def title: String
         |  def content: String
         |}
         |case class DefaultPanel(override val title: String,
         |                        override val content: String) extends Panel
         |
         |val panels = SeqProperty[Panel](
         |  DefaultPanel("Title 1", "Content of panel 1..."),
         |  DefaultPanel("Title 2", "Content of panel 2..."),
         |  DefaultPanel("Title 3", "Content of panel 3..."),
         |  DefaultPanel("Title 4", "Content of panel 4...")
         |)
         |val selected = Property[Panel](panels.elemProperties.head.get)
         |panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
         |
         |div(
         |  UdashNav.tabs(justified = true)(panels)(
         |    elemFactory = (panel) => a(href := "", onclick :+= ((ev: Event) => {
         |      selected.set(panel.get)
         |      true
         |    }))(bind(panel.asModel.subProp(_.title))).render,
         |    isActive = (panel) => panel.combine(selected)(
         |      (panel, selected) => panel.title == selected.title
         |    )
         |  ).render,
         |  div(BootstrapStyles.Well.well)(
         |    bind(selected.asModel.subProp(_.content))
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.navs()
    ),
    h3("Navbar"),
    CodeBlock(
      s"""
         |trait Panel {
         |  def title: String
         |  def content: String
         |}
         |case class DefaultPanel(override val title: String, override val content: String) extends Panel
         |
         |val panels = SeqProperty[Panel](
         |  DefaultPanel("Title 1", "Content of panel 1..."),
         |  DefaultPanel("Title 2", "Content of panel 2..."),
         |  DefaultPanel("Title 3", "Content of panel 3..."),
         |  DefaultPanel("Title 4", "Content of panel 4...")
         |)
         |panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
         |div(
         |  UdashNavbar(
         |    div(BootstrapStyles.Navigation.navbarBrand)("Udash").render,
         |    UdashNav.navbar(panels)(
         |      elemFactory = (panel) => a(href := "", onclick :+= ((ev: Event) => true))(
         |        bind(panel.asModel.subProp(_.title))
         |      ).render,
         |      isActive = (el) => el.transform(_.title.endsWith("1")),
         |      isDisabled = (el) => el.transform(_.title.endsWith("5"))
         |    )
         |  ).render
       """.stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.navbars()
    ),
    p("The following example presents a navbar with a dropdown item. It uses menu of this guide."),
    CodeBlock(
      s"""def linkFactory(l: MenuLink) =
          |  a(href := l.state.url)(span(l.name)).render
          |
         |val panels = SeqProperty[MenuEntry](mainMenuEntries.slice(0, 4))
          |div(
          |  UdashNavbar.inverted(
          |    div(BootstrapStyles.Navigation.navbarBrand)("Udash").render,
          |    UdashNav.navbar(panels)(
          |      elemFactory = (panel) => panel.get match {
          |        case MenuContainer(name, children) =>
          |          val childrenProperty = SeqProperty(children)
          |          UdashDropdown(childrenProperty)(
          |            (item: Property[MenuLink]) => li(linkFactory(item.get)).render)(
          |            name, " "
          |          ).linkRender
          |        case link: MenuLink =>
          |          linkFactory(link)
          |      },
          |      isDropdown = (panel) => panel.transform {
          |        case MenuContainer(name, children) => true
          |        case MenuLink(name, state) => false
          |      }
          |    )
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.udashNavigation()
    ),
    h3("Breadcrumbs"),
    CodeBlock(
      s"""
         |import io.udash.bootstrap.utils.UdashBreadcrumbs._
         |
         |val pages = SeqProperty[Breadcrumb](
         |  DefaultBreadcrumb("Udash", Url("http://udash.io/")),
         |  DefaultBreadcrumb("Dev's Guide", Url("http://guide.udash.io/")),
         |  DefaultBreadcrumb("Extensions", Url("http://guide.udash.io/")),
         |  DefaultBreadcrumb("Bootstrap wrapper", Url("http://guide.udash.io/ext/bootstrap"))
         |)
         |val breadcrumbs = UdashBreadcrumbs(pages)(
         |  defaultPageFactory,
         |  (item) => pages.get.last == item
         |)
         |breadcrumbs.render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.breadcrumbs()
    ),
    h3("Pagination"),
    CodeBlock(
      s"""import UdashPagination._
          |import Context._
          |
         |val showArrows = Property(true)
          |val highlightActive = Property(true)
          |val toggleArrows = UdashButton.toggle(active = showArrows)("Toggle arrows")
          |val toggleHighlight = UdashButton.toggle(active = highlightActive)("Toggle highlight")
          |
         |val pages = SeqProperty(Seq.tabulate[Page](7)(idx =>
          |  DefaultPage((idx+1).toString, Url(BootstrapExtState.url))
          |))
          |val selected = Property(0)
          |val pagination = UdashPagination(
          |  showArrows = showArrows, highlightActive = highlightActive
          |)(pages, selected)(defaultPageFactory)
          |val pager = UdashPagination.pager()(pages, selected)(defaultPageFactory)
          |div(
          |  div(
          |    UdashButtonGroup()(
          |      toggleArrows.render,
          |      toggleHighlight.render
          |    ).render
          |  ),
          |  div("Selected page index: ", bind(selected)),
          |  pagination.render,
          |  pager.render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.pagination()
    ),
    h3("Labels"),
    CodeBlock(
      s"""UdashLabel(UdashBootstrap.newId(), "Default").render,
         |UdashLabel.primary(UdashBootstrap.newId(), "Primary").render,
         |UdashLabel.success(UdashBootstrap.newId(), "Success").render,
         |UdashLabel.info(UdashBootstrap.newId(), "Info").render,
         |UdashLabel.warning(UdashBootstrap.newId(), "Warning").render,
         |UdashLabel.danger(UdashBootstrap.newId(), "Danger").render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.labels()
    ),
    h3("Badges"),
    CodeBlock(
      s"""|val counter = Property(0)
          |window.setInterval(() => counter.set(counter.get + 1), 3000)
          |UdashButton(buttonStyle = ButtonStyle.Primary, size = ButtonSize.Large)(
          |  "Button", UdashBadge(counter).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.badges()
    ),
    h3("Jumbotron"),
    p("A lightweight, flexible component that can optionally extend the entire viewport to showcase key content on your site."),
    CodeBlock(
      s"""UdashJumbotron(h1("Header), "Content...").render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(
      BootstrapDemos.jumbotron()
    ),
    h3("Page header"),
    CodeBlock(
      s"""UdashPageHeader(h1("Header ", small("Subtext"))).render""".stripMargin
    )(GuideStyles),
    h3("Alerts"),
    p("The ", i("UdashAlert")," component supports both regular and dismissible Bootstrap alerts with typesafe styling and ",
    i("Property"),"-based dismissal mechanism."),
    CodeBlock(
      s"""|val styles = Seq[(String) => DismissibleUdashAlert](
          |  (title) => DismissibleUdashAlert.info(title),
          |  (title) => DismissibleUdashAlert.danger(title),
          |  (title) => DismissibleUdashAlert.success(title),
          |  (title) => DismissibleUdashAlert.warning(title)
          |)
          |val dismissed = SeqProperty[String](Seq.empty)
          |def randomDismissible(): dom.Element = {
          |  val title = randomString()
          |  val alert = styles(Random.nextInt(styles.size))(title)
          |  alert.dismissed.listen(_ => dismissed.append(title))
          |  alert.render
          |}
          |val alerts = div(BootstrapStyles.Well.well, GlobalStyles.centerBlock)(
          |  UdashAlert.info("info").render,
          |  UdashAlert.success("success").render,
          |  UdashAlert.warning("warning").render,
          |  UdashAlert.danger("danger").render
          |).render
          |val create = UdashButton(
          |  size = ButtonSize.Large,
          |  block = true
          |)("Create dismissible alert")
          |create.listen { case _ => alerts.appendChild(randomDismissible()) }
          |div(
          |  create.render,
          |  alerts
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
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
          |  UdashProgressBar(value, showPercentage, Success)().render,
          |  UdashProgressBar(value, showPercentage, Striped)(value => value+" percent").render,
          |  UdashProgressBar.animated(value, showPercentage, animate, Danger)().render,
          |  NumberInput.debounced(value.transform(_.toString, Integer.parseInt))(
          |    BootstrapStyles.Form.formControl, placeholder := "Percentage"
          |  )
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.progressBar()
    ),
    h3("List group"),
    CodeBlock(
      s"""import io.udash.bootstrap.BootstrapImplicits._
          |val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
          |val listGroup = UdashListGroup(news)((news) =>
          |  li(
          |    BootstrapStyles.active.styleIf(news.transform(_.endsWith("1"))),
          |    BootstrapStyles.disabled.styleIf(news.transform(_.endsWith("2"))),
          |    BootstrapStyles.List.listItemSuccess.styleIf(news.transform(_.endsWith("3"))),
          |    BootstrapStyles.List.listItemDanger.styleIf(news.transform(_.endsWith("4"))),
          |    BootstrapStyles.List.listItemInfo.styleIf(news.transform(_.endsWith("5"))),
          |    BootstrapStyles.List.listItemWarning.styleIf(news.transform(_.endsWith("6")))
          |  )(bind(news)).render
          |)
          |
         |var i = 1
          |val appendHandler = window.setInterval(() => {
          |  news.append(s"Dynamic $i")
          |  i += 1
          |}, 2000)
          |window.setTimeout(() => window.clearInterval(appendHandler), 20000)
          |
         |listGroup.render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")( //force Boostrap styles
      BootstrapDemos.listGroup()
    ),
    h3("Panels"),
    CodeBlock(
      s"""val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
         |UdashPanel(PanelStyle.Success)(
         |  UdashPanel.heading("Panel heading"),
         |  UdashPanel.body("Some content panel..."),
         |  UdashListGroup(news)((news) =>
         |    li(bind(news)).render
         |  ).render,
         |  UdashPanel.footer("Panel footer")
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.panels()
    ),
    h3("Responsive embed"),
    CodeBlock(
      s"""div(
         |  div(BootstrapStyles.EmbedResponsive.embed,
         |      BootstrapStyles.EmbedResponsive.embed16by9)(
         |    iframe(BootstrapStyles.EmbedResponsive.item, src := "...")
         |  ),
         |  div(BootstrapStyles.EmbedResponsive.embed,
         |      BootstrapStyles.EmbedResponsive.embed4by3)(
         |    iframe(BootstrapStyles.EmbedResponsive.item, src := "...")
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.responsiveEmbed()
    ),
    h3("Wells"),
    CodeBlock(
      s"""div(
         |  div(BootstrapStyles.Well.well, BootstrapStyles.Well.wellSm)(
         |    "Small well..."
         |  ),
         |  div(BootstrapStyles.Well.well)(
         |    "Standard well..."
         |  ),
         |  div(BootstrapStyles.Well.well, BootstrapStyles.Well.wellLg)(
         |    "Large well..."
         |  )
         |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.wells()
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
      s"""|val events = SeqProperty[UdashModal.ModalEvent]
          |val header = () => div(
          |  "Modal events",
          |  UdashButton()(
          |    UdashModal.CloseButtonAttr,
          |    BootstrapStyles.close, "×"
          |  ).render
          |).render
          |val body = () => div(
          |  div(BootstrapStyles.Well.well)(
          |    ul(repeat(events)(event => li(event.get.toString).render))
          |  )
          |).render
          |val footer = () => div(
          |  UdashButton()(UdashModal.CloseButtonAttr, "Close").render,
          |  UdashButton(buttonStyle = ButtonStyle.Primary)("Something...").render
          |).render
          |
          |val modal = UdashModal(modalSize = ModalSize.Large)(
          |  headerFactory = Some(header),
          |  bodyFactory = Some(body),
          |  footerFactory = Some(footer)
          |)
          |modal.listen { case ev => events.append(ev) }
          |
          |val openModalButton = UdashButton(buttonStyle = ButtonStyle.Primary)(
          |  modal.openButtonAttrs(), "Show modal..."
          |)
          |val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
          |openAndCloseButton.listen{ case _ =>
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
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.simpleModal()
    ),
    h3("Tooltips"),
    CodeBlock(
      s"""|import scala.concurrent.duration.DurationInt
          |val label1 = UdashLabel(UdashBootstrap.newId(), "Tooltip on hover with delay").render
          |val label1Tooltip = UdashTooltip(
          |  trigger = Seq(UdashTooltip.HoverTrigger),
          |  delay = UdashTooltip.Delay(500 millis, 250 millis),
          |  title = (_) => "Tooltip..."
          |)(label1)
          |
          |val label2 = UdashLabel(UdashBootstrap.newId(), "Tooltip on click").render
          |val label2Tooltip = UdashTooltip(
          |  trigger = Seq(UdashTooltip.ClickTrigger),
          |  delay = UdashTooltip.Delay(0 millis, 250 millis),
          |  placement = (_, _) => Seq(UdashTooltip.BottomPlacement),
          |  title = (_) => "Tooltip 2..."
          |)(label2)
          |
          |val label3 = UdashLabel(UdashBootstrap.newId(), "Tooltip with JS toggler").render
          |val label3Tooltip = UdashTooltip(
          |  trigger = Seq(UdashTooltip.ManualTrigger),
          |  placement = (_, _) => Seq(UdashTooltip.RightPlacement),
          |  title = (_) => "Tooltip 3..."
          |)(label3)
          |
          |val button = UdashButton()("Toggle tooltip")
          |button.listen{ case _ => label3Tooltip.toggle() }
          |
          |div(
          |  label1, label2, label3, button.render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.tooltips()
    ),
    h3("Popovers"),
    CodeBlock(
      s"""
         |import scala.concurrent.duration.DurationInt
         |val label1 = UdashLabel(UdashBootstrap.newId(), "Popover on hover with delay").render
         |val label1Tooltip = UdashPopover(
         |  trigger = Seq(UdashPopover.HoverTrigger),
         |  delay = UdashPopover.Delay(500 millis, 250 millis),
         |  title = (_) => "Popover...",
         |  content = (_) => "Content..."
         |)(label1)
         |
         |val label2 = UdashLabel(UdashBootstrap.newId(), "Popover on click").render
         |val label2Tooltip = UdashPopover(
         |  trigger = Seq(UdashPopover.ClickTrigger),
         |  delay = UdashPopover.Delay(0 millis, 250 millis),
         |  placement = (_, _) => Seq(UdashPopover.BottomPlacement),
         |  title = (_) => "Popover 2...",
         |  content = (_) => "Content..."
         |)(label2)
         |
         |val label3 = UdashLabel(UdashBootstrap.newId(), "Popover with JS toggler").render
         |val label3Tooltip = UdashPopover(
         |  trigger = Seq(UdashPopover.ManualTrigger),
         |  placement = (_, _) => Seq(UdashPopover.LeftPlacement),
         |  html = true,
         |  title = (_) => "Popover 3...",
         |  content = (_) => {
         |    import scalatags.Text.all._
         |    Seq(
         |      p("HTML content..."),
         |      ul(li("Item 1"), li("Item 2"), li("Item 3"))
         |    ).render
         |  }
         |)(label3)
         |
         |val button = UdashButton()("Toggle popover")
         |button.listen { case _ => label3Tooltip.toggle() }
         |
         |div(
         |  label1, label2, label3, button.render
         |).render
       """.stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.popovers()
    ),
    h3("Collapse"),
    p(
      i("UdashCollapse"), " represents element with toggle behaviour. It provides methods ",
      i("toggle"), ", ", i("open"), " and ", i("close"), " for manual manipulation and ",
      i("toggleButtonAttrs"), " for easy creation of toggle button."
    ),
    CodeBlock(
      s"""|val events = SeqProperty[UdashCollapse.CollapseEvent]
          |val collapse = UdashCollapse()(
          |  div(BootstrapStyles.Well.well)(
          |    ul(repeat(events)(event => li(event.get.toString).render))
          |  )
          |)
          |collapse.listen { case ev => events.append(ev) }
          |
          |val toggleButton = UdashButton(style = ButtonStyle.Primary)(
          |  collapse.toggleButtonAttrs(), "Toggle..."
          |)
          |val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
          |openAndCloseButton.listen{ case _ =>
          |  collapse.show()
          |  window.setTimeout(() => collapse.hide(), 2000)
          |}
          |
          |div(
          |  UdashButtonGroup(justified = true)(
          |    toggleButton.render,
          |    openAndCloseButton.render
          |  ).render,
          |  collapse.render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.simpleCollapse()
    ),
    p(
      i("UdashAccordion"), " internally uses ", i("UdashCollapse"), ". It provides ",
      i("collapseOf"), " method for obtaining ", i("UdashCollapse"), " created for selected element."
    ),
    CodeBlock(
      s"""val events = SeqProperty[UdashCollapse.CollapseEvent]
          |val news = SeqProperty[String](
          |  "Title 1", "Title 2", "Title 3"
          |)
          |
          |val accordion = UdashAccordion(news)(
          |  (news) => span(news.get).render,
          |  (_) => div(BootstrapStyles.Panel.panelBody)(
          |    div(BootstrapStyles.Well.well)(
          |      ul(repeat(events)(event => li(event.get.toString).render))
          |    )
          |  ).render
          |)
          |
          |val accordionElement = accordion.render
          |news.elemProperties.map(news => {
          |  accordion.collapseOf(news)
          |}).filter(_.isDefined)
          |  .foreach(_.get.listen { case ev => events.append(ev) })
          |
          |div(
          |  accordionElement
          |).render
       """.stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.accordionCollapse()
    ),
    h3("Carousel"),
    p(
      i("UdashCarousel"), " is a slideshow component. It exposes its state (slides, current view) through ", i("Properties"),
      " and can be cycled through programatically."
    ),
    CodeBlock(
      s"""|def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
          |  Url("assets/images/ext/bootstrap/carousel.png")
          |)(
          |  h3(randomString()),
          |  p(randomString())
          |)
          |val slides = SeqProperty[UdashCarouselSlide](
          |  (1 to 4).map(_ => newSlide())
          |)
          |val active = Property(false)
          |import scala.concurrent.duration._
          |val carousel = UdashCarousel(slides, activeSlide = 1,
          |  animationOptions = AnimationOptions(interval = 2 seconds,
          |    keyboard = false, active = active.get)
          |)
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
          |  UdashButtonToolbar(
          |    UdashButton.toggle(active = active)("Run animation").render,
          |    UdashButtonGroup()(
          |      prevButton.render,
          |      nextButton.render
          |    ).render,
          |    UdashButtonGroup()(
          |      prependButton.render,
          |      appendButton.render
          |    ).render
          |  ).render,
          |  div(
          |    carousel.render
          |  ).render
          |).render""".stripMargin
    )(GuideStyles),
    div(cls := "bootstrap")(//force Bootstrap styles
      BootstrapDemos.carousel()
    ),
    h2("What's next?"),
    p("You can check the code for this page on our ", a(href := References.UdashGuideRepo)("GitHub repository"),
      ". It contains all the examples above and more, since UdashBootstrap is heavily used in the Udash Guide.")
  ).render

  override def renderChild(view: View): Unit = {}
}