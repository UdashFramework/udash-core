package io.udash.web.guide.demos.activity

import com.avsystem.commons.rpc.RPC

import scala.concurrent.Future

@RPC
trait CallServerRPC {
  def calls: Future[Seq[Call]]
}
