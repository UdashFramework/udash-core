package io.udash.bootstrap

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash.css.CssStyleName

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

  final class Size(val classMarker: String)(implicit enumCtx: EnumCtx) extends AbstractValueEnum
  object Size extends AbstractValueEnumCompanion[Size] {
    final val Small: Value = new Size("-sm")
    final val Large: Value = new Size("-lg")
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
  def disabled = CssStyleName("disabled")
  def fade = CssStyleName("fade")
  def hide = CssStyleName("hide")
  def iconBar = CssStyleName("icon-bar")
  def iconNext = CssStyleName("icon-next")
  def in = CssStyleName("in")
  def item = CssStyleName("item")
  def pillPane = CssStyleName("pill-pane")
  def preScrollable = CssStyleName("pre-scrollable")
  def prettyprint = CssStyleName("prettyprint")
  def previous = CssStyleName("previous")
  def show = CssStyleName("show")
  def top = CssStyleName("top")

  object Alert {
    def alert = CssStyleName("alert")

    def dismissible = CssStyleName("alert-dismissible")
    def link = CssStyleName("alert-link")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"alert${color.classMarker}")
  }

  object Background {
    def color(color: Color = Color.Secondary) =
      CssStyleName(s"bg${color.classMarker}")

    def transparent = CssStyleName("bg-transparent")
  }

  object Badge {
    def badge = CssStyleName("badge")
    def pill = CssStyleName("badge-pill")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"badge${color.classMarker}")
  }

  object Border {
    def border(side: Side) = CssStyleName(s"border${side.longClassMarker}")
    def border0(side: Side) = CssStyleName(s"border${side.longClassMarker}-0")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"border${color.classMarker}")

    def rounded(side: Side) = CssStyleName("rounded")
    def rounded0 = CssStyleName("rounded-0")
    def roundedCircle = CssStyleName("rounded-circle")
  }

  object Button {
    def btn = CssStyleName("btn")
    def block = CssStyleName("btn-block")
    def toolbar = CssStyleName("btn-toolbar")

    def group = CssStyleName("btn-group")
    def groupVertical = CssStyleName("btn-group-vertical")

    def groupSize(size: Size) =
      CssStyleName(s"btn-group${size.classMarker}")

    def size(size: Size) =
      CssStyleName(s"btn${size.classMarker}")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"btn${color.classMarker}")

    def outline(color: Color = Color.Secondary) =
      CssStyleName(s"btn-outline${color.classMarker}")
  }

  object Card {
    def card = CssStyleName("card")

    def body = CssStyleName("card-body")
    def group = CssStyleName("card-group")
    def footer = CssStyleName("card-footer")
    def header = CssStyleName("card-header")
    def title = CssStyleName("card-title")
  }

  object Carousel {
    def carousel = CssStyleName("carousel")

    def item = CssStyleName("carousel-item")

    def itemNext = CssStyleName("carousel-item-next")
    def itemPrev = CssStyleName("carousel-item-prev")
    def itemLeft = CssStyleName("carousel-item-left")
    def itemRight = CssStyleName("carousel-item-right")

    def control = CssStyleName("carousel-control")
    def controlNext = CssStyleName("carousel-control-next")
    def controlPrev = CssStyleName("carousel-control-prev")
    def controlNextIcon = CssStyleName("carousel-control-next-icon")
    def controlPrevIcon = CssStyleName("carousel-control-prev-icon")

    def caption = CssStyleName("carousel-caption")
    def indicators = CssStyleName("carousel-indicators")
    def inner = CssStyleName("carousel-inner")
    def fade = CssStyleName("carousel-fade")
    def slide = CssStyleName("slide")
  }

  object Collapse {
    def collapse = CssStyleName("collapse")
    def collapsing = CssStyleName("collapsing")
    def show = CssStyleName("show")
  }

  object Display {
    def block(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-block")

    def flex(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      Flex.flex(breakpoint)

    def inline(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-inline")

    def inlineBlock(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-inline-block")

    def inlineFlex(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      Flex.inlineFlex(breakpoint)

    def none(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-none")

    def table(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-table")

    def tableCell(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-table-cell")

    def tableRow(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-table-row")
  }

  object Dropdown {
    def dropdown = CssStyleName("dropdown")
    def dropup = CssStyleName("dropup")
    def dropleft = CssStyleName("dropleft")
    def dropright = CssStyleName("dropright")

    def backdrop = CssStyleName("dropdown-backdrop")
    def caret = CssStyleName("caret")
    def divider = CssStyleName("dropdown-divider")
    def header = CssStyleName("dropdown-header")
    def item = CssStyleName("dropdown-item")
    def menu = CssStyleName("dropdown-menu")
    def menuRight = CssStyleName("dropdown-menu-right")
    def toggle = CssStyleName("dropdown-toggle")
  }

  object EmbedResponsive {
    def responsive = CssStyleName("embed-responsive")
    def item = CssStyleName("embed-responsive-item")
    def embed16by9 = CssStyleName("embed-responsive-16by9")
    def embed4by3 = CssStyleName("embed-responsive-4by3")
  }

  object Flex {
    def flex(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-flex")
    def inlineFlex(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"d${breakpoint.classMarker}-inline-flex")

    def column(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-column")
    def columnReverse(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-column-reverse")

    def row(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-row")
    def rowReverse(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-row-reverse")

    def fill(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-fill")
    def grow(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-grow-1")
    def shrink(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-shrink-1")

    def autoMargin(side: Side) = CssStyleName(s"m${side.classMarker}-auto")

    def nowrap(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-nowrap")
    def wrap(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-wrap")
    def wrapReverse(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"flex${breakpoint.classMarker}-wrap-reverse")

    def justifyContentStart(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"justify-content${breakpoint.classMarker}-start")
    def justifyContentEnd(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"justify-content${breakpoint.classMarker}-end")
    def justifyContentCenter(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"justify-content${breakpoint.classMarker}-center")
    def justifyContentBetween(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"justify-content${breakpoint.classMarker}-between")
    def justifyContentAround(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"justify-content${breakpoint.classMarker}-around")

    def alignItemsStart(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-items${breakpoint.classMarker}-start")
    def alignItemsEnd(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-items${breakpoint.classMarker}-end")
    def alignItemsCenter(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-items${breakpoint.classMarker}-center")
    def alignItemsBaseline(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-items${breakpoint.classMarker}-baseline")
    def alignItemsStretch(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-items${breakpoint.classMarker}-stretch")

    def alignContentStart(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-content${breakpoint.classMarker}-start")
    def alignContentEnd(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-content${breakpoint.classMarker}-end")
    def alignContentCenter(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-content${breakpoint.classMarker}-center")
    def alignContentBaseline(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-content${breakpoint.classMarker}-baseline")
    def alignContentStretch(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-content${breakpoint.classMarker}-stretch")

    def alignSelfStart(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-self${breakpoint.classMarker}-start")
    def alignSelfEnd(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-self${breakpoint.classMarker}-end")
    def alignSelfCenter(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-self${breakpoint.classMarker}-center")
    def alignSelfBaseline(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-self${breakpoint.classMarker}-baseline")
    def alignSelfStretch(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"align-self${breakpoint.classMarker}-stretch")

    /** Supported size values: [1,12] */
    def order(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: Int) =
      CssStyleName(s"order${breakpoint.classMarker}-$size")
  }

  object Float {
    def left(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"float${breakpoint.classMarker}-left")
    def right(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"float${breakpoint.classMarker}-right")
    def none(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"float${breakpoint.classMarker}-none")
  }

  object Form {
    def group = CssStyleName("form-group")
    def inline = CssStyleName("form-inline")
    def text = CssStyleName("form-text")

    def check = CssStyleName("form-check")
    def checkInline = CssStyleName("form-check-inline")
    def checkInput = CssStyleName("form-check-input")
    def checkLabel = CssStyleName("form-check-label")

    def control = CssStyleName("form-control")
    def controlPlaintext = CssStyleName("form-control-plaintext")

    def size(size: Size) =
      CssStyleName(s"form-control${size.classMarker}")

    def hasFeedback = CssStyleName("has-feedback")
    def colFormLabel = CssStyleName("col-form-label")
  }

  object Grid {
    /** Supported size values: [1,12] */
    def col(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: Int) =
      CssStyleName(s"col${breakpoint.classMarker}-$size")

    /** Supported size values: [1,12] */
    def offset(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: Int) =
      CssStyleName(s"offset${breakpoint.classMarker}-$size")

    /** Supported size values: [1,12] */
    def order(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: Int) =
      Flex.order(breakpoint, size)
  }

  object Image {
    def caption = CssStyleName("caption")
    def circle = CssStyleName("rounded-circle")
    def imgFluid = CssStyleName("img-fluid")
    def imgThumbnail = CssStyleName("img-thumbnail")
    def rounded = CssStyleName("rounded")
    def thumbnail = CssStyleName("thumbnail")
  }

  object InputGroup {
    def inputGroup = CssStyleName("input-group")

    def append = CssStyleName("input-group-append")
    def prepend = CssStyleName("input-group-prepend")
    def text = CssStyleName("input-group-text")

    def customSelect = CssStyleName("custom-select")
    def customFile = CssStyleName("custom-file")

    def size(size: Size) =
      CssStyleName(s"input-group${size.classMarker}")
  }

  object Jumbotron {
    def jumbotron = CssStyleName("jumbotron")
    def fluid = CssStyleName("jumbotron-fluid")
  }

  object List {
    def inline = CssStyleName("list-inline")
    def inlineItem = CssStyleName("list-inline-item")
    def unstyled = CssStyleName("list-unstyled")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"list-group-item${color.classMarker}")
  }

  object ListGroup {
    def listGroup = CssStyleName("list-group")

    def flush = CssStyleName("list-group-flush")
    def item = CssStyleName("list-group-item")
    def itemAction = CssStyleName("list-group-item-action")
    def itemHeading = CssStyleName("list-group-item-heading")
    def itemText = CssStyleName("list-group-item-text")
  }

  object Media {
    def media = CssStyleName("media")

    def body = CssStyleName("media-body")
    def heading = CssStyleName("media-heading")
    def list = CssStyleName("media-list")
    def `object` = CssStyleName("media-object")
    def mediaObject = CssStyleName("media-object")

    def left = CssStyleName("media-left")
    def middle = CssStyleName("media-middle")
    def right = CssStyleName("media-right")
  }

  object Modal {
    def modal = CssStyleName("modal")

    def backdrop = CssStyleName("modal-backdrop")
    def body = CssStyleName("modal-body")
    def content = CssStyleName("modal-content")
    def dialog = CssStyleName("modal-dialog")
    def footer = CssStyleName("modal-footer")
    def header = CssStyleName("modal-header")
    def open = CssStyleName("modal-open")
    def title = CssStyleName("modal-title")

    def size(size: Size) =
      CssStyleName(s"modal${size.classMarker}")
  }

  object Navigation {
    def nav = CssStyleName("nav")

    def justifyCenter = CssStyleName("justify-content-center")
    def justifyRight = CssStyleName("justify-content-end")

    def breadcrumb = CssStyleName("breadcrumb")
    def divider = CssStyleName("nav-divider")
    def fill = CssStyleName("nav-fill")
    def justified = CssStyleName("nav-justified")
    def pills = CssStyleName("nav-pills")
    def tabs = CssStyleName("nav-tabs")
  }

  object NavigationBar {
    def navbar = CssStyleName("navbar")

    def dark = CssStyleName("navbar-dark")
    def light = CssStyleName("navbar-light")

    def brand = CssStyleName("navbar-brand")
    def btn = CssStyleName("navbar-btn")
    def collapse = CssStyleName("navbar-collapse")
    def header = CssStyleName("navbar-header")
    def inverse = CssStyleName("navbar-inverse")
    def left = CssStyleName("navbar-left")
    def link = CssStyleName("navbar-link")
    def nav = CssStyleName("navbar-nav")
    def right = CssStyleName("navbar-right")
    def text = CssStyleName("navbar-text")
    def toggler = CssStyleName("navbar-toggler")
    def togglerIcon = CssStyleName("navbar-toggler-icon")

    def expand(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"navbar-expand${breakpoint.classMarker}")
  }

  object Pagination {
    def pagination = CssStyleName("pagination")

    def item = CssStyleName("page-item")
    def link = CssStyleName("page-link")

    def size(size: Size) =
      CssStyleName(s"pagination${size.classMarker}")
  }

  object Popover {
    def popover = CssStyleName("popover")

    def content = CssStyleName("popover-content")
    def title = CssStyleName("popover-title")
  }

  object Position {
    def static = CssStyleName("position-static")
    def relative = CssStyleName("position-relative")
    def absolute = CssStyleName("position-absolute")
    def fixed = CssStyleName("position-fixed")
    def sticky = CssStyleName("position-sticky")

    def fixedTop = CssStyleName("fixed-top")
    def fixedBottom = CssStyleName("fixed-bottom")
    def stickyTop = CssStyleName("sticky-top")
  }

  object ProgressBar {
    def progress = CssStyleName("progress")
    def progressBar = CssStyleName("progress-bar")

    def animated = CssStyleName("progress-bar-animated")
    def striped = CssStyleName("progress-bar-striped")
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
    def margin(side: Side, breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: String = "3") =
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
    def padding(side: Side, breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All, size: String = "3") =
      CssStyleName(s"p${side.classMarker}${breakpoint.classMarker}-$size")
  }

  object Table {
    def table = CssStyleName("table")

    def active = CssStyleName("table-active")
    def bordered = CssStyleName("table-bordered")
    def borderless = CssStyleName("table-borderless")
    def hover = CssStyleName("table-hover")
    def dark = CssStyleName("table-dark")
    def light = CssStyleName("table-light")
    def small = CssStyleName("table-sm")
    def striped = CssStyleName("table-striped")

    def theadDark = CssStyleName("thead-dark")
    def theadLight = CssStyleName("thead-light")

    def responsive(breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"table-responsive${breakpoint.classMarker}")

    def rowColor(color: Color = Color.Secondary) =
      CssStyleName(s"table${color.classMarker}")
  }

  object Text {
    def align(align: Align, breakpoint: ResponsiveBreakpoint = ResponsiveBreakpoint.All) =
      CssStyleName(s"text${breakpoint.classMarker}${align.classMarker}")

    def color(color: Color = Color.Secondary) =
      CssStyleName(s"text${color.classMarker}")

    def black50 = CssStyleName("text-black-50")
    def white50 = CssStyleName("text-white-50")

    def nowrap = CssStyleName("text-nowrap")
    def truncate = CssStyleName("text-truncate")

    def lowercase = CssStyleName("text-lowercase")
    def uppercase = CssStyleName("text-uppercase")
    def capitalize = CssStyleName("text-capitalize")
    def monospace = CssStyleName("text-monospace")

    def weightBold = CssStyleName("font-weight-bold")
    def weightBolder = CssStyleName("font-weight-bolder")
    def weightNormal = CssStyleName("font-weight-normal")
    def weightLight = CssStyleName("font-weight-light")
    def weightLighter = CssStyleName("font-weight-lighter")
    def italic = CssStyleName("font-italic")

    def body = CssStyleName("text-body")
    def decorationNone = CssStyleName("text-decoration-none")
    def hide = CssStyleName("text-hide")
    def muted = CssStyleName("text-muted")
  }

  object Tooltip {
    def tooltip = CssStyleName("tooltip")

    def arrow = CssStyleName("tooltip-arrow")
    def inner = CssStyleName("tooltip-inner")
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
    def small = CssStyleName("small")
  }

  object VerticalAlign {
    def baseline = CssStyleName("align-baseline")
    def bottom = CssStyleName("align-bottom")
    def middle = CssStyleName("align-middle")
    def textBottom = CssStyleName("align-text-bottom")
    def textTop = CssStyleName("align-text-top")
    def top = CssStyleName("align-top")
  }

  object Visibility {
    def clearfix = CssStyleName("clearfix")

    def visible = CssStyleName("visible")
    def invisible = CssStyleName("invisible")

    def srOnly = CssStyleName("sr-only")
    def srOnlyFocusable = CssStyleName("sr-only-focusable")
  }
}