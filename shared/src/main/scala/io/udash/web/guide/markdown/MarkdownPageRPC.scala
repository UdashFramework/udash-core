package io.udash.web.guide.markdown

import io.udash.rpc.DefaultServerRpcCompanion

import scala.concurrent.Future

trait MarkdownPageRPC {
  def loadContent(page: MarkdownPage): Future[String]
}

object MarkdownPageRPC extends DefaultServerRpcCompanion[MarkdownPageRPC]