import com.karasiq.bootstrap._
import com.karasiq.bootstrap.alert.AlertStyle
import com.karasiq.bootstrap.buttons.{ButtonBuilder, ButtonSize, ButtonStyle}
import com.karasiq.bootstrap.grid.GridSystem.col.GridColSize
import com.karasiq.bootstrap.icons.IconModifier
import com.karasiq.bootstrap.modal.ModalBuilder
import com.karasiq.bootstrap.panel.PanelBuilder
import com.karasiq.bootstrap.table.TableRowStyle
import io.udash._
import io.udash.bootstrap.UdashBootstrapImplicits
import org.scalajs.dom

import scalatags.JsDom.all._

object Alert {
  def apply(style: AlertStyle, md: Modifier*): ConcreteHtmlTag[dom.html.Div] = alert.Alert(style, md: _*)
}

object Button {
  // Shortcut to ButtonBuilder()
  def apply(style: ButtonStyle = ButtonStyle.default, size: ButtonSize = ButtonSize.default, block: Boolean = false, active: Boolean = false, disabled: Boolean = false): ButtonBuilder =
    buttons.Button(style, size, block, active, disabled)
}

object Carousel extends UdashBootstrapImplicits {
  /*
    def reactive(data: SeqProperty[Modifier, Property[Modifier]], id: String = Bootstrap.newId): BoostrapCarousel =
      BoostrapCarousel.reactive(data, id)
  */

  def apply(content: Modifier*): carousel.Carousel =
    carousel.Carousel(content: _*)

}

object Collapse {
  def apply(btnTitle: String)(content: Modifier*): Modifier = collapse.Collapse(btnTitle)(content: _*)
}

object Dropdown {
  def item(md: Modifier*): Tag = dropdown.Dropdown.item(md: _*)

  def link(target: String, md: Modifier*): Tag = dropdown.Dropdown.link(target, md: _*)

  def apply(title: Modifier, items: Modifier*): Tag = dropdown.Dropdown(title, items: _*)

  def dropup(title: Modifier, items: Modifier*): Tag = dropdown.Dropdown.dropup(title, items: _*)
}

object Form {

  def submit(text: Modifier): ConcreteHtmlTag[dom.html.Button] = com.karasiq.bootstrap.form.Form.submit(text)

  def apply(md: Modifier*): ConcreteHtmlTag[dom.html.Form] = com.karasiq.bootstrap.form.Form(md: _*)

  def inline(md: Modifier*): ConcreteHtmlTag[dom.html.Form] = com.karasiq.bootstrap.form.Form.inline(md: _*)
}

object FormInput extends UdashBootstrapImplicits {

  import com.karasiq.bootstrap.form._

