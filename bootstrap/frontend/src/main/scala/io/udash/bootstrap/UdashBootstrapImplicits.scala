package io.udash.bootstrap

import com.karasiq.bootstrap.buttons.{DisabledButton, ToggleButton}
import com.karasiq.bootstrap.form.{FormRadio, FormRadioGroup}
import com.karasiq.bootstrap.navbar.{Navigation, NavigationBar, NavigationTab}
import com.karasiq.bootstrap.progressbar.ProgressBar
import com.karasiq.bootstrap.table.{PagedTable, Table, TableRow}
import com.karasiq.bootstrap.{BootstrapComponent, BootstrapHtmlComponent}
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

  implicit class FormRadioGroupOps(rg: FormRadioGroup)(implicit ec: ExecutionContext) {
    def radioListProperty: ReadableSeqProperty[FormRadio] = rg.radioList

    def valueProperty: Property[String] = rg.value
  }

  implicit class NavigationOps(nav: Navigation) {
    def contentProperty(implicit ec: ExecutionContext): ReadableSeqProperty[NavigationTab] = nav.content
  }

  implicit class NavigationBarOps(nb: NavigationBar) {
    def navigationTabsProperty(implicit ec: ExecutionContext): SeqProperty[NavigationTab] = nb.navigationTabs
  }

  implicit class ProgressBarOps(bar: ProgressBar) {
    def progressProperty(implicit ec: ExecutionContext): CastableReadableProperty[Int] = bar.progress
  }

  implicit class TableOps(table: Table)(implicit ec: ExecutionContext) {
    def headingProperty: ReadableSeqProperty[Modifier] = table.heading

    def contentProperty: ReadableSeqProperty[TableRow] = table.content
  }

  implicit class PagedTableOps(table: PagedTable)(implicit ec: ExecutionContext) {
    def currentPageProperty: Property[Int] = table.currentPage

    def pages: CastableReadableProperty[Int] = table.pages
  }

  implicit class ButtonOps(val button: ConcreteHtmlTag[dom.html.Button]) {
    def toggleButton: ToggleButton = new ToggleButton(button)

    def disabledButton: DisabledButton = new DisabledButton(button)
  }

  implicit def bootstrapHtmlComponentToTag[T <: dom.Element](bc: BootstrapHtmlComponent[T]): ConcreteHtmlTag[T] =
    BI.bootstrapHtmlComponentToTag(bc)


  implicit def renderBootstrapComponent(bc: BootstrapComponent): Modifier =
    BI.renderBootstrapComponent(bc)

  implicit def domListIndexedSeq[T](dl: DOMList[T]): DOMListIndexedSeq[T] = DOMListIndexedSeq(dl)

}