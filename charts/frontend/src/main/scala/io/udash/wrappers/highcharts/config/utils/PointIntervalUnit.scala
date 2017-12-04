package io.udash.wrappers.highcharts
package config
package utils

sealed class PointIntervalUnit(val name: String)

object PointIntervalUnit {
  val Day = new PointIntervalUnit("day")
  val Month = new PointIntervalUnit("month")
  val Year = new PointIntervalUnit("year")
  case class Custom(override val name: String) extends PointIntervalUnit(name)
}