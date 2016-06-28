package io.udash.bootstrap

import scala.language.postfixOps

object BootstrapTags {

  import scalatags.JsDom.all._

  val dataBackdrop = "data-backdrop".attr
  val dataBind = "data-bind".attr
  val dataDismiss = "data-dismiss".attr
  val dataKeyboard = "data-keyboard".attr
  val dataLabel = "data-label".attr
  val dataParent = "data-parent".attr
  val dataRide = "data-ride".attr
  val dataShow = "data-show".attr
  val dataSlide = "data-slide".attr
  val dataSlideTo = "data-slide-to".attr
  val dataTarget = "data-target".attr
  val dataToggle = "data-toggle".attr

}

object BootstrapStyles {
  case class BootstrapClass(cls: String) extends AnyVal
  
  val active = BootstrapClass("active")
  val container = BootstrapClass("container")
  val containerFluid = BootstrapClass("container-fluid")
  val row = BootstrapClass("row")
  val pullLeft = BootstrapClass("pull-left")
  val pullRight = BootstrapClass("pull-right")
  val centerBlock = BootstrapClass("center-block")
  val affix = BootstrapClass("affix")
  val arrow = BootstrapClass("arrow")
  val bottom = BootstrapClass("bottom")
  val close = BootstrapClass("close")
  val col = BootstrapClass("col")
  val collapsed = BootstrapClass("collapsed")
  val body = container
  val divider = BootstrapClass("divider")
  val disabled = BootstrapClass("disabled")
  val fade = BootstrapClass("fade")
  val hide = BootstrapClass("hide")
  val iconBar = BootstrapClass("icon-bar")
  val iconNext = BootstrapClass("icon-next")
  val in = BootstrapClass("in")
  val item = BootstrapClass("item")
  val jumbotron = BootstrapClass("jumbotron")
  val left = BootstrapClass("left")
  val next = BootstrapClass("next")
  val pillPane = BootstrapClass("pill-pane")
  val preScrollable = BootstrapClass("pre-scrollable")
  val prettyprint = BootstrapClass("prettyprint")
  val previous = BootstrapClass("previous")
  val prev = BootstrapClass("prev")
  val right = BootstrapClass("right")
  val show = BootstrapClass("show")
  val top = BootstrapClass("top")

  object Collapse {
    val collapse = BootstrapClass("collapse")
    val collapsing = BootstrapClass("collapsing")
    val collapseIn = in
  }

