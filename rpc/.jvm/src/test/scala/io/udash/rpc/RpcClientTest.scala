package io.udash
package rpc

import io.udash.rpc.serialization.DefaultExceptionCodecRegistry
import io.udash.rpc.testing.{RpcExposerTest, TestRpcExposer}
import io.udash.rpc.utils.TimeoutConfig
import io.udash.testing.TestRpcClient

import scala.collection.mutable.ArrayBuffer

class RpcClientTest extends RpcExposerTest("RpcClient") {
  override def createRpcExposer(calls: ArrayBuffer[String]): TestRpcExposer = {
    new TestRpcClient[TestRpc, TestRpc](
      TestRpc.rpcImpl((name: String, _, _) => calls.append(name)), "",
      new DefaultExceptionCodecRegistry, TimeoutConfig.Default
    )
  }
}
