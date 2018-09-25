package io.udash.rpc.serialization

object URLEncoder {
  def encode(query: String): String =
    java.net.URLEncoder.encode(query, "UTF-8")
      .replaceAll("\\%28", "(")
      .replaceAll("\\%29", ")")
      .replaceAll("\\+", "%20")
      .replaceAll("\\%27", "'")
      .replaceAll("\\%21", "!")
      .replaceAll("\\%7E", "~")

  def decode(query: String): String =
    java.net.URLDecoder.decode(query, "UTF-8")
}
