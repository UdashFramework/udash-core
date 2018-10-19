package io.udash
package rest

import com.avsystem.commons.rpc.AsRawReal

trait FloatingPointRestImplicits {
  implicit final val floatPathValueAsRealRaw: AsRawReal[PathValue, Float] =
    AsRawReal.create(v => PathValue(v.toString), _.value.toFloat)
  implicit final val floatHeaderValueAsRealRaw: AsRawReal[HeaderValue, Float] =
    AsRawReal.create(v => HeaderValue(v.toString), _.value.toFloat)
  implicit final val floatQueryValueAsRealRaw: AsRawReal[QueryValue, Float] =
    AsRawReal.create(v => QueryValue(v.toString), _.value.toFloat)

  implicit final val doublePathValueAsRealRaw: AsRawReal[PathValue, Double] =
    AsRawReal.create(v => PathValue(v.toString), _.value.toDouble)
  implicit final val doubleHeaderValueAsRealRaw: AsRawReal[HeaderValue, Double] =
    AsRawReal.create(v => HeaderValue(v.toString), _.value.toDouble)
  implicit final val doubleQueryValueAsRealRaw: AsRawReal[QueryValue, Double] =
    AsRawReal.create(v => QueryValue(v.toString), _.value.toDouble)
}
object FloatingPointRestImplicits extends FloatingPointRestImplicits