  def ofType(tpe: String, label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.ofType(tpe, label, md: _*)

  def text(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.text(label, md: _*)

  def number(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.number(label, md: _*)

  def email(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.email(label, md: _*)

  def password(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.password(label, md: _*)

  def file(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.file(label, md: _*)

  def textArea(title: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.textArea(title, md: _*)

  def checkbox(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.checkbox(label, md: _*)

  def radio(title: String, radioName: String, radioValue: String, radioId: String = Bootstrap.newId): FormRadio =
    com.karasiq.bootstrap.form.FormInput.radio(title, radioName, radioValue, radioId)

  def radioGroup(radios: FormRadio*): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios: _*)

  def radioGroup(radios: SeqProperty[FormRadio]): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios)

  def select(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options: _*)

  def select(title: Modifier, options: SeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options)

  def multipleSelect(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options: _*)

  def multipleSelect(title: Modifier, options: SeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options)

  def apply(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput(label, md: _*)
}

object FormInputGroup {

  def createInput(tpe: String, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.createInput(tpe, md: _*)

  def label(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.label(md: _*)

  def text(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.text(md: _*)

  def number(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.number(md: _*)

  def email(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.email(md: _*)

  def password(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.password(md: _*)

  def addon(md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup.addon(md: _*)

  def apply(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInputGroup(label, md: _*)
}

object GridSystem {

  def container: Tag = grid.GridSystem.container

  def containerFluid: Tag = grid.GridSystem.containerFluid

  def row: Tag = grid.GridSystem.row

  def mkRow(md: Modifier*): Tag = grid.GridSystem.mkRow(md: _*)

  object col {
    def xs(size: Int): GridColSize = grid.GridSystem.col.xs(size)

    def sm(size: Int): GridColSize = grid.GridSystem.col.sm(size)

    def md(size: Int): GridColSize = grid.GridSystem.col.md(size)

    def lg(size: Int): GridColSize = grid.GridSystem.col.lg(size)

    def responsive(xsSize: Int, smSize: Int, mdSize: Int, lgSize: Int): GridColSize =
      grid.GridSystem.col.responsive(xsSize, smSize, mdSize, lgSize)

    def apply(size: Int): GridColSize = grid.GridSystem.col(size)
  }

}

object BootstrapGlyphicon {
  def apply(name: String): icons.BootstrapGlyphicon = icons.BootstrapGlyphicon(name)
}

object FontAwesome {
  val Inverse = icons.FontAwesome.inverse
  // Size modifiers
  val Large = icons.FontAwesome.large
  val X2 = icons.FontAwesome.x2
  val X3 = icons.FontAwesome.x3
  val X4 = icons.FontAwesome.x4
  val X5 = icons.FontAwesome.x5
  // Fixed width
  val FixedWidth = icons.FontAwesome.fixedWidth
  // List icons
  val List = icons.FontAwesome.list
  val Line = icons.FontAwesome.line
  // Bordered & Pulled icons
  val Border = icons.FontAwesome.border
  val PullRight = icons.FontAwesome.pullRight
  val PullLeft = icons.FontAwesome.pullLeft
  // Animated icons
  val Spin = icons.FontAwesome.spin
  val Pulse = icons.FontAwesome.pulse
  // Rotated & Flipped
  val Rotate90 = icons.FontAwesome.rotate90
  val Rotate180 = icons.FontAwesome.rotate180
  val Rotate270 = icons.FontAwesome.rotate270
  val FlipHorizontal = icons.FontAwesome.flipHorizontal
  val FlipVertical = icons.FontAwesome.flipVertical
  val Stacked1x = icons.FontAwesome.stacked1x
  val Stacked2x = icons.FontAwesome.stacked2x

  def apply(name: String, styles: Modifier*): icons.FontAwesomeIcon = icons.FontAwesome(name, styles: _*)

  // Stacked icons
  def stacked(iconss: Tag*): Tag = icons.FontAwesome.stacked(iconss: _*)
}

object Modal {

  val Dismiss: Modifier = modal.Modal.dismiss

  def closeButton(title: String = "Close"): ConcreteHtmlTag[dom.html.Button] = modal.Modal.closeButton(title)

  def button(md: Modifier*): ConcreteHtmlTag[dom.html.Button] = modal.Modal.button(md: _*)

  def apply(title: Modifier = "Modal dialog", body: Modifier = "", buttons: Modifier = Modal.closeButton()): ModalBuilder =
    modal.Modal(title, body, buttons)
}

object Navigation extends UdashBootstrapImplicits {
  def tabs(tabs: navbar.NavigationTab*): navbar.Navigation = navbar.Navigation.tabs(tabs: _*)

  def pills(tabs: navbar.NavigationTab*): navbar.Navigation = navbar.Navigation.pills(tabs: _*)
}


object NavigationBar extends UdashBootstrapImplicits {
  def apply(tabs: Seq[navbar.NavigationTab] = Nil, barId: String = Bootstrap.newId, brand: Modifier = "Navigation",
            styles: Seq[navbar.NavigationBarStyle] = Seq(navbar.NavigationBarStyle.default, navbar.NavigationBarStyle.fixedTop),
            container: Modifier ⇒ Modifier = md ⇒ GridSystem.container(md),
            contentContainer: Modifier ⇒ Modifier = md ⇒ GridSystem.container(GridSystem.mkRow(md))) =
    navbar.NavigationBar(tabs, barId, brand, styles, container, contentContainer)
}

object NavigationBarStyle {

  val Default = navbar.NavigationBarStyle.default
  val Inverse = navbar.NavigationBarStyle.inverse

  val FixedTop = navbar.NavigationBarStyle.fixedTop
  val FixedBottom = navbar.NavigationBarStyle.fixedBottom
  val StaticTop = navbar.NavigationBarStyle.staticTop

}

object Panel {

  def collapse(panelId: String, modifiers: Modifier*): Tag = panel.Panel.collapse(panelId, modifiers: _*)

  def title(icon: IconModifier, title: Modifier, modifiers: Modifier*): Tag = panel.Panel.title(icon, title, modifiers: _*)

  def button(icon: IconModifier, modifiers: Modifier*): ConcreteHtmlTag[dom.html.Anchor] = panel.Panel.button(icon, modifiers: _*)

  def buttons(buttons: Modifier*): Tag = panel.Panel.buttons(buttons: _*)

  def apply(panelId: String = Bootstrap.newId, style: panel.PanelStyle = PanelStyle.Default, header: Option[Modifier] = None, footer: Option[Modifier] = None): PanelBuilder =
    panel.Panel(panelId, style, header, footer)
}

object PanelStyle {
  val Default: panel.PanelStyle = panel.PanelStyle.default

  val Primary: panel.PanelStyle = panel.PanelStyle.primary
  val Success: panel.PanelStyle = panel.PanelStyle.success
  val Info: panel.PanelStyle = panel.PanelStyle.info
  val Warning: panel.PanelStyle = panel.PanelStyle.warning
  val Danger: panel.PanelStyle = panel.PanelStyle.danger
}

object Popover {
  def apply(title: String, content: Modifier, placement: tooltip.TooltipPlacement = tooltip.TooltipPlacement.auto): popover.Popover =
    popover.Popover(title, content, placement)
}

object ProgressBar extends UdashBootstrapImplicits {
  def basic(value: Property[Int]): progressbar.ProgressBar = progressbar.ProgressBar.basic(value)

  def withLabel(value: Property[Int]): progressbar.ProgressBar = progressbar.ProgressBar.withLabel(value)
}

object PagedTable extends UdashBootstrapImplicits {

  /*  def apply(heading: Rx[Seq[Modifier]], content: Rx[Seq[TableRow]], perPage: Int = 20)(implicit ctx: Ctx.Owner): StaticPagedTable = {
      new StaticPagedTable(heading, content, perPage)
    }

    def static(heading: Seq[Modifier], content: Seq[TableRow], perPage: Int = 20)(implicit ctx: Ctx.Owner): StaticPagedTable = {
      this.apply(Rx(heading), Rx(content), perPage)
    }*/

}

object TableRow {
  def apply(data: Seq[Modifier], ms: Modifier*): com.karasiq.bootstrap.table.TableRow =
    com.karasiq.bootstrap.table.TableRow(data, ms: _*)

  val Default: TableRowStyle = com.karasiq.bootstrap.table.TableRow.default
  val Active: TableRowStyle = com.karasiq.bootstrap.table.TableRow.active
  val Success: TableRowStyle = com.karasiq.bootstrap.table.TableRow.success
  val Warning: TableRowStyle = com.karasiq.bootstrap.table.TableRow.warning
  val Danger: TableRowStyle = com.karasiq.bootstrap.table.TableRow.danger
  val Info: TableRowStyle = com.karasiq.bootstrap.table.TableRow.info
}

object TableStyle {

  val Striped: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.striped
  val Hover: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.hover
  val Bordered: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.bordered
  val Condensed: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.condensed
}

object Tooltip {
  def apply(content: String, placement: tooltip.TooltipPlacement = tooltip.TooltipPlacement.auto): tooltip.Tooltip =
    tooltip.Tooltip(content, placement)
}

object TooltipPlacement {
  val Auto = com.karasiq.bootstrap.tooltip.TooltipPlacement.auto
  val Left = com.karasiq.bootstrap.tooltip.TooltipPlacement.left
  val Right = com.karasiq.bootstrap.tooltip.TooltipPlacement.right
  val Top = com.karasiq.bootstrap.tooltip.TooltipPlacement.top
  val Bottom = com.karasiq.bootstrap.tooltip.TooltipPlacement.bottom
}




