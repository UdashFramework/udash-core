package io.udash
package rest

import sttp.client3.{HttpClientFutureBackend, SttpBackend}

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Any] = HttpClientFutureBackend()
}
