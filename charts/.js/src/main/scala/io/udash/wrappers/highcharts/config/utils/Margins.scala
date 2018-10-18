package io.udash.wrappers.highcharts
package config
package utils

case class Margins(top: Double, right: Double, bottom: Double, left: Double)

object Margins {
  def apply(all: Double): Margins =
    Margins(all, all, all, all)
}
