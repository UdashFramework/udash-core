package io.udash
package web.commons.components

import com.avsystem.commons._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap.progressbar.UdashProgressBar
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.web.commons.styles.components.CodeBlockStyles
import org.scalajs.dom.Element
import org.scalajs.dom.html.Pre
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

object CodeBlock {
  import io.udash.css.CssView._

  @js.native
  @JSGlobal("Prism")
  private object Prism extends js.Object {
    //https://prismjs.com/extending.html#api
    def highlightAllUnder(element: Element): Unit = js.native
  }

  def apply(data: String, language: String = "language-scala")(styles: CodeBlockStyles): JsDom.TypedTag[Pre] = {
    pre(styles.codeWrapper)(
      ol(styles.codeBlock)(
        data.split("\\r?\\n").map(line =>
          li(code(cls := language)(line))
        )
      )
    )
  }

  def lines(lines: Iterator[String], language: String = "language-scala")(styles: CodeBlockStyles): JsDom.TypedTag[Pre] = {
    pre(styles.codeWrapper)(
      ol(styles.codeBlock)(
        lines.map(line =>
          li(code(cls := language)(line))
        ).toList
      )
    )
  }

  def reactive(data: ReadableProperty[String], placeholder: Modifier, language: String = "language-scala")(styles: CodeBlockStyles): Binding = {
    val progressBar = UdashProgressBar(
      progress = 100.toProperty,
      showPercentage = true.toProperty,
      barStyle = Some(BootstrapStyles.Color.Success).toProperty,
      stripped = true.toProperty,
      animated = true.toProperty
    ) { case _ => b(placeholder) }
    produceWithNested(data)((data, nested) =>
      if (data.isEmpty) ForceBootstrap(nested(progressBar)).render
      else apply(data, language)(styles).render.setup(rendered => Prism.highlightAllUnder(rendered))
    )
  }
}
