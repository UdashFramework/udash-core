package io.udash.web.guide.rpc

import io.udash.rpc._
import io.udash.web.guide.MainServerRPC
import io.udash.web.guide.demos.activity.CallLogger
import io.udash.web.guide.demos.{DemosServer, DemosServerRPC}
import io.udash.web.guide.markdown.{MarkdownPageRPC, MarkdownPagesEndpoint}

class ExposedRpcInterfaces(callLogger: CallLogger, guideResourceBase: String)(implicit clientId: ClientId) extends MainServerRPC {
  import io.udash.web.Implicits._
  override def demos(): DemosServerRPC = new DemosServer(callLogger)
  override def pages(): MarkdownPageRPC = new MarkdownPagesEndpoint(guideResourceBase)
}