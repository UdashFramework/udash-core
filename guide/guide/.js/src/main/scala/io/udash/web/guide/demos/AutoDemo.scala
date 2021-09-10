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

  protected implicit def sourceOps(source: String): AutoDemo.SourceOps = new AutoDemo.SourceOps(source)

}

object AutoDemo {
  final class SourceOps(private val source: String) extends AnyVal {
    def dropFinalLine: String = source.linesWithSeparators.toSeq.view.dropRight(1).mkString
  }

  private val mpcRegex =
    """object (.+) \{
      |    implicit val .+: ModelPropertyCreator\[\1\] = ModelPropertyCreator\.materialize
      |}""".stripMargin.r
  private[demos] def mpcFix(source: String): String =
    mpcRegex.replaceAllIn(source, m => s"object ${m.group(1)} extends HasModelPropertyCreator[${m.group(1)}]")

  def snippet(code: String): Modifier = CodeBlock.lines(code.linesIterator.drop(1).map(_.drop(2)).toList.view.dropRight(1).iterator)(GuideStyles)
}
