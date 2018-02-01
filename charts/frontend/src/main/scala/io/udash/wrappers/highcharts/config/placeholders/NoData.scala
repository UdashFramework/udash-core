/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package config
package placeholders

import io.udash.wrappers.highcharts.config.utils.Position

import scala.scalajs.js


trait NoData extends js.Object {

  /**
    * An object of additional SVG attributes for the no-data label.
    */
  val attr: js.UndefOr[js.Object] = js.undefined

  /**
    * The position of the no-data label, relative to the plot area. 
    */
  val position: js.UndefOr[Position] = js.undefined

  /**
    * CSS styles for the no-data label. 
    */
  val style: js.UndefOr[js.Object] = js.undefined

  /**
    * Whether to insert the label as HTML, or as pseudo-HTML rendered with SVG.
    */
  val useHTML: js.UndefOr[Boolean] = js.undefined
}

object NoData {

  /**
    * @param attr An object of additional SVG attributes for the no-data label.
    * @param position The position of the no-data label, relative to the plot area. 
    * @param style CSS styles for the no-data label. 
    * @param useHTML Whether to insert the label as HTML, or as pseudo-HTML rendered with SVG.
    */
  def apply(attr: js.UndefOr[js.Object] = js.undefined,
            position: js.UndefOr[Position] = js.undefined,
            style: js.UndefOr[String] = js.undefined,
            useHTML: js.UndefOr[Boolean] = js.undefined): NoData = {
    val attrOuter = attr
    val positionOuter = position
    val styleOuter = style.map(stringToStyleObject)
    val useHTMLOuter = useHTML

    new NoData {
      override val attr = attrOuter
      override val position = positionOuter
      override val style = styleOuter
      override val useHTML = useHTMLOuter
    }
  }
}
