package io.udash.bootstrap

import scala.language.postfixOps
import scalacss.Defaults._

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

object BootstrapStyles extends StyleSheet.Inline {

  import dsl._

  val active = style(addClassName("active"))
  val container = style(addClassName("container"))
  val containerFluid = style(addClassName("container-fluid"))
  val row = style(addClassName("row"))
  val pullLeft = style(addClassName("pull-left"))
  val pullRight = style(addClassName("pull-right"))
  val centerBlock = style(addClassName("center-block"))
  val affix = style(addClassName("affix"))
  val arrow = style(addClassName("arrow"))
  val bottom = style(addClassName("bottom"))
  val close = style(addClassName("close"))
  val col = style(addClassName("col"))
  val collapsed = style(addClassName("collapsed"))
  val body = container
  val divider = style(addClassName("divider"))
  val disabled = style(addClassName("disabled"))
  val fade = style(addClassName("fade"))
  val hide = style(addClassName("hide"))
  val iconBar = style(addClassName("icon-bar"))
  val iconNext = style(addClassName("icon-next"))
  val in = style(addClassName("in"))
  val item = style(addClassName("item"))
  val jumbotron = style(addClassName("jumbotron"))
  val left = style(addClassName("left"))
  val next = style(addClassName("next"))
  val pillPane = style(addClassName("pill-pane"))
  val preScrollable = style(addClassName("pre-scrollable"))
  val prettyprint = style(addClassName("prettyprint"))
  val previous = style(addClassName("previous"))
  val prev = style(addClassName("prev"))
  val right = style(addClassName("right"))
  val show = style(addClassName("show"))
  val top = style(addClassName("top"))

  object Collapse {
    val collapse = style(addClassName("collapse"))
    val collapsing = style(addClassName("collapsing"))
    val collapseIn = in
  }

