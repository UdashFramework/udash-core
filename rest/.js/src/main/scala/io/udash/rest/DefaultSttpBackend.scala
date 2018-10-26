package io.udash
package rest

import com.softwaremill.sttp.{FetchBackend, SttpBackend}

import scala.concurrent.Future

private[rest] object DefaultSttpBackend {
  implicit val backend: SttpBackend[Future, Nothing] = FetchBackend()
}
