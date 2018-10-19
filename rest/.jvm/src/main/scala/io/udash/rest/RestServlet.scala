package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.meta.Mapping
import io.udash.rest.RestServlet.DefaultHandleTimeout
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.concurrent.duration._

class RestServlet(handleRequest: RawRest.HandleRequest, handleTimeout: FiniteDuration = DefaultHandleTimeout)
  extends HttpServlet {

  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    RestServlet.handle(handleRequest, req, resp, handleTimeout)
}

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds

  def apply[@explicitGenerics RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi, handleTimeout: FiniteDuration = DefaultHandleTimeout
  ): RestServlet = new RestServlet(RawRest.asHandleRequest[RestApi](apiImpl))

  def readParameters(request: HttpServletRequest): RestParameters = {
    // can't use request.getPathInfo because it decodes the URL before we can split it
    val pathPrefix = request.getContextPath.orEmpty + request.getServletPath.orEmpty
    val path = PathValue.splitDecode(request.getRequestURI.stripPrefix(pathPrefix))
    val query = request.getQueryString.opt.map(QueryValue.decode).getOrElse(Mapping.empty)
    val headersBuilder = Mapping.newBuilder[HeaderValue]
    request.getHeaderNames.asScala.foreach { headerName =>
      headersBuilder += headerName -> HeaderValue(request.getHeader(headerName))
    }
    val headers = headersBuilder.result()
    RestParameters(path, headers, query)
  }

  def readBody(request: HttpServletRequest): HttpBody = {
    val mimeType = request.getContentType.opt.map(_.split(";", 2).head)
    mimeType.fold(HttpBody.empty) { mimeType =>
      val bodyReader = request.getReader
      val bodyBuilder = new JStringBuilder
      Iterator.continually(bodyReader.read())
        .takeWhile(_ != -1).foreach(bodyBuilder.appendCodePoint)
      HttpBody(bodyBuilder.toString, mimeType)
    }
  }

  def readRequest(request: HttpServletRequest): RestRequest = {
    val method = HttpMethod.byName(request.getMethod)
    val parameters = readParameters(request)
    val body = readBody(request)
    RestRequest(method, parameters, body)
  }

  def writeResponse(response: HttpServletResponse, restResponse: RestResponse, charset: String = "utf-8"): Unit = {
    response.setStatus(restResponse.code)
    restResponse.headers.foreach {
      case (name, HeaderValue(value)) => response.addHeader(name, value)
    }
    restResponse.body.forNonEmpty { (content, mimeType) =>
      response.setContentType(s"$mimeType;charset=$charset")
      response.getWriter.write(content)
    }
  }

  def writeFailure(response: HttpServletResponse, message: Opt[String], charset: String = "utf-8"): Unit = {
    response.setStatus(500)
    message.foreach { msg =>
      response.setContentType(s"text/plain;charset=$charset")
      response.getWriter.write(msg)
    }
  }

  def handle(
    handleRequest: RawRest.HandleRequest,
    request: HttpServletRequest,
    response: HttpServletResponse,
    handleTimeout: FiniteDuration = DefaultHandleTimeout,
    charset: String = "utf-8"
  ): Unit = {
    val asyncContext = request.startAsync().setup(_.setTimeout(handleTimeout.toMillis))
    RawRest.safeAsync(handleRequest(readRequest(request))) {
      case Success(restResponse) =>
        writeResponse(response, restResponse, charset)
        asyncContext.complete()
      case Failure(e) =>
        writeFailure(response, e.getMessage.opt, charset)
        asyncContext.complete()
    }
  }
}
