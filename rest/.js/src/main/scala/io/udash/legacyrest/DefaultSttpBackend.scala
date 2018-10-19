package io.udash.legacyrest

import com.softwaremill.sttp.{FetchBackend, SttpBackend}

import scala.concurrent.Future

private[legacyrest] object DefaultSttpBackend {
  implicit val backend: SttpBackend[Future, Nothing] = FetchBackend()
}
