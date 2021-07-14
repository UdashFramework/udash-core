package io.udash.web.guide.demos

import com.avsystem.commons.SharedExtensions
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

trait AutoDemo extends SharedExtensions {


  final def demoWithSnippet(): (Modifier, Modifier) = {
    val (demo, code) = demoWithSource()
    (demo, AutoDemo.snippet(code))
  }

  protected def demoWithSource(): (Modifier, String)

}

object AutoDemo {
  def snippet(code: String): Modifier = CodeBlock.lines(code.linesIterator.drop(1).map(_.drop(2)).toList.view.dropRight(1).iterator)(GuideStyles)
}
