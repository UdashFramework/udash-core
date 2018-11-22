package io.udash.rpc

import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.CallLogging.CallLog
import io.udash.rpc.utils.{CallLogging, ClientId, TimeoutConfig}

import scala.concurrent.duration.FiniteDuration

class DefaultRpcServer[LocalRpcApi : RawRpc.AsRawRpc : RpcMetadata, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: (RpcServer[LocalRpcApi, RemoteRpcApi], ClientId) => LocalRpcApi,
  exceptionsRegistry: ExceptionCodecRegistry,
  timeouts: TimeoutConfig,
  clientCleanup: FiniteDuration,
  callLogger: CallLog => Unit
) extends RpcServer[LocalRpcApi, RemoteRpcApi](localApiImpl, exceptionsRegistry, timeouts, clientCleanup) {
  override protected def createSocket(id: ClientId): RpcSocket = {
    new LoggingRpcSocket(id)
  }

  protected class LoggingRpcSocket(id: ClientId) extends RpcSocket(id) with CallLogging[LocalRpcApi, RemoteRpcApi] {
    override protected val metadata: RpcMetadata[LocalRpcApi] = implicitly[RpcMetadata[LocalRpcApi]]
    override def log(callLog: CallLog): Unit = callLogger(callLog)
  }
}

object DefaultRpcServer {
  def apply[LocalRpcApi : RawRpc.AsRawRpc : RpcMetadata, RemoteRpcApi : RawRpc.AsRealRpc](
    local: LocalRpcApi,
    exceptionsRegistry: ExceptionCodecRegistry,
    callLogger: CallLog => Unit
  ): DefaultRpcServer[LocalRpcApi, RemoteRpcApi] = {
    apply((_, _) => local, exceptionsRegistry, TimeoutConfig.Default, TimeoutConfig.Default.callResponseTimeout, callLogger)
  }

  def apply[LocalRpcApi : RawRpc.AsRawRpc : RpcMetadata, RemoteRpcApi : RawRpc.AsRealRpc](
    local: (RpcServer[LocalRpcApi, RemoteRpcApi], ClientId) => LocalRpcApi,
    exceptionsRegistry: ExceptionCodecRegistry,
    timeouts: TimeoutConfig,
    clientCleanup: FiniteDuration,
    callLogger: CallLog => Unit
  ): DefaultRpcServer[LocalRpcApi, RemoteRpcApi] = {
    new DefaultRpcServer[LocalRpcApi, RemoteRpcApi](local, exceptionsRegistry, timeouts, clientCleanup, callLogger)
  }
}