package io.udash.web.commons.components

import io.udash.web.commons.styles.components.CodeBlockStyles
import scalatags.JsDom.all._

object CodeBlock {
  import io.udash.css.CssView._

  def apply(data: String, language: String = "language-scala")(styles: CodeBlockStyles): Modifier =
    pre(styles.codeWrapper)(
      ol(styles.codeBlock)(
        data.split("\\r?\\n").map(line =>
          li(code(cls := language)(line))
      )
    ))

  def lines(lines: Iterator[String], language: String = "language-scala")(styles: CodeBlockStyles): Modifier =
    pre(styles.codeWrapper)(
      ol(styles.codeBlock)(
        lines.map(line =>
          li(code(cls := language)(line))
        ).toList
      ))
}
