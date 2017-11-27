package io.udash.wrappers.highcharts
package config
package utils

sealed class VerticalAlign(val name: String)
object VerticalAlign {
  val Top = new VerticalAlign("top")
  val Middle = new VerticalAlign("middle")
  val Bottom = new VerticalAlign("bottom")
  case class Custom(override val name: String) extends VerticalAlign(name)

  val ByName: Map[String, VerticalAlign] = Seq(Bottom, Middle, Top).map(i => (i.name, i)).toMap
}











