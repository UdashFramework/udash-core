package io.udash.web.guide.demos.rest

import com.avsystem.commons.serialization.GenCodec

case class RestExampleClass(i: Int, s: String, tuple: (Double, String))

object RestExampleClass {
  implicit val phoneBookInfoGenCodec: GenCodec[RestExampleClass] = GenCodec.materialize
}