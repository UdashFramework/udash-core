package io.udash
package rpc

import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.TimeoutConfig
import javax.websocket._

class DefaultRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  serverUrl: String,
  exceptionsRegistry: ExceptionCodecRegistry,
  websocketContainer: WebSocketContainer,
  protected override val timeouts: TimeoutConfig
) extends JvmRpcClient[LocalRpcApi, RemoteRpcApi](
  localApiImpl, serverUrl, exceptionsRegistry, websocketContainer, timeouts
)

object DefaultRpcClient {
  def apply[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
    localApiImpl: LocalRpcApi,
    serverUrl: String,
    exceptionsRegistry: ExceptionCodecRegistry,
    timeouts: TimeoutConfig = TimeoutConfig.Default
  ): DefaultRpcClient[LocalRpcApi, RemoteRpcApi] = {
    new DefaultRpcClient(
      localApiImpl, serverUrl, exceptionsRegistry,
      ContainerProvider.getWebSocketContainer, timeouts
    )
  }
}