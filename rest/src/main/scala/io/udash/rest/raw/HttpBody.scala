package io.udash
package rest.raw

import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput, JsonStringOutput}
import com.avsystem.commons.{JStringBuilder, Opt, OptArg}

import scala.annotation.implicitNotFound

/**
  * Value used to represent HTTP body. Also used as direct encoding of [[io.udash.rest.Body Body]] parameter in
  * [[io.udash.rest.FormBody FormBody]] methods.
  * Types that have encoding to [[io.udash.rest.raw.JsonValue JsonValue]] automatically have encoding to
  * [[io.udash.rest.raw.HttpBody HttpBody]] which uses application/json MIME type.
  * There is also a specialized encoding provided for `Unit` which returns empty HTTP body when writing and ignores
  * the body when reading.
  */
sealed trait HttpBody {
  final def contentOpt: Opt[String] = this match {
    case HttpBody(content, _) => Opt(content)
    case HttpBody.Empty => Opt.Empty
  }

  final def mimeTypeOpt: Opt[String] = this match {
    case HttpBody(_, mimeType) => Opt(mimeType)
    case HttpBody.Empty => Opt.Empty
  }

  final def forNonEmpty(consumer: (String, String) => Unit): Unit = this match {
    case HttpBody(content, mimeType) => consumer(content, mimeType)
    case HttpBody.Empty =>
  }

  final def readContent(): String = this match {
    case HttpBody(content, _) => content
    case HttpBody.Empty => throw new ReadFailure("Expected non-empty body")
  }

  final def readJson(): JsonValue = JsonValue(readContent(HttpBody.JsonType))
  final def readForm(): String = readContent(HttpBody.FormType)

  final def readContent(mimeType: String): String = this match {
    case HttpBody(content, `mimeType`) => content
    case HttpBody(_, actualMimeType) =>
      throw new ReadFailure(s"Expected body with $mimeType type, got $actualMimeType")
    case HttpBody.Empty =>
      throw new ReadFailure(s"Expected body with $mimeType type, got empty body")
  }

  final def defaultStatus: Int = this match {
    case HttpBody.Empty => 204
    case _ => 200
  }

  final def defaultResponse: RestResponse =
    RestResponse(defaultStatus, IMapping.empty, this)
}
object HttpBody extends HttpBodyLowPrio {
  case object Empty extends HttpBody
  final case class NonEmpty(content: String, mimeType: String) extends HttpBody

  def empty: HttpBody = Empty

  def apply(content: String, mimeType: String): HttpBody =
    NonEmpty(content, mimeType)

  def unapply(body: HttpBody): Opt[(String, String)] = body match {
    case Empty => Opt.Empty
    case NonEmpty(content, mimeType) => Opt((content, mimeType))
  }

  final val PlainType = "text/plain"
  final val JsonType = "application/json"
  final val FormType = "application/x-www-form-urlencoded"

  def plain(content: OptArg[String] = OptArg.Empty): HttpBody =
    content.toOpt.map(HttpBody(_, PlainType)).getOrElse(Empty)

  def json(json: JsonValue): HttpBody = HttpBody(json.value, JsonType)

  def createFormBody(values: Mapping[PlainValue]): HttpBody =
    if (values.isEmpty) HttpBody.Empty else HttpBody(PlainValue.encodeQuery(values), FormType)

  def parseFormBody(body: HttpBody): Mapping[PlainValue] = body match {
    case HttpBody.Empty => Mapping.empty[PlainValue]
    case _ => PlainValue.decodeQuery(body.readForm())
  }

  def createJsonBody(fields: Mapping[JsonValue]): HttpBody =
    if (fields.isEmpty) HttpBody.Empty else {
      val sb = new JStringBuilder
      val oo = new JsonStringOutput(sb).writeObject()
      fields.entries.foreach {
        case (key, JsonValue(json)) =>
          oo.writeField(key).writeRawJson(json)
      }
      oo.finish()
      HttpBody.json(JsonValue(sb.toString))
    }

  def parseJsonBody(body: HttpBody): Mapping[JsonValue] = body match {
    case HttpBody.Empty => Mapping.empty
    case _ =>
      val oi = new JsonStringInput(new JsonReader(body.readJson().value)).readObject()
      val builder = Mapping.newBuilder[JsonValue]
      while (oi.hasNext) {
        val fi = oi.nextField()
        builder += ((fi.fieldName, JsonValue(fi.readRawJson())))
      }
      builder.result()
  }

  implicit val emptyBodyForUnit: AsRawReal[HttpBody, Unit] =
    AsRawReal.create(_ => HttpBody.Empty, _ => ())
}
trait HttpBodyLowPrio { this: HttpBody.type =>
  implicit def httpBodyJsonAsRaw[T](implicit jsonAsRaw: AsRaw[JsonValue, T]): AsRaw[HttpBody, T] =
    AsRaw.create(v => HttpBody.json(jsonAsRaw.asRaw(v)))
  implicit def httpBodyJsonAsReal[T](implicit jsonAsReal: AsReal[JsonValue, T]): AsReal[HttpBody, T] =
    AsReal.create(v => jsonAsReal.asReal(v.readJson()))

  @implicitNotFound("Cannot deserialize ${T} from HttpBody, because:\n#{forJson}")
  implicit def asRealNotFound[T](
    implicit forJson: ImplicitNotFound[AsReal[JsonValue, T]]
  ): ImplicitNotFound[AsReal[HttpBody, T]] = ImplicitNotFound()

  @implicitNotFound("Cannot serialize ${T} into HttpBody, because:\n#{forJson}")
  implicit def asRawNotFound[T](
    implicit forJson: ImplicitNotFound[AsRaw[JsonValue, T]]
  ): ImplicitNotFound[AsRaw[HttpBody, T]] = ImplicitNotFound()
}
