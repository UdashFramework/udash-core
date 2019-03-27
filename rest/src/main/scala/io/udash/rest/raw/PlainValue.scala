package io.udash
package rest.raw

import io.udash.utils.URLEncoder

/**
  * Value used as encoding of [[io.udash.rest.Path Path]], [[io.udash.rest.Header Header]] and
  * [[io.udash.rest.Query Query]] parameters as well as [[io.udash.rest.Body Body]] parameters of
  * [[io.udash.rest.FormBody FormBody]] methods.
  *
  * Wrapped string MUST NOT be URL-encoded.
  */
final case class PlainValue(value: String) extends AnyVal
object PlainValue extends (String => PlainValue) {
  def decodePath(path: String): List[PlainValue] =
    path.split("/").iterator.map(s => PlainValue(URLEncoder.decode(s, plusAsSpace = false))).toList match {
      case PlainValue("") :: tail => tail
      case res => res
    }

  def encodePath(path: List[PlainValue]): String =
    path.iterator.map(pv => URLEncoder.encode(pv.value, spaceAsPlus = false)).mkString("/", "/", "")

  final val FormKVSep = "="
  final val FormKVPairSep = "&"

  def encodeQuery(query: Mapping[PlainValue]): String =
    query.entries.iterator.map { case (name, PlainValue(value)) =>
      s"${URLEncoder.encode(name, spaceAsPlus = true)}$FormKVSep${URLEncoder.encode(value, spaceAsPlus = true)}"
    }.mkString(FormKVPairSep)

  def decodeQuery(queryString: String): Mapping[PlainValue] = {
    val builder = Mapping.newBuilder[PlainValue]
    queryString.split(FormKVPairSep).iterator.filter(_.nonEmpty).map(_.split(FormKVSep, 2)).foreach {
      case Array(name, value) => builder +=
        URLEncoder.decode(name, plusAsSpace = true) -> PlainValue(URLEncoder.decode(value, plusAsSpace = true))
      case _ => throw new IllegalArgumentException(s"invalid query string $queryString")
    }
    builder.result()
  }
}
