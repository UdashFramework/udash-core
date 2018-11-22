package io.udash
package rpc

import io.udash.rpc.serialization.DefaultExceptionCodecRegistry
import io.udash.rpc.testing.{TestClientWrapper, RpcExposerTest, TestRpcExposer}
import io.udash.rpc.utils.TimeoutConfig

import scala.collection.mutable.ArrayBuffer

class RpcClientTest extends RpcExposerTest("RpcClient") {
  override def createRpcExposer(calls: ArrayBuffer[String]): TestRpcExposer = {
    new TestClientWrapper.TestRpcClient[TestRpc, TestRpc](
      TestRpc.rpcImpl((name: String, _, _) => calls.append(name)), "",
      new DefaultExceptionCodecRegistry, TimeoutConfig.Default
    )
  }
}