  object Grid {
    val colLg1 = BootstrapClass("col-lg-1")
    val colLg10 = BootstrapClass("col-lg-10")
    val colLg11 = BootstrapClass("col-lg-11")
    val colLg12 = BootstrapClass("col-lg-12")
    val colLg2 = BootstrapClass("col-lg-2")
    val colLg3 = BootstrapClass("col-lg-3")
    val colLg4 = BootstrapClass("col-lg-4")
    val colLg5 = BootstrapClass("col-lg-5")
    val colLg6 = BootstrapClass("col-lg-6")
    val colLg7 = BootstrapClass("col-lg-7")
    val colLg8 = BootstrapClass("col-lg-8")
    val colLg9 = BootstrapClass("col-lg-9")
    val colLgOffset0 = BootstrapClass("col-lg-offset-0")
    val colLgOffset1 = BootstrapClass("col-lg-offset-1")
    val colLgOffset10 = BootstrapClass("col-lg-offset-10")
    val colLgOffset11 = BootstrapClass("col-lg-offset-11")
    val colLgOffset2 = BootstrapClass("col-lg-offset-2")
    val colLgOffset3 = BootstrapClass("col-lg-offset-3")
    val colLgOffset4 = BootstrapClass("col-lg-offset-4")
    val colLgOffset5 = BootstrapClass("col-lg-offset-5")
    val colLgOffset6 = BootstrapClass("col-lg-offset-6")
    val colLgOffset7 = BootstrapClass("col-lg-offset-7")
    val colLgOffset8 = BootstrapClass("col-lg-offset-8")
    val colLgOffset9 = BootstrapClass("col-lg-offset-9")
    val colLgPull0 = BootstrapClass("col-lg-pull-0")
    val colLgPull1 = BootstrapClass("col-lg-pull-1")
    val colLgPull10 = BootstrapClass("col-lg-pull-10")
    val colLgPull11 = BootstrapClass("col-lg-pull-11")
    val colLgPull2 = BootstrapClass("col-lg-pull-2")
    val colLgPull3 = BootstrapClass("col-lg-pull-3")
    val colLgPull4 = BootstrapClass("col-lg-pull-4")
    val colLgPull5 = BootstrapClass("col-lg-pull-5")
    val colLgPull6 = BootstrapClass("col-lg-pull-6")
    val colLgPull7 = BootstrapClass("col-lg-pull-7")
    val colLgPull8 = BootstrapClass("col-lg-pull-8")
    val colLgPull9 = BootstrapClass("col-lg-pull-9")
    val colLgPush0 = BootstrapClass("col-lg-push-0")
    val colLgPush1 = BootstrapClass("col-lg-push-1")
    val colLgPush10 = BootstrapClass("col-lg-push-10")
    val colLgPush11 = BootstrapClass("col-lg-push-11")
    val colLgPush2 = BootstrapClass("col-lg-push-2")
    val colLgPush3 = BootstrapClass("col-lg-push-3")
    val colLgPush4 = BootstrapClass("col-lg-push-4")
    val colLgPush5 = BootstrapClass("col-lg-push-5")
    val colLgPush6 = BootstrapClass("col-lg-push-6")
    val colLgPush7 = BootstrapClass("col-lg-push-7")
    val colLgPush8 = BootstrapClass("col-lg-push-8")
    val colLgPush9 = BootstrapClass("col-lg-push-9")
    val colMd1 = BootstrapClass("col-md-1")
    val colMd10 = BootstrapClass("col-md-10")
    val colMd11 = BootstrapClass("col-md-11")
    val colMd12 = BootstrapClass("col-md-12")
    val colMd2 = BootstrapClass("col-md-2")
    val colMd3 = BootstrapClass("col-md-3")
    val colMd4 = BootstrapClass("col-md-4")
    val colMd5 = BootstrapClass("col-md-5")
    val colMd6 = BootstrapClass("col-md-6")
    val colMd7 = BootstrapClass("col-md-7")
    val colMd8 = BootstrapClass("col-md-8")
    val colMd9 = BootstrapClass("col-md-9")
    val colMdOffset0 = BootstrapClass("col-md-offset-0")
    val colMdOffset1 = BootstrapClass("col-md-offset-1")
    val colMdOffset10 = BootstrapClass("col-md-offset-10")
    val colMdOffset11 = BootstrapClass("col-md-offset-11")
    val colMdOffset2 = BootstrapClass("col-md-offset-2")
    val colMdOffset3 = BootstrapClass("col-md-offset-3")
    val colMdOffset4 = BootstrapClass("col-md-offset-4")
    val colMdOffset5 = BootstrapClass("col-md-offset-5")
    val colMdOffset6 = BootstrapClass("col-md-offset-6")
    val colMdOffset7 = BootstrapClass("col-md-offset-7")
    val colMdOffset8 = BootstrapClass("col-md-offset-8")
    val colMdOffset9 = BootstrapClass("col-md-offset-9")
    val colMdPull0 = BootstrapClass("col-md-pull-0")
    val colMdPull1 = BootstrapClass("col-md-pull-1")
    val colMdPull10 = BootstrapClass("col-md-pull-10")
    val colMdPull11 = BootstrapClass("col-md-pull-11")
    val colMdPull2 = BootstrapClass("col-md-pull-2")
    val colMdPull3 = BootstrapClass("col-md-pull-3")
    val colMdPull4 = BootstrapClass("col-md-pull-4")
    val colMdPull5 = BootstrapClass("col-md-pull-5")
    val colMdPull6 = BootstrapClass("col-md-pull-6")
    val colMdPull7 = BootstrapClass("col-md-pull-7")
    val colMdPull8 = BootstrapClass("col-md-pull-8")
    val colMdPull9 = BootstrapClass("col-md-pull-9")
    val colMdPush0 = BootstrapClass("col-md-push-0")
    val colMdPush1 = BootstrapClass("col-md-push-1")
    val colMdPush10 = BootstrapClass("col-md-push-10")
    val colMdPush11 = BootstrapClass("col-md-push-11")
    val colMdPush2 = BootstrapClass("col-md-push-2")
    val colMdPush3 = BootstrapClass("col-md-push-3")
    val colMdPush4 = BootstrapClass("col-md-push-4")
    val colMdPush5 = BootstrapClass("col-md-push-5")
    val colMdPush6 = BootstrapClass("col-md-push-6")
    val colMdPush7 = BootstrapClass("col-md-push-7")
    val colMdPush8 = BootstrapClass("col-md-push-8")
    val colMdPush9 = BootstrapClass("col-md-push-9")
    val colSm1 = BootstrapClass("col-sm-1")
    val colSm10 = BootstrapClass("col-sm-10")
    val colSm11 = BootstrapClass("col-sm-11")
    val colSm12 = BootstrapClass("col-sm-12")
    val colSm2 = BootstrapClass("col-sm-2")
    val colSm3 = BootstrapClass("col-sm-3")
    val colSm4 = BootstrapClass("col-sm-4")
    val colSm5 = BootstrapClass("col-sm-5")
    val colSm6 = BootstrapClass("col-sm-6")
    val colSm7 = BootstrapClass("col-sm-7")
    val colSm8 = BootstrapClass("col-sm-8")
    val colSm9 = BootstrapClass("col-sm-9")
    val colSmOffset1 = BootstrapClass("col-sm-offset-1")
    val colSmOffset10 = BootstrapClass("col-sm-offset-10")
    val colSmOffset11 = BootstrapClass("col-sm-offset-11")
    val colSmOffset2 = BootstrapClass("col-sm-offset-2")
    val colSmOffset3 = BootstrapClass("col-sm-offset-3")
    val colSmOffset4 = BootstrapClass("col-sm-offset-4")
    val colSmOffset5 = BootstrapClass("col-sm-offset-5")
    val colSmOffset6 = BootstrapClass("col-sm-offset-6")
    val colSmOffset7 = BootstrapClass("col-sm-offset-7")
    val colSmOffset8 = BootstrapClass("col-sm-offset-8")
    val colSmOffset9 = BootstrapClass("col-sm-offset-9")
    val colSmPull1 = BootstrapClass("col-sm-pull-1")
    val colSmPull10 = BootstrapClass("col-sm-pull-10")
    val colSmPull11 = BootstrapClass("col-sm-pull-11")
    val colSmPull2 = BootstrapClass("col-sm-pull-2")
    val colSmPull3 = BootstrapClass("col-sm-pull-3")
    val colSmPull4 = BootstrapClass("col-sm-pull-4")
    val colSmPull5 = BootstrapClass("col-sm-pull-5")
    val colSmPull6 = BootstrapClass("col-sm-pull-6")
    val colSmPull7 = BootstrapClass("col-sm-pull-7")
    val colSmPull8 = BootstrapClass("col-sm-pull-8")
    val colSmPull9 = BootstrapClass("col-sm-pull-9")
    val colSmPush1 = BootstrapClass("col-sm-push-1")
    val colSmPush10 = BootstrapClass("col-sm-push-10")
    val colSmPush11 = BootstrapClass("col-sm-push-11")
    val colSmPush2 = BootstrapClass("col-sm-push-2")
    val colSmPush3 = BootstrapClass("col-sm-push-3")
    val colSmPush4 = BootstrapClass("col-sm-push-4")
    val colSmPush5 = BootstrapClass("col-sm-push-5")
    val colSmPush6 = BootstrapClass("col-sm-push-6")
    val colSmPush7 = BootstrapClass("col-sm-push-7")
    val colSmPush8 = BootstrapClass("col-sm-push-8")
    val colSmPush9 = BootstrapClass("col-sm-push-9")
    val colXs1 = BootstrapClass("col-xs-1")
    val colXs10 = BootstrapClass("col-xs-10")
    val colXs11 = BootstrapClass("col-xs-11")
    val colXs12 = BootstrapClass("col-xs-12")
    val colXs2 = BootstrapClass("col-xs-2")
    val colXs3 = BootstrapClass("col-xs-3")
    val colXs4 = BootstrapClass("col-xs-4")
    val colXs5 = BootstrapClass("col-xs-5")
    val colXs6 = BootstrapClass("col-xs-6")
    val colXs7 = BootstrapClass("col-xs-7")
    val colXs8 = BootstrapClass("col-xs-8")
    val colXs9 = BootstrapClass("col-xs-9")
  }

