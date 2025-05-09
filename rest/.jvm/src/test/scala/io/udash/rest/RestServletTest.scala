package io.udash
package rest

import com.avsystem.commons.*
import monix.execution.Scheduler
import monix.reactive.Observable
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}

import java.io.*
import javax.servlet.AsyncContext
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.concurrent.Future

class RestServletTest extends AnyFunSuite with ScalaFutures with Matchers with BeforeAndAfterEach with Eventually {
  implicit def scheduler: Scheduler = Scheduler.global

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(
    timeout = Span(5, Seconds),
    interval = Span(50, Millis)
  )

  var request: HttpServletRequest = _
  var response: HttpServletResponse = _
  var asyncContext: AsyncContext = _
  var outputStream: ByteArrayOutputStream = _
  var writer: StringWriter = _
  var printWriter: PrintWriter = _
  var servlet: RestServlet = _

  override def beforeEach(): Unit = {
    super.beforeEach()

    request = mock(classOf[HttpServletRequest])
    response = mock(classOf[HttpServletResponse])
    asyncContext = mock(classOf[AsyncContext])
    outputStream = new ByteArrayOutputStream()
    writer = new StringWriter()
    printWriter = new PrintWriter(writer)

    when(request.startAsync()).thenReturn(asyncContext)
    when(response.getOutputStream).thenReturn(spy(new ServletOutputStreamMock(outputStream)))
    when(response.getWriter).thenReturn(printWriter)

    servlet = RestServlet[TestApi](new TestApiImpl(),
      maxPayloadSize = 1024 * 1024
    )
  }

  def setupRequest(
    method: String = "GET",
    path: String,
    contentType: String = null,
    body: String = "",
    queryString: String = null,
    headers: Map[String, String] = Map.empty,
    cookies: Array[javax.servlet.http.Cookie] = null
  ): Unit = {
    when(request.getMethod).thenReturn(method)
    when(request.getRequestURI).thenReturn(path)
    when(request.getQueryString).thenReturn(queryString)
    when(request.getContextPath).thenReturn("")
    when(request.getServletPath).thenReturn("")
    when(request.getContentType).thenReturn(contentType)

    if (contentType != null && body.nonEmpty) {
      when(request.getContentLengthLong).thenReturn(body.getBytes.length.toLong)
      when(request.getReader).thenReturn(new BufferedReader(new StringReader(body)))
    } else {
      when(request.getContentLengthLong).thenReturn(-1L)
    }

    if (headers.isEmpty) {
      when(request.getHeaderNames).thenReturn(java.util.Collections.emptyEnumeration())
    } else {
      val headerNames = java.util.Collections.enumeration(headers.keys.toList.asJava)
      when(request.getHeaderNames).thenReturn(headerNames)
      headers.foreach { case (name, value) =>
        when(request.getHeader(name)).thenReturn(value)
      }
    }

    when(request.getCookies).thenReturn(cookies)
  }

  def setupGetRequest(path: String, queryString: String = null): Unit = {
    setupRequest(path = path, queryString = queryString)
  }

  def setupPostRequest(path: String, contentType: String, body: String): Unit = {
    setupRequest(method = "POST", path = path, contentType = contentType, body = body)
  }

  def verifyResponse(expectedStatus: Int, expectedContentType: Opt[String] = Opt.empty): Unit = {
    verify(response).setStatus(expectedStatus)
    expectedContentType.foreach { contentType =>
      verify(response).setContentType(argThat((argument: String) => argument.startsWith(contentType)))
    }
    verify(asyncContext).complete()
  }

