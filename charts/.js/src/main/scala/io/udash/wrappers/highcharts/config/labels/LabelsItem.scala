/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package labels

import scala.scalajs.js


trait LabelsItem extends js.Object {

  /**
    * Inner HTML or text for the label.
    */
  val html: js.UndefOr[String] = js.undefined

  /**
    * CSS styles for each label. To position the label, use left and top like this:
    * <pre>style: {
    *   left: '100px',
    *   top: '100px'
    * }</pre>
    */
  val style: js.UndefOr[js.Object] = js.undefined
}

object LabelsItem {
  import scala.scalajs.js.JSConverters._

  /**
    * @param html  Inner HTML or text for the label.
    * @param style CSS styles for each label. To position the label, use left and top like this:. <pre>style: {. 	left: '100px',. 	top: '100px'. }</pre>
    */
  def apply(html: js.UndefOr[String] = js.undefined, style: js.UndefOr[String] = js.undefined): LabelsItem = {
    val htmlOuter = html
    val styleOuter = style.map(_.toJSArray)

    new LabelsItem {
      override val html = htmlOuter
      override val style = styleOuter
    }
  }
}
