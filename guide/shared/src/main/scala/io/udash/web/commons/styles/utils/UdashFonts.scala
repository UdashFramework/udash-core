package io.udash.web.commons.styles.utils

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx, ValueEnumCompanion}
import io.udash.css.{CssBase, CssStyle}
import scalacss.internal.{AV, FontFace, NonEmptyVector}

import scala.collection.immutable

final class FontWeight(val value: AV)(implicit enumCtx: EnumCtx) extends AbstractValueEnum

object FontWeight extends AbstractValueEnumCompanion[FontWeight] with CssBase {

  import dsl._

  final val Thin: Value = new FontWeight(fontWeight._100)
  final val Light: Value = new FontWeight(fontWeight._300)
  final val Regular: Value = new FontWeight(fontWeight._400)
  final val Medium: Value = new FontWeight(fontWeight._500)
  final val Bold: Value = new FontWeight(fontWeight._700)
  final val Black: Value = new FontWeight(fontWeight._900)
}

final class FontStyle(val value: AV)(implicit enumCtx: EnumCtx) extends AbstractValueEnum

object FontStyle extends AbstractValueEnumCompanion[FontStyle] with CssBase {

  import dsl._

  final val Normal: Value = new FontStyle(fontStyle.normal)
  final val Italic: Value = new FontStyle(fontStyle.italic)
}

final class FontFileType(val extension: String, val format: Option[String])(implicit enumCtx: EnumCtx) extends AbstractValueEnum

object FontFileType extends ValueEnumCompanion[FontFileType] {
  final val EotFont: Value = new FontFileType("eot", None)
  final val WoffFont: Value = new FontFileType("woff", Some("format('woff')"))
  final val Woff2Font: Value = new FontFileType("woff2", Some("format('woff2')"))
  final val TruetypeFont: Value = new FontFileType("ttf", Some("format('truetype')"))
}

final case class FontVariation(path: String, fontWeight: FontWeight = FontWeight.Regular, fontStyle: FontStyle = FontStyle.Normal)

object FontFamily {
  val Roboto = "'Roboto', sans-serif"
}

object UdashFonts extends CssBase {
  import dsl._

  def roboto(fontWeight: FontWeight = FontWeight.Regular, fontStyle: FontStyle = FontStyle.Normal): CssStyle = mixin(
    fontFamily :=! FontFamily.Roboto,
    fontStyle.value,
    fontWeight.value
  )

  private val fontFiles = immutable.Seq(
    FontVariation("/fonts/roboto/Roboto-Black", FontWeight.Black, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-BlackItalic", FontWeight.Black, FontStyle.Italic),
    FontVariation("/fonts/roboto/Roboto-Bold", FontWeight.Bold, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-BoldItalic", FontWeight.Bold, FontStyle.Italic),
    FontVariation("/fonts/roboto/Roboto-Medium", FontWeight.Medium, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-MediumItalic", FontWeight.Medium, FontStyle.Italic),
    FontVariation("/fonts/roboto/Roboto-Regular", FontWeight.Regular, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-Italic", FontWeight.Regular, FontStyle.Italic),
    FontVariation("/fonts/roboto/Roboto-Light", FontWeight.Light, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-LightItalic", FontWeight.Light, FontStyle.Italic),
    FontVariation("/fonts/roboto/Roboto-Thin", FontWeight.Thin, FontStyle.Normal),
    FontVariation("/fonts/roboto/Roboto-ThinItalic", FontWeight.Thin, FontStyle.Italic)
  )

  val font: immutable.Map[FontVariation, CssStyle] = fontFiles.map(fontFile =>
    fontFile ->
      namedFontFace(FontFamily.Roboto,
        _ => {
          new FontFace(
            fontFamily = Some(FontFamily.Roboto),
            src = NonEmptyVector(
              fontSrc(fontFile.path, FontFileType.TruetypeFont.extension, FontFileType.TruetypeFont.format)
            ),
            fontWeightValue = Some(fontFile.fontWeight.value.value),
            fontStyleValue = Some(fontFile.fontStyle.value.value)
          )
        }
      )
  ).toMap

  private def fontSrc(fileUrl: String, fileExt: String, fileFormat: Option[String]) =
    s"url('$fileUrl.$fileExt')${fileFormat.map(v => s" $v").getOrElse("")}"
}
