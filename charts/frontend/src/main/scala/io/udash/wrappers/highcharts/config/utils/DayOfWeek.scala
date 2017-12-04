package io.udash.wrappers.highcharts
package config
package utils

final class DayOfWeek(val id: Int) extends AnyVal

object DayOfWeek {
  val Sunday = new DayOfWeek(0)
  val Monday = new DayOfWeek(1)
  val Tuesday = new DayOfWeek(2)
  val Wednesday = new DayOfWeek(3)
  val Thursday = new DayOfWeek(4)
  val Friday = new DayOfWeek(5)
  val Saturday = new DayOfWeek(6)
}