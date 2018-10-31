package io.udash.web.guide.markdown
import java.io.{BufferedReader, File, FileReader}

import scala.concurrent.{ExecutionContext, Future}

class MarkdownPagesEndpoint(guideResourceBase: String)(implicit ec: ExecutionContext) extends MarkdownPageRPC {
  import com.vladsch.flexmark.html.HtmlRenderer
  import com.vladsch.flexmark.parser.Parser

  private val parser = Parser.builder.build
  private val renderer = HtmlRenderer.builder.build

  override def loadContent(page: MarkdownPage): Future[String] = Future {
    require(MarkdownPage.values.contains(page), s"Unknown page: ${page.file}")

    val file = new File(guideResourceBase + page.file)
    val reader = new BufferedReader(new FileReader(file))
    val document = parser.parseReader(reader)
    renderer.render(document)
  }
}