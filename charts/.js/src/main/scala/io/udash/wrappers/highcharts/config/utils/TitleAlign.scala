package io.udash.wrappers.highcharts
package config
package utils

sealed class TitleAlign(val name: String)

object TitleAlign {
  val Low = new TitleAlign("low")
  val Middle = new TitleAlign("middle")
  val High = new TitleAlign("high")
  case class Custom(override val name: String) extends TitleAlign(name)

  val ByName: Map[String, TitleAlign] = Seq(Low, Middle, High).map(i => (i.name, i)).toMap
}