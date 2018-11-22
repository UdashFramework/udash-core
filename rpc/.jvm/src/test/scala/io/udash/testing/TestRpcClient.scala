package io.udash
package testing

import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.testing.TestRpcExposer
import io.udash.rpc.utils.TimeoutConfig
import io.udash.rpc._
import io.udash.rpc.internals.JvmWebSocketConnector
import javax.websocket._
import org.scalamock.scalatest.MockFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class TestRpcClient[LocalRpcApi : RawRpc.AsRawRpc, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: LocalRpcApi,
  serverUrl: String,
  exceptionsRegistry: ExceptionCodecRegistry,
  timeouts: TimeoutConfig
)(implicit
  ec: ExecutionContext
) extends DefaultRpcClient[LocalRpcApi, RemoteRpcApi](
  localApiImpl, serverUrl, exceptionsRegistry, ContainerProvider.getWebSocketContainer, timeouts
) with MockFactory with TestRpcExposer {
  val mockedSession = mock[Session]
  val mockedEndpoint = mock[RemoteEndpoint.Basic]

  override protected def createSession(): WebSocketConnector = new JvmWebSocketConnector(mockedSession)

  override def connectionWithMessages(msgs: Seq[RpcMessage], responses: mutable.Buffer[RpcMessage]): Unit = {
    implicit val er: ExceptionCodecRegistry = exceptionsRegistry

    (mockedSession.addMessageHandler(_: MessageHandler)).expects(*).once()
    (mockedSession.isOpen _).expects().anyNumberOfTimes().returning(true)
    (mockedSession.getBasicRemote _).expects().anyNumberOfTimes().returning(mockedEndpoint)
    (mockedEndpoint.sendText(_: String)).expects(
      where { v: String =>
        responses += JsonStringInput.read[RpcMessage](v)
        true
      }
    ).anyNumberOfTimes()

    val clientSocket = new RpcSocket

    clientSocket.onOpen(mockedSession, null)

    msgs.foreach(msg => clientSocket.onWebSocketText(mockedSession, JsonStringOutput.write(msg)))

    clientSocket.onClose(mockedSession, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE," Connection closed by user."))
  }
}