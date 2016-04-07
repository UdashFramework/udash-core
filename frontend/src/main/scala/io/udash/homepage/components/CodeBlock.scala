package io.udash.homepage.components

import io.udash.homepage.styles.partials.HomepageStyles
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scalatags.JsDom
import scalatags.JsDom.all._
import scalacss.ScalatagsCss._

object CodeBlock {
  def apply(data: String, language: String = "language-scala")(xs: Modifier*): Element =
    pre(HomepageStyles.codeWrapper)(
      ol(HomepageStyles.codeBlock)(
      data.split("\\r?\\n").map(line =>
        li(
          code(cls := language, xs)(line)
        )
      )
    )).render
}