  object Background {
    val bgPrimary = BootstrapClass("bg-primary")
    val bgSuccess = BootstrapClass("bg-success")
    val bgInfo = BootstrapClass("bg-info")
    val bgWarning = BootstrapClass("bg-warning")
    val bgDanger = BootstrapClass("bg-danger")
  }

  object Button {
    val btn = BootstrapClass("btn")
    val btnBlock = BootstrapClass("btn-block")
    val btnDanger = BootstrapClass("btn-danger")
    val btnDefault = BootstrapClass("btn-default")
    val btnGroup = BootstrapClass("btn-group")
    val btnGroupJustified = BootstrapClass("btn-group-justified")
    val btnGroupVertical = BootstrapClass("btn-group-vertical")
    val btnInfo = BootstrapClass("btn-info")
    val btnLg = BootstrapClass("btn-lg")
    val btnLink = BootstrapClass("btn-link")
    val btnPrimary = BootstrapClass("btn-primary")
    val btnSuccess = BootstrapClass("btn-success")
    val btnToolbar = BootstrapClass("btn-toolbar")
    val btnWarning = BootstrapClass("btn-warning")
    val btnXs = BootstrapClass("btn-xs")
    val btnSm = BootstrapClass("btn-sm")
  }

  object Well {
    val well = BootstrapClass("well")
    val wellLg = BootstrapClass("well-lg")
    val wellSm = BootstrapClass("well-sm")
  }

