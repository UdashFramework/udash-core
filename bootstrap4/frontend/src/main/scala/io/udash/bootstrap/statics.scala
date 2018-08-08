package io.udash.bootstrap

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.css.CssStyleName

object BootstrapTags {
  import scalatags.JsDom.all._

  val dataBackdrop = attr("data-backdrop")
  val dataBind = attr("data-bind")
  val dataDismiss = attr("data-dismiss")
  val dataKeyboard = attr("data-keyboard")
  val dataLabel = attr("data-label")
  val dataParent = attr("data-parent")
  val dataRide = attr("data-ride")
  val dataShow = attr("data-show")
  val dataSlide = attr("data-slide")
  val dataSlideTo = attr("data-slide-to")
  val dataTarget = attr("data-target")
  val dataToggle = attr("data-toggle")

}

object BootstrapStyles {
  final class ResponsiveBreakpoint(val classMarker: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object ResponsiveBreakpoint extends AbstractValueEnumCompanion[ResponsiveBreakpoint] {
    final val All: Value = new ResponsiveBreakpoint("")
    final val Small: Value = new ResponsiveBreakpoint("-sm")
    final val Medium: Value = new ResponsiveBreakpoint("-md")
    final val Large: Value = new ResponsiveBreakpoint("-lg")
    final val ExtraLarge: Value = new ResponsiveBreakpoint("-xl")
    final val Print: Value = new ResponsiveBreakpoint("-print")
  }

  final class Side(val classMarker: String, val longClassMarker: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Side extends AbstractValueEnumCompanion[Side] {
    final val All: Value = new Side("", "")
    final val Top: Value = new Side("t", "-top")
    final val Bottom: Value = new Side("b", "-bottom")
    final val Left: Value = new Side("l", "-left")
    final val Right: Value = new Side("r", "-right")
    /** This value is not always supported by Bootstrap. */
    final val X: Value = new Side("x", "")
    /** This value is not always supported by Bootstrap. */
    final val Y: Value = new Side("y", "")
  }

  final class Align(val classMarker: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Align extends AbstractValueEnumCompanion[Align] {
    final val Left: Value = new Align("-left")
    final val Right: Value = new Align("-right")
    final val Center: Value = new Align("-center")
  }

  final class Color(val classMarker: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Color extends AbstractValueEnumCompanion[Color] {
    final val Primary: Value = new Color("-primary")
    final val Secondary: Value = new Color("-secondary")
    final val Success: Value = new Color("-success")
    final val Danger: Value = new Color("-danger")
    final val Warning: Value = new Color("-warning")
    final val Info: Value = new Color("-info")
    final val Light: Value = new Color("-light")
    final val Dark: Value = new Color("-dark")
    final val White: Value = new Color("-white")
    /** This value is only supported in buttons. */
    final val Link: Value = new Color("-link")
  }

  def active = CssStyleName("active")
  def container = CssStyleName("container")
  def containerFluid = CssStyleName("container-fluid")
  def row = CssStyleName("row")
  def pullLeft = CssStyleName("pull-left")
  def pullRight = CssStyleName("pull-right")
  def centerBlock = CssStyleName("center-block")
  def affix = CssStyleName("affix")
  def arrow = CssStyleName("arrow")
  def bottom = CssStyleName("bottom")
  def close = CssStyleName("close")
  def col = CssStyleName("col")
  def collapsed = CssStyleName("collapsed")
  def body = container
  def divider = CssStyleName("divider")
  def disabled = CssStyleName("disabled")
  def fade = CssStyleName("fade")
  def hide = CssStyleName("hide")
  def iconBar = CssStyleName("icon-bar")
  def iconNext = CssStyleName("icon-next")
  def in = CssStyleName("in")
  def item = CssStyleName("item")
  def jumbotron = CssStyleName("jumbotron")
  def left = CssStyleName("left")
  def next = CssStyleName("next")
  def pillPane = CssStyleName("pill-pane")
  def preScrollable = CssStyleName("pre-scrollable")
  def prettyprint = CssStyleName("prettyprint")
  def previous = CssStyleName("previous")
  def prev = CssStyleName("prev")
  def right = CssStyleName("right")
  def show = CssStyleName("show")
  def top = CssStyleName("top")

  object Border {
    def border(side: Side) = CssStyleName(s"border${side.longClassMarker}")
    def border0(side: Side) = CssStyleName(s"border${side.longClassMarker}-0")

    def borderColor(color: Color) = CssStyleName(s"border${color.classMarker}")

    def borderRounded(side: Side) = CssStyleName("rounded")
    def borderRounded0 = CssStyleName("rounded-0")
    def borderRoundedCircle = CssStyleName("rounded-circle")
  }

  object Collapse {
    def collapse = CssStyleName("collapse")
    def collapsing = CssStyleName("collapsing")
    def collapseIn = in
  }

  object Display {
    def block(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-block")
    def flex(breakpoint: ResponsiveBreakpoint) = Flex.flex(breakpoint)
    def inline(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-inline")
    def inlineBlock(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-inline-block")
    def inlineFlex(breakpoint: ResponsiveBreakpoint) = Flex.inlineFlex(breakpoint)
    def none(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-none")
    def table(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-table")
    def tableCell(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-table-cell")
    def tableRow(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-table-row")
  }

  object Flex {
    def flex(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-flex")
    def inlineFlex(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"d${breakpoint.classMarker}-inline-flex")

    def column(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-column")
    def columnReverse(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-column-reverse")

    def row(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-row")
    def rowReverse(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-row-reverse")

    def fill(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-fill")
    def grow(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-grow-1")
    def shrink(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-shrink-1")

    def autoMargin(side: Side) = CssStyleName(s"m${side.classMarker}-auto")

    def nowrap(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-nowrap")
    def wrap(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-wrap")
    def wrapReverse(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"flex${breakpoint.classMarker}-wrap-reverse")

    def justifyContentStart(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"justify-content${breakpoint.classMarker}-start")
    def justifyContentEnd(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"justify-content${breakpoint.classMarker}-end")
    def justifyContentCenter(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"justify-content${breakpoint.classMarker}-center")
    def justifyContentBetween(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"justify-content${breakpoint.classMarker}-between")
    def justifyContentAround(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"justify-content${breakpoint.classMarker}-around")

    def alignItemsStart(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-items${breakpoint.classMarker}-start")
    def alignItemsEnd(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-items${breakpoint.classMarker}-end")
    def alignItemsCenter(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-items${breakpoint.classMarker}-center")
    def alignItemsBaseline(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-items${breakpoint.classMarker}-baseline")
    def alignItemsStretch(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-items${breakpoint.classMarker}-stretch")

    def alignContentStart(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-content${breakpoint.classMarker}-start")
    def alignContentEnd(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-content${breakpoint.classMarker}-end")
    def alignContentCenter(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-content${breakpoint.classMarker}-center")
    def alignContentBaseline(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-content${breakpoint.classMarker}-baseline")
    def alignContentStretch(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-content${breakpoint.classMarker}-stretch")

    def alignSelfStart(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-self${breakpoint.classMarker}-start")
    def alignSelfEnd(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-self${breakpoint.classMarker}-end")
    def alignSelfCenter(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-self${breakpoint.classMarker}-center")
    def alignSelfBaseline(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-self${breakpoint.classMarker}-baseline")
    def alignSelfStretch(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"align-self${breakpoint.classMarker}-stretch")

    /** Supported size values: [1,12] */
    def order(breakpoint: ResponsiveBreakpoint, size: Int) = CssStyleName(s"order${breakpoint.classMarker}-$size")
  }

  object Grid {
    /** Supported size values: [1,12] */
    def col(breakpoint: ResponsiveBreakpoint, size: Int) = CssStyleName(s"col${breakpoint.classMarker}-$size")

    /** Supported size values: [1,12] */
    def offset(breakpoint: ResponsiveBreakpoint, size: Int) = CssStyleName(s"offset${breakpoint.classMarker}-$size")

    /** Supported size values: [1,12] */
    def order(breakpoint: ResponsiveBreakpoint, size: Int) = Flex.order(breakpoint, size)
  }

  object Float {
    def left(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"float${breakpoint.classMarker}-left")
    def right(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"float${breakpoint.classMarker}-right")
    def none(breakpoint: ResponsiveBreakpoint) = CssStyleName(s"float${breakpoint.classMarker}-none")
  }

  object Sizing {
    def widthAuto = CssStyleName("w-auto")
    def width25 = CssStyleName("w-25")
    def width50 = CssStyleName("w-50")
    def width75 = CssStyleName("w-75")
    def width100 = CssStyleName("w-100")

    def heightAuto = CssStyleName("h-auto")
    def height25 = CssStyleName("h-25")
    def height50 = CssStyleName("h-50")
    def height75 = CssStyleName("h-75")
    def height100 = CssStyleName("h-100")

    def maxWidth = CssStyleName("mw-100")
    def maxHeight = CssStyleName("mh-100")
  }

  object Spacing {
    /**
      * Possible sizes:
      * <ul>
      *   <li>0 - for classes that eliminate the margin or padding by setting it to 0</li>
      *   <li>1 - (by default) for classes that set the margin or padding to spacer * .25</li>
      *   <li>2 - (by default) for classes that set the margin or padding to spacer * .5</li>
      *   <li>3 - (by default) for classes that set the margin or padding to spacer</li>
      *   <li>4 - (by default) for classes that set the margin or padding to spacer * 1.5</li>
      *   <li>5 - (by default) for classes that set the margin or padding to spacer * 3</li>
      *   <li>auto - for classes that set the margin to auto</li>
      * </ul>
      *
      * See more: <a href="https://getbootstrap.com/docs/4.1/utilities/spacing/#notation">Bootstrap docs</a>
      */
    def margin(side: Side, breakpoint: ResponsiveBreakpoint, size: String = "3") =
      CssStyleName(s"m${side.classMarker}${breakpoint.classMarker}-$size")

    /**
      * Possible sizes:
      * <ul>
      *   <li>0 - for classes that eliminate the margin or padding by setting it to 0</li>
      *   <li>1 - (by default) for classes that set the margin or padding to spacer * .25</li>
      *   <li>2 - (by default) for classes that set the margin or padding to spacer * .5</li>
      *   <li>3 - (by default) for classes that set the margin or padding to spacer</li>
      *   <li>4 - (by default) for classes that set the margin or padding to spacer * 1.5</li>
      *   <li>5 - (by default) for classes that set the margin or padding to spacer * 3</li>
      *   <li>auto - for classes that set the margin to auto</li>
      * </ul>
      *
      * See more: <a href="https://getbootstrap.com/docs/4.1/utilities/spacing/#notation">Bootstrap docs</a>
      */
    def padding(side: Side, breakpoint: ResponsiveBreakpoint, size: String = "3") =
      CssStyleName(s"p${side.classMarker}${breakpoint.classMarker}-$size")
  }

  object Background {
    def color(color: Color) = CssStyleName(s"bg${color.classMarker}")
    def transparent = CssStyleName("bg-transparent")
  }

  object Button {
    def btn = CssStyleName("btn")
    def btnBlock = CssStyleName("btn-block")
    def btnGroup = CssStyleName("btn-group")
    def btnGroupJustified = CssStyleName("btn-group-justified")
    def btnGroupVertical = CssStyleName("btn-group-vertical")
    def btnLg = CssStyleName("btn-lg")
    def btnToolbar = CssStyleName("btn-toolbar")
    def btnXs = CssStyleName("btn-xs")
    def btnSm = CssStyleName("btn-sm")

    def color(color: Color) = CssStyleName(s"btn${color.classMarker}")
  }

  object Position {
    def positionStatic = CssStyleName("position-static")
    def positionRelative = CssStyleName("position-relative")
    def positionAbsolute = CssStyleName("position-absolute")
    def positionFixed = CssStyleName("position-fixed")
    def positionSticky = CssStyleName("position-sticky")

    def fixedTop = CssStyleName("fixed-top")
    def fixedBottom = CssStyleName("fixed-bottom")

    def stickyTop = CssStyleName("sticky-top")
  }

  object Carousel {
    def carousel = CssStyleName("carousel")
    def carouselCaption = CssStyleName("carousel-caption")
    def carouselControl = CssStyleName("carousel-control")
    def carouselIndicators = CssStyleName("carousel-indicators")
    def carouselInner = CssStyleName("carousel-inner")
    def slide = CssStyleName("slide")
  }

  object Alert {
    def alert = CssStyleName("alert")
    def alertDismissible = CssStyleName("alert-dismissible")
    def alertLink = CssStyleName("alert-link")

    def color(color: Color) = CssStyleName(s"alert${color.classMarker}")
  }

  object Dropdown {
    def dropdown = CssStyleName("dropdown")
    def dropdownBackdrop = CssStyleName("dropdown-backdrop")
    def dropdownHeader = CssStyleName("dropdown-header")
    def dropdownMenu = CssStyleName("dropdown-menu")
    def dropdownToggle = CssStyleName("dropdown-toggle")
    def dropup = CssStyleName("dropup")
    def caret = CssStyleName("caret")
  }

  object Visibility {
    def clearfix = CssStyleName("clearfix")

    def visible = CssStyleName("visible")
    def invisible = CssStyleName("invisible")

    def srOnly = CssStyleName("sr-only")
    def srOnlyFocusable = CssStyleName("sr-only-focusable")
  }

  object VerticalAlign {
    def baseline = CssStyleName("align-baseline")
    def top = CssStyleName("align-top")
    def middle = CssStyleName("align-middle")
    def bottom = CssStyleName("align-bottom")
    def textTop = CssStyleName("align-text-top")
    def textBottom = CssStyleName("align-text-bottom")
  }

  object Text {
    def align(align: Align, breakpoint: ResponsiveBreakpoint) =
      CssStyleName(s"text${breakpoint.classMarker}${align.classMarker}")

    def nowrap = CssStyleName("text-nowrap")
    def truncate = CssStyleName("text-truncate")

    def lowercase = CssStyleName("text-lowercase")
    def uppercase = CssStyleName("text-uppercase")
    def capitalize = CssStyleName("text-capitalize")
    def monospace = CssStyleName("text-monospace")

    def weightBold = CssStyleName("font-weight-bold")
    def weightNormal = CssStyleName("font-weight-normal")
    def weightLight = CssStyleName("font-weight-light")
    def italic = CssStyleName("font-italic")
  }

  object Typography {
    def h1 = CssStyleName("h1")
    def h2 = CssStyleName("h2")
    def h3 = CssStyleName("h3")
    def h4 = CssStyleName("h4")
    def h5 = CssStyleName("h5")
    def h6 = CssStyleName("h6")

    def blockquote = CssStyleName("blockquote")
    def blockquoteFooter = CssStyleName("blockquote-footer")
    def initialism = CssStyleName("initialism")
    def lead = CssStyleName("lead")
    def mark = CssStyleName("mark")
    def muted = CssStyleName("text-muted")
    def small = CssStyleName("small")

    def textDanger = CssStyleName("text-danger")
    def textDark = CssStyleName("text-dark")
    def textHide = CssStyleName("text-hide")
    def textMuted = CssStyleName("text-muted")
    def textWhite = CssStyleName("text-white")
    def textWhite50 = CssStyleName("text-white-50")
  }

  object List {
    def listGroup = CssStyleName("list-group")
    def listGroupItem = CssStyleName("list-group-item")
    def listGroupItemHeading = CssStyleName("list-group-item-heading")
    def listGroupItemText = CssStyleName("list-group-item-text")
    def listInline = CssStyleName("list-inline")
    def listInlineItem = CssStyleName("list-inline-item")
    def listUnstyled = CssStyleName("list-unstyled")

    def itemColor(color: Color) = CssStyleName(s"list-group-item${color.classMarker}")
  }

  object Table {
    def table = CssStyleName("table")
    def tableBordered = CssStyleName("table-bordered")
    def tableResponsive = CssStyleName("table-responsive")
    def tableStriped = CssStyleName("table-striped")
    def tableHover = CssStyleName("table-hover")
    def tableSm = CssStyleName("table-sm")
    def tableInverse = CssStyleName("table-inverse")
    def tableActive = CssStyleName("active")
    def theadDefault = CssStyleName("thead-default")
    def theadInverse = CssStyleName("thead-inverse")

    def rowColor(color: Color) = CssStyleName(s"table${color.classMarker}")
  }

  object Form {
    def formCheck = CssStyleName("form-check")
    def formCheckInline = CssStyleName("form-check-inline")
    def formCheckInput = CssStyleName("form-check-input")
    def formCheckLabel = CssStyleName("form-check-label")
    def formControl = CssStyleName("form-control")
    def formControlPlaintext = CssStyleName("form-control-plaintext")
    def formGroup = CssStyleName("form-group")
    def formInline = CssStyleName("form-inline")
    def inputGroup = CssStyleName("input-group")
    def inputGroupAppend = CssStyleName("input-group-append")
    def inputGroupPrepend = CssStyleName("input-group-prepend")
    def inputGroupLg = CssStyleName("input-group-lg")
    def inputGroupSm = CssStyleName("input-group-sm")
    def inputGroupText = CssStyleName("input-group-text")
    def formControlLg = CssStyleName("form-control-lg")
    def formControlSm = CssStyleName("form-control-sm")
    def hasFeedback = CssStyleName("has-feedback")
    def formText = CssStyleName("form-text")
    def colFormLabel = CssStyleName("col-form-label")
  }

  object Image {
    def roundedCircle = CssStyleName("rounded-circle")
    def imgFluid = CssStyleName("img-fluid")
    def rounded = CssStyleName("rounded")
    def imgThumbnail = CssStyleName("img-thumbnail")
    def caption = CssStyleName("caption")
    def thumbnail = CssStyleName("thumbnail")
  }

  object Navigation {
    def nav = CssStyleName("nav")
    def navbar = CssStyleName("navbar")
    def navbarBrand = CssStyleName("navbar-brand")
    def navbarBtn = CssStyleName("navbar-btn")
    def navbarCollapse = CssStyleName("navbar-collapse")
    def navbarDefault = CssStyleName("navbar-default")
    def navbarFixedBottom = CssStyleName("navbar-fixed-bottom")
    def navbarFixedTop = CssStyleName("navbar-fixed-top")
    def navbarForm = CssStyleName("navbar-form")
    def navbarHeader = CssStyleName("navbar-header")
    def navbarInverse = CssStyleName("navbar-inverse")
    def navbarLeft = CssStyleName("navbar-left")
    def navbarLink = CssStyleName("navbar-link")
    def navbarNav = CssStyleName("navbar-nav")
    def navbarRight = CssStyleName("navbar-right")
    def navbarStaticTop = CssStyleName("navbar-static-top")
    def navbarText = CssStyleName("navbar-text")
    def navbarToggle = CssStyleName("navbar-toggle")
    def navDivider = CssStyleName("nav-divider")
    def navJustified = CssStyleName("nav-justified")
    def navStacked = CssStyleName("nav-stacked")
    def navPills = CssStyleName("nav-pills")
    def navTabs = CssStyleName("nav-tabs")
    def navTabsJustified = CssStyleName("nav-tabs-justified")
    def breadcrumb = CssStyleName("breadcrumb")
  }

  object Badge {
    def badge = CssStyleName("badge")
    def pill = CssStyleName("badge-pill")

    def color(color: Color) = CssStyleName(s"badge${color.classMarker}")
  }

  object Media {
    def media = CssStyleName("media")
    def mediaBody = CssStyleName("media-body")
    def mediaHeading = CssStyleName("media-heading")
    def mediaList = CssStyleName("media-list")
    def mediaObject = CssStyleName("media-object")
    def mediaLeft = CssStyleName("media-left")
    def mediaMiddle = CssStyleName("media-middle")
    def mediaRight = CssStyleName("media-right")
  }

  object Modal {
    def modal = CssStyleName("modal")
    def modalBackdrop = CssStyleName("modal-backdrop")
    def modalBody = CssStyleName("modal-body")
    def modalContent = CssStyleName("modal-content")
    def modalDialog = CssStyleName("modal-dialog")
    def modalFooter = CssStyleName("modal-footer")
    def modalHeader = CssStyleName("modal-header")
    def modalLarge = CssStyleName("modal-lg")
    def modalOpen = CssStyleName("modal-open")
    def modalSmall = CssStyleName("modal-sm")
    def modalTitle = CssStyleName("modal-title")
  }

  object Pagination {
    def pagination = CssStyleName("pagination")
    def paginationLg = CssStyleName("pagination-lg")
    def paginationSm = CssStyleName("pagination-sm")
    def pager = CssStyleName("pager")
  }

  object Popover {
    def popover = CssStyleName("popover")
    def popoverContent = CssStyleName("popover-content")
    def popoverTitle = CssStyleName("popover-title")
  }

  object ProgressBar {
    def progress = CssStyleName("progress")
    def progressBar = CssStyleName("progress-bar")
    def progressBarStriped = CssStyleName("progress-bar-striped")
  }

  object Tooltip {
    def tooltip = CssStyleName("tooltip")
    def tooltipArrow = CssStyleName("tooltip-arrow")
    def tooltipInner = CssStyleName("tooltip-inner")
  }

  object EmbedResponsive {
    def responsive = CssStyleName("embed-responsive")
    def item = CssStyleName("embed-responsive-item")
    def embed16by9 = CssStyleName("embed-responsive-16by9")
    def embed4by3 = CssStyleName("embed-responsive-4by3")
  }
}