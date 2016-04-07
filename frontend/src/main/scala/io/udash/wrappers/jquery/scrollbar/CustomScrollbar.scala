package io.udash.wrappers.jquery.scrollbar

import io.udash.wrappers.jquery.JQuery

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * Created by malchik on 2016-04-05.
  */
@js.native
trait CustomScrollbar extends JQuery {
  @JSName("mCustomScrollbar")
  def customScrollbar(): CustomScrollbar = js.native

  @JSName("mCustomScrollbar")
  def customScrollbar(options: CustomScrollbarOptions): CustomScrollbar = js.native

  @JSName("mCustomScrollbar")
  def customScrollbar(cmd: String, params: js.Any*): CustomScrollbar = js.native
}

@js.native
trait CustomScrollbarOptions extends js.Object
object CustomScrollbarOptions extends CustomScrollbarOptionsBuilder(noOpts)
class CustomScrollbarOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[CustomScrollbarOptions, CustomScrollbarOptionsBuilder](new CustomScrollbarOptionsBuilder(_)) {
  /**
    * By default, the script applies a vertical scrollbar. To add a horizontal or 2-axis scrollbars, invoke mCustomScrollbar function with the axis option set to "x" or "yx" respectively
    */
  def axis(ax: CustomScrollbarAxis) = jsOpt("axis", ax.value)

  /**
    * Enable or disable auto-hiding the scrollbar when inactive.
    */
  def autoHideScrollbar(value: Boolean) = jsOpt("autoHideScrollbar", value)
}

abstract class CustomScrollbarAxis(val value: String)
object CustomScrollbarAxis {
  case object X extends CustomScrollbarAxis("x")
  case object XY extends CustomScrollbarAxis("xy")
}


class CustomScrollCommands(val scrollbar: CustomScrollbar) extends AnyVal {
  def maskCmd(cmd: String, params: js.Any*) = scrollbar.customScrollbar(cmd, params: _*)

  /**
    * Calling destroy method will completely remove the custom scrollbar and return the element to its original state
    * @return
    */
  def destroy() = maskCmd("destroy")
}


