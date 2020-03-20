package io.udash
package rest

import sttp.client.{FetchBackend, SttpBackend}

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Nothing, Nothing] = FetchBackend()
}
