package io.udash.selenium.views

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.css.CssView
import io.udash.selenium.routing.BootstrapDemosState
import io.udash.selenium.views.demos.bootstrap.BootstrapDemos
import scalatags.JsDom.all._

object BootstrapDemosViewFactory extends StaticViewFactory[BootstrapDemosState.type](() => new BootstrapDemosView)

class BootstrapDemosView extends FinalView with CssView {
  def demoHeader(name: String): Modifier =
    h4(name, BootstrapStyles.Spacing.margin(BootstrapStyles.Side.Top))

  private val content = div(
    h3("Bootstrap 4 demos"),
    demoHeader("Statics"),
    BootstrapDemos.statics(),
    demoHeader("FontAwesome"),
    BootstrapDemos.icons(),
    demoHeader("Date Picker"),
    BootstrapDemos.datePicker(),
    demoHeader("Date Picker Range"),
    BootstrapDemos.datePickerRange(),
    demoHeader("Tables"),
    BootstrapDemos.tables(),
    demoHeader("Dropdowns"),
    BootstrapDemos.dropdown(),
    demoHeader("Button"),
    BootstrapDemos.buttonsDemo(),
    demoHeader("Toggle Buttons"),
    BootstrapDemos.toggleButton(),
    demoHeader("Button groups"),
    div(BootstrapStyles.Grid.row)(
      div(BootstrapStyles.Grid.col(4))(
        BootstrapDemos.staticButtonsGroup()
      ),
      div(BootstrapStyles.Grid.col(8))(
        BootstrapDemos.buttonToolbar()
      )
    ),
    demoHeader("Checkboxes group"),
    BootstrapDemos.checkboxButtons(),
    demoHeader("Radio buttons group"),
    BootstrapDemos.radioButtons(),
    demoHeader("Button dropdowns"),
    BootstrapDemos.buttonDropdown(),
    demoHeader("Input groups"),
    BootstrapDemos.inputGroups(),
    demoHeader("Forms"),
    BootstrapDemos.simpleForm(),
    demoHeader("Inline Forms"),
    BootstrapDemos.inlineForm(),
    demoHeader("Navs"),
    BootstrapDemos.navs(),
    demoHeader("Navbar"),
    BootstrapDemos.navbars(),
    demoHeader("Breadcrumbs"),
    BootstrapDemos.breadcrumbs(),
    demoHeader("Pagination"),
    BootstrapDemos.pagination(),
    demoHeader("Labels"),
    BootstrapDemos.labels(),
    demoHeader("Badges"),
    BootstrapDemos.badges(),
    demoHeader("Jumbotron"),
    BootstrapDemos.jumbotron(),
    demoHeader("Alerts"),
    BootstrapDemos.alerts(),
    demoHeader("Progress bars"),
    BootstrapDemos.progressBar(),
    demoHeader("List group"),
    BootstrapDemos.listGroup(),
    demoHeader("Card"),
    BootstrapDemos.cards(),
    demoHeader("Modals"),
    BootstrapDemos.simpleModal(),
    demoHeader("Tooltips"),
    BootstrapDemos.tooltips(),
    demoHeader("Popovers"),
    BootstrapDemos.popovers(),
    demoHeader("Collapse"),
    BootstrapDemos.simpleCollapse(),
    demoHeader("Accordion"),
    BootstrapDemos.accordionCollapse(),
    demoHeader("Carousel"),
    BootstrapDemos.carousel(),
    demoHeader("Responsive embed"),
    BootstrapDemos.responsiveEmbed()
  )

  override def getTemplate: Modifier = content
}