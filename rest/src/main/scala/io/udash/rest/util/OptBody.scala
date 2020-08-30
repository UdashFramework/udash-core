package io.udash.rest.util

import com.avsystem.commons.Opt
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.openapi.{RefOr, RestRequestBody}
import io.udash.rest.raw.HttpBody

final case class OptBody[T](body: Opt[T])
object OptBody {
  implicit def asHttpBody[T](implicit wrapped: AsRaw[HttpBody, T]): AsRaw[HttpBody, OptBody[T]] = {
    case OptBody(Opt(t)) => wrapped.asRaw(t)
    case OptBody(Opt.Empty) => HttpBody.empty
  }

  implicit def fromHttpBody[T](implicit wrapped: AsReal[HttpBody, T]): AsReal[HttpBody, OptBody[T]] = {
    case HttpBody.Empty => OptBody(Opt.Empty)
    case body => OptBody(Opt(wrapped.asReal(body)))
  }

  implicit def restRequestBody[T: RestRequestBody]: RestRequestBody[OptBody[T]] =
    (resolver, schemaTransform) => RestRequestBody[T].requestBody(resolver, schemaTransform) match {
      case Opt(RefOr.Value(body)) => Opt(RefOr(body.copy(required = false)))
      case body => body
    }
}
