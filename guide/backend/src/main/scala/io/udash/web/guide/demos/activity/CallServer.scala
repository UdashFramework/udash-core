package io.udash.web.guide.demos.activity

import scala.concurrent.Future

class CallServer(callLogger: CallLogger) extends CallServerRPC {
  override def calls: Future[Seq[Call]] = Future.successful(callLogger.calls)
}
