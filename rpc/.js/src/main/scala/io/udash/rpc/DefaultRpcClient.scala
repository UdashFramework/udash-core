package io.udash
package rpc

import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.TimeoutConfig

class DefaultRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  serverUrl: String,
  exceptionsRegistry: ExceptionCodecRegistry,
  protected override val timeouts: TimeoutConfig
) extends JsRpcClient[LocalRpcApi, RemoteRpcApi](
  localApiImpl, serverUrl, exceptionsRegistry, timeouts
)

object DefaultRpcClient {
  def apply[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
    localApiImpl: LocalRpcApi,
    serverUrl: String,
    exceptionsRegistry: ExceptionCodecRegistry,
    timeouts: TimeoutConfig = TimeoutConfig.Default
  ): DefaultRpcClient[LocalRpcApi, RemoteRpcApi] = {
    new DefaultRpcClient(
      localApiImpl, serverUrl, exceptionsRegistry, timeouts
    )
  }
}