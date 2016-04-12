package io.udash.guide.components

import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom
import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

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


