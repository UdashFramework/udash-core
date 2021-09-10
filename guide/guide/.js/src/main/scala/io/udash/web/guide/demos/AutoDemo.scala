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

  private def companionRegex[T: ClassTag]: Regex = {
    val className = classTag[T].runtimeClass.getSimpleName
    s"""object (.+) \\{
       |    implicit val .+: $className\\[\\1\\] = $className\\.materialize
       |}""".stripMargin.r
  }

  private val MpcRegex = companionRegex[ModelPropertyCreator[_]]
  private val GenCodecRegex = companionRegex[GenCodec[_]]

  private[demos] def mpcFix(source: String): String =
    MpcRegex.replaceAllIn(source, m => s"object ${m.group(1)} extends HasModelPropertyCreator[${m.group(1)}]")

  private[demos] def codecFix(source: String): String =
    GenCodecRegex.replaceAllIn(source, m => s"object ${m.group(1)} extends HasGenCodec[${m.group(1)}]")

  def snippet(code: String): Modifier = CodeBlock.lines(code.linesIterator.drop(1).map(_.drop(2)).toList.view.dropRight(1).iterator)(GuideStyles)
}
