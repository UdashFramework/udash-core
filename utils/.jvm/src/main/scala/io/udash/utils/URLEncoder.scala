package io.udash.utils

object URLEncoder {
  def encode(query: String, spaceAsPlus: Boolean): String = {
    val res = java.net.URLEncoder.encode(query, "UTF-8")
    if (spaceAsPlus) res else res.replace("+", "%20")
  }

  def decode(query: String, plusAsSpace: Boolean): String = {
    val pre = if (plusAsSpace) query else query.replace("+", "%2B")
    java.net.URLDecoder.decode(pre, "UTF-8")
  }
}
