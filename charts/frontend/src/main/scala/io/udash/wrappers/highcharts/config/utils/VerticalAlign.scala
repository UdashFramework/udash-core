package io.udash.wrappers.highcharts
package config
package utils

sealed class VerticalAlign(val name: String)
object VerticalAlign {
  case object Top extends VerticalAlign("top")
  case object Middle extends VerticalAlign("middle")
  case object Bottom extends VerticalAlign("bottom")
  case class Custom(override val name: String) extends VerticalAlign(name)

  val ByName: Map[String, VerticalAlign] = Seq(Bottom, Middle, Top).map(i => (i.name, i)).toMap
}











