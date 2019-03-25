package io.udash
package rest.raw

import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal}
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.json.{JsonReader, JsonStringInput, JsonStringOutput}
import com.avsystem.commons.{JStringBuilder, Opt, OptArg, _}

import scala.annotation.implicitNotFound

/**
  * Value used to represent HTTP body. Also used as direct encoding of [[io.udash.rest.Body Body]] parameter in
  * [[io.udash.rest.CustomBody CustomBody]] methods.
  * Types that have encoding to [[io.udash.rest.raw.JsonValue JsonValue]] automatically have encoding to
  * [[io.udash.rest.raw.HttpBody HttpBody]] with `application/json` media type.
  * There is also a specialized encoding provided for `Unit` which returns empty HTTP body when writing and ignores
  * the body when reading.
  */
sealed trait HttpBody {
  def nonEmptyOpt: Opt[HttpBody.NonEmpty] = this match {
    case ne: HttpBody.NonEmpty => Opt(ne)
    case _ => Opt.Empty
  }

  final def textualContentOpt: Opt[String] = this match {
    case HttpBody.Textual(content, _, _) => Opt(content)
    case _ => Opt.Empty
  }

  final def readJson(): JsonValue = JsonValue(readText(HttpBody.JsonType))
  final def readForm(): String = readText(HttpBody.FormType)

  final def readText(requiredMediaType: OptArg[String] = OptArg.Empty): String = this match {
    case HttpBody.Textual(content, mediaType, _) if requiredMediaType.forall(_ == mediaType) => content
    case HttpBody.Empty =>
      throw new ReadFailure("Expected non-empty textual body")
    case HttpBody.Binary(_, mediaType) =>
      throw new ReadFailure(s"Expected non-empty textual body" +
        s"${requiredMediaType.fold("")(mt => s" with media type $mt")}, " +
        s"got binary body with media type $mediaType")
  }

  final def readBytes(requiredMediaType: OptArg[String] = OptArg.Empty): Array[Byte] =
    this match {
      case HttpBody.Empty => throw new ReadFailure("Expected non-empty body")
      case ne: HttpBody.NonEmpty =>
        if (requiredMediaType.forall(_ == ne.mediaType)) ne.bytes
        else throw new ReadFailure(s"Expected non-empty body" +
          requiredMediaType.fold("")(mt => s" with media type $mt") +
          s" but got body with content type ${ne.contentType}")
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

  sealed trait NonEmpty extends HttpBody {
    def mediaType: String
    def contentType: String
    def bytes: Array[Byte]
  }

  /**
    * Represents textual HTTP body. A body is considered textual if `Content-Type` has `charset` defined.
    */
  final case class Textual(content: String, mediaType: String, charset: String) extends NonEmpty {
    def contentType: String = s"$mediaType;charset=$charset"
    lazy val bytes: Array[Byte] = content.getBytes(charset)
  }

  /**
    * Represents binary HTTP body. A body is considered binary if `Content-Type` does not have `charset` defined.
    */
  final case class Binary(bytes: Array[Byte], contentType: String) extends NonEmpty {
    def mediaType: String = mediaTypeOf(contentType)
  }

  def empty: HttpBody = Empty

  def textual(content: String, mediaType: String = PlainType, charset: String = Utf8Charset): HttpBody =
    Textual(content, mediaType, charset)

  def binary(bytes: Array[Byte], contentType: String = OctetStreamType): HttpBody =
    Binary(bytes, contentType)

  final val PlainType = "text/plain"
  final val JsonType = "application/json"
  final val FormType = "application/x-www-form-urlencoded"
  final val OctetStreamType = "application/octet-stream"

  final val CharsetParamRegex = """;\s*charset=([^;]*)""".r

  final val Utf8Charset = "utf-8"

  def mediaTypeOf(contentType: String): String =
    contentType.indexOf(';') match {
      case -1 => contentType.trim
      case idx => contentType.substring(0, idx).trim
    }

  def charsetOf(contentType: String): Opt[String] =
    CharsetParamRegex.findFirstMatchIn(contentType)
      .toOpt.map(_.group(1).trim)
      .map { charset =>
        if (charset.startsWith("\"") && charset.endsWith("\""))
          charset.substring(1, charset.length - 1)
        else charset
      }

  def plain(content: OptArg[String] = OptArg.Empty): HttpBody =
    content.toOpt.map(textual(_, PlainType)).getOrElse(Empty)

  def json(json: JsonValue): HttpBody = textual(json.value, JsonType)

  def createFormBody(values: Mapping[PlainValue]): HttpBody =
    if (values.isEmpty) HttpBody.Empty else textual(PlainValue.encodeQuery(values), FormType)

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

  implicit val octetStreamBodyForByteArray: AsRawReal[HttpBody, Array[Byte]] =
    AsRawReal.create(binary(_), body => body.readBytes(OctetStreamType))
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