  object Carousel {
    val carousel = BootstrapClass("carousel")
    val carouselCaption = BootstrapClass("carousel-caption")
    val carouselControl = BootstrapClass("carousel-control")
    val carouselIndicators = BootstrapClass("carousel-indicators")
    val carouselInner = BootstrapClass("carousel-inner")
    val slide = BootstrapClass("slide")
  }

  object Alert {
    val alert = BootstrapClass("alert")
    val alertDanger = BootstrapClass("alert-danger")
    val alertDismissible = BootstrapClass("alert-dismissible")
    val alertInfo = BootstrapClass("alert-info")
    val alertLink = BootstrapClass("alert-link")
    val alertSuccess = BootstrapClass("alert-success")
    val alertWarning = BootstrapClass("alert-warning")
  }

  object Dropdown {
    val dropdown = BootstrapClass("dropdown")
    val dropdownBackdrop = BootstrapClass("dropdown-backdrop")
    val dropdownHeader = BootstrapClass("dropdown-header")
    val dropdownMenu = BootstrapClass("dropdown-menu")
    val dropdownToggle = BootstrapClass("dropdown-toggle")
    val dropup = BootstrapClass("dropup")
    val caret = BootstrapClass("caret")
  }

  object Visibility {
    val clearfix = BootstrapClass("clearfix")
    val hidden = BootstrapClass("hidden")
    val hiddenLg = BootstrapClass("hidden-lg")
    val hiddenMd = BootstrapClass("hidden-md")
    val hiddenPrint = BootstrapClass("hidden-print")
    val hiddenSm = BootstrapClass("hidden-sm")
    val hiddenXs = BootstrapClass("hidden-xs")
    val visibleLg = BootstrapClass("visible-lg")
    val visibleMd = BootstrapClass("visible-md")
    val visiblePrint = BootstrapClass("visible-print")
    val visibleSm = BootstrapClass("visible-sm")
    val visibleXs = BootstrapClass("visible-xs")
    val invisible = BootstrapClass("invisible")
    val srOnly = BootstrapClass("sr-only")
    val srOnlyFocusable = BootstrapClass("sr-only-focusable")
  }

  object Typography {
    val h1 = BootstrapClass("h1")
    val h2 = BootstrapClass("h2")
    val h3 = BootstrapClass("h3")
    val h4 = BootstrapClass("h4")
    val h5 = BootstrapClass("h5")
    val h6 = BootstrapClass("h6")
    val pageHeader = BootstrapClass("page-header")
    val lead = BootstrapClass("lead")
    val textCenter = BootstrapClass("text-center")
    val textDanger = BootstrapClass("text-danger")
    val textHide = BootstrapClass("text-hide")
    val textInfo = BootstrapClass("text-info")
    val textLeft = BootstrapClass("text-left")
    val textMuted = BootstrapClass("text-muted")
    val textPrimary = BootstrapClass("text-primary")
    val textRight = BootstrapClass("text-right")
    val textSuccess = BootstrapClass("text-success")
    val textWarning = BootstrapClass("text-warning")
    val textNoWrap = BootstrapClass("text-nowrap")
    val textLowercase = BootstrapClass("text-lowercase")
    val textUppercase = BootstrapClass("text-uppercase")
    val textCapitalize = BootstrapClass("text-capitalize")
    val initialism = BootstrapClass("initialism")
  }

