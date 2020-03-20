package io.udash
package rest

import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()
}
