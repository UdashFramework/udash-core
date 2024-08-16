package io.udash
package rest.openapi

import com.avsystem.commons.OptArg
import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.serialization.json.JsonStringOutput
import jakarta.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

object OpenApiServlet {
  @explicitGenerics def apply[RestApi: OpenApiMetadata](
    info: Info,
    components: Components = Components(),
    servers: List[Server] = Nil,
    security: List[SecurityRequirement] = Nil,
    tags: List[Tag] = Nil,
    externalDocs: OptArg[ExternalDocumentation] = OptArg.Empty
  ): OpenApiServlet = new OpenApiServlet {
    protected def render(request: HttpServletRequest): OpenApi =
      implicitly[OpenApiMetadata[RestApi]].openapi(info, components, servers, security, tags, externalDocs)
  }
}

abstract class OpenApiServlet extends HttpServlet {
  protected def render(request: HttpServletRequest): OpenApi

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp.setContentType("application/json;charset=utf-8")
    resp.getWriter.write(JsonStringOutput.writePretty(render(req)))
  }
}
