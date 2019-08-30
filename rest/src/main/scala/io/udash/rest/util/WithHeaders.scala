package io.udash
package rest.util

import com.avsystem.commons.ISeq
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.openapi.{RestResponses, RestSchema, SchemaResolver}
import io.udash.rest.raw.{HttpBody, IMapping, PlainValue, RestResponse}

/**
  * Wrapper over a type which adds some arbitrary header values to it.
  * Can be used as response type of REST HTTP methods. Since these additional headers may be arbitrary,
  * their presence will not be reflected in the OpenAPI documentation.
  * If you want to include this information into OpenAPI definition for method that returns `WithHeaders`,
  * you may use [[io.udash.rest.adjustResponse adjustResponse]] on it.
  */
case class WithHeaders[+T](value: T, headers: ISeq[(String, String)])
object WithHeaders {
  implicit def asResponse[T](implicit wrapped: AsRaw[HttpBody, T]): AsRaw[RestResponse, WithHeaders[T]] =
    AsRaw.create {
      case WithHeaders(value, headers) =>
        val mapping = IMapping(headers.map({ case (n, v) => (n, PlainValue(v)) }): _*)
        wrapped.asRaw(value).defaultResponse.copy(headers = mapping).recoverHttpError
    }

  implicit def fromResponse[T](implicit wrapped: AsReal[HttpBody, T]): AsReal[RestResponse, WithHeaders[T]] =
    AsReal.create { resp =>
      resp.ensureNonError
      WithHeaders(wrapped.asReal(resp.body), resp.headers.entries.map({ case (n, PlainValue(v)) => (n, v) }))
    }

  implicit def responses[T: RestResponses]: RestResponses[WithHeaders[T]] =
    (resolver: SchemaResolver, schemaTransform: RestSchema[_] => RestSchema[_]) =>
      RestResponses[T].responses(resolver, schemaTransform)
}
