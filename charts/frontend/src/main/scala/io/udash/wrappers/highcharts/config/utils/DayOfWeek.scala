package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class DayOfWeek(val id: Int)

object DayOfWeek {
  case object Sunday extends DayOfWeek(0)
  case object Monday extends DayOfWeek(1)
  case object Tuesday extends DayOfWeek(2)
  case object Wednesday extends DayOfWeek(3)
  case object Thursday extends DayOfWeek(4)
  case object Friday extends DayOfWeek(5)
  case object Saturday extends DayOfWeek(6)
}