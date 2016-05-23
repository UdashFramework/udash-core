package io.udash.bootstrap

import com.karasiq.bootstrap.form.{FormRadio, FormRadioGroup}
import com.karasiq.bootstrap.navbar.{Navigation, NavigationBar, NavigationTab}
import com.karasiq.bootstrap.progressbar.ProgressBar
import com.karasiq.bootstrap.table.{PagedTable, Table, TableRow}
import io.udash._

import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

/**
  * [[Property]] adapters for scalajs-bootstrap.
  * Use with com.karasiq.bootstrap.BootstrapImplicits import.
  */
trait UdashBootstrapImplicits extends RxConverters {

  import com.karasiq.bootstrap.BootstrapImplicits._

  implicit def udashInputOps[T](value: Property[String]): RxInputOps[T] = RxInputOps(value)

  implicit def udashIntInputOps[T](value: Property[Int]): RxIntInputOps[T] = RxIntInputOps(value)

  implicit def udashDoubleInputOps[T](value: Property[Double]): RxDoubleInputOps[T] = RxDoubleInputOps(value)

  implicit def udashBooleanInputOps[T](value: Property[Boolean]): RxBooleanInputOps[T] = RxBooleanInputOps(value)

  implicit def udashStateOps(state: Property[Boolean]): RxStateOps = RxStateOps(state)

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

}