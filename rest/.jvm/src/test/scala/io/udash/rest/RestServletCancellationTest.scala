package io.udash
package rest

import io.udash.rest.raw.{IMapping, JsonValue, RawRest, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.schedulers.TestScheduler
import monix.reactive.Observable
import org.slf4j.event.{Level, SubstituteLoggingEvent}
import org.slf4j.helpers.SubstituteLogger

import java.io.EOFException
import java.lang.reflect.Proxy
import java.util.{ArrayDeque, Collections}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncContext, AsyncEvent, AsyncListener, ServletOutputStream, WriteListener}
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

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
   * unless `failFirstWrite` is off. Any later write fails. Asserts that nothing reached the uncaught failure reporter
   * and returns what the servlet logged.
   */
  private def disconnectDuringFirstWrite(
    body: StreamedBody.NonEmpty,
    failFirstWrite: Boolean = true,
  ): Seq[SubstituteLoggingEvent] = {
    implicit val scheduler: TestScheduler = TestScheduler()
    val logEvents = new ArrayDeque[SubstituteLoggingEvent]
    val logger = new SubstituteLogger(classOf[RestServlet].getName, logEvents, false)
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

    val handleRequest: RawRest.HandleRequestWithStreaming = _ => Task.now(StreamedRestResponse(200, IMapping.empty, body))
    new RestServlet(handleRequest, customLogger = logger, cancelOnDisconnect = true).service(request, response)
    scheduler.tick()

    assert(writes > 0, "nothing was written")
    val uncaught = Option(scheduler.state.lastReportedError)
    assert(uncaught.isEmpty, uncaught.fold("")(e => s"${e.getClass.getName}: ${e.getMessage}"))
    logEvents.asScala.toList
  }

  private def assertLogged(events: Seq[SubstituteLoggingEvent], level: Level, message: String, failure: String): Unit =
    assert(
      events.exists(e => e.getLevel == level && e.getMessage.contains(message) && e.getThrowable.getMessage.contains(failure)),
      events.map(e => s"${e.getLevel} ${e.getMessage}: ${e.getThrowable}").mkString(", "),
    )

  private def binary(content: Observable[Array[Byte]]): StreamedBody.NonEmpty =
    StreamedBody.RawBinary(content, "application/octet-stream")

  "RestServlet" should {
    "log a binary chunk write failing after a disconnect cancelled the handler instead of reporting it as uncaught" in {
      val events = disconnectDuringFirstWrite(binary(Observable.repeat(Array[Byte](1))))
      assertLogged(events, Level.WARN, "cancelled by the client", "reset")
    }

    "log a JSON list batch write failing after a disconnect cancelled the handler instead of reporting it as uncaught" in {
      val events = disconnectDuringFirstWrite(StreamedBody.JsonList(Observable.repeat(JsonValue("1"))))
      assertLogged(events, Level.WARN, "cancelled by the client", "reset")
    }

    "log a stream failing after a disconnect cancelled the handler instead of reporting it as uncaught" in {
      var emitted = false
      // fails the chunk after the one whose write saw the disconnect, as a source closed by the cancellation would
      val chunks = Observable.repeat(Array[Byte](1)).map { chunk =>
        if (emitted) throw new IllegalStateException("source closed")
        emitted = true
        chunk
      }
      val events = disconnectDuringFirstWrite(binary(chunks), failFirstWrite = false)
      assertLogged(events, Level.WARN, "after the request had already been completed", "source closed")
    }
  }
}
