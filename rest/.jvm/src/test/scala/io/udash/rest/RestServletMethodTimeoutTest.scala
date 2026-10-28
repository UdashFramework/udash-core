package io.udash
package rest

import com.avsystem.commons.Opt
import io.udash.rest.raw.{AbstractRestResponse, RawRest, RestRequest, RestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.eclipse.jetty.ee8.servlet.{ServletContextHandler, ServletHolder}
import org.eclipse.jetty.server.Server
import org.slf4j.event.{Level, SubstituteLoggingEvent}
import org.slf4j.helpers.SubstituteLogger

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

trait MethodTimeoutApi {
  @timeout(300.millis) def shortHang: Task[Unit]
  @timeout(10.seconds) def longSleep: Task[String]
  def defaultHang: Task[Unit]
  @GET
  @timeout(10.seconds)
  @streamingResponseBatchSize(1) def progress: Observable[Int]
  @timeout(300.millis) def shortPrefix: MethodTimeoutSubApi
}
object MethodTimeoutApi extends DefaultRestApiCompanion[MethodTimeoutApi]

trait MethodTimeoutSubApi {
  def inheritedHang: Task[Unit]
  @timeout(500.millis) def overriddenHang: Task[Unit]
}
object MethodTimeoutSubApi extends DefaultRestApiCompanion[MethodTimeoutSubApi]

/** Covers `@timeout` as read from the API's metadata, for each way of mounting the API on a servlet. */
class RestServletMethodTimeoutTest extends UdashSharedTest with UsesHttpServer {

  private val HandleTimeout = 1.second

  private implicit val scheduler: Scheduler = Scheduler.global

  private val secondEmitted = new AtomicBoolean(false)

  private val impl = new MethodTimeoutApi with MethodTimeoutSubApi {
    def shortHang: Task[Unit] = Task.never
    def longSleep: Task[String] = Task.sleep(3.seconds).as("slept")
    def defaultHang: Task[Unit] = Task.never
    def progress: Observable[Int] = Observable(1) ++ Observable.evalDelayed(3.seconds, { secondEmitted.set(true); 2 })
    def shortPrefix: MethodTimeoutSubApi = this
    def inheritedHang: Task[Unit] = Task.never
    def overriddenHang: Task[Unit] = Task.never
  }

  private val streaming = RawRest.asHandleRequestWithStreaming[MethodTimeoutApi](impl)

  // Resolves every call to a timeout under a millisecond, and answers only after handleTimeout
  private val zeroTimeoutHandler = new RawRest.ResolvingHandleRequest[AbstractRestResponse] {
    def resolve(request: RestRequest): (Opt[FiniteDuration], Task[AbstractRestResponse]) =
      (Opt(Duration.Zero), Task.sleep(HandleTimeout + 500.millis).as(RestResponse.plain(200, "late")))
    def apply(request: RestRequest): Task[AbstractRestResponse] = resolve(request)._2
  }

  protected def setupServer(server: Server): Unit = {
    val handler = new ServletContextHandler()
    def mount(path: String, servlet: RestServlet): Unit = handler.addServlet(new ServletHolder(servlet), s"/$path/*")
    mount("api", RestServlet[MethodTimeoutApi](impl, handleTimeout = HandleTimeout))
    mount("plain-api", new RestServlet(RawRest.asHandleRequest[MethodTimeoutApi](impl), HandleTimeout))
    mount("wrapped-api", new RestServlet(RawRest.wrapHandleRequest(streaming)((_, task) => task), HandleTimeout))
    mount("lambda-api", new RestServlet(request => streaming(request), HandleTimeout))
    mount(
      "lambda-timeouts-api",
      new RestServlet(RawRest.withTimeouts(MethodTimeoutApi.restMetadata)(request => streaming(request)), HandleTimeout),
    )
    mount("zero-timeout-api", new RestServlet(zeroTimeoutHandler, HandleTimeout))
    server.setHandler(handler)
  }

  private lazy val client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()

  private def post(path: String): HttpResponse[String] = {
    val request = HttpRequest.newBuilder(URI.create(s"$baseUrl/$path")).POST(HttpRequest.BodyPublishers.noBody()).build()
    client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get(8, TimeUnit.SECONDS)
  }

  private def assertTimedOut(response: HttpResponse[String], timeout: FiniteDuration): Unit = {
    assert(response.statusCode() == 500)
    assert(response.body() == s"server operation timed out after $timeout")
  }

  "RestServlet" should {
    "bound a call by its method's timeout when it is shorter than handleTimeout" in {
      assertTimedOut(post("api/shortHang"), 300.millis)
    }

    "let a call run past handleTimeout when its method's timeout is longer" in {
      val response = post("api/longSleep")
      assert(response.statusCode() == 200)
      assert(response.body() == "\"slept\"")
    }

    "bound a call without @timeout by handleTimeout" in {
      assertTimedOut(post("api/defaultHang"), HandleTimeout)
    }

    "keep a stream open across a gap longer than handleTimeout within its method's timeout" in {
      val request = HttpRequest.newBuilder(URI.create(s"$baseUrl/api/progress")).GET().build()
      val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).get(8, TimeUnit.SECONDS)
      assert(response.statusCode() == 200)
      val body = response.body()
      val first = new String(body.readNBytes(2), UTF_8)
      assert(first == "[1")
      assert(!secondEmitted.get, "the first element arrived only with the second one")
      assert(first + new String(body.readAllBytes(), UTF_8) == "[1,2]")
    }

    "apply a prefix's timeout to the methods under it unless they have their own" in {
      assertTimedOut(post("api/shortPrefix/inheritedHang"), 300.millis)
      assertTimedOut(post("api/shortPrefix/overriddenHang"), 500.millis)
    }

    for (
      (mount, handler) <- List(
        "plain-api" -> "RawRest.asHandleRequest",
        "wrapped-api" -> "RawRest.wrapHandleRequest",
        "lambda-timeouts-api" -> "RawRest.withTimeouts over a plain function",
      )
    ) {
      s"honour @timeout for a servlet whose handler comes from $handler" in {
        assertTimedOut(post(s"$mount/shortHang"), 300.millis)
      }
    }

    "not bound a call resolved to a timeout under a millisecond" in {
      val response = post("zero-timeout-api/any")
      assert(response.statusCode() == 200)
      assert(response.body() == "late")
    }

    "fall back to handleTimeout for a handler wrapped in a plain function" in {
      assertTimedOut(post("lambda-api/shortHang"), HandleTimeout)
    }

    "log at debug when created with a handler wrapped in a plain function" in {
      def creationLog(handler: RawRest.HandleRequestWithStreaming): List[(Level, String)] = {
        val events = new ConcurrentLinkedQueue[SubstituteLoggingEvent]
        new RestServlet(handler, customLogger = new SubstituteLogger(classOf[RestServlet].getName, events, false))
        events.asScala.map(e => (e.getLevel, e.getMessage)).toList
      }
      assert(
        creationLog(request => streaming(request)) == List(
          Level.DEBUG ->
            ("RestServlet's handler does not resolve calls, so @timeout does not apply to them. " +
              "Apply RawRest.withTimeouts to it, or wrap the RawRest.asHandleRequest* handler itself with " +
              "RawRest.wrapHandleRequest instead of a function, for @timeout to apply.")
        )
      )
      assert(creationLog(RawRest.wrapHandleRequest(streaming)((_, task) => task)).isEmpty)
      assert(creationLog(RawRest.withTimeouts(MethodTimeoutApi.restMetadata)(request => streaming(request))).isEmpty)
    }
  }
}
