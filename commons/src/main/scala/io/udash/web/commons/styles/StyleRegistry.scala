package io.udash.web.commons.styles

import io.udash.wrappers.jquery.jQ

import scala.collection.mutable
import scalacss.Defaults._
import scalacss.StyleSheet.{Inline => ISS}
import scalatags.JsDom.all._
import scalatags.JsDom.tags2.style

/**
  * Created by malchik on 2015-12-10.
  */
object StyleRegistry {
  val styleSheet = style(`type` := "text/css").render

  private val jqStyleSheet = jQ(styleSheet)
  private val registrations: mutable.HashSet[ISS] = mutable.HashSet[ISS]()

  private[styles] def register(ss: ISS*): Unit = {
    ss.foreach( s => {
      registrations += s
      jqStyleSheet.append(s.render[String])
    })
  }
}
