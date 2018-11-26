package io.udash
package testing

import java.util.UUID

import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.testing.TestRpcExposer
import io.udash.rpc.utils.{ClientId, TimeoutConfig}
import io.udash.rpc._
import javax.websocket.{CloseReason, MessageHandler, RemoteEndpoint, Session}
import org.scalamock.scalatest.MockFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class TestRpcServer[LocalRpcApi : RawRpc.AsRawRpc : RpcMetadata, RemoteRpcApi : RawRpc.AsRealRpc](
  localApiImpl: (RpcServer[LocalRpcApi, RemoteRpcApi], ClientId) => LocalRpcApi,
  exceptionsRegistry: ExceptionCodecRegistry,
  timeouts: TimeoutConfig,
  clientCleanup: FiniteDuration
)(implicit
  ec: ExecutionContext
) extends DefaultRpcServer[LocalRpcApi, RemoteRpcApi](localApiImpl, exceptionsRegistry, timeouts, clientCleanup, _ => ())
    with MockFactory with TestRpcExposer {
  def clientsCount: Int = openSessions.size

  override def connectionWithMessages(msgs: Seq[RpcMessage], responses: mutable.Buffer[RpcMessage]): Unit = {
    val mockedSession = mock[Session]
    val mockedEndpoint = mock[RemoteEndpoint.Basic]
    implicit val er: ExceptionCodecRegistry = exceptionsRegistry

    (mockedSession.addMessageHandler(_: MessageHandler)).expects(*).once()
    (mockedSession.isOpen _).expects().anyNumberOfTimes().returning(true)
    (mockedSession.getBasicRemote _).expects().anyNumberOfTimes().returning(mockedEndpoint)
    (mockedEndpoint.sendText(_: String)).expects(
      where { v: String =>
        JsonStringInput.read[RpcMessage](v) match {
          case _: RpcClientInit => // ignore
          case msg => responses += msg
        }
        true
      }
    ).anyNumberOfTimes()

    val clientSocket = new RpcSocket(ClientId(UUID.randomUUID().toString))

    clientSocket.onOpen(mockedSession, null)

    msgs.foreach(msg => clientSocket.onWebSocketText(mockedSession, JsonStringOutput.write(msg)))

    clientSocket.onClose(mockedSession, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE," Connection closed by user."))
  }
}