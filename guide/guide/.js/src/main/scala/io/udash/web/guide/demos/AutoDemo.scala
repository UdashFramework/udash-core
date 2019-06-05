package io.udash.web.guide.demos

import com.avsystem.commons.SharedExtensions
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles

trait AutoDemo extends SharedExtensions {

  import scalatags.JsDom.all._

  final def demoWithSnippet(): (Modifier, Modifier) = {
    val (demo, code) = demoWithSource()
    (demo, CodeBlock(code.map(_.drop(2)).mkString("\n"))(GuideStyles))
  }

  protected def demoWithSource(): (Modifier, Iterator[String])

}