  object List {
    val listGroup = BootstrapClass("list-group")
    val listGroupItem = BootstrapClass("list-group-item")
    val listGroupItemHeading = BootstrapClass("list-group-item-heading")
    val listGroupItemText = BootstrapClass("list-group-item-text")
    val listInline = BootstrapClass("list-inline")
    val listUnstyled = BootstrapClass("list-unstyled")
    val listItemSuccess = BootstrapClass("list-group-item-success")
    val listItemInfo = BootstrapClass("list-group-item-info")
    val listItemWarning = BootstrapClass("list-group-item-warning")
    val listItemDanger = BootstrapClass("list-group-item-danger")
    val dlHorizontal = BootstrapClass("dl-horizontal")
  }

  object Table {
    val table = BootstrapClass("table")
    val tableBordered = BootstrapClass("table-bordered")
    val tableResponsive = BootstrapClass("table-responsive")
    val tableStriped = BootstrapClass("table-striped")
    val tableHover = BootstrapClass("table-hover")
    val tableCondensed = BootstrapClass("table-condensed")
    val success = BootstrapClass("success")
    val info = BootstrapClass("info")
    val warning = BootstrapClass("warning")
    val danger = BootstrapClass("danger")
  }

  object Form {
    val formControl = BootstrapClass("form-control")
    val formControlStatic = BootstrapClass("form-control-static")
    val formGroup = BootstrapClass("form-group")
    val formGroupLg = BootstrapClass("form-group-lg")
    val formGroupSm = BootstrapClass("form-group-sm")
    val formInline = BootstrapClass("form-inline")
    val formHorizontal = BootstrapClass("form-horizontal")
    val inputGroup = BootstrapClass("input-group")
    val inputGroupAddon = BootstrapClass("input-group-addon")
    val inputGroupBtn = BootstrapClass("input-group-btn")
    val inputGroupLg = BootstrapClass("input-group-lg")
    val inputGroupSm = BootstrapClass("input-group-sm")
    val inputLg = BootstrapClass("input-lg")
    val inputSm = BootstrapClass("input-sm")
    val checkbox = BootstrapClass("checkbox")
    val checkboxInline = BootstrapClass("checkbox-inline")
    val hasWarning = BootstrapClass("has-warning")
    val hasError = BootstrapClass("has-error")
    val hasSuccess = BootstrapClass("has-success")
    val hasFeedback = BootstrapClass("has-feedback")
    val helpBlock = BootstrapClass("help-block")
    val radio = BootstrapClass("radio")
    val radioInline = BootstrapClass("radio-inline")
    val controlLabel = BootstrapClass("control-label")
  }

  object Image {
    val imgCircle = BootstrapClass("img-circle")
    val imgResponsive = BootstrapClass("img-responsive")
    val imgRounded = BootstrapClass("img-rounded")
    val imgThumbnail = BootstrapClass("img-thumbnail")
    val caption = BootstrapClass("caption")
    val thumbnail = BootstrapClass("thumbnail")

    val _fa = BootstrapClass("fa")
    val _glyphicon = BootstrapClass("glyphicon")

    //todo private or use dynamic scalacss
    def fa(name: String) = Seq(_fa, BootstrapClass(s"fa-$name"))
    def glyphicon(name: String) = Seq(_glyphicon, BootstrapClass(s"glyphicon-$name"))
  }

  object Navigation {
    val nav = BootstrapClass("nav")
    val navbar = BootstrapClass("navbar")
    val navbarBrand = BootstrapClass("navbar-brand")
    val navbarBtn = BootstrapClass("navbar-btn")
    val navbarCollapse = BootstrapClass("navbar-collapse")
    val navbarDefault = BootstrapClass("navbar-default")
    val navbarFixedBottom = BootstrapClass("navbar-fixed-bottom")
    val navbarFixedTop = BootstrapClass("navbar-fixed-top")
    val navbarForm = BootstrapClass("navbar-form")
    val navbarHeader = BootstrapClass("navbar-header")
    val navbarInverse = BootstrapClass("navbar-inverse")
    val navbarLeft = BootstrapClass("navbar-left")
    val navbarLink = BootstrapClass("navbar-link")
    val navbarNav = BootstrapClass("navbar-nav")
    val navbarRight = BootstrapClass("navbar-right")
    val navbarStaticTop = BootstrapClass("navbar-static-top")
    val navbarText = BootstrapClass("navbar-text")
    val navbarToggle = BootstrapClass("navbar-toggle")
    val navDivider = BootstrapClass("nav-divider")
    val navJustified = BootstrapClass("nav-justified")
    val navStacked = BootstrapClass("nav-stacked")
    val navPills = BootstrapClass("nav-pills")
    val navTabs = BootstrapClass("nav-tabs")
    val navTabsJustified = BootstrapClass("nav-tabs-justified")
    val breadcrumb = BootstrapClass("breadcrumb")
  }

