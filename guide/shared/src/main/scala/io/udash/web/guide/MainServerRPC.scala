package io.udash.web.guide

import io.udash.rpc._
import io.udash.web.guide.demos.DemosServerRPC
import io.udash.web.guide.markdown.MarkdownPageRPC

trait MainServerRPC {
  def demos(): DemosServerRPC
  def pages(): MarkdownPageRPC
}

object MainServerRPC extends DefaultServerRpcCompanion[MainServerRPC]