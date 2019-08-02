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
}
