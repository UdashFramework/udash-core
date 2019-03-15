package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.annotation.explicitGenerics
import com.typesafe.scalalogging.LazyLogging
import io.udash.rest.RestServlet.DefaultHandleTimeout
import io.udash.rest.raw._
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.concurrent.duration._

object RestServlet {
  final val DefaultHandleTimeout = 30.seconds

  @explicitGenerics def apply[RestApi: RawRest.AsRawRpc : RestMetadata](
    apiImpl: RestApi, handleTimeout: FiniteDuration = DefaultHandleTimeout
  ): RestServlet = new RestServlet(RawRest.asHandleRequest[RestApi](apiImpl))
}

class RestServlet(handleRequest: RawRest.HandleRequest, handleTimeout: FiniteDuration = DefaultHandleTimeout)
  extends HttpServlet with LazyLogging {

  override def service(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val asyncContext = request.startAsync().setup(_.setTimeout(handleTimeout.toMillis))
    RawRest.safeAsync(handleRequest(readRequest(request))) {
      case Success(restResponse) =>
        writeResponse(response, restResponse)
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
      headersBuilder += headerName -> PlainValue(request.getHeader(headerName))
    }
    val headers = headersBuilder.result()
    RestParameters(path, headers, query)
  }

  private def readBody(request: HttpServletRequest): HttpBody = {
    val mimeType = request.getContentType.opt.map(_.split(";", 2).head)
    mimeType.fold(HttpBody.empty) { mimeType =>
      val bodyReader = request.getReader
      val bodyBuilder = new JStringBuilder
      Iterator.continually(bodyReader.read())
        .takeWhile(_ != -1).foreach(bodyBuilder.appendCodePoint)
      HttpBody(bodyBuilder.toString, mimeType)
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
    restResponse.body.forNonEmpty { (content, mimeType) =>
      response.setContentType(s"$mimeType;charset=utf-8")
      response.getWriter.write(content)
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
