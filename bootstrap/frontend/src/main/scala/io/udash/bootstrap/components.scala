package io.udash.bootstrap


import com.karasiq.bootstrap
import io.udash._
import org.scalajs.dom

import scalatags.JsDom.all._

object Alert {
  def apply(style: bootstrap.alert.AlertStyle, md: Modifier*): ConcreteHtmlTag[dom.html.Div] = bootstrap.alert.Alert(style, md: _*)
}

object Button {
  // Shortcut to ButtonBuilder()
  def apply(style: bootstrap.buttons.ButtonStyle = bootstrap.buttons.ButtonStyle.default, size: bootstrap.buttons.ButtonSize = bootstrap.buttons.ButtonSize.default, block: Boolean = false, active: Boolean = false, disabled: Boolean = false): bootstrap.buttons.ButtonBuilder =
    bootstrap.buttons.Button(style, size, block, active, disabled)
}

object Carousel extends UdashBootstrapImplicits {
  /*
    def reactive(data: SeqProperty[Modifier, Property[Modifier]], id: String = Bootstrap.newId): BoostrapCarousel =
      BoostrapCarousel.reactive(data, id)
  */

  def apply(content: Modifier*): bootstrap.carousel.Carousel =
    bootstrap.carousel.Carousel(content: _*)

}

object Collapse {
  def apply(btnTitle: String)(content: Modifier*): Modifier = bootstrap.collapse.Collapse(btnTitle)(content: _*)
}

object Dropdown {
  def item(md: Modifier*): Tag = bootstrap.dropdown.Dropdown.item(md: _*)

  def link(target: String, md: Modifier*): Tag = bootstrap.dropdown.Dropdown.link(target, md: _*)

  def apply(title: Modifier, items: Modifier*): Tag = bootstrap.dropdown.Dropdown(title, items: _*)

  def dropup(title: Modifier, items: Modifier*): Tag = bootstrap.dropdown.Dropdown.dropup(title, items: _*)
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

  def radio(title: String, radioName: String, radioValue: String, radioId: String = bootstrap.Bootstrap.newId): FormRadio =
    com.karasiq.bootstrap.form.FormInput.radio(title, radioName, radioValue, radioId)

  def radioGroup(radios: FormRadio*): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios: _*)

  def radioGroup(radios: ReadableSeqProperty[FormRadio]): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios)

  def select(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options: _*)

  def select(title: Modifier, options: ReadableSeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options)

  def multipleSelect(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options: _*)

  def multipleSelect(title: Modifier, options: ReadableSeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options)

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

  def container: Tag = bootstrap.grid.GridSystem.container

  def containerFluid: Tag = bootstrap.grid.GridSystem.containerFluid

  def row: Tag = bootstrap.grid.GridSystem.row

  def mkRow(md: Modifier*): Tag = bootstrap.grid.GridSystem.mkRow(md: _*)

  object col {
    def xs(size: Int): bootstrap.grid.GridSystem.col.GridColSize = bootstrap.grid.GridSystem.col.xs(size)

    def sm(size: Int): bootstrap.grid.GridSystem.col.GridColSize = bootstrap.grid.GridSystem.col.sm(size)

    def md(size: Int): bootstrap.grid.GridSystem.col.GridColSize = bootstrap.grid.GridSystem.col.md(size)

    def lg(size: Int): bootstrap.grid.GridSystem.col.GridColSize = bootstrap.grid.GridSystem.col.lg(size)

    def responsive(xsSize: Int, smSize: Int, mdSize: Int, lgSize: Int): bootstrap.grid.GridSystem.col.GridColSize =
      bootstrap.grid.GridSystem.col.responsive(xsSize, smSize, mdSize, lgSize)

    def apply(size: Int): bootstrap.grid.GridSystem.col.GridColSize = bootstrap.grid.GridSystem.col(size)
  }

}

object BootstrapGlyphicon {
  def apply(name: String): bootstrap.icons.BootstrapGlyphicon = bootstrap.icons.BootstrapGlyphicon(name)
}

object FontAwesome {
  val Inverse = bootstrap.icons.FontAwesome.inverse
  // Size modifiers
  val Large = bootstrap.icons.FontAwesome.large
  val X2 = bootstrap.icons.FontAwesome.x2
  val X3 = bootstrap.icons.FontAwesome.x3
  val X4 = bootstrap.icons.FontAwesome.x4
  val X5 = bootstrap.icons.FontAwesome.x5
  // Fixed width
  val FixedWidth = bootstrap.icons.FontAwesome.fixedWidth
  // List icons
  val List = bootstrap.icons.FontAwesome.list
  val Line = bootstrap.icons.FontAwesome.line
  // Bordered & Pulled icons
  val Border = bootstrap.icons.FontAwesome.border
  val PullRight = bootstrap.icons.FontAwesome.pullRight
  val PullLeft = bootstrap.icons.FontAwesome.pullLeft
  // Animated icons
  val Spin = bootstrap.icons.FontAwesome.spin
  val Pulse = bootstrap.icons.FontAwesome.pulse
  // Rotated & Flipped
  val Rotate90 = bootstrap.icons.FontAwesome.rotate90
  val Rotate180 = bootstrap.icons.FontAwesome.rotate180
  val Rotate270 = bootstrap.icons.FontAwesome.rotate270
  val FlipHorizontal = bootstrap.icons.FontAwesome.flipHorizontal
  val FlipVertical = bootstrap.icons.FontAwesome.flipVertical
  val Stacked1x = bootstrap.icons.FontAwesome.stacked1x
  val Stacked2x = bootstrap.icons.FontAwesome.stacked2x