  test("GET request should return simple response") {
    setupGetRequest("/hello", "name=TestUser")

    servlet.service(request, response)
    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("Hello, TestUser")
    }
  }

  test("POST request should process JSON body") {
    setupPostRequest(
      "/echo",
      "application/json;charset=utf-8",
      """{"message":"Hello World"}"""
    )

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("""Hello World""")
    }
  }

  test("Binary streaming should work correctly") {
    setupGetRequest("/binary", "size=10")

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/octet-stream"))
      val bytes = outputStream.toByteArray
      bytes.length should be(10)
      outputStream.toString shouldEqual "A".repeat(10)
    }
  }

  test("Error response should be handled properly") {
    setupGetRequest("/error")

    servlet.service(request, response)

    eventually {
      verifyResponse(500, Opt("text/plain"))
      writer.toString should include("Test error")
    }
  }

  test("PUT request should update a resource") {
    setupRequest(
      method = "PUT",
      path = "/update/123",
      contentType = "application/json;charset=utf-8",
      body = """{"data":"new content"}"""
    )

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("Updated 123")
    }
  }

  test("DELETE request should remove a resource") {
    setupRequest(method = "DELETE", path = "/remove/123")

    servlet.service(request, response)

    eventually {
      verifyResponse(204)
    }
  }

  test("Form body should be processed correctly") {
    setupRequest(
      method = "POST",
      path = "/form",
      contentType = "application/x-www-form-urlencoded;charset=utf-8",
      body = "name=John%20Doe&age=30",
      queryString = "id=user123"
    )

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("Form: user123, John Doe, 30")
    }
  }

  test("Empty streaming response should be handled correctly") {
    setupGetRequest("/emptyStream")

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString shouldEqual "[]"
    }
  }

  test("Cookie values should be processed correctly") {
    val cookies = Array(new javax.servlet.http.Cookie("sessionId", "abc123"))
    setupRequest(path = "/withCookie", cookies = cookies)

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("Session: abc123")
    }
  }

  test("Request headers should be processed correctly") {
    setupRequest(
      path = "/withHeader",
      headers = Map("X-Custom" -> "test-value")
    )

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString should include("Header: test-value")
    }
  }

  test("JSON streaming response should be delivered in chunks") {
    setupGetRequest("/jsonBatched", "count=5")

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      val output = outputStream.toString("UTF-8")
      output shouldEqual "[{\"value\":1},{\"value\":2},{\"value\":3},{\"value\":4},{\"value\":5}]"
    }
  }

  test("Streaming error should be handled properly") {
    setupGetRequest("/streamError", "failAt=3")

    servlet.service(request, response)

    eventually {
      verifyResponse(200, Opt("application/json"))
      outputStream.toString shouldEqual "[\"item-1\",\"item-2\""
      // closes HTTP connection on failure
      verify(response.getOutputStream).close()
    }
  }

  test("Request exceeding payload limit should return 413") {
    val smallServlet = RestServlet[TestApi](new TestApiImpl(), maxPayloadSize = 10)

    setupPostRequest("/echo", "application/json", "{\"message\":\"This payload is too long and should fail\"}")

    smallServlet.service(request, response)

    eventually {
      verify(response).setStatus(413)
      verify(asyncContext).complete()
    }
  }
}

class ServletOutputStreamMock(baos: ByteArrayOutputStream) extends javax.servlet.ServletOutputStream {
  def write(b: Int): Unit = baos.write(b)
  def isReady: Boolean = true
  def setWriteListener(writeListener: javax.servlet.WriteListener): Unit = {}
}

trait TestApi {
  @GET def hello(@Query name: String): Future[String]
  @POST def echo(message: String): Future[String]
  @PUT def update(@Path id: String, data: String): Future[String]
  @DELETE def remove(@Path id: String): Future[Unit]
  @GET def binary(@Query size: Int): Observable[Array[Byte]]
  @GET def error: Future[String]
  @streamingResponseBatchSize(1)
  @GET def streamError(@Query failAt: Int): Observable[String]
  @POST @FormBody def form(@Query id: String, name: String, age: Int): Future[String]
  @GET def emptyStream: Observable[String]
  @GET def longRunning(@Query seconds: Int): Future[String]
  @GET def withCookie(@Cookie sessionId: String): Future[String]
  @GET def withHeader(@Header("X-Custom") custom: String): Future[String]
  @GET def jsonBatched(@Query count: Int): Observable[Map[String, Int]]
}
object TestApi extends DefaultRestApiCompanion[TestApi]

class TestApiImpl extends TestApi {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def hello(name: String): Future[String] =
    Future.successful(s"Hello, $name")

  def echo(message: String): Future[String] =
    Future.successful(message)

  def update(id: String, data: String): Future[String] =
    Future.successful(s"Updated $id with $data")

  def remove(id: String): Future[Unit] =
    Future.successful(())

  @streamingResponseBatchSize(1)
  def numbers(count: Int): Observable[Int] =
    Observable.range(1, count + 1).map(_.toInt)

  def binary(size: Int): Observable[Array[Byte]] = {
    val chunk = "A".repeat(size).getBytes
    Observable.pure(chunk)
  }

  def error: Future[String] =
    Future.failed(new RuntimeException("Test error"))

  def streamError(failAt: Int): Observable[String] =
    Observable.range(1, 10).map(i =>
      if (i == failAt) throw new RuntimeException(s"Error at item $i")
      else s"item-$i"
    )

  def form(id: String, name: String, age: Int): Future[String] =
    Future.successful(s"Form: $id, $name, $age")

  def emptyStream: Observable[String] =
    Observable.empty

  def longRunning(seconds: Int): Future[String] =
    Future {
      Thread.sleep(seconds * 1000)
      s"Completed after $seconds seconds"
    }

  def withCookie(sessionId: String): Future[String] =
    Future.successful(s"Session: $sessionId")

  def withHeader(custom: String): Future[String] =
    Future.successful(s"Header: $custom")

  def jsonBatched(count: Int): Observable[Map[String, Int]] =
    Observable.range(1, count + 1).map(i => Map("value" -> i.toInt))
}