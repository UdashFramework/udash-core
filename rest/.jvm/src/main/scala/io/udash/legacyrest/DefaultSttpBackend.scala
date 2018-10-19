package io.udash.legacyrest

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

private[legacyrest] object DefaultSttpBackend {
  implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
}
