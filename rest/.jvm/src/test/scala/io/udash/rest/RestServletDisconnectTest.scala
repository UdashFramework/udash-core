package io.udash
package rest

import io.udash.rest.raw.{AbstractRestResponse, HttpBody, HttpErrorException, IMapping, JsonValue, PlainValue, RawRest, RestResponse, StreamedBody, StreamedRestResponse}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.exceptions.UpstreamTimeoutException
import monix.execution.{ExecutionModel, Scheduler, UncaughtExceptionReporter}
import monix.reactive.Observable
import org.eclipse.jetty.ee8.nested.{ErrorHandler, HttpChannelState, Request as JettyRequest}
import org.eclipse.jetty.ee8.servlet.{FilterHolder, ServletContextHandler, ServletHolder}
import org.eclipse.jetty.http.{HttpFields, HttpURI, HttpVersion, MetaData}
import org.eclipse.jetty.http2.frames.{HeadersFrame, PrefaceFrame, SettingsFrame}
import org.eclipse.jetty.http2.generator.Generator
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory
import org.eclipse.jetty.io.{ByteBufferPool, RetainableByteBuffer}
import org.eclipse.jetty.server.{HttpConnectionFactory, Server, ServerConnector}
import org.slf4j.event.{Level, SubstituteLoggingEvent}
import org.slf4j.helpers.{MessageFormatter, SubstituteLogger}

import java.io.{EOFException, IOException}
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.{Socket, URI}
import java.util.EnumSet
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue, CountDownLatch, ScheduledThreadPoolExecutor, TimeUnit}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse, HttpServletResponseWrapper}
import javax.servlet.{DispatcherType, Filter, FilterChain, ServletOutputStream, ServletRequest, ServletResponse, WriteListener}
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/**
 * Covers cancellation of the handler `Task` when the client abandons a request, and how the servlet bounds a call by
 * its timeout.
 *
 * The server speaks both HTTP/1.1 and cleartext HTTP/2, because disconnect detection differs between them:
 * an HTTP/2 abort arrives as `RST_STREAM` on a connection the server is already reading and surfaces as
 * `AsyncListener.onError`, while an aborted HTTP/1.1 request goes unnoticed until the call times out.
 */
class RestServletDisconnectTest extends UdashSharedTest with UsesHttpServer {

  private val HandleTimeout = 8.seconds // long enough to tell a disconnect apart from a timeout
  private val ShortHandleTimeout = 1.second

  private val started = new ConcurrentHashMap[String, CountDownLatch]
  private val cancelled = new ConcurrentHashMap[String, CountDownLatch]
  private val finished = new ConcurrentHashMap[String, CountDownLatch]
  private val writing = new ConcurrentHashMap[String, CountDownLatch]
  private val uncaughtErrors = new ConcurrentLinkedQueue[Throwable]
  private val logEvents = new ConcurrentLinkedQueue[SubstituteLoggingEvent]
  private val errorDispatches = new ConcurrentLinkedQueue[String]
  private val channelStates = new ConcurrentHashMap[String, HttpChannelState]

  private implicit val scheduler: Scheduler = Scheduler(
    Scheduler.global,
    UncaughtExceptionReporter(t => { uncaughtErrors.add(t); () }),
    ExecutionModel.Default,
  )

  private def latch(latches: ConcurrentHashMap[String, CountDownLatch], scenario: String): CountDownLatch =
    latches.computeIfAbsent(scenario, _ => new CountDownLatch(1))

  // The path is "<behaviour>/<scenario>": "ping" answers immediately, "finishing" answers with a non-empty body
  // a second later, "uncancelable" does the same but ignores cancellation, "stream" streams chunks until stopped,
  // "uncancelable-stream" starts streaming a second later ignoring cancellation, "eof" and "bad-status" fail
  // and anything else hangs until cancelled. Each scenario reports its progress through latches.
  // For the timeout scenarios: "finite-stream" streams chunks for about 3 seconds, "two-chunks" streams two at once,
  // "idle-stream" goes silent after one chunk, "silent-stream" and "silent-list" never emit, "failing-list" stages
  // headers, one of them also set by the filter, and fails with 400, "paced-list" emits six numbers 300 ms apart, "slow-uncancelable-stream" starts streaming 3 seconds later
  // ignoring cancellation, and "own-timeout" fails with the stream's own upstream timeout, before or after a chunk.
  // "partial-list" goes silent after two numbers, less than a batch, and "late-silent-stream" returns a silent stream
  // half a second later.
  private def chunk: Array[Byte] = Array.fill[Byte](64)('x')

