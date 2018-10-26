package io.udash.selenium.demos.activity

import io.udash.selenium.rpc.demos.activity.{Call, CallServerRPC}

import scala.concurrent.Future

class CallServer(callLogger: CallLogger) extends CallServerRPC {
  override def calls: Future[Seq[Call]] = Future.successful(callLogger.calls)
}
