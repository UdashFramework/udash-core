package io.udash.css

import scalacss.internal.{FontFace, StyleS}

/** Representation of stylesheet elements. In JS it's always `CssStyleName`. */
sealed trait CssStyle {
  def className: String
  // Primarily introduced for FontAwesome support, which requires adding two classes, e.g. "fa fa-adjust"
  def commonPrefixClass: Option[String] = None
  def classNames: Seq[String] = commonPrefixClass.toList :+ className
}

final case class CssStyleName(className: String) extends CssStyle

final case class CssPrefixedStyleName(prefixClass: String, actualClassSuffix: String) extends CssStyle {
  val className = s"$prefixClass-$actualClassSuffix"
  override val commonPrefixClass: Option[String] = Some(prefixClass)
}

final case class CssStyleNameWithSharedCompanion(companionClass: String, commonPrefix: String, className: String) extends CssStyle {
  override val commonPrefixClass: Option[String] = Some(commonPrefix)
  override def classNames: Seq[String] = Seq(companionClass, className)
}

final case class CssStyleImpl(className: String, impl: StyleS) extends CssStyle
final case class CssKeyframes(className: String, steps: Seq[(Double, StyleS)]) extends CssStyle
final case class CssFontFace(className: String, font: FontFace[Option[String]]) extends CssStyle
