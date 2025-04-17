package io.udash
package rest

import com.avsystem.commons.*
import com.avsystem.commons.annotation.explicitGenerics
import com.typesafe.scalalogging.LazyLogging
import io.udash.rest.RestServlet.*
import io.udash.rest.raw.*
import io.udash.utils.URLEncoder
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}

import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncEvent, AsyncListener}
import scala.annotation.tailrec
import scala.concurrent.duration.*

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds
  final val DefaultMaxPayloadSize = 16 * 1024 * 1024L // 16MB
  final val CookieHeader = "Cookie"
  final val DefaultStreamingBatchSize = 100
  private final val BufferSize = 8192

  /**
   * Wraps an implementation of some REST API trait into a Java Servlet.
   *
   * @param apiImpl        implementation of some REST API trait
   * @param handleTimeout  maximum time the servlet will wait for results returned by REST API implementation
   * @param maxPayloadSize maximum acceptable incoming payload size, in bytes;
   *                       if exceeded, `413 Payload Too Large` response will be sent back
   * @param defaultStreamingBatchSize default batch when streaming [[StreamedBody.JsonList]]
   */
  @explicitGenerics def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration = DefaultHandleTimeout,
    maxPayloadSize: Long = DefaultMaxPayloadSize,
    defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
  )(implicit
    scheduler: Scheduler
  ): RestServlet =
    new RestServlet(
      handleRequest = RawRest.asHandleRequestWithStreaming[RestApi](apiImpl),
      handleTimeout = handleTimeout,
      maxPayloadSize = maxPayloadSize,
      defaultStreamingBatchSize = defaultStreamingBatchSize,
    )
}

