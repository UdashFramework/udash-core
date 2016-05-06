package io.udash.web.commons.components

import io.udash.web.commons.styles.components.CodeBlockStyles
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object CodeBlock {
  def apply(data: String, language: String = "language-scala")(styles: CodeBlockStyles): Element =
    pre(styles.codeWrapper)(
      ol(styles.codeBlock)(
        data.split("\\r?\\n").map(line =>
          li(code(cls := language)(line))
      )
    )).render
}
