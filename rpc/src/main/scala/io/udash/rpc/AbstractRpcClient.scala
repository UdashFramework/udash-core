package io.udash
package rpc

import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.ClientId
import io.udash.utils.URLEncoder

import scala.util.control.NonFatal
import scala.util.{Success, Try}

abstract class AbstractRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  exceptionsRegistry: ExceptionCodecRegistry
) extends RpcParty[LocalRpcApi, RemoteRpcApi] {
  protected def createSession(): WebSocketConnector

  protected final val serverRpc: RemoteRpcApi = AsReal[RawRpc, RemoteRpcApi].asReal(new RawRemoteRPC(Nil))
  override protected def rawLocalRpc: RawRpc = AsRaw[RawRpc, LocalRpcApi].asRaw(localApiImpl)

  private var _clientId: Option[ClientId] = None
  protected def clientId: Option[ClientId] = _clientId
  private var _connector: WebSocketConnector = _
  protected def connector: WebSocketConnector = _connector

  def call(): RemoteRpcApi = {
    serverRpc
  }

  def open(): Unit = {
    if (connector == null || connector.isClosed) {
      _connector = createSession()
    }
  }

  def close(): Unit = {
    connector.close()
    connectorClosed()
  }

  protected def connectorClosed(): Unit = {
    _connector = null
  }

  override protected def sendRpcMessage(request: RpcMessage, retryNumber: Int = 0): Unit = {
    implicit val er: ExceptionCodecRegistry = exceptionsRegistry
    try {
      val serializedMsg = JsonStringOutput.write[RpcMessage](request)
      open()
      if (connector.isOpen) {
        connector.send(serializedMsg)
      } else {
        enqueueMessage(request, retryNumber)
      }
    } catch {
      case NonFatal(ex) =>
        logger.debug("Failed to send a message, the message will be enqueued and resend later.", ex)
        enqueueMessage(request, retryNumber)
    }
  }

  override protected def rpcMessageHandler(implicit exceptionsRegistry: ExceptionCodecRegistry): PartialFunction[Try[RpcMessage], Unit] = {
    val clientInitHandler: PartialFunction[Try[RpcMessage], Unit] = {
      case Success(init: RpcClientInit) =>
        clientInitialize(init)
    }

    clientInitHandler.orElse(super.rpcMessageHandler)
  }

  protected def clientInitialize(init: RpcClientInit): Unit = {
    _clientId = Option(init.clientId)
  }

  protected def withClientIdParam(url: String, clientId: Option[ClientId]): String = {
    clientId match {
      case Some(id) =>
        val name = URLEncoder.encode(AbstractRpcClient.ClientIdQueryParamName, spaceAsPlus = true)
        val value = URLEncoder.encode(id.id, spaceAsPlus = true)
        val separator = if (url.contains("?")) "&" else "?"
        s"$url$separator$name=$value"
      case None =>
        url
    }
  }
}

object AbstractRpcClient {
  val ClientIdQueryParamName = "udash-rpc-clientid"
}