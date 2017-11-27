/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package labels

import scala.scalajs.js


@js.annotation.ScalaJSDefined
trait Labels extends js.Object {

  /**
    * A HTML label that can be positioned anywhere in the chart area.
    */
  val items: js.UndefOr[js.Array[LabelsItem]] = js.undefined

  /**
    * Shared CSS styles for all labels. Defaults to:
    * <pre>style: {
    *   color: '#3E576F'
    * }</pre>
    */
  val style: js.UndefOr[js.Object] = js.undefined
}

object Labels {
  import scala.scalajs.js.JSConverters._

  /**
    * @param items A HTML label that can be positioned anywhere in the chart area.
    * @param style Shared CSS styles for all labels. Defaults to:. <pre>style: {. 	color: '#3E576F'. }</pre>
    */
  def apply(items: js.UndefOr[Seq[LabelsItem]] = js.undefined, style: js.UndefOr[String] = js.undefined): Labels = {
    val itemsOuter = items.map(_.toJSArray)
    val styleOuter = style.map(stringToStyleObject)

    new Labels {
      override val items = itemsOuter
      override val style = styleOuter
    }
  }
}
