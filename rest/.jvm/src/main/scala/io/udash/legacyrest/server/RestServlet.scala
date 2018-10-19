package io.udash.legacyrest.server

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.avsystem.commons.SharedExtensions._
import io.udash.legacyrest._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/** Servlet for exposing REST interfaces. */
abstract class RestServlet(implicit ec: ExecutionContext) extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    if (req.getMethod == "PATCH") doPatch(req, resp)
    else super.service(req, resp)
  }

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handle(req, resp, classOf[GET])

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handle(req, resp, classOf[POST])

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handle(req, resp, classOf[PUT])

  def doPatch(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handle(req, resp, classOf[PATCH])

  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handle(req, resp, classOf[DELETE])

  /** Creates endpoints for the request. It can handle authentication.
    * Default auth exception: [[ExposesREST.Unauthorized]] */
  protected def createEndpoint(req: HttpServletRequest): ExposesREST[_]

  /** Handles exceptions thrown by the endpoints implementation. */
  protected def handleServerException(req: HttpServletRequest, resp: HttpServletResponse)(ex: Throwable): Unit =
    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString)

  private def handle(req: HttpServletRequest, resp: HttpServletResponse, httpMethod: Class[_ <: RESTMethod]): Unit = {
    val asyncCtx = req.startAsync()

    Future.fromTry(Try(createEndpoint(req)))
      .flatMapNow(_.handleRestCall(req, httpMethod))
      .onComplete {
        case Success(response) =>
          resp.getOutputStream.print(response)
          resp.getOutputStream.flush()
          resp.setStatus(HttpServletResponse.SC_OK)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.NotFound) =>
          resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.Unauthorized) =>
          resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.MethodNotAllowed) =>
          resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.BadRequestException) =>
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex) =>
          handleServerException(req, resp)(ex)
          asyncCtx.complete()
      }
  }
}

class DefaultRestServlet(exposedInterfaces: ExposesREST[_])(implicit ec: ExecutionContext) extends RestServlet {
  override protected def createEndpoint(req: HttpServletRequest): ExposesREST[_] =
    exposedInterfaces
}
