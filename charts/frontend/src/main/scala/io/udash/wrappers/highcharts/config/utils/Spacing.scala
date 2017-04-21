package io.udash.wrappers.highcharts
package config
package utils

case class Spacing(top: Double, right: Double, bottom: Double, left: Double)

object Spacing {
  def apply(all: Double): Spacing =
    Spacing(all, all, all, all)
}
