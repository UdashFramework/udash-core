/** Based on <a href="https://github.com/Karasiq/scalajs-highcharts">Karasiq wrapper</a>. */
package io.udash.wrappers.highcharts
package api

import scala.scalajs.js

@js.native
trait Title extends js.Object {
  def update(options: config.title.Title): Unit = js.native
}

@js.native
trait Subtitle extends js.Object {
  def update(options: config.title.Subtitle): Unit = js.native
}
