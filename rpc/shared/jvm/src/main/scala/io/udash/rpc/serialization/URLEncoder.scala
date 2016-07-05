package io.udash.rpc.serialization

object URLEncoder {
  def encode(query: String): String =
    java.net.URLEncoder.encode(query, "UTF-8").replace("+", "%20")

  def decode(query: String): String =
    java.net.URLDecoder.decode(query, "UTF-8")
}
