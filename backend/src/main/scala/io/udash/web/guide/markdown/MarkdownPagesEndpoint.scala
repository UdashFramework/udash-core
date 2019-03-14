package io.udash.web.guide.markdown

import java.io.{BufferedReader, File, FileReader}
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

import com.avsystem.commons._
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser

import scala.concurrent.{ExecutionContext, Future}

final class MarkdownPagesEndpoint(guideResourceBase: String)(implicit ec: ExecutionContext) extends MarkdownPageRPC {

  private val parser = Parser.builder.build
  private val renderer = HtmlRenderer.builder.build
  private val renderedPages = new ConcurrentHashMap[MarkdownPage, (Future[String], Instant)]

  private def render(file: File): Future[String] = Future {
    val reader = new BufferedReader(new FileReader(file))
    val document = parser.parseReader(reader)
    renderer.render(document)
  }

  override def loadContent(page: MarkdownPage): Future[String] = {
    val (result, _) = renderedPages.compute(page, { (_, cached) =>
      val pageFile = new File(guideResourceBase + page.file)
      cached.opt.filter {
        case (currentRender, renderedInstant) =>
          currentRender.value.exists(_.isSuccess) && renderedInstant.toEpochMilli >= pageFile.lastModified()
      }.getOrElse((render(pageFile), Instant.ofEpochMilli(pageFile.lastModified())))
    })
    result
  }
}