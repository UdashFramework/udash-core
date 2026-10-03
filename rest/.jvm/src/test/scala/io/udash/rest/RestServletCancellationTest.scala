package io.udash
package rest

import io.udash.rest.raw.{IMapping, RawRest, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.{ExecutionModel, Scheduler, UncaughtExceptionReporter}
import monix.reactive.Observable

import java.io.EOFException
import java.lang.reflect.Proxy
import java.util.Collections
import java.util.concurrent.{ConcurrentLinkedQueue, CountDownLatch, TimeUnit}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncContext, AsyncEvent, AsyncListener, ServletOutputStream, WriteListener}
import scala.jdk.CollectionConverters.*

/**
 * Drives [[RestServlet]] through fake servlet objects, so that a disconnect can be ordered exactly against a write in
 * flight - an ordering that a real container leaves to chance.
 */
class RestServletCancellationTest extends UdashSharedTest {

  private val uncaughtErrors = new ConcurrentLinkedQueue[Throwable]

  private implicit val scheduler: Scheduler = Scheduler(
    Scheduler.global,
    UncaughtExceptionReporter(t => { uncaughtErrors.add(t); () }),
    ExecutionModel.Default,
  )

  // Methods not answered return null, so every method returning a primitive that the servlet calls must be answered
  private def fake[T](cls: Class[T])(answer: PartialFunction[(String, Seq[AnyRef]), AnyRef]): T =
    Proxy.newProxyInstance(cls.getClassLoader, Array[Class[?]](cls), (_, method, args) =>
      answer.applyOrElse((method.getName, Option(args).fold(Seq.empty[AnyRef])(_.toSeq)), (_: (String, Seq[AnyRef])) => null)
    ).asInstanceOf[T]

  "RestServlet" should {
    "not report a streamed write failing after a disconnect cancelled the handler as an uncaught error" in {
      val writing = new CountDownLatch(1)
      val failWrite = new CountDownLatch(1)
      val writeFailed = new CountDownLatch(1)
      // blocks the first chunk write until the disconnect has been handled, then fails it as a reset stream does
      val output = new ServletOutputStream {
        def isReady: Boolean = true
        def setWriteListener(writeListener: WriteListener): Unit = ()
        def write(b: Int): Unit = write(Array(b.toByte))
        override def write(b: Array[Byte], off: Int, len: Int): Unit = {
          writing.countDown()
          failWrite.await(4, TimeUnit.SECONDS)
          writeFailed.countDown()
          throw new EOFException("reset")
        }
      }

      val listeners = new ConcurrentLinkedQueue[AsyncListener]
      val asyncContext = fake(classOf[AsyncContext]) {
        case ("addListener", Seq(listener: AsyncListener)) => listeners.add(listener); null
      }
      val request = fake(classOf[HttpServletRequest]) {
        case ("startAsync", _) => asyncContext
        case ("getMethod", _) => "GET"
        case ("getRequestURI", _) => "/stream"
        case ("getHeaderNames", _) => Collections.emptyEnumeration[String]()
        case ("getContentLengthLong", _) => Long.box(-1L)
      }
      val response = fake(classOf[HttpServletResponse]) {
        case ("getOutputStream", _) => output
        case ("isCommitted", _) => java.lang.Boolean.FALSE
      }

      val handleRequest: RawRest.HandleRequestWithStreaming = _ => Task.now(StreamedRestResponse(
        200, IMapping.empty, StreamedBody.RawBinary(Observable.repeat(Array[Byte](1)), "application/octet-stream")
      ))
      new RestServlet(handleRequest, cancelOnDisconnect = true).service(request, response)

      assert(writing.await(4, TimeUnit.SECONDS), "no chunk was written")
      val disconnect = new AsyncEvent(asyncContext, request, response, new EOFException("reset"))
      listeners.asScala.foreach(_.onError(disconnect))
      failWrite.countDown()
      assert(writeFailed.await(4, TimeUnit.SECONDS))
      Thread.sleep(500) // lets the write failure propagate
      assert(uncaughtErrors.isEmpty, uncaughtErrors.asScala.map(e => s"${e.getClass.getName}: ${e.getMessage}").mkString(", "))
    }
  }
}
