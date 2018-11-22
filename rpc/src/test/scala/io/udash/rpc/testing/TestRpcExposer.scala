package io.udash
package rpc.testing

import io.udash.rpc.RpcMessage

import scala.collection.mutable

trait TestRpcExposer {
  def connectionWithMessages(msgs: Seq[RpcMessage], responses: mutable.Buffer[RpcMessage]): Unit
}
