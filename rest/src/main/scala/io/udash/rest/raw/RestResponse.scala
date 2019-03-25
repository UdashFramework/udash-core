package io.udash
package rest.raw

import com.avsystem.commons._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import com.avsystem.commons.{OptArg, Success, Try}

import scala.annotation.implicitNotFound
import scala.util.Failure

case class RestResponse(code: Int, headers: IMapping[PlainValue], body: HttpBody) {
  def toHttpError: HttpErrorException =
    HttpErrorException(code, body.textualContentOpt.toOptArg)
  def ensureNonError: RestResponse =
    if (code >= 200 && code < 300) this else throw toHttpError
}

object RestResponse extends RestResponseLowPrio {
  def plain(status: Int, message: OptArg[String] = OptArg.Empty): RestResponse =
    RestResponse(status, IMapping.empty, HttpBody.plain(message))

  class LazyOps(private val resp: () => RestResponse) extends AnyVal {
    def recoverHttpError: RestResponse = try resp() catch {
      case e: HttpErrorException => e.toResponse
    }
  }
  implicit def lazyOps(resp: => RestResponse): LazyOps = new LazyOps(() => resp)

  implicit class AsyncOps(private val asyncResp: RawRest.Async[RestResponse]) extends AnyVal {
    def recoverHttpError: RawRest.Async[RestResponse] =
      callback => asyncResp {
        case Failure(e: HttpErrorException) => callback(Success(e.toResponse))
        case tr => callback(tr)
      }
  }

  implicit def effectFromAsyncResp[F[_], T](
    implicit asyncEff: RawRest.AsyncEffect[F], asResponse: AsReal[RestResponse, T]
  ): AsReal[RawRest.Async[RestResponse], Try[F[T]]] =
    AsReal.create(async => Success(asyncEff.fromAsync(RawRest.mapAsync(async)(resp => asResponse.asReal(resp)))))

  implicit def effectToAsyncResp[F[_], T](
    implicit asyncEff: RawRest.AsyncEffect[F], asResponse: AsRaw[RestResponse, T]
  ): AsRaw[RawRest.Async[RestResponse], Try[F[T]]] =
    AsRaw.create(_.fold(
      RawRest.failingAsync,
      ft => RawRest.mapAsync(asyncEff.toAsync(ft))(asResponse.asRaw)
    ).recoverHttpError)

  // following two implicits provide nice error messages when serialization is lacking for HTTP method result
  // while the async wrapper is fine (e.g. Future)

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRealNotFound[F[_], T](implicit
    fromAsync: RawRest.AsyncEffect[F],
    forResponseType: ImplicitNotFound[AsReal[RestResponse, T]]
  ): ImplicitNotFound[AsReal[RawRest.Async[RestResponse], Try[F[T]]]] = ImplicitNotFound()

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRawNotFound[F[_], T](implicit
    toAsync: RawRest.AsyncEffect[F],
    forResponseType: ImplicitNotFound[AsRaw[RestResponse, T]]
  ): ImplicitNotFound[AsRaw[RawRest.Async[RestResponse], Try[F[T]]]] = ImplicitNotFound()

  // following two implicits provide nice error messages when result type of HTTP method is totally wrong

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRealNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsReal[RawRest.Async[RestResponse], Try[T]]] = ImplicitNotFound()

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRawNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsRaw[RawRest.Async[RestResponse], Try[T]]] = ImplicitNotFound()
}
trait RestResponseLowPrio { this: RestResponse.type =>
  implicit def bodyBasedFromResponse[T](implicit bodyAsReal: AsReal[HttpBody, T]): AsReal[RestResponse, T] =
    AsReal.create(resp => bodyAsReal.asReal(resp.ensureNonError.body))

  implicit def bodyBasedToResponse[T](implicit bodyAsRaw: AsRaw[HttpBody, T]): AsRaw[RestResponse, T] =
    AsRaw.create(value => bodyAsRaw.asRaw(value).defaultResponse.recoverHttpError)

  // following two implicits forward implicit-not-found error messages for HttpBody as error messages for RestResponse

  @implicitNotFound("Cannot deserialize ${T} from RestResponse, because:\n#{forBody}")
  implicit def asRealNotFound[T](
    implicit forBody: ImplicitNotFound[AsReal[HttpBody, T]]
  ): ImplicitNotFound[AsReal[RestResponse, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into RestResponse, because:\n#{forBody}")
  implicit def asRawNotFound[T](
    implicit forBody: ImplicitNotFound[AsRaw[HttpBody, T]]
  ): ImplicitNotFound[AsRaw[RestResponse, T]] = ImplicitNotFound()
}
