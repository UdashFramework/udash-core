package io.udash
package rest

import com.softwaremill.sttp.{FetchBackend, SttpBackend}

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Nothing] = FetchBackend()
}
