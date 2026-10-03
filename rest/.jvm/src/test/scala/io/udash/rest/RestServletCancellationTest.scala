package io.udash
package rest

import io.udash.rest.raw.{IMapping, JsonValue, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.schedulers.TestScheduler
import monix.reactive.Observable

import java.io.EOFException
import java.lang.reflect.Proxy
import java.util.Collections
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncContext, AsyncEvent, AsyncListener, ServletOutputStream, WriteListener}
import scala.collection.mutable

/**
 * Drives [[RestServlet]] through fake servlet objects on a [[TestScheduler]], so that a disconnect can be ordered
 * exactly against a write in flight - an ordering that a real container leaves to chance - and everything the request
 * started has run by the time its outcome is checked.
 */
class RestServletCancellationTest extends UdashSharedTest {

  // Methods not answered return null, so every method returning a primitive that the servlet calls must be answered.
  // Object's own methods are answered by identity.
  private def fake[T](cls: Class[T])(answer: PartialFunction[(String, Seq[AnyRef]), AnyRef]): T =
    Proxy.newProxyInstance(cls.getClassLoader, Array[Class[?]](cls), (proxy, method, args) =>
      (method.getName, Option(args).fold(Seq.empty[AnyRef])(_.toSeq)) match {
        case ("hashCode", Seq()) => Int.box(System.identityHashCode(proxy))
        case ("equals", Seq(other)) => Boolean.box(proxy eq other)
        case ("toString", Seq()) => s"fake ${cls.getSimpleName}"
        case call => answer.applyOrElse(call, (_: (String, Seq[AnyRef])) => null)
      }
    ).asInstanceOf[T]

  /**
   * Serves `body` with the client disconnecting during the first write into the response: the container reports the
   * disconnect to the servlet's listeners, which cancel the handler, and then fails the write as a reset stream does,
   * unless `failFirstWrite` is off. Any later write fails. Asserts that nothing reached the uncaught failure reporter.
   */
  private def assertDisconnectHandled(body: StreamedBody.NonEmpty, failFirstWrite: Boolean = true): Unit = {
    implicit val scheduler: TestScheduler = TestScheduler()
    val listeners = new mutable.ListBuffer[AsyncListener]
    var writes = 0

    val asyncContext = fake(classOf[AsyncContext]) {
      case ("addListener", Seq(listener: AsyncListener)) => listeners += listener; null
    }
    val request = fake(classOf[HttpServletRequest]) {
      case ("startAsync", _) => asyncContext
      case ("getMethod", _) => "GET"
      case ("getRequestURI", _) => "/stream"
      case ("getHeaderNames", _) => Collections.emptyEnumeration[String]()
      case ("getContentLengthLong", _) => Long.box(-1L)
    }
    lazy val response: HttpServletResponse = fake(classOf[HttpServletResponse]) {
      case ("getOutputStream", _) => output
      case ("isCommitted", _) => java.lang.Boolean.FALSE
    }
    lazy val output: ServletOutputStream = new ServletOutputStream {
      def isReady: Boolean = true
      def setWriteListener(writeListener: WriteListener): Unit = ()
      def write(b: Int): Unit = write(Array(b.toByte))
      override def write(b: Array[Byte], off: Int, len: Int): Unit = {
        writes += 1
        if (writes == 1) {
          val disconnect = new AsyncEvent(asyncContext, request, response, new EOFException("reset"))
          listeners.foreach(_.onError(disconnect))
        }
        if (writes > 1 || failFirstWrite) throw new EOFException("reset")
      }
    }

    new RestServlet(_ => Task.now(StreamedRestResponse(200, IMapping.empty, body)), cancelOnDisconnect = true)
      .service(request, response)
    scheduler.tick()

    assert(writes > 0, "nothing was written")
    val uncaught = Option(scheduler.state.lastReportedError)
    assert(uncaught.isEmpty, uncaught.fold("")(e => s"${e.getClass.getName}: ${e.getMessage}"))
  }

  private def binary(content: Observable[Array[Byte]]): StreamedBody.NonEmpty =
    StreamedBody.RawBinary(content, "application/octet-stream")

  "RestServlet" should {
    "not report a binary chunk write failing after a disconnect cancelled the handler as an uncaught error" in {
      assertDisconnectHandled(binary(Observable.repeat(Array[Byte](1))))
    }

    "not report a JSON list batch write failing after a disconnect cancelled the handler as an uncaught error" in {
      assertDisconnectHandled(StreamedBody.JsonList(Observable.repeat(JsonValue("1"))))
    }

    "not report a stream failing after a disconnect cancelled the handler as an uncaught error" in {
      var emitted = false
      // fails the chunk after the one whose write saw the disconnect, as a source closed by the cancellation would
      val chunks = Observable.repeat(Array[Byte](1)).map { chunk =>
        if (emitted) throw new IllegalStateException("source closed")
        emitted = true
        chunk
      }
      assertDisconnectHandled(binary(chunks), failFirstWrite = false)
    }
  }
}
