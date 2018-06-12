package io.udash.css.macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.blackbox
import scalacss.internal.DslBase.ToStyle
import scalacss.internal.FontFace

class StyleMacros(override val c: blackbox.Context) extends AbstractMacroCommons(c) {
  import c.universe._

  val Package = q"_root_.io.udash.css"
  val StyleCls = tq"$Package.CssStyle"
  val StyleNameCls = tq"$Package.CssStyleName"
  val StyleImplCls = tq"$Package.CssStyleImpl"
  val KeyframesCls = tq"$Package.CssKeyframes"
  val FontFaceCls = tq"$Package.CssFontFace"

  val Dsl = q"scalacss.internal.Dsl"
  val Compose = q"scalacss.internal.Compose"
  val FontSrcSelector = tq"scalacss.internal.FontFace.FontSrcSelector"

  private def handleScalaJs(name: Tree, other: Tree): Tree =
    if (isScalaJs) {
      q"""new $StyleNameCls($name)"""
    } else other

  private def style(name: Tree, impl: Expr[ToStyle]*): c.Tree =
    handleScalaJs(name,
      q"""
        {
          val tmp = new $StyleImplCls($name, $Dsl.style(..$impl)($Compose.trust))
          ${c.prefix}.elementsBuffer += tmp
          tmp
        }
      """)

  def mixin(impl: Expr[ToStyle]*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    handleScalaJs(q"$fullName", q"""new $StyleImplCls($fullName, $Dsl.style(..$impl)($Compose.trust))""")
  }

  def style(impl: Expr[ToStyle]*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    style(q"$fullName", impl: _*)
  }

  def namedStyle(className: Expr[String], impl: Expr[ToStyle]*): Tree =
    style(className.tree, impl: _*)

  private def keyframes(name: Tree, impl: Expr[(Double, Seq[ToStyle])]*): Tree =
    handleScalaJs(name,
      q"""
        {
          val tmp = new $KeyframesCls($name,
            Seq(..$impl).map { case (p, s) =>
              (p, $Dsl.style(s: _*)($Compose.trust))
            }
          )
          ${c.prefix}.elementsBuffer += tmp
          tmp
        }
      """)

  def keyframes(impl: Expr[(Double, Seq[ToStyle])]*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    keyframes(q"$fullName", impl: _*)
  }

  def namedKeyframes(className: Expr[String], impl: Expr[(Double, Seq[ToStyle])]*): Tree =
    keyframes(className.tree, impl: _*)

  private def fontFace(name: Tree, font: Expr[FontFace.FontSrcSelector => FontFace[Option[String]]]): Tree =
    handleScalaJs(name,
      q"""
        {
          val tmp = new $FontFaceCls($name, $font.apply(new $FontSrcSelector(None)))
          ${c.prefix}.elementsBuffer += tmp
          tmp
        }
      """)

  def fontFace(font: Expr[FontFace.FontSrcSelector => FontFace[Option[String]]]): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    fontFace(q"$fullName", font)
  }

  def nameFontFace(className: Expr[String], font: Expr[FontFace.FontSrcSelector => FontFace[Option[String]]]): Tree =
    fontFace(className.tree, font)
}
