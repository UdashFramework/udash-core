package io.udash.rest.server

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.avsystem.commons.concurrent.RunNowEC
import io.udash.rest._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RestServlet(exposedInterfaces: ExposesREST[_])(implicit ec: ExecutionContext) extends HttpServlet {
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

  private def handle(req: HttpServletRequest, resp: HttpServletResponse, httpMethod: Class[_ <: RESTMethod]): Unit = {
    val asyncCtx = req.startAsync()
    Future(exposedInterfaces.parseHttpRequest(req, httpMethod))(RunNowEC)
      .flatMap { case (getterChain, inv) =>
        exposedInterfaces.handleRestCall(getterChain, inv)
      }.onComplete {
        case Success(response) =>
          resp.getOutputStream.print(response)
          resp.getOutputStream.flush()
          resp.setStatus(HttpServletResponse.SC_OK)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.NotFound) =>
          resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.MethodNotAllowed) =>
          resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex: ExposesREST.BadRequestException) =>
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage)
          asyncCtx.complete()
        case Failure(ex) =>
          resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage)
          asyncCtx.complete()
      }
  }
}