  object Label {
    val badge = BootstrapClass("badge")
    val label = BootstrapClass("label")
    val labelDanger = BootstrapClass("label-danger")
    val labelDefault = BootstrapClass("label-default")
    val labelInfo = BootstrapClass("label-info")
    val labelPrimary = BootstrapClass("label-primary")
    val labelSuccess = BootstrapClass("label-success")
    val labelWarning = BootstrapClass("label-warning")
  }

  object Media {
    val media = BootstrapClass("media")
    val mediaBody = BootstrapClass("media-body")
    val mediaHeading = BootstrapClass("media-heading")
    val mediaList = BootstrapClass("media-list")
    val mediaObject = BootstrapClass("media-object")
    val mediaLeft = BootstrapClass("media-left")
    val mediaMiddle = BootstrapClass("media-middle")
    val mediaRight = BootstrapClass("media-right")
  }

  object Modal {
    val modal = BootstrapClass("modal")
    val modalBackdrop = BootstrapClass("modal-backdrop")
    val modalBody = BootstrapClass("modal-body")
    val modalContent = BootstrapClass("modal-content")
    val modalDialog = BootstrapClass("modal-dialog")
    val modalFooter = BootstrapClass("modal-footer")
    val modalHeader = BootstrapClass("modal-header")
    val modalLarge = BootstrapClass("modal-lg")
    val modalOpen = BootstrapClass("modal-open")
    val modalSmall = BootstrapClass("modal-sm")
    val modalTitle = BootstrapClass("modal-title")
  }

  object Pagination {
    val pagination = BootstrapClass("pagination")
    val paginationLg = BootstrapClass("pagination-lg")
    val paginationSm = BootstrapClass("pagination-sm")
    val pager = BootstrapClass("pager")
  }

  object Panel {
    val panel = BootstrapClass("panel")
    val panelBody = BootstrapClass("panel-body")
    val panelCollapse = BootstrapClass("panel-collapse")
    val panelDanger = BootstrapClass("panel-danger")
    val panelDefault = BootstrapClass("panel-default")
    val panelFooter = BootstrapClass("panel-footer")
    val panelGroup = BootstrapClass("panel-group")
    val panelHeading = BootstrapClass("panel-heading")
    val panelInfo = BootstrapClass("panel-info")
    val panelPrimary = BootstrapClass("panel-primary")
    val panelSuccess = BootstrapClass("panel-success")
    val panelTitle = BootstrapClass("panel-title")
    val panelWarning = BootstrapClass("panel-warning")
  }

  object Popover {
    val popover = BootstrapClass("popover")
    val popoverContent = BootstrapClass("popover-content")
    val popoverTitle = BootstrapClass("popover-title")
  }

  object ProgressBar {
    val progress = BootstrapClass("progress")
    val progressBar = BootstrapClass("progress-bar")
    val progressBarDanger = BootstrapClass("progress-bar-danger")
    val progressBarInfo = BootstrapClass("progress-bar-info")
    val progressBarSuccess = BootstrapClass("progress-bar-success")
    val progressBarWarning = BootstrapClass("progress-bar-warning")
    val progressBarStriped = BootstrapClass("progress-bar-striped")
  }

  object Tooltip {
    val tooltip = BootstrapClass("tooltip")
    val tooltipArrow = BootstrapClass("tooltip-arrow")
    val tooltipInner = BootstrapClass("tooltip-inner")
  }

  object EmbedResponsive {
    val embed = BootstrapClass("embed-responsive")
    val item = BootstrapClass("embed-responsive-item")
    val embed16by9 = BootstrapClass("embed-responsive-16by9")
    val embed4by3 = BootstrapClass("embed-responsive-4by3")
  }
}