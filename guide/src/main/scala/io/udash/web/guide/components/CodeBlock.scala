package io.udash.web.guide.components

import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalacss.ScalatagsCss._
import scalatags.JsDom
import scalatags.JsDom.all._

object CodeBlock {
  def apply(data: String, language: String = "language-scala")(xs: Modifier*): JsDom.TypedTag[dom.html.Pre] =
    pre(GuideStyles.codeWrapper)(
      ol(GuideStyles.codeBlock)(
      data.split("\\r?\\n").map(line =>
        li(
          code(cls := language, xs)(line)
        )
      )
    ))
}
