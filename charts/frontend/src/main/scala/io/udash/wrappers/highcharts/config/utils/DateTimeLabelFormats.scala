package io.udash.wrappers.highcharts
package config
package utils

import scala.scalajs.js

/** For an overview of the replacement codes, see <a href="http://api.highcharts.com/highcharts/xAxis#Highcharts.dateFormat">dateFormat</a>.*/
case class DateTimeLabelFormats(
  millisecond: String = "%H:%M:%S.%L",
  second: String = "%H:%M:%S",
  minute: String = "%H:%M",
  hour: String = "%H:%M",
  day: String = "%e. %b",
  week: String = "%e. %b",
  month: String = "%b '%y",
  year: String = "%Y"
)

object DateTimeLabelFormats {
  implicit def toJSDict(self: DateTimeLabelFormats): js.Dictionary[String] =
    js.Dictionary[String](
      "millisecond" -> self.millisecond,
      "second" -> self.second,
      "minute" -> self.minute,
      "hour" -> self.hour,
      "day" -> self.day,
      "week" -> self.week,
      "month" -> self.month,
      "year" -> self.year
    )
}