  object Grid {
    val colLg1 = style(addClassName("col-lg-1"))
    val colLg10 = style(addClassName("col-lg-10"))
    val colLg11 = style(addClassName("col-lg-11"))
    val colLg12 = style(addClassName("col-lg-12"))
    val colLg2 = style(addClassName("col-lg-2"))
    val colLg3 = style(addClassName("col-lg-3"))
    val colLg4 = style(addClassName("col-lg-4"))
    val colLg5 = style(addClassName("col-lg-5"))
    val colLg6 = style(addClassName("col-lg-6"))
    val colLg7 = style(addClassName("col-lg-7"))
    val colLg8 = style(addClassName("col-lg-8"))
    val colLg9 = style(addClassName("col-lg-9"))
    val colLgOffset0 = style(addClassName("col-lg-offset-0"))
    val colLgOffset1 = style(addClassName("col-lg-offset-1"))
    val colLgOffset10 = style(addClassName("col-lg-offset-10"))
    val colLgOffset11 = style(addClassName("col-lg-offset-11"))
    val colLgOffset2 = style(addClassName("col-lg-offset-2"))
    val colLgOffset3 = style(addClassName("col-lg-offset-3"))
    val colLgOffset4 = style(addClassName("col-lg-offset-4"))
    val colLgOffset5 = style(addClassName("col-lg-offset-5"))
    val colLgOffset6 = style(addClassName("col-lg-offset-6"))
    val colLgOffset7 = style(addClassName("col-lg-offset-7"))
    val colLgOffset8 = style(addClassName("col-lg-offset-8"))
    val colLgOffset9 = style(addClassName("col-lg-offset-9"))
    val colLgPull0 = style(addClassName("col-lg-pull-0"))
    val colLgPull1 = style(addClassName("col-lg-pull-1"))
    val colLgPull10 = style(addClassName("col-lg-pull-10"))
    val colLgPull11 = style(addClassName("col-lg-pull-11"))
    val colLgPull2 = style(addClassName("col-lg-pull-2"))
    val colLgPull3 = style(addClassName("col-lg-pull-3"))
    val colLgPull4 = style(addClassName("col-lg-pull-4"))
    val colLgPull5 = style(addClassName("col-lg-pull-5"))
    val colLgPull6 = style(addClassName("col-lg-pull-6"))
    val colLgPull7 = style(addClassName("col-lg-pull-7"))
    val colLgPull8 = style(addClassName("col-lg-pull-8"))
    val colLgPull9 = style(addClassName("col-lg-pull-9"))
    val colLgPush0 = style(addClassName("col-lg-push-0"))
    val colLgPush1 = style(addClassName("col-lg-push-1"))
    val colLgPush10 = style(addClassName("col-lg-push-10"))
    val colLgPush11 = style(addClassName("col-lg-push-11"))
    val colLgPush2 = style(addClassName("col-lg-push-2"))
    val colLgPush3 = style(addClassName("col-lg-push-3"))
    val colLgPush4 = style(addClassName("col-lg-push-4"))
    val colLgPush5 = style(addClassName("col-lg-push-5"))
    val colLgPush6 = style(addClassName("col-lg-push-6"))
    val colLgPush7 = style(addClassName("col-lg-push-7"))
    val colLgPush8 = style(addClassName("col-lg-push-8"))
    val colLgPush9 = style(addClassName("col-lg-push-9"))
    val colMd1 = style(addClassName("col-md-1"))
    val colMd10 = style(addClassName("col-md-10"))
    val colMd11 = style(addClassName("col-md-11"))
    val colMd12 = style(addClassName("col-md-12"))
    val colMd2 = style(addClassName("col-md-2"))
    val colMd3 = style(addClassName("col-md-3"))
    val colMd4 = style(addClassName("col-md-4"))
    val colMd5 = style(addClassName("col-md-5"))
    val colMd6 = style(addClassName("col-md-6"))
    val colMd7 = style(addClassName("col-md-7"))
    val colMd8 = style(addClassName("col-md-8"))
    val colMd9 = style(addClassName("col-md-9"))
    val colMdOffset0 = style(addClassName("col-md-offset-0"))
    val colMdOffset1 = style(addClassName("col-md-offset-1"))
    val colMdOffset10 = style(addClassName("col-md-offset-10"))
    val colMdOffset11 = style(addClassName("col-md-offset-11"))
    val colMdOffset2 = style(addClassName("col-md-offset-2"))
    val colMdOffset3 = style(addClassName("col-md-offset-3"))
    val colMdOffset4 = style(addClassName("col-md-offset-4"))
    val colMdOffset5 = style(addClassName("col-md-offset-5"))
    val colMdOffset6 = style(addClassName("col-md-offset-6"))
    val colMdOffset7 = style(addClassName("col-md-offset-7"))
    val colMdOffset8 = style(addClassName("col-md-offset-8"))
    val colMdOffset9 = style(addClassName("col-md-offset-9"))
    val colMdPull0 = style(addClassName("col-md-pull-0"))
    val colMdPull1 = style(addClassName("col-md-pull-1"))
    val colMdPull10 = style(addClassName("col-md-pull-10"))
    val colMdPull11 = style(addClassName("col-md-pull-11"))
    val colMdPull2 = style(addClassName("col-md-pull-2"))
    val colMdPull3 = style(addClassName("col-md-pull-3"))
    val colMdPull4 = style(addClassName("col-md-pull-4"))
    val colMdPull5 = style(addClassName("col-md-pull-5"))
    val colMdPull6 = style(addClassName("col-md-pull-6"))
    val colMdPull7 = style(addClassName("col-md-pull-7"))
    val colMdPull8 = style(addClassName("col-md-pull-8"))
    val colMdPull9 = style(addClassName("col-md-pull-9"))
    val colMdPush0 = style(addClassName("col-md-push-0"))
    val colMdPush1 = style(addClassName("col-md-push-1"))
    val colMdPush10 = style(addClassName("col-md-push-10"))
    val colMdPush11 = style(addClassName("col-md-push-11"))
    val colMdPush2 = style(addClassName("col-md-push-2"))
    val colMdPush3 = style(addClassName("col-md-push-3"))
    val colMdPush4 = style(addClassName("col-md-push-4"))
    val colMdPush5 = style(addClassName("col-md-push-5"))
    val colMdPush6 = style(addClassName("col-md-push-6"))
    val colMdPush7 = style(addClassName("col-md-push-7"))
    val colMdPush8 = style(addClassName("col-md-push-8"))
    val colMdPush9 = style(addClassName("col-md-push-9"))
    val colSm1 = style(addClassName("col-sm-1"))
    val colSm10 = style(addClassName("col-sm-10"))
    val colSm11 = style(addClassName("col-sm-11"))
    val colSm12 = style(addClassName("col-sm-12"))
    val colSm2 = style(addClassName("col-sm-2"))
    val colSm3 = style(addClassName("col-sm-3"))
    val colSm4 = style(addClassName("col-sm-4"))
    val colSm5 = style(addClassName("col-sm-5"))
    val colSm6 = style(addClassName("col-sm-6"))
    val colSm7 = style(addClassName("col-sm-7"))
    val colSm8 = style(addClassName("col-sm-8"))
    val colSm9 = style(addClassName("col-sm-9"))
    val colSmOffset1 = style(addClassName("col-sm-offset-1"))
    val colSmOffset10 = style(addClassName("col-sm-offset-10"))
    val colSmOffset11 = style(addClassName("col-sm-offset-11"))
    val colSmOffset2 = style(addClassName("col-sm-offset-2"))
    val colSmOffset3 = style(addClassName("col-sm-offset-3"))
    val colSmOffset4 = style(addClassName("col-sm-offset-4"))
    val colSmOffset5 = style(addClassName("col-sm-offset-5"))
    val colSmOffset6 = style(addClassName("col-sm-offset-6"))
    val colSmOffset7 = style(addClassName("col-sm-offset-7"))
    val colSmOffset8 = style(addClassName("col-sm-offset-8"))
    val colSmOffset9 = style(addClassName("col-sm-offset-9"))
    val colSmPull1 = style(addClassName("col-sm-pull-1"))
    val colSmPull10 = style(addClassName("col-sm-pull-10"))
    val colSmPull11 = style(addClassName("col-sm-pull-11"))
    val colSmPull2 = style(addClassName("col-sm-pull-2"))
    val colSmPull3 = style(addClassName("col-sm-pull-3"))
    val colSmPull4 = style(addClassName("col-sm-pull-4"))
    val colSmPull5 = style(addClassName("col-sm-pull-5"))
    val colSmPull6 = style(addClassName("col-sm-pull-6"))
    val colSmPull7 = style(addClassName("col-sm-pull-7"))
    val colSmPull8 = style(addClassName("col-sm-pull-8"))
    val colSmPull9 = style(addClassName("col-sm-pull-9"))
    val colSmPush1 = style(addClassName("col-sm-push-1"))
    val colSmPush10 = style(addClassName("col-sm-push-10"))
    val colSmPush11 = style(addClassName("col-sm-push-11"))
    val colSmPush2 = style(addClassName("col-sm-push-2"))
    val colSmPush3 = style(addClassName("col-sm-push-3"))
    val colSmPush4 = style(addClassName("col-sm-push-4"))
    val colSmPush5 = style(addClassName("col-sm-push-5"))
    val colSmPush6 = style(addClassName("col-sm-push-6"))
    val colSmPush7 = style(addClassName("col-sm-push-7"))
    val colSmPush8 = style(addClassName("col-sm-push-8"))
    val colSmPush9 = style(addClassName("col-sm-push-9"))
    val colXs1 = style(addClassName("col-xs-1"))
    val colXs10 = style(addClassName("col-xs-10"))
    val colXs11 = style(addClassName("col-xs-11"))
    val colXs12 = style(addClassName("col-xs-12"))
    val colXs2 = style(addClassName("col-xs-2"))
    val colXs3 = style(addClassName("col-xs-3"))
    val colXs4 = style(addClassName("col-xs-4"))
    val colXs5 = style(addClassName("col-xs-5"))
    val colXs6 = style(addClassName("col-xs-6"))
    val colXs7 = style(addClassName("col-xs-7"))
    val colXs8 = style(addClassName("col-xs-8"))
    val colXs9 = style(addClassName("col-xs-9"))
  }