class RestServlet(
  handleRequest: RawRest.HandleRequestWithStreaming,
  handleTimeout: FiniteDuration = DefaultHandleTimeout,
  maxPayloadSize: Long = DefaultMaxPayloadSize,
  defaultStreamingBatchSize: Int = DefaultStreamingBatchSize,
)(implicit
  scheduler: Scheduler
) extends HttpServlet with LazyLogging {

  import RestServlet._

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val asyncContext = request.startAsync()
    val completed = new AtomicBoolean(false)

    // Need to protect asyncContext from being completed twice because after a timeout the
    // servlet may recycle the same context instance between subsequent requests (not cool)
    // https://stackoverflow.com/a/27744537
    def completeWith(code: => Unit): Unit =
      if (!completed.getAndSet(true)) {
        code
        asyncContext.complete()
      }

    // readRequest must execute in Jetty thread but we want exceptions to be handled uniformly, hence the Try
    val udashRequest = Try(readRequest(request))
    val cancelable = Task.defer(handleRequest(udashRequest.get)).flatMap { rr =>
      Task(setResponseHeaders(response, rr.code, rr.headers)) >>
        writeResponseBody(response, rr)
    }.executeAsync.runAsync {
      case Right(_) =>
        asyncContext.complete()
      case Left(e: HttpErrorException) =>
        completeWith(writeResponse(response, e.toResponse))
      case Left(e) =>
        logger.error("Failed to handle REST request", e)
        completeWith(writeFailure(response, e.getMessage.opt))
    }

    asyncContext.setTimeout(handleTimeout.toMillis)
    asyncContext.addListener(new AsyncListener {
      def onComplete(event: AsyncEvent): Unit = ()
      def onTimeout(event: AsyncEvent): Unit = {
        cancelable.cancel()
        completeWith(writeFailure(response, Opt(s"server operation timed out after $handleTimeout")))
      }
      def onError(event: AsyncEvent): Unit = ()
      def onStartAsync(event: AsyncEvent): Unit = ()
    })
  }

  private def setResponseHeaders(response: HttpServletResponse, code: Int, headers: IMapping[PlainValue]): Unit = {
    response.setStatus(code)
    headers.entries.foreach {
      case (name, PlainValue(value)) => response.addHeader(name, value)
    }
  }

  private def writeNonEmptyBody(response: HttpServletResponse, body: HttpBody.NonEmpty): Unit = {
    val bytes = body.bytes
    response.setContentType(body.contentType)
    response.setContentLength(bytes.length)
    response.getOutputStream.write(bytes)
  }

  private def writeNonEmptyStreamedBody(
    response: HttpServletResponse,
    stream: StreamedRestResponse,
    body: StreamedBody.NonEmpty,
  ): Task[Unit] = Task.defer {
    // The Content-Length header is intentionally omitted for streams.
    // This signals to the client that the response body size is not predetermined and will be streamed.
    // Clients implementing the streaming part of the REST interface contract MUST be prepared
    // to handle responses without Content-Length by reading data incrementally until the stream completes.
    body match {
      case single: StreamedBody.Single =>
        Task.eval(writeNonEmptyBody(response, single.body))
      case binary: StreamedBody.RawBinary =>
        response.setContentType(binary.contentType)
        binary.content
          .consumeWith(Consumer.foreach { chunk =>
            response.getOutputStream.write(chunk)
            response.getOutputStream.flush()
          })
      case jsonList: StreamedBody.JsonList =>
        response.setContentType(jsonList.contentType)
        jsonList.elements
          .bufferTumbling(jsonList.customBatchSize.getOrElse(defaultStreamingBatchSize))
          .switchIfEmpty(Observable(Seq.empty))
          .zipWithIndex
          .consumeWith(Consumer.foreach { case (batch, idx) =>
            val firstBatch = idx == 0
            if (firstBatch) {
              response.getOutputStream.write("[".getBytes(jsonList.charset))
              batch.iterator.zipWithIndex.foreach { case (e, idx) =>
                if (idx != 0) {
                  response.getOutputStream.write(",".getBytes(jsonList.charset))
                }
                response.getOutputStream.write(e.value.getBytes(jsonList.charset))
              }
            } else
              batch.foreach { e =>
                response.getOutputStream.write(",".getBytes(jsonList.charset))
                response.getOutputStream.write(e.value.getBytes(jsonList.charset))
              }
            response.getOutputStream.flush()
          })
          .map(_ => response.getOutputStream.write("]".getBytes(jsonList.charset)))
    }
  }

  private def writeResponseBody(response: HttpServletResponse, rr: AbstractRestResponse): Task[Unit] =
    rr match {
      case resp: RestResponse =>
        resp.body match {
          case HttpBody.Empty => Task.unit
          case neBody: HttpBody.NonEmpty => Task(writeNonEmptyBody(response, neBody))
        }
      case stream: StreamedRestResponse =>
        stream.body match {
          case StreamedBody.Empty => Task.unit
          case neBody: StreamedBody.NonEmpty => writeNonEmptyStreamedBody(response, stream, neBody)
        }
    }

  private def writeResponse(response: HttpServletResponse, restResponse: RestResponse): Unit = {
    setResponseHeaders(response, restResponse.code, restResponse.headers)
    restResponse.body match {
      case HttpBody.Empty  =>
      case neBody: HttpBody.NonEmpty => writeNonEmptyBody(response, neBody)
    }
  }

  private def writeFailure(response: HttpServletResponse, message: Opt[String]): Unit = {
    response.setStatus(500)
    message.foreach { msg =>
      response.setContentType(s"text/plain;charset=utf-8")
      response.getWriter.write(msg)
    }
  }

  private def readParameters(request: HttpServletRequest): RestParameters = {
    // can't use request.getPathInfo because it decodes the URL before we can split it
    val pathPrefix = request.getContextPath.orEmpty + request.getServletPath.orEmpty
    val path = PlainValue.decodePath(request.getRequestURI.stripPrefix(pathPrefix))

    val query = request.getQueryString.opt.map(PlainValue.decodeQuery).getOrElse(Mapping.empty)

    val headersBuilder = IMapping.newBuilder[PlainValue]
    request.getHeaderNames.asScala.foreach { headerName =>
      if (!headerName.equalsIgnoreCase(CookieHeader)) { // cookies are separate, don't include them into header params
        headersBuilder += headerName -> PlainValue(request.getHeader(headerName))
      }
    }
    val headers = headersBuilder.result()

    val cookiesBuilder = Mapping.newBuilder[PlainValue]
    request.getCookies.opt.getOrElse(Array.empty).foreach { cookie =>
      val cookieName = URLEncoder.decode(cookie.getName, plusAsSpace = true)
      val cookieValue = URLEncoder.decode(cookie.getValue, plusAsSpace = true)
      cookiesBuilder += cookieName -> PlainValue(cookieValue)
    }
    val cookies = cookiesBuilder.result()

    RestParameters(path, headers, query, cookies)
  }

  private def readBody(request: HttpServletRequest): HttpBody = {
    val contentLength = request.getContentLengthLong.opt.filter(_ != -1)
    contentLength.filter(_ > maxPayloadSize).foreach { length =>
      throw HttpErrorException.plain(413, s"Payload is larger than maximum $maxPayloadSize bytes ($length)")
    }

    request.getContentType.opt.fold(HttpBody.empty) { contentType =>
      val mediaType = HttpBody.mediaTypeOf(contentType)
      HttpBody.charsetOf(contentType) match {
        // if Content-Length is undefined, always read as binary in order to validate maximum length
        case Opt(charset) if contentLength.isDefined =>
          val bodyReader = request.getReader
          val bodyBuilder = new JStringBuilder
          val cbuf = new Array[Char](BufferSize)
          @tailrec def readLoop(): Unit = bodyReader.read(cbuf) match {
            case -1 =>
            case len =>
              bodyBuilder.append(cbuf, 0, len)
              readLoop()
          }
          readLoop()
          HttpBody.textual(bodyBuilder.toString, mediaType, charset)

        case _ =>
          val bodyIs = request.getInputStream
          val bodyOs = new ByteArrayOutputStream
          val bbuf = new Array[Byte](BufferSize)
          @tailrec def readLoop(): Unit = bodyIs.read(bbuf) match {
            case -1 =>
            case len =>
              bodyOs.write(bbuf, 0, len)
              if (bodyOs.size > maxPayloadSize) {
                throw HttpErrorException.plain(413, s"Payload is larger than maximum $maxPayloadSize bytes")
              }
              readLoop()
          }
          readLoop()
          HttpBody.binary(bodyOs.toByteArray, contentType)
      }
    }
  }

  private def readRequest(request: HttpServletRequest): RestRequest = {
    val method = HttpMethod.byName(request.getMethod)
    val parameters = readParameters(request)
    val body = readBody(request)
    RestRequest(method, parameters, body)
  }
}
