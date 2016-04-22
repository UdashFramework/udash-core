package io.udash.guide.rpc

import io.udash.guide.MainClientRPC
import io.udash.rpc._

import scala.concurrent.ExecutionContext

object ClientRPC {
  def apply(target: ClientRPCTarget)(implicit ec: ExecutionContext): MainClientRPC = {
    new DefaultClientRPC[MainClientRPC](target).get
  }
}
