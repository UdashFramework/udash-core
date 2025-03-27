package io.udash
package rest

import io.udash.rest.raw.{RawRest, RestRequest, RestResponse, StreamedRestResponse}
import monix.eval.Task

class DirectRestApiTest extends RestApiTestScenarios with StreamingRestApiTestScenarios {
  def clientHandle: RawRest.HandleRequest = serverHandle

  override def streamingClientHandler: RawRest.RestRequestHandler = new RawRest.RestRequestHandler {
    override def handleRequest(request: RestRequest): Task[RestResponse] =
      streamingServerHandle(request).map(_.asInstanceOf[RestResponse])

    override def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
      streamingServerHandle(request).map(_.asInstanceOf[StreamedRestResponse])
  }
}
