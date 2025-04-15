package io.udash
package rest.raw

import com.avsystem.commons.*
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.rest.raw.RawRest.FromTask
import io.udash.rest.util.Utils
import monix.eval.{Task, TaskLike}
import monix.reactive.Observable

import scala.annotation.implicitNotFound

/** Base trait for REST response types, either standard or streaming. Contains common properties like status code and headers. */
sealed trait AbstractRestResponse {
  def code: Int
  def headers: IMapping[PlainValue]

  final def isSuccess: Boolean = code >= 200 && code < 300
}

/** Standard REST response containing a status code, headers, and a body. The body is loaded fully in memory as an [[HttpBody]]. */
final case class RestResponse(
  code: Int,
  headers: IMapping[PlainValue],
  body: HttpBody,
) extends AbstractRestResponse {

  def header(name: String, value: String): RestResponse =
    copy(headers = headers.append(name, PlainValue(value)))

  def toHttpError: HttpErrorException =
    HttpErrorException(code, body)

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
    implicit fromTask: FromTask[F],
    fromResponse: AsReal[RestResponse, T],
  ): AsReal[Task[RestResponse], Try[F[T]]] =
    rawTask => Success(fromTask.fromTask(rawTask.map(fromResponse.asReal)))

  implicit def taskLikeToResponseTask[F[_], T](
    implicit taskLike: TaskLike[F],
    asResponse: AsRaw[RestResponse, T],
  ): AsRaw[Task[RestResponse], Try[F[T]]] =
    _.fold(Task.raiseError, ft => Task.from(ft).map(asResponse.asRaw)).recoverHttpError

  // following two implicits provide nice error messages when serialization is lacking for HTTP method result
  // while the async wrapper is fine (e.g. Future)

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRealNotFound[F[_], T](implicit
    fromAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsReal[RestResponse, T]],
  ): ImplicitNotFound[AsReal[Task[RestResponse], Try[F[T]]]] = ImplicitNotFound()

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRawNotFound[F[_], T](implicit
    toAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsRaw[RestResponse, T]],
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

/**
 * Streaming REST response containing a status code, headers, and a streamed body.
 * Unlike standard [[RestResponse]], the body content can be delivered incrementally through a reactive stream.
 */
final case class StreamedRestResponse(
  code: Int,
  headers: IMapping[PlainValue],
  body: StreamedBody,
  customBatchSize: Opt[Int] = Opt.Empty,
) extends AbstractRestResponse {

  def header(name: String, value: String): StreamedRestResponse =
    copy(headers = headers.append(name, PlainValue(value)))

  def ensureNonError: StreamedRestResponse =
    if (isSuccess) this else throw HttpErrorException(code, StreamedBody.toHttpBody(body))
}

object StreamedRestResponse extends StreamedRestResponseLowPrio {

  /**
   * Converts a [[StreamedRestResponse]] to a standard [[RestResponse]] by materializing streamed content.
   * This is useful for compatibility with APIs that don't support streaming.
   */
  def fallbackToRestResponse(response: StreamedRestResponse): Task[RestResponse] = {
    val httpBody: Task[HttpBody] = response.body match {
      case StreamedBody.Empty =>
        Task.now(HttpBody.Empty)
      case binary: StreamedBody.RawBinary =>
        Utils.mergeArrays(binary.content).map(HttpBody.Binary(_, binary.contentType))
      case jsonList: StreamedBody.JsonList =>
        jsonList.elements
          .foldLeftL(new StringBuilder("[")) { case (sb, json) =>
            if (sb.sizeCompare(1) > 0) {
              sb.append(',')
            }
            sb.append(json.value)
          }
          .map(_.append(']').result())
          .map(rawJson => HttpBody.json(JsonValue(rawJson)))
      case single: StreamedBody.Single =>
        Task.now(single.body)
    }
    httpBody.map(RestResponse(response.code, response.headers, _))
  }

  /**
   * Converts any [[AbstractRestResponse]] to a standard [[RestResponse]] by materializing streamed content if necessary.
   * This is useful for compatibility with APIs that don't support streaming.
   */
  def fallbackToRestResponse(response: Task[AbstractRestResponse]): Task[RestResponse] =
    response.flatMap {
      case restResponse: RestResponse => Task.now(restResponse)
      case streamedResponse: StreamedRestResponse => fallbackToRestResponse(streamedResponse)
    }

  def fromHttpError(error: HttpErrorException): StreamedRestResponse =
    StreamedRestResponse(error.code, IMapping.empty, StreamedBody.fromHttpBody(error.payload))

  class LazyOps(private val resp: () => StreamedRestResponse) extends AnyVal {
    def recoverHttpError: StreamedRestResponse = try resp() catch {
      case e: HttpErrorException => StreamedRestResponse.fromHttpError(e)
    }
  }
  implicit def lazyOps(resp: => StreamedRestResponse): LazyOps = new LazyOps(() => resp)

