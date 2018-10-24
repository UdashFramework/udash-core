package io.udash
package rest.examples

import io.udash.rest._
import io.udash.rest.raw._
import com.avsystem.commons.serialization.GenCodec

import scala.concurrent.Future

trait GenericApi[T] {
  def process(value: T): Future[T]
}
object GenericApi {
  import DefaultRestImplicits._
  implicit def restAsRawReal[T: GenCodec]: RawRest.AsRawRealRpc[GenericApi[T]] = RawRest.materializeAsRawReal
  implicit def restMetadata[T]: RestMetadata[GenericApi[T]] = RestMetadata.materialize

  import openapi._
  implicit def openApiMetadata[T: RestSchema]: OpenApiMetadata[GenericApi[T]] = OpenApiMetadata.materialize
}