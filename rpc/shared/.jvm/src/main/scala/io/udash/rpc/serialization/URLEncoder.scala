package io.udash.rpc.serialization

object URLEncoder {
  def encode(query: String): String =
    new java.net.URI(null, null, query, null).toASCIIString

  def decode(query: String): String =
    new java.net.URI(query).getSchemeSpecificPart
}
