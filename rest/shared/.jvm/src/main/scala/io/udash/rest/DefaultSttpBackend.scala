package io.udash.rest

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

private[rest] object DefaultSttpBackend {
  implicit val backend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
}
