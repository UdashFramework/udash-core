package io.udash
package rest

import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
}
