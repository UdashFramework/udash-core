package io.udash
package rpc.testing

import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import io.udash.rpc.serialization.ExceptionCodecRegistry
import io.udash.rpc.utils.TimeoutConfig
import io.udash.rpc.{DefaultRpcClient, RawRpc, RpcMessage, WebSocketConnector}
import org.scalajs.dom.raw.MessageEvent
import org.scalamock.scalatest.MockFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.scalajs.js

object TestClientWrapper {
  // Scala.js tries to run this class as a test suite when it's top level class...
  class TestRpcClient[LocalRpcApi: RawRpc.AsRawRpc, RemoteRpcApi: RawRpc.AsRealRpc](
    localApiImpl: LocalRpcApi,
    serverUrl: String,
    exceptionsRegistry: ExceptionCodecRegistry,
    timeouts: TimeoutConfig
  )(implicit
    ec: ExecutionContext
  ) extends DefaultRpcClient[LocalRpcApi, RemoteRpcApi](
    localApiImpl, serverUrl, exceptionsRegistry, timeouts
  ) with MockFactory with TestRpcExposer {
    val mockedSession = mock[WebSocketConnector]

    override protected def createSession(): WebSocketConnector = mockedSession

    override def connectionWithMessages(msgs: Seq[RpcMessage], responses: mutable.Buffer[RpcMessage]): Unit = {
      implicit val er: ExceptionCodecRegistry = exceptionsRegistry

      (mockedSession.isOpen _).expects().anyNumberOfTimes().returning(true)
      (mockedSession.isClosed _).expects().anyNumberOfTimes().returning(false)
      (mockedSession.send(_: String)).expects(
        where { v: String =>
          responses += JsonStringInput.read[RpcMessage](v)
          true
        }
      ).anyNumberOfTimes()

      onOpen(null)

      msgs.foreach { msg =>
        val mockedEvent = js.Dictionary("data" -> JsonStringOutput.write(msg)).asInstanceOf[MessageEvent]
        onWebSocketText(mockedEvent)
      }

      onClose(null) // TODO CloseEvent
    }
  }
}