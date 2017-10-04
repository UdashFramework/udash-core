/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package exporting

import scala.scalajs.js


@js.annotation.ScalaJSDefined
trait ExportingButtons extends js.Object {

  /**
    * Options for the export button.
    */
  val contextButton: js.UndefOr[ExportingContextButton] = js.undefined
}

object ExportingButtons {
  import scala.scalajs.js.JSConverters._

  /**
    * @param contextButton Options for the export button.
    */
  def apply(contextButton: js.UndefOr[ExportingContextButton] = js.undefined): ExportingButtons = {
    val contextButtonOuter = contextButton

    new ExportingButtons {
      override val contextButton = contextButtonOuter
    }
  }
}