  private val handleRequest: RawRest.HandleRequestWithStreaming = request => {
    val path = request.parameters.path.map(_.value)
    val scenario = path.last
    def report(latches: ConcurrentHashMap[String, CountDownLatch]): Task[Unit] =
      Task(latch(latches, scenario).countDown())
    def lateResponse: Task[AbstractRestResponse] =
      Task.sleep(1.second)
        .map(_ => RestResponse(200, IMapping.empty, HttpBody.plain("finished after the client left")))
        .doOnFinish(_ => report(finished))
        .doOnCancel(report(cancelled))
    def finishing[A](content: Observable[A]): Observable[A] =
      content.guaranteeCase(_ => report(finished))
    // "started" fires with the first chunk written, so an abort lands mid-stream
    def chunks: Observable[Array[Byte]] = finishing(Observable.intervalAtFixedRate(20.millis)
      .map(_ => chunk)
      .doOnStart(_ => report(started)))
    def binary(content: Observable[Array[Byte]]): AbstractRestResponse =
      StreamedRestResponse(200, IMapping.empty, StreamedBody.RawBinary(content, "application/octet-stream"))
    def jsonList(elements: Observable[Int], headers: IMapping[PlainValue] = IMapping.empty): AbstractRestResponse =
      StreamedRestResponse(200, headers, StreamedBody.JsonList(elements.map(i => JsonValue(i.toString))))
    def ownTimeout: Observable[Nothing] = Observable.raiseError(UpstreamTimeoutException(ShortHandleTimeout))
    path.headOption match {
      case Some("ping") =>
        report(started).map(_ => RestResponse(204, IMapping.empty, HttpBody.Empty))
      case Some("finishing") =>
        report(started).flatMap(_ => lateResponse)
      case Some("uncancelable") =>
        report(started).flatMap(_ => lateResponse.uncancelable)
      case Some("stream") =>
        Task.now(binary(chunks))
      case Some("uncancelable-stream") =>
        report(started).flatMap(_ => Task.sleep(1.second).map(_ => binary(chunks)).uncancelable)
      case Some("finite-stream") =>
        Task.now(binary(chunks.take(150)))
      case Some("two-chunks") =>
        Task.now(binary(finishing(Observable(chunk, chunk))))
      case Some("idle-stream") =>
        Task.now(binary(finishing(Observable(chunk) ++ Observable.never)))
      case Some("silent-stream") =>
        Task.now(binary(finishing(Observable.never)))
      case Some("silent-list") =>
        Task.now(jsonList(finishing(Observable.never)))
      case Some("failing-list") =>
        Task.now(jsonList(Observable.raiseError(HttpErrorException.plain(400, "bad list")),
          IMapping.create("X-Stream" -> PlainValue("yes"), "X-Filter" -> PlainValue("staged"))))
      case Some("paced-list") =>
        Task.now(jsonList(Observable.intervalAtFixedRate(300.millis).map(_.toInt + 1).take(6)))
      case Some("slow-uncancelable-stream") =>
        report(started).flatMap(_ => Task.sleep(3.seconds).map(_ => binary(chunks)).uncancelable)
      case Some("partial-list") =>
        Task.now(jsonList(Observable(1, 2) ++ Observable.never))
      case Some("late-silent-stream") =>
        report(started).flatMap(_ => Task.sleep(500.millis).map(_ => binary(finishing(Observable.never))))
      case Some("own-timeout") =>
        Task.now(binary(ownTimeout))
      case Some("own-timeout-after-chunk") =>
        Task.now(binary(Observable(chunk) ++ ownTimeout))
      case Some("eof") =>
        Task.raiseError(new EOFException("downstream service closed the connection"))
      case Some("bad-status") =>
        Task.raiseError(HttpErrorException.plain(1000, "not a status code"))
      case _ =>
        report(started).flatMap(_ => Task.never[AbstractRestResponse].doOnCancel(report(cancelled)))
    }
  }

