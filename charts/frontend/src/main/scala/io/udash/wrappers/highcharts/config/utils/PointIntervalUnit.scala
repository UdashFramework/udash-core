package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class PointIntervalUnit(val name: String)

object PointIntervalUnit {
  case object Day extends PointIntervalUnit("day")
  case object Month extends PointIntervalUnit("month")
  case object Year extends PointIntervalUnit("year")
  case class Custom(override val name: String) extends PointIntervalUnit(name)
}