  object Background {
    val bgPrimary = style(addClassName("bg-primary"))
    val bgSuccess = style(addClassName("bg-success"))
    val bgInfo = style(addClassName("bg-info"))
    val bgWarning = style(addClassName("bg-warning"))
    val bgDanger = style(addClassName("bg-danger"))
  }

  object Button {
    val btn = style(addClassName("btn"))
    val btnBlock = style(addClassName("btn-block"))
    val btnDanger = style(addClassName("btn-danger"))
    val btnDefault = style(addClassName("btn-default"))
    val btnGroup = style(addClassName("btn-group"))
    val btnGroupJustified = style(addClassName("btn-group-justified"))
    val btnGroupVertical = style(addClassName("btn-group-vertical"))
    val btnInfo = style(addClassName("btn-info"))
    val btnLg = style(addClassName("btn-lg"))
    val btnLink = style(addClassName("btn-link"))
    val btnPrimary = style(addClassName("btn-primary"))
    val btnSuccess = style(addClassName("btn-success"))
    val btnToolbar = style(addClassName("btn-toolbar"))
    val btnWarning = style(addClassName("btn-warning"))
    val btnXs = style(addClassName("btn-xs"))
    val btnSm = style(addClassName("btn-sm"))
  }

  object Well {
    val well = style(addClassName("well"))
    val wellLg = style(addClassName("well-lg"))
    val wellSm = style(addClassName("well-sm"))
  }

