package io.udash.wrappers.highcharts
package config
package utils

sealed abstract class Align(val name: String)

object Align {
  case object Left extends Align("left")
  case object Center extends Align("center")
  case object Right extends Align("right")
  case class Custom(override val name: String) extends Align(name)

  val ByName: Map[String, Align] = Seq(Left, Center, Right).map(i => (i.name, i)).toMap
}