package io.udash
package rest

import java.io.ByteArrayOutputStream

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import com.typesafe.scalalogging.LazyLogging
import io.udash.rest.RestServlet._
import io.udash.rest.raw._
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import javax.servlet.{AsyncEvent, AsyncListener}

import scala.annotation.tailrec
import scala.concurrent.duration._

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds
  final val DefaultMaxPayloadSize = 16 * 1024 * 1024L // 16MB
  final val CookieHeader = "Cookie"

  /**
   * Wraps an implementation of some REST API trait into a Java Servlet.
   *
   * @param apiImpl        implementation of some REST API trait
   * @param handleTimeout  maximum time the servlet will wait for results returned by REST API implementation
   * @param maxPayloadSize maximum acceptable incoming payload size, in bytes;
   *                       if exceeded, `413 Payload Too Large` response will be sent back
   */
  @explicitGenerics def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi,
    handleTimeout: FiniteDuration = DefaultHandleTimeout,
    maxPayloadSize: Long = DefaultMaxPayloadSize
  ): RestServlet = new RestServlet(RawRest.asHandleRequest[RestApi](apiImpl), handleTimeout, maxPayloadSize)

  private final val BufferSize = 8192
}

class RestServlet(
  handleRequest: RawRest.HandleRequest,
  handleTimeout: FiniteDuration = DefaultHandleTimeout,
  maxPayloadSize: Long = DefaultMaxPayloadSize
) extends HttpServlet with LazyLogging {

  import RestServlet._

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val asyncContext = request.startAsync()
    asyncContext.setTimeout(handleTimeout.toMillis)
    asyncContext.addListener(new AsyncListener {
      def onComplete(event: AsyncEvent): Unit = ()
      def onTimeout(event: AsyncEvent): Unit = {
        writeFailure(response, Opt("server operation timed out"))
        asyncContext.complete()
      }
      def onError(event: AsyncEvent): Unit = ()
      def onStartAsync(event: AsyncEvent): Unit = ()
    })
    RawRest.safeAsync(handleRequest(readRequest(request))) {
      case Success(restResponse) =>
        writeResponse(response, restResponse)
        asyncContext.complete()
      case Failure(e: HttpErrorException) =>
        writeResponse(response, e.toResponse)
        asyncContext.complete()
      case Failure(e) =>
        writeFailure(response, e.getMessage.opt)
        logger.error("Failed to handle REST request", e)
        asyncContext.complete()
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

    val cookies = request.getHeaders(CookieHeader).asScala.foldLeft(Mapping.empty[PlainValue]) {
      (cookies, headerValue) => cookies ++ PlainValue.decodeCookies(headerValue)
    }

    RestParameters(path, headers, query, cookies)
  }

  private def readBody(request: HttpServletRequest): HttpBody = {
    val contentLength = request.getContentLengthLong.opt.filter(_ != -1)
    contentLength.filter(_ > maxPayloadSize).foreach { length =>
      throw HttpErrorException(413, s"Payload is larger than maximum $maxPayloadSize bytes ($length)")
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
                throw HttpErrorException(413, s"Payload is larger than maximum $maxPayloadSize bytes")
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

  private def writeResponse(response: HttpServletResponse, restResponse: RestResponse): Unit = {
    response.setStatus(restResponse.code)
    restResponse.headers.entries.foreach {
      case (name, PlainValue(value)) => response.addHeader(name, value)
    }
    restResponse.body match {
      case HttpBody.Empty =>
      case neBody: HttpBody.NonEmpty =>
        // TODO: can we improve performance by avoiding intermediate byte array for textual content?
        val bytes = neBody.bytes
        response.setContentType(neBody.contentType)
        response.setContentLength(bytes.length)
        response.getOutputStream.write(bytes)
    }
  }

  private def writeFailure(response: HttpServletResponse, message: Opt[String]): Unit = {
    response.setStatus(500)
    message.foreach { msg =>
      response.setContentType(s"text/plain;charset=utf-8")
      response.getWriter.write(msg)
    }
  }
}