  object Carousel {
    val carousel = style(addClassName("carousel"))
    val carouselCaption = style(addClassName("carousel-caption"))
    val carouselControl = style(addClassName("carousel-control"))
    val carouselIndicators = style(addClassName("carousel-indicators"))
    val carouselInner = style(addClassName("carousel-inner"))
  }

  object Alert {
    val alert = style(addClassName("alert"))
    val alertDanger = style(addClassName("alert-danger"))
    val alertDismissible = style(addClassName("alert-dismissible"))
    val alertInfo = style(addClassName("alert-info"))
    val alertLink = style(addClassName("alert-link"))
    val alertSuccess = style(addClassName("alert-success"))
    val alertWarning = style(addClassName("alert-warning"))
  }

  object Dropdown {
    val dropdown = style(addClassName("dropdown"))
    val dropdownBackdrop = style(addClassName("dropdown-backdrop"))
    val dropdownHeader = style(addClassName("dropdown-header"))
    val dropdownMenu = style(addClassName("dropdown-menu"))
    val dropdownToggle = style(addClassName("dropdown-toggle"))
    val dropup = style(addClassName("dropup"))
    val caret = style(addClassName("caret"))
  }

  object Visibility {
    val clearfix = style(addClassName("clearfix"))
    val hidden = style(addClassName("hidden"))
    val hiddenLg = style(addClassName("hidden-lg"))
    val hiddenMd = style(addClassName("hidden-md"))
    val hiddenPrint = style(addClassName("hidden-print"))
    val hiddenSm = style(addClassName("hidden-sm"))
    val hiddenXs = style(addClassName("hidden-xs"))
    val visibleLg = style(addClassName("visible-lg"))
    val visibleMd = style(addClassName("visible-md"))
    val visiblePrint = style(addClassName("visible-print"))
    val visibleSm = style(addClassName("visible-sm"))
    val visibleXs = style(addClassName("visible-xs"))
    val invisible = style(addClassName("invisible"))
    val srOnly = style(addClassName("sr-only"))
    val srOnlyFocusable = style(addClassName("sr-only-focusable"))
  }

  object Typography {
    val h1 = style(addClassName("h1"))
    val h2 = style(addClassName("h2"))
    val h3 = style(addClassName("h3"))
    val h4 = style(addClassName("h4"))
    val h5 = style(addClassName("h5"))
    val h6 = style(addClassName("h6"))
    val pageHeader = style(addClassName("page-header"))
    val lead = style(addClassName("lead"))
    val textCenter = style(addClassName("text-center"))
    val textDanger = style(addClassName("text-danger"))
    val textHide = style(addClassName("text-hide"))
    val textInfo = style(addClassName("text-info"))
    val textLeft = style(addClassName("text-left"))
    val textMuted = style(addClassName("text-muted"))
    val textPrimary = style(addClassName("text-primary"))
    val textRight = style(addClassName("text-right"))
    val textSuccess = style(addClassName("text-success"))
    val textWarning = style(addClassName("text-warning"))
    val textNoWrap = style(addClassName("text-nowrap"))
    val textLowercase = style(addClassName("text-lowercase"))
    val textUppercase = style(addClassName("text-uppercase"))
    val textCapitalize = style(addClassName("text-capitalize"))
    val initialism = style(addClassName("initialism"))
  }

