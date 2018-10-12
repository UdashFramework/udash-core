package io.udash.css.macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.blackbox

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

  private def style(name: Tree, impl: Tree*): c.Tree =
    handleScalaJs(name,
      q"""
        {
          val tmp = new $StyleImplCls($name, $Dsl.style(..$impl)($Compose.trust))
          ${c.prefix}.elementsBuffer += tmp
          tmp
        }
      """)

  def mixin(impl: Tree*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    handleScalaJs(q"$fullName", q"""new $StyleImplCls($fullName, $Dsl.style(..$impl)($Compose.trust))""")
  }

  def style(impl: Tree*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    style(q"$fullName", impl: _*)
  }

  def namedStyle(className: Expr[String], impl: Tree*): Tree =
    style(className.tree, impl: _*)

  private def keyframes(name: Tree, impl: Tree*): Tree =
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

  def keyframes(impl: Tree*): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    keyframes(q"$fullName", impl: _*)
  }

  def namedKeyframes(className: Expr[String], impl: Tree*): Tree =
    keyframes(className.tree, impl: _*)

  private def fontFace(name: Tree, font: Tree): Tree =
    handleScalaJs(name,
      q"""
        {
          val tmp = new $FontFaceCls($name, $font.apply(new $FontSrcSelector(None)))
          ${c.prefix}.elementsBuffer += tmp
          tmp
        }
      """)

  def fontFace(font: Tree): Tree = {
    val fullName = c.internal.enclosingOwner.fullName.replace('.', '-')
    fontFace(q"$fullName", font)
  }

  def nameFontFace(className: Expr[String], font: Tree): Tree =
    fontFace(className.tree, font)
}
