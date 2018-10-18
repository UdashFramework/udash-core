package io.udash.wrappers.highcharts

import scala.scalajs.js

package object config {
  def stringToStyleObject(s: String): js.Object =
    js.eval(s"($s)".replaceAll(";", ",")).asInstanceOf[js.Object]
}