  implicit class TaskOps(private val asyncResp: Task[StreamedRestResponse]) extends AnyVal {
    def recoverHttpError: Task[StreamedRestResponse] =
      asyncResp.onErrorRecover {
        case e: HttpErrorException => StreamedRestResponse.fromHttpError(e)
      }
  }

  implicit def taskLikeFromResponseTask[F[_], T](
    implicit fromTask: FromTask[F],
    fromResponse: AsReal[StreamedRestResponse, T],
  ): AsReal[Task[StreamedRestResponse], Try[F[T]]] =
    rawTask => Success(fromTask.fromTask(rawTask.map(fromResponse.asReal)))

  implicit def taskLikeToResponseTask[F[_], T](
    implicit taskLike: TaskLike[F],
    asResponse: AsRaw[StreamedRestResponse, T],
  ): AsRaw[Task[StreamedRestResponse], Try[F[T]]] =
    _.fold(Task.raiseError, ft => Task.from(ft).map(asResponse.asRaw)).recoverHttpError

  implicit def observableFromResponseTask[T](
    implicit fromResponse: AsReal[StreamedRestResponse, Observable[T]]
  ): AsReal[Task[StreamedRestResponse], Try[Observable[T]]] =
    rawTask => Success(Observable.fromTask(rawTask).flatMap(fromResponse.asReal))

  implicit def observableToResponseTask[T](
    implicit asResponse: AsRaw[StreamedRestResponse, Observable[T]]
  ): AsRaw[Task[StreamedRestResponse], Try[Observable[T]]] =
    _.fold(Task.raiseError, ft => Task.eval(ft).map(asResponse.asRaw)).recoverHttpError

  // following implicits provide nice error messages when serialization is lacking for HTTP method result
  // while the async wrapper is fine

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRealNotFound[F[_], T](implicit
    fromAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsReal[StreamedRestResponse, T]]
  ): ImplicitNotFound[AsReal[Task[StreamedRestResponse], Try[F[T]]]] = ImplicitNotFound()

  @implicitNotFound("${F}[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def effAsyncAsRawNotFound[F[_], T](implicit
    toAsync: TaskLike[F],
    forResponseType: ImplicitNotFound[AsRaw[StreamedRestResponse, T]]
  ): ImplicitNotFound[AsRaw[Task[StreamedRestResponse], Try[F[T]]]] = ImplicitNotFound()

  @implicitNotFound("Observable[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def observableAsRealNotFound[T](implicit
    forResponseType: ImplicitNotFound[AsReal[StreamedBody, Observable[T]]]
  ): ImplicitNotFound[AsReal[Task[StreamedRestResponse], Try[Observable[T]]]] = ImplicitNotFound()

  @implicitNotFound("Observable[${T}] is not a valid result type because:\n#{forResponseType}")
  implicit def observableAsRawNotFound[T](implicit
    forResponseType: ImplicitNotFound[AsRaw[StreamedBody, Observable[T]]]
  ): ImplicitNotFound[AsRaw[Task[StreamedRestResponse], Try[Observable[T]]]] = ImplicitNotFound()

  // following two implicits provide nice error messages when result type of HTTP method is totally wrong

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRealNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsReal[Task[StreamedRestResponse], Try[T]]] = ImplicitNotFound()

  @implicitNotFound("#{forResponseType}")
  implicit def asyncAsRawNotFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[AsRaw[Task[StreamedRestResponse], Try[T]]] = ImplicitNotFound()
}
trait StreamedRestResponseLowPrio { this: StreamedRestResponse.type =>
  implicit def bodyBasedFromResponse[T](implicit bodyAsReal: AsReal[StreamedBody, T]): AsReal[StreamedRestResponse, T] =
    resp => bodyAsReal.asReal(resp.ensureNonError.body)

  implicit def bodyBasedToResponse[T](implicit bodyAsRaw: AsRaw[StreamedBody, T]): AsRaw[StreamedRestResponse, T] =
    value => bodyAsRaw.asRaw(value).defaultResponse.recoverHttpError

  // following two implicits forward implicit-not-found error messages for StreamedBody as error messages for StreamedRestResponse

  @implicitNotFound("Cannot deserialize ${T} from StreamedRestResponse, because:\n#{forBody}")
  implicit def asRealNotFound[T](
    implicit forBody: ImplicitNotFound[AsReal[StreamedBody, T]]
  ): ImplicitNotFound[AsReal[StreamedRestResponse, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into StreamedRestResponse, because:\n#{forBody}")
  implicit def asRawNotFound[T](
    implicit forBody: ImplicitNotFound[AsRaw[StreamedBody, T]]
  ): ImplicitNotFound[AsRaw[StreamedRestResponse, T]] = ImplicitNotFound()
}
