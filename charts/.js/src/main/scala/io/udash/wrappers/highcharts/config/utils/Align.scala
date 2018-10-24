package io.udash.wrappers.highcharts
package config
package utils

sealed class Align(val name: String)

object Align {
  val Left = new Align("left")
  val Center = new Align("center")
  val Right = new Align("right")
  case class Custom(override val name: String) extends Align(name)

  val ByName: Map[String, Align] = Seq(Left, Center, Right).map(i => (i.name, i)).toMap
}