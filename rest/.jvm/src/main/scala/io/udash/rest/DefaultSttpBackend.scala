package io.udash
package rest

import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

object DefaultSttpBackend {
  def apply(): SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()
}
