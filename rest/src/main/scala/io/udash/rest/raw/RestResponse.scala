package io.udash
package rest.raw

import com.avsystem.commons._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.raw.RawRest.FromTask
import monix.eval.{Task, TaskLike}

import scala.annotation.implicitNotFound

final case class RestResponse(code: Int, headers: IMapping[PlainValue], body: HttpBody) {
  def header(name: String, value: String): RestResponse =
    copy(headers = headers.append(name, PlainValue(value)))

  def isSuccess: Boolean =
    code >= 200 && code < 300
  def toHttpError: HttpErrorException =
    HttpErrorException(code, body.textualContentOpt.toOptArg)
  def ensureNonError: RestResponse =
    if (isSuccess) this else throw toHttpError
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

  implicit class TaskOps(private val asyncResp: Task[RestResponse]) extends AnyVal {
    def recoverHttpError: Task[RestResponse] =
      asyncResp.onErrorRecover {
        case e: HttpErrorException => e.toResponse
      }
  }

  implicit def taskLikeFromResponseTask[F[_], T](
    implicit fromTask: FromTask[F], fromResponse: AsReal[RestResponse, T]
  ): AsReal[Task[RestResponse], Try[F[T]]] =
    rawTask => Success(fromTask.fromTask(rawTask.map(fromResponse.asReal)))

  implicit def taskLikeToResponseTask[F[_], T](
    implicit taskLike: TaskLike[F], asResponse: AsRaw[RestResponse, T]
  ): AsRaw[Task[RestResponse], Try[F[T]]] =
    _.fold(Task.raiseError, ft => Task.from(ft).map(asResponse.asRaw)).recoverHttpError

  // following two implicits provide nice error messages when serialization is lacking for HTTP method result
  // while the async wrapper is fine (e.g. Future)

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRealNotFound[F[_], T](implicit
    fromAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsReal[RestResponse, T]]
  ): ImplicitNotFound[AsReal[Task[RestResponse], Try[F[T]]]] = ImplicitNotFound()

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRawNotFound[F[_], T](implicit
    toAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsRaw[RestResponse, T]]
  ): ImplicitNotFound[AsRaw[Task[RestResponse], Try[F[T]]]] = ImplicitNotFound()

  // following two implicits provide nice error messages when result type of HTTP method is totally wrong

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRealNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsReal[Task[RestResponse], Try[T]]] = ImplicitNotFound()

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRawNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsRaw[Task[RestResponse], Try[T]]] = ImplicitNotFound()
}
trait RestResponseLowPrio { this: RestResponse.type =>
  implicit def bodyBasedFromResponse[T](implicit bodyAsReal: AsReal[HttpBody, T]): AsReal[RestResponse, T] =
    resp => bodyAsReal.asReal(resp.ensureNonError.body)

  implicit def bodyBasedToResponse[T](implicit bodyAsRaw: AsRaw[HttpBody, T]): AsRaw[RestResponse, T] =
    value => bodyAsRaw.asRaw(value).defaultResponse.recoverHttpError

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
