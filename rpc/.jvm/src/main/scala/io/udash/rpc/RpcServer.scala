package io.udash
package rpc

import java.io.IOException
import java.util.UUID

import com.avsystem.commons.{MHashMap, MMap}
import com.avsystem.commons.misc.Timestamp
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.logging.CrossLogging
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.{ClientId, TimeoutConfig}
import javax.websocket._
import javax.websocket.server.{HandshakeRequest, ServerEndpointConfig}

import scala.concurrent.duration.FiniteDuration
import scala.util.Try
import scala.util.control.NonFatal

class RpcServer[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: (RpcServer[LocalRpcApi, RemoteRpcApi], ClientId) => LocalRpcApi,
  exceptionsRegistry: ExceptionCodecRegistry,
  timeouts: TimeoutConfig,
  clientCleanup: FiniteDuration
) extends CrossLogging {

  protected val openSessions: MMap[String, RpcSocket] = MHashMap.empty

  def endpointConfig(path: String): ServerEndpointConfig = {
    ServerEndpointConfig.Builder
      .create(classOf[RpcSocket], path)
      .configurator(new RpcSocketConfigurator)
      .build()
  }

  def call(clientId: ClientId): RemoteRpcApi = {
    openSessions.synchronized {
      openSessions(clientId.id).clientRpc
    }
  }

  def call[T](clientIds: Iterable[ClientId], method: RemoteRpcApi => T): Map[ClientId, Try[T]] = {
    clientIds.map { id =>
      id -> Try(method(call(id)))
    }.toMap
  }

  protected def createSocket(id: ClientId): RpcSocket = {
    new RpcSocket(id)
  }

  protected class RpcSocketConfigurator extends ServerEndpointConfig.Configurator {
    private var clientId: ClientId = _

    override def modifyHandshake(config: ServerEndpointConfig, request: HandshakeRequest, response: HandshakeResponse): Unit = {
      super.modifyHandshake(config, request, response)
      val clientIdHeaders = request.getParameterMap.get(AbstractRpcClient.ClientIdQueryParamName)
      if (clientIdHeaders != null && clientIdHeaders.size() > 0) {
        clientId = ClientId(clientIdHeaders.get(0))
      } else {
        clientId = ClientId(UUID.randomUUID().toString)
      }
    }

    override def getEndpointInstance[T](endpointClass: Class[T]): T = {
      openSessions.synchronized {
        openSessions.get(clientId.id)
      }.getOrElse(createSocket(clientId)).asInstanceOf[T]
    }
  }

  protected class RpcSocket(val clientId: ClientId)
    extends Endpoint with RpcParty[LocalRpcApi, RemoteRpcApi] with JvmUsesRemoteRpc[RemoteRpcApi] {

    val clientRpc: RemoteRpcApi = AsReal[RawRpc, RemoteRpcApi].asReal(new RawRemoteRPC(Nil))

    openSessions.synchronized {
      openSessions(clientId.id) = this
    }

    protected val apiImpl: LocalRpcApi = localApiImpl(RpcServer.this, clientId)
    override protected val rawLocalRpc: RawRpc = AsRaw[RawRpc, LocalRpcApi].asRaw(apiImpl)

    override protected def timeouts: TimeoutConfig = RpcServer.this.timeouts

    private var lastConnection: Timestamp= Timestamp(0)
    private var session: Session = _

    /** Sends RPCRequest to the other node. */
    override protected def sendRpcMessage(request: RpcMessage, retryNumber: Int = 0): Unit = synchronized {
      if (session != null && session.isOpen) {
        implicit val er: ExceptionCodecRegistry = exceptionsRegistry
        try {
          val serializedMsg = JsonStringOutput.write[RpcMessage](request)
          session.getBasicRemote.sendText(serializedMsg)
        } catch {
          case ex: IOException =>
            logger.debug("Failed to send a message, the message will be enqueued and resend later.", ex)
            enqueueMessage(request, retryNumber)
          case NonFatal(ex) =>
            logger.error("Failed to send a message.", ex)
        }
      } else {
        logger.debug(s"There is no open client's connection for $clientId. The message will be enqueued and resend later.")
        enqueueMessage(request, retryNumber)
      }
    }


    override def onOpen(session: Session, config: EndpointConfig): Unit = synchronized {
      this.session = session
      lastConnection = Timestamp.now()
      sendRpcMessage(RpcClientInit(clientId))
      session.addMessageHandler(new MessageHandler.Whole[String] {
        override def onMessage(message: String): Unit = {
          onWebSocketText(session, message)
        }
      })
    }


    override def onClose(session: Session, closeReason: CloseReason): Unit = {
      super.onClose(session, closeReason)
      cleanupConnection()
    }

    override def onError(session: Session, cause: Throwable): Unit = {
      super.onError(session, cause)
      logger.warn("WebSocket connection error", cause)
      cleanupConnection()
    }

    def onWebSocketText(session: Session, message: String): Unit = {
      handleMessage(message)(exceptionsRegistry)
    }

    private def cleanupConnection(): Unit = synchronized {
      this.session = null
      timeoutCallback(() => {
        if (this.session == null && Timestamp.now().millis - lastConnection.millis >= clientCleanup.toMillis) {
          openSessions.synchronized {
            openSessions.remove(clientId.id)
          }
        }
      }, clientCleanup)
    }
  }
}