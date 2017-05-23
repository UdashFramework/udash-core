package io.udash.css

import scalacss.internal.{FontFace, StyleS}

sealed trait CssStyle {
  val className: String
}
case class CssStyleName(className: String) extends CssStyle
case class CssStyleImpl(className: String, impl: StyleS) extends CssStyle
case class CssKeyframes(className: String, steps: Map[Double, StyleS]) extends CssStyle
case class CssFontFace(className: String, font: FontFace[Option[String]]) extends CssStyle
