package io.udash.web.guide.demos

import com.avsystem.commons.SharedExtensions
import com.avsystem.commons.serialization.GenCodec
import io.udash.properties.ModelPropertyCreator
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

import scala.reflect.{ClassTag, classTag}
import scala.util.matching.Regex

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

  private def companionFix[T: ClassTag]: String => String = {
    val className = classTag[T].runtimeClass.getSimpleName
    val companionRegex: Regex =
      s"""object (.+) \\{
         |    implicit val .+: $className\\[\\1\\] = $className\\.materialize
         |}""".stripMargin.r

    code => companionRegex.replaceAllIn(code, m => s"object ${m.group(1)} extends Has$className[${m.group(1)}]")
  }

  private[demos] val mpcFix = companionFix[ModelPropertyCreator[_]]
  private[demos] val codecFix = companionFix[GenCodec[_]]

  def snippet(code: String): Modifier = CodeBlock.lines(code.linesIterator.drop(1).map(_.drop(2)).toList.view.dropRight(1).iterator)(GuideStyles)
}
