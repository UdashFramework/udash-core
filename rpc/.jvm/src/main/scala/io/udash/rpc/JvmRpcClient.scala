package io.udash
package rpc

import java.net.URI

import io.udash.logging.CrossLogging
import io.udash.rpc.internals.JvmWebSocketConnector
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.TimeoutConfig
import javax.websocket._

class JvmRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  serverUrl: String,
  exceptionsRegistry: ExceptionCodecRegistry,
  websocketContainer: WebSocketContainer,
  protected override val timeouts: TimeoutConfig
) extends AbstractRpcClient[LocalRpcApi, RemoteRpcApi](localApiImpl, exceptionsRegistry) with JvmUsesRemoteRpc[RemoteRpcApi] with CrossLogging {
  protected def createSession(): WebSocketConnector = {
    new JvmWebSocketConnector(
      websocketContainer.connectToServer(
        new RpcSocket,
        URI.create(withClientIdParam(serverUrl, clientId))
      )
    )
  }

  final protected class RpcSocket extends Endpoint {
    override def onOpen(session: Session, config: EndpointConfig): Unit = {
      session.addMessageHandler(new MessageHandler.Whole[String] {
        override def onMessage(message: String): Unit = {
          onWebSocketText(session, message)
        }
      })
    }

    override def onClose(session: Session, closeReason: CloseReason): Unit = {
      connectorClosed()
      super.onClose(session, closeReason)
    }

    override def onError(session: Session, cause: Throwable): Unit = {
      logger.warn("WebSocket connection error", cause)
      connectorClosed()
      super.onError(session, cause)
    }

    def onWebSocketText(session: Session, message: String): Unit = {
      handleMessage(message)(exceptionsRegistry)
    }
  }
}
