package io.udash.web.guide.markdown

import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}

final class MarkdownPage(val file: String)(implicit val ctx: EnumCtx) extends AbstractValueEnum
object MarkdownPage extends AbstractValueEnumCompanion[MarkdownPage] {
  final val Intro: Value = new MarkdownPage("assets/pages/intro.md")
  final val Rest: Value = new MarkdownPage("assets/pages/rest.md")
  final val License: Value = new MarkdownPage("assets/pages/license.md")
}
