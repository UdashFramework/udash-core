package io.udash.bootstrap

import com.karasiq.bootstrap
import io.udash._
import org.scalajs.dom
import org.scalajs.dom.DOMList

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

/**
  * [[Property]] adapters for scalajs-bootstrap.
  */
trait UdashBootstrapImplicits extends RxConverters {

  import com.karasiq.bootstrap.BootstrapImplicits._
  import com.karasiq.bootstrap.{BootstrapImplicits => BI}

  implicit def udashInputOps[T](value: Property[String]): RxInputOps[T] = RxInputOps(value)

  implicit def udashIntInputOps[T](value: Property[Int]): RxIntInputOps[T] = RxIntInputOps(value)

  implicit def udashDoubleInputOps[T](value: Property[Double]): RxDoubleInputOps[T] = RxDoubleInputOps(value)

  implicit def udashBooleanInputOps[T](value: Property[Boolean]): RxBooleanInputOps[T] = RxBooleanInputOps(value)

  implicit def udashStateOps(state: Property[Boolean]): RxStateOps = RxStateOps(state)

  implicit def udashVariableOps[T](value: Property[T]): RxVariableOps[T] = RxVariableOps(value)

  implicit def udashValueOps[T](value: Property[T]): RxValueOps[T] = RxValueOps(value)

  implicit def propertyNode(prop: Property[dom.Node]): RxNode = RxNode(prop)

  implicit def propertyFragNode[T](prop: Property[T])(implicit ev: T => Frag): RxFragNode[T] = RxFragNode(prop)

  implicit class FormRadioGroupOps(rg: bootstrap.form.FormRadioGroup)(implicit ec: ExecutionContext) {
    def radioListProperty: ReadableSeqProperty[bootstrap.form.FormRadio] = rg.radioList

    def valueProperty: Property[String] = rg.value
  }

  implicit class NavigationOps(nav: bootstrap.navbar.Navigation) {
    def contentProperty(implicit ec: ExecutionContext): ReadableSeqProperty[bootstrap.navbar.NavigationTab] = nav.content
  }

  implicit class NavigationBarOps(nb: bootstrap.navbar.NavigationBar) {
    def navigationTabsProperty(implicit ec: ExecutionContext): SeqProperty[bootstrap.navbar.NavigationTab] = nb.navigationTabs
  }

  implicit class ProgressBarOps(bar: bootstrap.progressbar.ProgressBar) {
    def progressProperty(implicit ec: ExecutionContext): CastableReadableProperty[Int] = bar.progress
  }

  implicit class TableOps(table: bootstrap.table.Table)(implicit ec: ExecutionContext) {
    def headingProperty: ReadableSeqProperty[Modifier] = table.heading

    def contentProperty: ReadableSeqProperty[bootstrap.table.TableRow] = table.content
  }

  implicit class PagedTableOps(table: bootstrap.table.PagedTable)(implicit ec: ExecutionContext) {
    def currentPageProperty: Property[Int] = table.currentPage

    def pages: CastableReadableProperty[Int] = table.pages
  }

  implicit class ButtonOps(val button: ConcreteHtmlTag[dom.html.Button]) {
    def toggleButton: bootstrap.buttons.ToggleButton = new bootstrap.buttons.ToggleButton(button)

    def disabledButton: bootstrap.buttons.DisabledButton = new bootstrap.buttons.DisabledButton(button)
  }

  implicit def bootstrapHtmlComponentToTag[T <: dom.Element](bc: bootstrap.BootstrapHtmlComponent[T]): ConcreteHtmlTag[T] =
    BI.bootstrapHtmlComponentToTag(bc)


  implicit def renderBootstrapComponent(bc: bootstrap.BootstrapComponent): Modifier =
    BI.renderBootstrapComponent(bc)

  implicit def domListIndexedSeq[T](dl: DOMList[T]): DOMListIndexedSeq[T] = DOMListIndexedSeq(dl)

}