package io.udash.css

import scala.collection.mutable
import scalacss.internal.Dsl.StyleS
import scalacss.internal.DslBase.ToStyle
import scalacss.internal.{AV, Attrs, ClassName, Compose, Cond, Css, Dsl, DslBase, Env, FontFace, Keyframes, Macros, Percentage, Renderer, StyleA}
import scala.language.implicitConversions

/**
  * Base trait for all stylesheets.
  *
  * Example:
  * <pre>
  * object ExampleStylesheet extends CssBase {
  *   import dsl._
  *
  *   val s = style(...)
  * }
  * </pre>
  */
trait CssBase {
  class Dsl(val elementsBuffer: mutable.ArrayBuffer[CssStyle]) extends DslBase {
    implicit def compose: Compose = Compose.trust

    def &(): Cond =
      Cond.empty

    def style(impl: ToStyle*): CssStyle =
      macro io.udash.css.macros.StyleMacros.style

    def namedStyle(className: String, impl: ToStyle*): CssStyle =
      macro io.udash.css.macros.StyleMacros.namedStyle

    def keyframe(impl: ToStyle*): Seq[ToStyle] =
      impl

    def keyframes(impl: (Double, Seq[ToStyle])*): CssStyle =
      macro io.udash.css.macros.StyleMacros.keyframes

    def namedKeyframes(className: String, impl: (Double, Seq[ToStyle])*): CssStyle =
      macro io.udash.css.macros.StyleMacros.namedKeyframes

    def fontFace(font: FontFace.FontSrcSelector => FontFace[Option[String]]): CssStyle =
      macro io.udash.css.macros.StyleMacros.fontFace

    def namedFontFace(className: String, font: FontFace.FontSrcSelector => FontFace[Option[String]]): CssStyle =
      macro io.udash.css.macros.StyleMacros.nameFontFace

    override def unsafeChild(n: String)(t: ToStyle*)(implicit c: Compose): Style.UnsafeExt =
      unsafeExt(root => n.split(",").map(el => s"$root $el").mkString(","))(t: _*)(c)

    override protected def styleS(t: ToStyle*)(implicit c: Compose): StyleS =
      Dsl.style(t: _*)(Compose.trust)

    implicit def backToStyleS(s: CssStyle): ToStyle =
      s match {
        case CssStyleImpl(_, style) => new ToStyle(style)
        case _ => new ToStyle(StyleS.empty)
      }

    implicit def backToStyleSeq(s: CssStyle): Seq[ToStyle] =
      s match {
        case CssStyleImpl(_, style) => Seq(new ToStyle(style))
        case _ => Seq(new ToStyle(StyleS.empty))
      }

    implicit def animationNameExt(n: Attrs.animationName.type): CssBase.AnimationNameExt =
      new CssBase.AnimationNameExt(n)

    implicit def fontFamilyExt(n: Attrs.fontFamily.type): CssBase.FontFamilyExt =
      new CssBase.FontFamilyExt(n)

    @inline override implicit def colourLiteralMacro(sc: StringContext): Macros.ColourLiteral =
      new Macros.ColourLiteral(sc)
  }

  private val elementsBuffer = mutable.ArrayBuffer.empty[CssStyle]
  val dsl: Dsl = new Dsl(elementsBuffer)

  def elements: Seq[CssStyle] =
    elementsBuffer.toVector

  def render(implicit renderer: Renderer[String]): String = {
    elementsBuffer.iterator.map {
      case CssStyleImpl(className, impl) =>
        renderer.apply(
          Css.styleA(StyleA(
            new ClassName(className),
            Vector.empty,
            impl
          ))(Env.empty)
        )
      case CssKeyframes(className, steps) =>
        renderer.apply(Vector(
          Css.keyframes(Keyframes(
            new ClassName(className),
            steps.toSeq.map { case (p, impl) =>
              (Percentage(p): scalacss.internal.KeyframeSelector, StyleA(
                new ClassName(className),
                Vector.empty,
                impl
              ))
            }
          ))(Env.empty)
        ))
      case CssFontFace(className, font) =>
        renderer.apply(Vector(
          Css.fontFaces(
            FontFace[String](
              className,
              src = font.src,
              fontStretchValue = font.fontStretchValue,
              fontStyleValue = font.fontStyleValue,
              fontWeightValue = font.fontWeightValue,
              unicodeRangeValue = font.unicodeRangeValue
            )
          )
        ))
      case _ =>
    }.mkString
  }
}

object CssBase {
  class AnimationNameExt(private val n: Attrs.animationName.type) extends AnyVal {
    def apply(s: CssStyle): AV =
      AV(n.attr, s.className)
  }

  class FontFamilyExt(private val n: Attrs.fontFamily.type) extends AnyVal {
    def apply(s: CssStyle): AV =
      AV(n.attr, s.className)
  }
}