  object List {
    val listGroup = style(addClassName("list-group"))
    val listGroupItem = style(addClassName("list-group-item"))
    val listGroupItemHeading = style(addClassName("list-group-item-heading"))
    val listGroupItemText = style(addClassName("list-group-item-text"))
    val listInline = style(addClassName("list-inline"))
    val listUnstyled = style(addClassName("list-unstyled"))
    val listItemSuccess = style(addClassName("list-group-item-success"))
    val listItemInfo = style(addClassName("list-group-item-info"))
    val listItemWarning = style(addClassName("list-group-item-warning"))
    val listItemDanger = style(addClassName("list-group-item-danger"))
    val dlHorizontal = style(addClassName("dl-horizontal"))
  }

  object Table {
    val table = style(addClassName("table"))
    val tableBordered = style(addClassName("table-bordered"))
    val tableResponsive = style(addClassName("table-responsive"))
    val tableStriped = style(addClassName("table-striped"))
    val tableHover = style(addClassName("table-hover"))
    val tableCondensed = style(addClassName("table-condensed"))
    val success = style(addClassName("success"))
    val info = style(addClassName("info"))
    val warning = style(addClassName("warning"))
    val danger = style(addClassName("danger"))
  }

  object Form {
    val formControl = style(addClassName("form-control"))
    val formControlStatic = style(addClassName("form-control-static"))
    val formGroup = style(addClassName("form-group"))
    val formGroupLg = style(addClassName("form-group-lg"))
    val formGroupSm = style(addClassName("form-group-sm"))
    val formInline = style(addClassName("form-inline"))
    val formHorizontal = style(addClassName("form-horizontal"))
    val inputGroup = style(addClassName("input-group"))
    val inputGroupAddon = style(addClassName("input-group-addon"))
    val inputGroupBtn = style(addClassName("input-group-btn"))
    val inputGroupLg = style(addClassName("input-group-lg"))
    val inputGroupSm = style(addClassName("input-group-sm"))
    val inputLg = style(addClassName("input-lg"))
    val inputSm = style(addClassName("input-sm"))
    val checkbox = style(addClassName("checkbox"))
    val checkboxInline = style(addClassName("checkbox-inline"))
    val hasWarning = style(addClassName("has-warning"))
    val hasError = style(addClassName("has-error"))
    val hasSuccess = style(addClassName("has-success"))
    val hasFeedback = style(addClassName("has-feedback"))
    val helpBlock = style(addClassName("help-block"))
    val radio = style(addClassName("radio"))
    val radioInline = style(addClassName("radio-inline"))
    val controlLabel = style(addClassName("control-label"))
  }

  object Image {
    val imgCircle = style(addClassName("img-circle"))
    val imgResponsive = style(addClassName("img-responsive"))
    val imgRounded = style(addClassName("img-rounded"))
    val imgThumbnail = style(addClassName("img-thumbnail"))
    val caption = style(addClassName("caption"))
    val thumbnail = style(addClassName("thumbnail"))

    val _fa = style(addClassName("fa"))
    val _glyphicon = style(addClassName("glyphicon"))

    //todo private or use dynamic scalacss
    def fa(name: String) = Seq(_fa, style(addClassName(s"fa-$name")))
    def glyphicon(name: String) = Seq(_glyphicon, style(addClassName(s"glyphicon-$name")))
  }

  object Navigation {
    val nav = style(addClassName("nav"))
    val navbar = style(addClassName("navbar"))
    val navbarBrand = style(addClassName("navbar-brand"))
    val navbarBtn = style(addClassName("navbar-btn"))
    val navbarCollapse = style(addClassName("navbar-collapse"))
    val navbarDefault = style(addClassName("navbar-default"))
    val navbarFixedBottom = style(addClassName("navbar-fixed-bottom"))
    val navbarFixedTop = style(addClassName("navbar-fixed-top"))
    val navbarForm = style(addClassName("navbar-form"))
    val navbarHeader = style(addClassName("navbar-header"))
    val navbarInverse = style(addClassName("navbar-inverse"))
    val navbarLeft = style(addClassName("navbar-left"))
    val navbarLink = style(addClassName("navbar-link"))
    val navbarNav = style(addClassName("navbar-nav"))
    val navbarRight = style(addClassName("navbar-right"))
    val navbarStaticTop = style(addClassName("navbar-static-top"))
    val navbarText = style(addClassName("navbar-text"))
    val navbarToggle = style(addClassName("navbar-toggle"))
    val navDivider = style(addClassName("nav-divider"))
    val navJustified = style(addClassName("nav-justified"))
    val navTabs = style(addClassName("nav-tabs"))
    val navTabsJustified = style(addClassName("nav-tabs-justified"))
    val breadcrumb = style(addClassName("breadcrumb"))
  }