  def apply(name: String, styles: Modifier*): bootstrap.icons.FontAwesomeIcon = bootstrap.icons.FontAwesome(name, styles: _*)

  // Stacked icons
  def stacked(iconss: Tag*): Tag = bootstrap.icons.FontAwesome.stacked(iconss: _*)
}

object Modal {

  val Dismiss: Modifier = bootstrap.modal.Modal.dismiss

  def closeButton(title: String = "Close"): ConcreteHtmlTag[dom.html.Button] = bootstrap.modal.Modal.closeButton(title)

  def button(md: Modifier*): ConcreteHtmlTag[dom.html.Button] = bootstrap.modal.Modal.button(md: _*)

  def apply(title: Modifier = "Modal dialog", body: Modifier = "", buttons: Modifier = Modal.closeButton()): bootstrap.modal.ModalBuilder =
    bootstrap.modal.Modal(title, body, buttons)
}

object Navigation extends UdashBootstrapImplicits {
  def tabs(tabs: bootstrap.navbar.NavigationTab*): bootstrap.navbar.Navigation = bootstrap.navbar.Navigation.tabs(tabs: _*)

  def pills(tabs: bootstrap.navbar.NavigationTab*): bootstrap.navbar.Navigation = bootstrap.navbar.Navigation.pills(tabs: _*)
}


object NavigationBar extends UdashBootstrapImplicits {
  def apply(tabs: Seq[bootstrap.navbar.NavigationTab] = Nil, barId: String = bootstrap.Bootstrap.newId, brand: Modifier = "Navigation",
            styles: Seq[bootstrap.navbar.NavigationBarStyle] = Seq(bootstrap.navbar.NavigationBarStyle.default, bootstrap.navbar.NavigationBarStyle.fixedTop),
            container: Modifier ⇒ Modifier = md ⇒ GridSystem.container(md),
            contentContainer: Modifier ⇒ Modifier = md ⇒ GridSystem.container(GridSystem.mkRow(md))) =
    bootstrap.navbar.NavigationBar(tabs, barId, brand, styles, container, contentContainer)
}

object NavigationBarStyle {

  val Default = bootstrap.navbar.NavigationBarStyle.default
  val Inverse = bootstrap.navbar.NavigationBarStyle.inverse

  val FixedTop = bootstrap.navbar.NavigationBarStyle.fixedTop
  val FixedBottom = bootstrap.navbar.NavigationBarStyle.fixedBottom
  val StaticTop = bootstrap.navbar.NavigationBarStyle.staticTop

}

object Panel {

  def collapse(panelId: String, modifiers: Modifier*): Tag = bootstrap.panel.Panel.collapse(panelId, modifiers: _*)

  def title(icon: bootstrap.icons.IconModifier, title: Modifier, modifiers: Modifier*): Tag = bootstrap.panel.Panel.title(icon, title, modifiers: _*)

  def button(icon: bootstrap.icons.IconModifier, modifiers: Modifier*): ConcreteHtmlTag[dom.html.Anchor] = bootstrap.panel.Panel.button(icon, modifiers: _*)

  def buttons(buttons: Modifier*): Tag = bootstrap.panel.Panel.buttons(buttons: _*)

  def apply(panelId: String = bootstrap.Bootstrap.newId, style: bootstrap.panel.PanelStyle = PanelStyle.Default, header: Option[Modifier] = None, footer: Option[Modifier] = None): bootstrap.panel.PanelBuilder =
    bootstrap.panel.Panel(panelId, style, header, footer)
}

object PanelStyle {
  val Default: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.default

  val Primary: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.primary
  val Success: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.success
  val Info: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.info
  val Warning: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.warning
  val Danger: bootstrap.panel.PanelStyle = bootstrap.panel.PanelStyle.danger
}

object Popover {
  def apply(title: String, content: Modifier, placement: bootstrap.tooltip.TooltipPlacement = bootstrap.tooltip.TooltipPlacement.auto): bootstrap.popover.Popover =
    bootstrap.popover.Popover(title, content, placement)
}

object ProgressBar extends UdashBootstrapImplicits {
  def basic(value: ReadableProperty[Int]): bootstrap.progressbar.ProgressBar = bootstrap.progressbar.ProgressBar.basic(value)

  def withLabel(value: ReadableProperty[Int]): bootstrap.progressbar.ProgressBar = bootstrap.progressbar.ProgressBar.withLabel(value)
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

  val Default: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.default
  val Active: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.active
  val Success: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.success
  val Warning: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.warning
  val Danger: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.danger
  val Info: bootstrap.table.TableRowStyle = com.karasiq.bootstrap.table.TableRow.info
}

object TableStyle {

  val Striped: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.striped
  val Hover: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.hover
  val Bordered: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.bordered
  val Condensed: com.karasiq.bootstrap.table.TableStyle = com.karasiq.bootstrap.table.TableStyle.condensed
}

object Tooltip {
  def apply(content: String, placement: bootstrap.tooltip.TooltipPlacement = bootstrap.tooltip.TooltipPlacement.auto): bootstrap.tooltip.Tooltip =
    bootstrap.tooltip.Tooltip(content, placement)
}

object TooltipPlacement {
  val Auto = com.karasiq.bootstrap.tooltip.TooltipPlacement.auto
  val Left = com.karasiq.bootstrap.tooltip.TooltipPlacement.left
  val Right = com.karasiq.bootstrap.tooltip.TooltipPlacement.right
  val Top = com.karasiq.bootstrap.tooltip.TooltipPlacement.top
  val Bottom = com.karasiq.bootstrap.tooltip.TooltipPlacement.bottom
}




