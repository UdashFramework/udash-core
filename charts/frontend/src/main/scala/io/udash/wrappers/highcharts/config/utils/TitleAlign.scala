package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class TitleAlign(val name: String)

object TitleAlign {
  case object Low extends TitleAlign("low")
  case object Middle extends TitleAlign("middle")
  case object High extends TitleAlign("high")
  case class Custom(override val name: String) extends TitleAlign(name)

  val ByName: Map[String, TitleAlign] = Seq(Low, Middle, High).map(i => (i.name, i)).toMap
}