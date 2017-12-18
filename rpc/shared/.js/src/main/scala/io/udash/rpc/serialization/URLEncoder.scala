package io.udash.rpc.serialization

import scala.scalajs.js

object URLEncoder {
  def encode(query: String): String =
    js.URIUtils.encodeURIComponent(query)

  def decode(query: String): String =
    js.URIUtils.decodeURIComponent(query)
}