  protected def setupServer(server: Server): Unit = {
    val connector = server.getConnectors.head.asInstanceOf[ServerConnector]
    val httpConfig = connector.getConnectionFactory(classOf[HttpConnectionFactory]).getHttpConfiguration
    connector.addConnectionFactory(new HTTP2CServerConnectionFactory(httpConfig))

    def servlet(handleTimeout: FiniteDuration, cancelOnDisconnect: Boolean): ServletHolder =
      new ServletHolder(new RestServlet(handleRequest, handleTimeout, customLogger = servletLogger, cancelOnDisconnect = cancelOnDisconnect))

    val handler = new ServletContextHandler()
    handler.setErrorHandler(new ErrorHandler {
      override def handle(target: String, baseRequest: JettyRequest, request: HttpServletRequest, response: HttpServletResponse): Unit = {
        errorDispatches.add(s"${request.getDispatcherType} $target -> ${response.getStatus}")
        super.handle(target, baseRequest, request, response)
      }
    })
    handler.addServlet(servlet(HandleTimeout, cancelOnDisconnect = true), "/api/*")
    handler.addServlet(servlet(HandleTimeout, cancelOnDisconnect = false), "/no-cancel-api/*")
    handler.addServlet(servlet(ShortHandleTimeout, cancelOnDisconnect = true), "/short-timeout-api/*")
    handler.addServlet(servlet(ShortHandleTimeout, cancelOnDisconnect = false), "/short-no-cancel-api/*")
    handler.addFilter(new FilterHolder(new Filter {
      def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
        val scenario = request.asInstanceOf[HttpServletRequest].getRequestURI.split('/').last
        channelStates.put(scenario, JettyRequest.getBaseRequest(request).getHttpChannelState)
        chain.doFilter(request, response)
      }
    }), "/*", EnumSet.of(DispatcherType.REQUEST))
    for ((handleTimeout, mount) <- UnboundedMounts)
      handler.addServlet(servlet(handleTimeout, cancelOnDisconnect = true), s"/$mount/*")
    // stands for a filter in front of the servlet that sets a header, e.g. for CORS
    val headerFilter = new FilterHolder(new Filter {
      def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
        response.asInstanceOf[HttpServletResponse].setHeader("X-Filter", "kept")
        chain.doFilter(request, response)
      }
    })
    headerFilter.setAsyncSupported(true)
    handler.addFilter(headerFilter, "/*", EnumSet.of(DispatcherType.REQUEST))
    handler.addServlet(new ServletHolder(slowWrites(new RestServlet(handleRequest, SlowWriteTimeout, customLogger = servletLogger))),
      "/slow-write-api/*")
    handler.addServlet(new ServletHolder(new RestServlet(blockingHandler, ShortHandleTimeout, customLogger = servletLogger,
      timer = Scheduler(blockedTimer))(blockingScheduler)), "/blocked-api/*")
    handler.addServlet(new ServletHolder(blockedCancelServlet), "/blocked-cancel-api/*")
    handler.addServlet(new ServletHolder(slowWrites(new RestServlet(handleRequest, SlowWriteTimeout, customLogger = servletLogger)(pooledScheduler))),
      "/pooled-slow-write-api/*")
    handler.addServlet(new ServletHolder(failingFirstWrite(new RestServlet(handleRequest, HandleTimeout, customLogger = servletLogger))),
      "/failing-write-api/*")
    server.setHandler(handler)
  }

  private val servletLogger = new SubstituteLogger(classOf[RestServlet].getName, logEvents, false)

  private val UnboundedMounts =
    List(Duration.Zero -> "unbounded-api", -1.second -> "negative-timeout-api", 500.micros -> "sub-milli-timeout-api")

  private val SlowWriteTimeout = 300.millis

  // Runs `beforeWrite` ahead of each write of an array, with the scenario and how many such writes came before
  private def interceptWrites(servlet: RestServlet)(beforeWrite: (String, Int) => Unit): HttpServlet = new HttpServlet {
    override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
      val scenario = request.getRequestURI.split('/').last
      servlet.service(request, new HttpServletResponseWrapper(response) {
        private lazy val output = new ServletOutputStream {
          private val underlying = response.getOutputStream
          private val writes = new AtomicInteger
          def isReady: Boolean = underlying.isReady
          def setWriteListener(listener: WriteListener): Unit = underlying.setWriteListener(listener)
          def write(b: Int): Unit = underlying.write(b)
          override def write(b: Array[Byte], off: Int, len: Int): Unit = {
            beforeWrite(scenario, writes.getAndIncrement())
            underlying.write(b, off, len)
          }
          override def flush(): Unit = underlying.flush()
          override def close(): Unit = underlying.close()
        }
        override def getOutputStream: ServletOutputStream = output
      })
    }
  }

  // Each write takes over three times SlowWriteTimeout, as if the client read slowly
  private def slowWrites(servlet: RestServlet): HttpServlet = interceptWrites(servlet) { (scenario, _) =>
    latch(writing, scenario).countDown()
    Thread.sleep(1000)
  }

  // As if the container failed to buffer the first write
  private def failingFirstWrite(servlet: RestServlet): HttpServlet = interceptWrites(servlet) { (_, written) =>
    if (written == 0) throw new IOException("write rejected")
  }

  private val blockingScheduler = Scheduler.singleThread("rest-servlet-test-blocked")
  private val pooledScheduler = Scheduler.fixedPool("rest-servlet-test-pooled", 2)
  // dedicated, as the blocking handler takes the only thread of blockingScheduler
  private val blockedTimer = timerExecutor()
  private val blockedCancelTimer = timerExecutor()

  private def timerExecutor(): ScheduledThreadPoolExecutor = {
    val executor = new ScheduledThreadPoolExecutor(1)
    // so that the queue holds only the pending timeouts
    executor.setRemoveOnCancelPolicy(true)
    executor
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    blockingScheduler.shutdown()
    pooledScheduler.shutdown()
    blockedTimer.shutdown()
    blockedCancelTimer.shutdown()
  }

  // Blocks the only thread of its Scheduler for 4 seconds
  private val blockingHandler: RawRest.HandleRequestWithStreaming = request => Task.eval {
    val scenario = request.parameters.path.last.value
    latch(started, scenario).countDown()
    Thread.sleep(4000)
    latch(finished, scenario).countDown()
    RestResponse(204, IMapping.empty, HttpBody.Empty)
  }

  private lazy val blockedCancelServlet =
    new RestServlet(blockingHandler, HandleTimeout, customLogger = servletLogger, cancelOnDisconnect = true,
      timer = Scheduler(blockedCancelTimer))(blockingScheduler)

  /**
   * Waits for the handler to start and for the container to return from dispatching the request. Jetty hands a failure
   * of the request to the `AsyncListener`s only once the dispatch has returned and drops one arriving before that, so
   * an abort sent while the servlet is still in `service` would go unnoticed until `handleTimeout`. Any state past
   * `HANDLING` counts, since a short `handleTimeout` may already have woken the request again.
   */
  private def awaitStarted(path: String): Unit = {
    val scenario = path.split('/').last
    assert(latch(started, scenario).await(4, TimeUnit.SECONDS), s"handler for $path never started")
    val deadline = 4.seconds.fromNow
    while (channelStates.get(scenario).getState == HttpChannelState.State.HANDLING) {
      assert(deadline.hasTimeLeft(), s"dispatch of $path never returned")
      Thread.sleep(5)
    }
  }

  private lazy val http2Client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()

  private def http2Request(path: String): HttpRequest =
    HttpRequest.newBuilder(URI.create(s"$baseUrl/$path")).GET().build()

  private lazy val http1Client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()

  // A cleartext HTTP/2 connection starts as an HTTP/1.1 upgrade, so make sure the pooled connection already speaks h2
  private def warmUpHttp2(): Unit = {
    val ping = http2Client.send(http2Request("api/ping/warmup"), HttpResponse.BodyHandlers.discarding())
    assert(ping.version() == HttpClient.Version.HTTP_2, s"expected an HTTP/2 connection, got ${ping.version()}")
  }

  /** Sends a GET over "h1" or "h2" and waits for the whole response. */
  private def fetch[T](protocol: String, path: String, body: HttpResponse.BodyHandler[T]): HttpResponse[T] = {
    val (client, version) =
      if (protocol == "h2") { warmUpHttp2(); (http2Client, HttpClient.Version.HTTP_2) }
      else (http1Client, HttpClient.Version.HTTP_1_1)
    val response = client.sendAsync(http2Request(path), body).get(8, TimeUnit.SECONDS)
    assert(response.version() == version)
    response
  }

  private def timedOut(timeout: FiniteDuration): String = s"server operation timed out after $timeout"

  /** Sends a request over HTTP/2, waits for the handler to start, then aborts it - resetting the stream. */
  private def abortHttp2Request(path: String): Unit = {
    // aborting before the upgrade completes would close the connection instead of resetting a stream
    warmUpHttp2()

    val response = http2Client.sendAsync(http2Request(path), HttpResponse.BodyHandlers.discarding())
    awaitStarted(path)
    assert(response.cancel(true), "request already completed, nothing was aborted")
  }

  /**
   * Starts a request on an HTTP/2 connection opened with prior knowledge, waits for the handler, then shuts down the
   * client's output. The socket stays open while `check` runs, since closing it with the server's frames unread would
   * reset the connection instead of closing it.
   */
  private def dropHttp2Connection(path: String)(check: => Unit): Unit = {
    val generator = new Generator(new ByteBufferPool.NonPooling)
    val frames = new RetainableByteBuffer.DynamicCapacity()
    generator.control(frames, new PrefaceFrame)
    generator.control(frames, new SettingsFrame(new java.util.HashMap, false))
    val request = new MetaData.Request("GET", HttpURI.from(s"$baseUrl/$path"), HttpVersion.HTTP_2, HttpFields.EMPTY)
    generator.control(frames, new HeadersFrame(1, request, null, true))
    val socket = new Socket("localhost", port)
    try {
      socket.getOutputStream.write(frames.takeByteArray())
      socket.getOutputStream.flush()
      awaitStarted(path)
      socket.shutdownOutput()
      check
    } finally socket.close()
  }

  /** Sends a request over HTTP/1.1, waits for the handler to start, then closes the socket abruptly (TCP RST). */
  private def abortHttp1Request(path: String): Unit = {
    val socket = new Socket("localhost", port)
    socket.setSoLinger(true, 0)
    try {
      val out = socket.getOutputStream
      out.write(s"GET /$path HTTP/1.1\r\nHost: localhost\r\n\r\n".getBytes("utf-8"))
      out.flush()
      awaitStarted(path)
    } finally socket.close()
  }

  private def assertNoFailures(minLevel: Level = Level.WARN): Unit = {
    val errors = uncaughtErrors.asScala.toList
    assert(errors.isEmpty, errors.map(e => s"${e.getClass.getName}: ${e.getMessage}").mkString(", "))
    val problems = logEvents.asScala.filter(_.getLevel.toInt >= minLevel.toInt).toList
    assert(problems.isEmpty, problems.map(e => s"${e.getLevel} ${e.getMessage}: ${e.getThrowable}").mkString(", "))
  }

  private def resetFailures(): Unit = {
    uncaughtErrors.clear()
    logEvents.clear()
  }

  private def formatted(event: SubstituteLoggingEvent): String =
    MessageFormatter.arrayFormat(event.getMessage, event.getArgumentArray).getMessage

  // Takes the warning of a call that timed out before it responded, which is expected, out of the log
  private def takeTimeoutWarning(message: String): Unit = {
    val warnings = logEvents.asScala.filter(e => e.getLevel == Level.WARN && e.getMessage.startsWith("REST request timed out")).toList
    assert(warnings.map(formatted) == List(message), logEvents.asScala.map(e => s"${e.getLevel} ${formatted(e)}").mkString(", "))
    warnings.foreach(logEvents.remove)
  }

  private def awaitUntil(what: String, within: FiniteDuration = 4.seconds)(condition: => Boolean): Unit = {
    val deadline = within.fromNow
    while (!condition && deadline.hasTimeLeft()) Thread.sleep(10)
    assert(condition, what)
  }

  // Lets the servlet's own completion, which follows the handler's, run before inspecting side effects
  private def settle(): Unit = Thread.sleep(500)

  "RestServlet" should {
    "cancel the handler task when an HTTP/2 client aborts the request" in {
      abortHttp2Request("api/hanging/h2-abort")
      assert(latch(cancelled, "h2-abort").await(4, TimeUnit.SECONDS))
    }

    "let the handler task finish when cancelOnDisconnect is disabled" in {
      resetFailures()
      abortHttp2Request("no-cancel-api/finishing/h2-abort-disabled")
      assert(latch(finished, "h2-abort-disabled").await(4, TimeUnit.SECONDS), "handler never finished")
      assert(!latch(cancelled, "h2-abort-disabled").await(0, TimeUnit.SECONDS), "handler was cancelled")
      settle()
      assertNoFailures()
    }

    "complete the aborted request itself instead of leaving it to the container's error dispatch" in {
      errorDispatches.clear()
      abortHttp2Request("api/hanging/h2-abort-no-error-page")
      assert(latch(cancelled, "h2-abort-no-error-page").await(4, TimeUnit.SECONDS))
      settle()
      assert(errorDispatches.isEmpty, errorDispatches.asScala.mkString(", "))
    }

    "cancel the handler task and complete the request itself when the HTTP/2 connection is dropped" in {
      resetFailures()
      errorDispatches.clear()
      dropHttp2Connection("api/hanging/h2-drop") {
        assert(latch(cancelled, "h2-drop").await(4, TimeUnit.SECONDS))
        settle()
      }
      assert(errorDispatches.isEmpty, errorDispatches.asScala.mkString(", "))
      assertNoFailures()
    }

    "not write into or complete the recycled request when an uncancelable handler finishes after the abort" in {
      resetFailures()
      abortHttp2Request("api/uncancelable/late-response")
      assert(latch(finished, "late-response").await(4, TimeUnit.SECONDS), "handler never finished")
      settle()
      assertNoFailures()
    }

    "stop a streamed response when the client aborts" in {
      resetFailures()
      abortHttp2Request("api/stream/h2-abort-stream")
      assert(latch(finished, "h2-abort-stream").await(4, TimeUnit.SECONDS), "stream was never stopped")
      settle()
      // a chunk write racing the abort is reported at warn, which is expected
      assertNoFailures(minLevel = Level.ERROR)
    }

    "stop a streamed response when the client aborts and cancelOnDisconnect is disabled" in {
      resetFailures()
      abortHttp2Request("no-cancel-api/stream/h2-abort-stream-disabled")
      assert(latch(finished, "h2-abort-stream-disabled").await(4, TimeUnit.SECONDS), "stream was never stopped")
      settle()
      assertNoFailures(minLevel = Level.ERROR)
    }

    "run the finalizers of a stream returned after the abort without writing it" in {
      resetFailures()
      abortHttp2Request("api/uncancelable-stream/late-stream")
      assert(latch(finished, "late-stream").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
      settle()
      assertNoFailures()
    }

    for (protocol <- List("h1", "h2")) {
      s"complete a stream that runs past its timeout without going idle over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/finite-stream/finite-$protocol", HttpResponse.BodyHandlers.ofByteArray())
        assert(response.statusCode() == 200)
        assert(response.body().length == 150 * 64)
        assert(latch(finished, s"finite-$protocol").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
        settle()
        assertNoFailures()
      }

      s"end a stream that goes idle at its timeout over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/idle-stream/idle-$protocol", HttpResponse.BodyHandlers.ofByteArray())
        assert(response.statusCode() == 200)
        assert(response.body().length == 64)
        assert(latch(finished, s"idle-$protocol").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
        settle()
        assertNoFailures(minLevel = Level.ERROR)
        val cutShort = logEvents.asScala.filter(e => e.getLevel == Level.WARN && e.getMessage.contains("cut short"))
        assert(cutShort.size == 1, logEvents.asScala.map(e => s"${e.getLevel} ${e.getMessage}").mkString(", "))
      }

      s"answer with 500 when a stream emits nothing within its timeout over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/silent-stream/silent-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == timedOut(ShortHandleTimeout))
        assert(response.headers().firstValue("X-Filter").orElse(null) == "kept")
        assert(latch(finished, s"silent-$protocol").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
        settle()
        takeTimeoutWarning(s"REST request timed out: its stream emitted nothing for $ShortHandleTimeout before the response was sent")
        assertNoFailures()
      }

      s"answer with 500 when a JSON list emits nothing within its timeout over $protocol" in {
        val response = fetch(protocol, s"short-timeout-api/silent-list/silent-list-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == timedOut(ShortHandleTimeout))
      }

      s"answer with 500 when a JSON list goes silent before filling its first batch over $protocol" in {
        val response = fetch(protocol, s"short-timeout-api/partial-list/partial-list-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == timedOut(ShortHandleTimeout))
      }

      s"answer with the error status when a JSON list fails before its first flush over $protocol" in {
        val response = fetch(protocol, s"short-timeout-api/failing-list/failing-list-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 400)
        assert(response.body() == "bad list")
        assert(response.headers().allValues("X-Filter").asScala == List("kept"))
        assert(!response.headers().firstValue("X-Stream").isPresent, "the failed stream's header was kept")
      }

      s"complete a batched JSON list whose elements come within the timeout over $protocol" in {
        val response = fetch(protocol, s"short-timeout-api/paced-list/paced-list-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 200)
        assert(response.body() == "[1,2,3,4,5,6]")
      }

      s"answer with 500 and cancel a buffered call that exceeds its timeout over $protocol" in {
        val response = fetch(protocol, s"short-timeout-api/hanging/buffered-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == timedOut(ShortHandleTimeout))
        assert(latch(cancelled, s"buffered-$protocol").await(4, TimeUnit.SECONDS), "handler was never cancelled")
      }

      s"run the finalizers of a stream returned after the timeout over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/slow-uncancelable-stream/late-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == timedOut(ShortHandleTimeout))
        assert(latch(finished, s"late-$protocol").await(6, TimeUnit.SECONDS), "stream finalizer never ran")
        settle()
        takeTimeoutWarning(s"REST request timed out: no response within $ShortHandleTimeout")
        assertNoFailures()
      }

      s"not count the time spent writing towards a stream's timeout over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"slow-write-api/two-chunks/slow-write-$protocol", HttpResponse.BodyHandlers.ofByteArray())
        assert(response.statusCode() == 200)
        assert(response.body().length == 2 * 64)
        assert(latch(finished, s"slow-write-$protocol").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
        settle()
        assertNoFailures()
      }

      s"answer with the stream's own upstream timeout as a handler failure over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/own-timeout/own-timeout-$protocol", HttpResponse.BodyHandlers.ofString())
        assert(response.statusCode() == 500)
        assert(response.body() == s"Upstream timeout after $ShortHandleTimeout")
        settle()
        assert(logEvents.asScala.exists(e => e.getLevel == Level.ERROR && e.getMessage.contains("Failed to handle")))
      }

      s"end a stream that fails with its own upstream timeout after a chunk as a failure over $protocol" in {
        resetFailures()
        val response = fetch(protocol, s"short-timeout-api/own-timeout-after-chunk/own-timeout-chunk-$protocol",
          HttpResponse.BodyHandlers.ofByteArray())
        assert(response.statusCode() == 200)
        assert(response.body().length == 64)
        settle()
        assert(logEvents.asScala.exists(e => e.getLevel == Level.ERROR && e.getMessage.contains("Failure during streaming")))
        assert(!logEvents.asScala.exists(_.getMessage.contains("cut short")))
      }
    }

    "answer with 500 at the timeout even while the handler's Scheduler is saturated" in {
      val response = fetch("h1", "blocked-api/blocking/saturated", HttpResponse.BodyHandlers.ofString())
      assert(response.statusCode() == 500)
      assert(response.body() == timedOut(ShortHandleTimeout))
      assert(latch(finished, "saturated").getCount == 1, "the 500 waited for the handler")
      assert(latch(finished, "saturated").await(6, TimeUnit.SECONDS))
    }

    "drain a silent stream returned after its client left until it goes idle" in {
      resetFailures()
      abortHttp2Request("short-no-cancel-api/late-silent-stream/drained-idle")
      assert(latch(finished, "drained-idle").await(4, TimeUnit.SECONDS), "stream finalizer never ran")
      settle()
      assertNoFailures()
      assert(logEvents.asScala.exists(e => e.getLevel == Level.DEBUG && e.getMessage.contains("went idle while being drained")))
    }

    "stop timing a call whose client left before it started" in {
      warmUpHttp2()
      val blocker = http1Client.sendAsync(http2Request("blocked-cancel-api/blocking/queue-blocker"), HttpResponse.BodyHandlers.discarding())
      awaitStarted("queue-blocker")
      val queued = http2Client.sendAsync(http2Request("blocked-cancel-api/blocking/queued"), HttpResponse.BodyHandlers.discarding())
      awaitUntil("the queued call was never timed")(blockedCancelTimer.getQueue.size == 2)
      assert(queued.cancel(true), "request already completed, nothing was aborted")
      // well before the blocker ends, which drops its own timeout
      awaitUntil("the abandoned call's timeout is still pending", within = 2.seconds)(blockedCancelTimer.getQueue.size == 1)
      assert(blocker.get(8, TimeUnit.SECONDS).statusCode() == 204)
      awaitUntil("a timeout outlived its call")(blockedCancelTimer.getQueue.size == 0)
      assert(latch(started, "queued").getCount == 1, "the abandoned call started")
    }

    "keep a Scheduler thread free while a stream's idle check waits for a slow write" in {
      val stream = http1Client.sendAsync(http2Request("pooled-slow-write-api/two-chunks/pooled-slow-write"),
        HttpResponse.BodyHandlers.ofByteArray())
      assert(latch(writing, "pooled-slow-write").await(4, TimeUnit.SECONDS), "the stream was never written")
      // past SlowWriteTimeout, so that the idle check runs during the first write
      Thread.sleep(SlowWriteTimeout.toMillis + 200)
      val start = System.nanoTime()
      val ping = fetch("h1", "pooled-slow-write-api/ping/pooled-ping", HttpResponse.BodyHandlers.discarding())
      val waited = (System.nanoTime() - start).nanos
      assert(ping.statusCode() == 204)
      assert(waited < 300.millis, s"the call waited $waited for a Scheduler thread")
      assert(stream.get(8, TimeUnit.SECONDS).body().length == 2 * 64)
    }

    "answer with the failure when a stream's first write fails before the response is committed" in {
      val response = fetch("h1", "failing-write-api/two-chunks/failing-write", HttpResponse.BodyHandlers.ofString())
      assert(response.statusCode() == 500)
      assert(response.body() == "write rejected")
    }

    "set no bound for a handleTimeout under a millisecond, as for the container's own timeout" in {
      for (handleTimeout <- List(Duration.Zero, -1.second, 999.micros))
        assert(RestServlet.handleTimeoutBound(handleTimeout).isEmpty, handleTimeout)
      for (handleTimeout <- List(1.millisecond, 1500.micros, RestServlet.DefaultHandleTimeout))
        assert(RestServlet.handleTimeoutBound(handleTimeout).contains(handleTimeout), handleTimeout)
    }

    for ((handleTimeout, mount) <- UnboundedMounts)
      s"not bound calls or streams when handleTimeout is $handleTimeout" in {
        val buffered = fetch("h1", s"$mount/finishing/unbounded-buffered-$mount", HttpResponse.BodyHandlers.ofString())
        assert(buffered.statusCode() == 200)
        assert(buffered.body() == "finished after the client left")
        val list = fetch("h1", s"$mount/paced-list/unbounded-list-$mount", HttpResponse.BodyHandlers.ofString())
        assert(list.statusCode() == 200)
        assert(list.body() == "[1,2,3,4,5,6]")
      }

    "cancel an abandoned call at its timeout when cancelOnDisconnect is disabled" in {
      abortHttp2Request("short-no-cancel-api/hanging/h2-abort-abandoned")
      assert(latch(cancelled, "h2-abort-abandoned").await(4, TimeUnit.SECONDS), "handler was never cancelled")
    }

    "answer with 500 when the handler itself fails with EOFException" in {
      val response = http2Client.send(http2Request("api/eof/handler-eof"), HttpResponse.BodyHandlers.ofString())
      assert(response.statusCode() == 500)
      assert(response.body() == "downstream service closed the connection")
    }

    "answer with 500 when the error status is not a valid HTTP status" in {
      val response = http2Client.send(http2Request("api/bad-status/status-1000"), HttpResponse.BodyHandlers.ofString())
      assert(response.statusCode() == 500)
    }

    // Jetty does not read an HTTP/1.1 connection while the async cycle is idle, so the abort itself goes
    // unnoticed and the handler is only bounded by its timeout.
    "cancel the handler of an aborted HTTP/1.1 request once its timeout elapses" in {
      abortHttp1Request("short-timeout-api/hanging/h1-abort")
      assert(latch(cancelled, "h1-abort").await(4, TimeUnit.SECONDS))
    }
  }
}
