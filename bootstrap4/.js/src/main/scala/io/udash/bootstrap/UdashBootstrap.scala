package io.udash.bootstrap

import io.udash._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom
import org.scalajs.dom.Element
import scalatags.JsDom.all._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object UdashBootstrap {
  final val False: ReadableProperty[Boolean] = false.toProperty
  final val True: ReadableProperty[Boolean] = true.toProperty
  final val ColorSecondary: ReadableProperty[BootstrapStyles.Color] = BootstrapStyles.Color.Secondary.toProperty
  private final val NoneProperty = scala.None.toProperty
  def None[A]: ReadableProperty[Option[A]] = NoneProperty.asInstanceOf[ReadableProperty[Option[A]]]

  /** Loads FontAwesome styles. */
  def loadFontAwesome(): Element =
    link(rel := "stylesheet", href := "https://use.fontawesome.com/releases/v5.10.1/css/all.css").render
}

@js.native
@JSImport("bootstrap", JSImport.Namespace)
private object BootstrapRequire extends js.Any

@js.native
@JSImport("tempusdominus-bootstrap-4", JSImport.Namespace)
private object BootstrapDatepickerRequire extends js.Any

@js.native
@JSImport("moment", JSImport.Default)
final class Moment(locale: String, time: js.Any, format: String) extends js.Any {
  def format(dateFormat: String): String = js.native
  def valueOf(): Double = js.native
}

//noinspection ScalaUnusedSymbol
object BootstrapJs {

  def jqueryInterface(el: dom.Element): BootstrapJQuery = {
    val requireBootstrap: BootstrapRequire.type = BootstrapRequire
    jQ(el).asInstanceOf[BootstrapJQuery]
  }
  def datepickerInterface(el: dom.Element): DatePickerJQuery = {
    //js.Dynamic.global.jQuery = jQ
    //val requireDatepicker: BootstrapDatepickerRequire.type = BootstrapDatepickerRequire
    jQ(el).asInstanceOf[DatePickerJQuery]
  }

  @js.native
  trait BootstrapJQuery extends JQuery {
    def alert(cmd: String): Unit = js.native
    def tooltip(arg: js.Any): Unit = js.native
    def popover(arg: js.Any): Unit = js.native
  }

  @js.native
  trait DatePickerJQuery extends JQuery {
    def datetimepicker(settings: js.Dictionary[js.Any]): Unit = js.native
    def datetimepicker(): Unit = js.native
    def datetimepicker(function: String): Unit = js.native
    def datetimepicker(option: String, value: js.Any): Unit = js.native
  }
}