  object Label {
    val badge = style(addClassName("badge"))
    val label = style(addClassName("label"))
    val labelDanger = style(addClassName("label-danger"))
    val labelDefault = style(addClassName("label-default"))
    val labelInfo = style(addClassName("label-info"))
    val labelPrimary = style(addClassName("label-primary"))
    val labelSuccess = style(addClassName("label-success"))
    val labelWarning = style(addClassName("label-warning"))
  }

  object Media {
    val media = style(addClassName("media"))
    val mediaBody = style(addClassName("media-body"))
    val mediaHeading = style(addClassName("media-heading"))
    val mediaList = style(addClassName("media-list"))
    val mediaObject = style(addClassName("media-object"))
    val mediaLeft = style(addClassName("media-left"))
    val mediaMiddle = style(addClassName("media-middle"))
    val mediaRight = style(addClassName("media-right"))
  }

  object Modal {
    val modal = style(addClassName("modal"))
    val modalBackdrop = style(addClassName("modal-backdrop"))
    val modalBody = style(addClassName("modal-body"))
    val modalContent = style(addClassName("modal-content"))
    val modalDialog = style(addClassName("modal-dialog"))
    val modalFooter = style(addClassName("modal-footer"))
    val modalHeader = style(addClassName("modal-header"))
    val modalLarge = style(addClassName("modal-lg"))
    val modalOpen = style(addClassName("modal-open"))
    val modalSmall = style(addClassName("modal-sm"))
    val modalTitle = style(addClassName("modal-title"))
  }

  object Pagination {
    val pagination = style(addClassName("pagination"))
    val paginationLg = style(addClassName("pagination-lg"))
    val paginationSm = style(addClassName("pagination-sm"))
    val pager = style(addClassName("pager"))
  }

  object Panel {
    val panel = style(addClassName("panel"))
    val panelBody = style(addClassName("panel-body"))
    val panelCollapse = style(addClassName("panel-collapse"))
    val panelDanger = style(addClassName("panel-danger"))
    val panelDefault = style(addClassName("panel-default"))
    val panelFooter = style(addClassName("panel-footer"))
    val panelGroup = style(addClassName("panel-group"))
    val panelHeading = style(addClassName("panel-heading"))
    val panelInfo = style(addClassName("panel-info"))
    val panelPrimary = style(addClassName("panel-primary"))
    val panelSuccess = style(addClassName("panel-success"))
    val panelTitle = style(addClassName("panel-title"))
    val panelWarning = style(addClassName("panel-warning"))
  }

  object Popover {
    val popover = style(addClassName("popover"))
    val popoverContent = style(addClassName("popover-content"))
    val popoverTitle = style(addClassName("popover-title"))
  }

  object ProgressBar {
    val progress = style(addClassName("progress"))
    val progressBar = style(addClassName("progress-bar"))
    val progressBarDanger = style(addClassName("progress-bar-danger"))
    val progressBarInfo = style(addClassName("progress-bar-info"))
    val progressBarSuccess = style(addClassName("progress-bar-success"))
    val progressBarWarning = style(addClassName("progress-bar-warning"))
    val progressBarStriped = style(addClassName("progress-bar-striped"))
  }

  object Tooltip {
    val tooltip = style(addClassName("tooltip"))
    val tooltipArrow = style(addClassName("tooltip-arrow"))
    val tooltipInner = style(addClassName("tooltip-inner"))
  }

  object EmbedResponsive {
    val embed = style(addClassName("embed-responsive"))
    val item = style(addClassName("embed-responsive-item"))
    val embed16by9 = style(addClassName("embed-responsive-16by9"))
    val embed4by3 = style(addClassName("embed-responsive-4by3"))
  }
}