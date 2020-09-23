package io.udash.web.guide.views.ext

import io.udash._
import io.udash.web.commons.components.{CodeBlock, ForceBootstrap}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.bootstrap._
import io.udash.web.guide.views.{References, Versions}
import scalatags.JsDom

case object BootstrapExtViewFactory extends StaticViewFactory[BootstrapExtState.type](() => new BootstrapExtView)


class BootstrapExtView extends View {
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
  private val (inlineFormDemo, inlineFormSnippet) = InlineFormDemo.demoWithSnippet()
  private val (navsDemo, navsSnippet) = NavsDemo.demoWithSnippet()
  private val (navbarDemo, navbarSnippet) = NavbarDemo.demoWithSnippet()
  private val (navigationDemo, navigationSnippet) = NavigationDemo.demoWithSnippet()
  private val (breadcrumbsDemo, breadcrumbsSnippet) = BreadcrumbsDemo.demoWithSnippet()
  private val (paginationDemo, paginationSnippet) = PaginationDemo.demoWithSnippet()
  private val (labelsDemo, labelsSnippet) = LabelsDemo.demoWithSnippet()
  private val (badgesDemo, badgesSnippet) = BadgesDemo.demoWithSnippet()
  private val (jumbotronDemo, jumbotronSnippet) = JumbotronDemo.demoWithSnippet()
  private val (alertsDemo, alertsSnippet) = AlertsDemo.demoWithSnippet()
  private val (progressBarDemo, progressBarSnippet) = ProgressBarDemo.demoWithSnippet()
  private val (listGroupDemo, listGroupSnippet) = ListGroupDemo.demoWithSnippet()
  private val (cardsDemo, cardsSnippet) = CardsDemo.demoWithSnippet()
  private val (responsiveEmbedDemo, responsiveEmbedSnippet) = ResponsiveEmbedDemo.demoWithSnippet()
  private val (simpleModalDemo, simpleModalSnippet) = SimpleModalDemo.demoWithSnippet()
  private val (tooltipsDemo, tooltipsSnippet) = TooltipsDemo.demoWithSnippet()
  private val (popoversDemo, popoversSnippet) = PopoversDemo.demoWithSnippet()
  private val (simpleCollapseDemo, simpleCollapseSnippet) = SimpleCollapseDemo.demoWithSnippet()
  private val (accordionCollapseDemo, accordionCollapseSnippet) = AccordionDemo.demoWithSnippet()
  private val (carouselDemo, carouselSnippet) = CarouselDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h1("Udash Bootstrap Components"),
    h2("First steps"),
    p("To start development with the Bootstrap wrapper add the following line in you frontend module dependencies: "),
    CodeBlock(
      s""""io.udash" %%% "udash-bootstrap4" % "${Versions.udashVersion}"""".stripMargin
    )(GuideStyles),
    p("The wrapper provides a typed equivalent of the ", a(href := References.BootstrapHomepage, target := "_blank")("Twitter Bootstrap"), " API."),
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
    h3("FontAwesome"),
    p("The icons from ", i("FontAwesome"), " package is accessible in ", i("Icons"), " object."),
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
    inlineFormSnippet,
    ForceBootstrap(inlineFormDemo),
    h3("Navs"),
    navsSnippet,
    ForceBootstrap(navsDemo),
    h3("Navbar"),
    navbarSnippet,
    ForceBootstrap(navbarDemo),
    p("The following example presents a navbar with a dropdown item. It uses menu of this guide."),
    navigationSnippet,
    ForceBootstrap(navigationDemo),
    h3("Breadcrumbs"),
    breadcrumbsSnippet,
    ForceBootstrap(breadcrumbsDemo),
    h3("Pagination"),
    paginationSnippet,
    ForceBootstrap(paginationDemo),
    h3("Labels"),
    labelsSnippet,
    ForceBootstrap(labelsDemo),
    h3("Badges"),
    badgesSnippet,
    ForceBootstrap(badgesDemo),
    h3("Jumbotron"),
    p("A lightweight, flexible component that can optionally extend the entire viewport to showcase key content on your site."),
    jumbotronSnippet,
    div(cls := "bootstrap")(jumbotronDemo),
    h3("Alerts"),
    p("The ", i("UdashAlert")," component supports both regular and dismissible Bootstrap alerts with type-safe styling and ",
      i("Property"), "-based dismissal mechanism."),
    alertsSnippet,
    ForceBootstrap(alertsDemo),
    h3("Progress bars"),
    p("The ", i("UdashProgressBar"), " component provides a simple way to use built-in Bootstrap progress bars ",
      "with custom stringifiers and ", i("Property"), "-controlled value, percentage showing and animation."),
    progressBarSnippet,
    ForceBootstrap(progressBarDemo),
    h3("List group"),
    listGroupSnippet,
    ForceBootstrap(listGroupDemo),
    h3("Card"),
    cardsSnippet,
    ForceBootstrap(cardsDemo),
    h3("Responsive embed"),
    responsiveEmbedSnippet,
    ForceBootstrap(responsiveEmbedDemo),
    h3("Modals"),
    p(
      "The modal window constructor takes three optional methods as the arguments. The first one is used to create ",
      "a modal window's header, the second creates a body and the last produces a window's footer."
    ),
    p(
      "The ", i("UdashModal"), " class exposes methods for opening/hiding window. It is also possible to listen on window's events."
    ),
    simpleModalSnippet,
    ForceBootstrap(simpleModalDemo),
    h3("Tooltips"),
    tooltipsSnippet,
    ForceBootstrap(tooltipsDemo),
    h3("Popovers"),
    popoversSnippet,
    ForceBootstrap(popoversDemo),
    h3("Collapse"),
    p(
      i("UdashCollapse"), " represents element with toggle behaviour. It provides methods ",
      i("toggle"), ", ", i("open"), " and ", i("close"), " for manual manipulation and ",
      i("toggleButtonAttrs"), " for easy creation of toggle button."
    ),
    simpleCollapseSnippet,
    ForceBootstrap(simpleCollapseDemo),
    p(
      i("UdashAccordion"), " internally uses ", i("UdashCollapse"), ". It provides ",
      i("collapseOf"), " method for obtaining ", i("UdashCollapse"), " created for selected element."
    ),
    accordionCollapseSnippet,
    ForceBootstrap(accordionCollapseDemo),
    h3("Carousel"),
    p(
      i("UdashCarousel"), " is a slideshow component. It exposes its state (slides, current view) through ", i("Properties"),
      " and can be cycled through programatically."
    ),
    carouselSnippet,
    ForceBootstrap(carouselDemo),
    h2("What's next?"),
    p("You can check the code for this page on our ", a(href := References.UdashGuideRepo, target := "_blank")("GitHub repository"),
      ". It contains all the examples above and more, since UdashBootstrap is heavily used in the Udash Guide.")
  )
}