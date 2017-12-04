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

    /**
      * Creates CSS style which will not be rendered. <br/><br/>
      *
      * Example:
      * <pre>
      *   def utils(x: Int): CssStyle = mixin(
      *     margin(x px, auto),
      *     textAlign.left,
      *     cursor.pointer
      *   )
      * </pre>
      */
    def mixin(impl: ToStyle*): CssStyle =
      macro io.udash.css.macros.StyleMacros.mixin

    /** Creates CSS style with auto-generated name. */

    /**
      * Creates CSS style with auto-generated name. <br/><br/>
      *
      * Example:
      * <pre>
      *   val example: CssStyle = style(
      *     utils(12), // mixin usage
      *
      *     &.hover(
      *       cursor.zoomIn
      *     ),
      *
      *     media.not.handheld.landscape.maxWidth(640 px)(
      *       width(400 px)
      *     )
      *   )
      * </pre>
      */
    def style(impl: ToStyle*): CssStyle =
      macro io.udash.css.macros.StyleMacros.style

    /** Creates CSS style with provided name. */
    def namedStyle(className: String, impl: ToStyle*): CssStyle =
      macro io.udash.css.macros.StyleMacros.namedStyle

    /** Creates CSS keyframe. */
    def keyframe(impl: ToStyle*): Seq[ToStyle] =
      impl

    /**
      * Creates CSS keyframes animation with auto-generated name. <br/><br/>
      *
      * Example:
      * <pre>
      *   val animation = keyframes(
      *     0d -> keyframe(height(10 px)),
      *     20d -> keyframe(height(50 px)),
      *     100d -> keyframe(height(200 px))
      *   )
      * </pre>
      */
    def keyframes(impl: (Double, Seq[ToStyle])*): CssStyle =
      macro io.udash.css.macros.StyleMacros.keyframes

    /** Creates CSS keyframes animation with provided name. */
    def namedKeyframes(className: String, impl: (Double, Seq[ToStyle])*): CssStyle =
      macro io.udash.css.macros.StyleMacros.namedKeyframes

    /**
      * Creates CSS fontface with auto-generated name. <br/><br/>
      *
      * Example:
      * <pre>
      *   val ff = fontFace(
      *     _.src("url(font.woff)")
      *       .fontStretch.expanded
      *       .fontStyle.italic
      *       .unicodeRange(0, 5)
      *   )
      * </pre>
      */
    def fontFace(font: FontFace.FontSrcSelector => FontFace[Option[String]]): CssStyle =
      macro io.udash.css.macros.StyleMacros.fontFace

    /** Creates CSS fontface with provided name. */
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
  lazy val dsl: Dsl = new Dsl(elementsBuffer)

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
      AV(n.attr, s.classNames.mkString(" "))
  }

  class FontFamilyExt(private val n: Attrs.fontFamily.type) extends AnyVal {
    def apply(s: CssStyle): AV =
      AV(n.attr, s.classNames.mkString(" "))
  }
}