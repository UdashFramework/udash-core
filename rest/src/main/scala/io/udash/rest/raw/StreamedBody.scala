package io.udash
package rest.raw

import com.avsystem.commons.annotation.explicitGenerics
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import monix.reactive.Observable

import scala.annotation.implicitNotFound
import scala.reflect.{ClassTag, classTag}

sealed trait StreamedBody {
  final def defaultStatus: Int = this match {
    case StreamedBody.Empty => 204
    case _ => 200
  }

  final def defaultResponse: StreamedRestResponse =
    StreamedRestResponse(
      code = defaultStatus,
      headers = IMapping.empty,
      body = this,
      batchSize = 1,
    )
}
object StreamedBody extends StreamedBodyLowPrio {
  case object Empty extends StreamedBody

  sealed trait NonEmpty extends StreamedBody {
    def contentType: String
  }

  /**
   * Represents a binary streamed response body.
   * The content is delivered as a stream of byte arrays which can be processed incrementally.
   * Useful for large binary files or content that is generated dynamically.
   */
  final case class RawBinary(content: Observable[Array[Byte]]) extends NonEmpty {
    val contentType: String = HttpBody.OctetStreamType

    override def toString: String = super.toString
  }

  /**
   * Represents a streamed list of JSON values.
   * Each element in the stream is a complete JSON value, allowing for incremental processing
   * of potentially large collections without loading everything into memory at once.
   */
  final case class JsonList(
    elements: Observable[JsonValue],
    charset: String = HttpBody.Utf8Charset,
  ) extends NonEmpty {
    val contentType: String = s"${HttpBody.JsonType};charset=$charset"

    override def toString: String = super.toString
  }

  /**
   * Represents a single non-empty HTTP body that will be delivered as a streaming response.
   * Used when the content is already fully loaded but needs to be returned through a streaming API
   * for consistency with other streaming operations.
   */
  final case class Single(body: HttpBody.NonEmpty) extends NonEmpty {
    override def contentType: String = body.contentType
  }

  def empty: StreamedBody = Empty

  def fromHttpBody(body: HttpBody): StreamedBody = body match {
    case HttpBody.Empty => StreamedBody.Empty
    case nonEmpty: HttpBody.NonEmpty => StreamedBody.Single(nonEmpty)
  }

  def toHttpBody(body: StreamedBody): HttpBody = body match {
    case StreamedBody.Empty => HttpBody.Empty
    case nonEmpty: StreamedBody.NonEmpty => castOrFail[Single](nonEmpty).body
  }

  @explicitGenerics
  def castOrFail[T <: StreamedBody: ClassTag](body: StreamedBody): T =
    body match {
      case expected: T => expected
      case unexpected =>
        throw new ReadFailure(
          s"Expected ${classTag[T].runtimeClass.getSimpleName} body representation, got ${unexpected.getClass.getSimpleName}"
        )
    }

  implicit val rawBinaryBodyForByteArray: AsRawReal[StreamedBody, Observable[Array[Byte]]] =
    AsRawReal.create(
      bytes => RawBinary(bytes),
      body => StreamedBody.castOrFail[RawBinary](body).content,
    )
}
trait StreamedBodyLowPrio { this: StreamedBody.type =>
  implicit def bodyJsonListAsRaw[T](implicit jsonAsRaw: AsRaw[JsonValue, T]): AsRaw[StreamedBody, Observable[T]] =
    v => StreamedBody.JsonList(v.map(jsonAsRaw.asRaw))
  implicit def bodyJsonListAsReal[T](implicit jsonAsReal: AsReal[JsonValue, T]): AsReal[StreamedBody, Observable[T]] =
    v => StreamedBody.castOrFail[StreamedBody.JsonList](v).elements.map(jsonAsReal.asReal)

  @implicitNotFound("Cannot deserialize ${T} from StreamedBody, because:\n#{forJson}")
  implicit def asRealNotFound[T](
    implicit forJson: ImplicitNotFound[AsReal[JsonValue, T]]
  ): ImplicitNotFound[AsReal[StreamedBody, Observable[T]]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into StreamedBody, because:\n#{forJson}")
  implicit def asRawNotFound[T](
    implicit forJson: ImplicitNotFound[AsRaw[JsonValue, T]]
  ): ImplicitNotFound[AsRaw[StreamedBody, Observable[T]]] = ImplicitNotFound()
}
