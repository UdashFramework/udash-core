package io.udash.utils

import scala.scalajs.js

object URLEncoder {
  def encode(query: String, spaceAsPlus: Boolean): String = {
    val res = js.URIUtils.encodeURIComponent(query)
      .replace("!", "%21")
      .replace("'", "%27")
      .replace("(", "%28")
      .replace(")", "%29")
      .replace("~", "%7E")

    if (spaceAsPlus) res.replace("%20", "+") else res
  }

  def decode(query: String, plusAsSpace: Boolean): String = {
    val pre = if (plusAsSpace) query.replace("+", "%20") else query
    js.URIUtils.decodeURIComponent(pre)
  }
}
