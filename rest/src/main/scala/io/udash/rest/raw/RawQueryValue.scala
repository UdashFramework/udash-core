package io.udash
package rest.raw

import com.avsystem.commons._
import com.avsystem.commons.misc.ImplicitNotFound
import com.avsystem.commons.rpc.{AsRaw, AsReal}
import io.udash.utils.URLEncoder

import scala.annotation.{implicitNotFound, tailrec}
import scala.collection.AbstractIterator
import scala.collection.generic.CanBuildFrom

/**
  * The raw data type which all query parameter values are serialized into, i.e. the RPC macro engine looks
  * for `AsRaw/AsReal[RawQueryValue, T]` for every [[io.udash.rest.Query Query]] parameter of type `T`.
  *
  * Represents an URI query parameter (or sequence of adjacent parameters with the same name),
  * in a raw form which makes it possible to support repeated, flag and binary parameters.
  * Unlike [[PlainValue]], raw strings inside `RawQueryValue` must be already URL-encoded.
  *
  * Examples:
  * `&flag` -> `Single(Opt.Empty)`
  * `&key=value` -> `Single(Opt("value"))`
  * `&key=value1&key=value2` -> `Multiple(Opt("value1"), Single(Opt("value2"))`
  * `&key=va%7Clue` -> `Single(Opt("va%7Clue")`
  */
sealed abstract class RawQueryValue {

  import RawQueryValue._

  def first: Opt[String] = this match {
    case Single(encoded) => encoded
    case Repeated(encoded, _) => encoded
  }

  def iterator: Iterator[Opt[String]] = new AbstractIterator[Opt[String]] {
    private var _next: RawQueryValue = RawQueryValue.this
    def hasNext: Boolean = _next != null
    def next(): Opt[String] = _next match {
      case null => throw new NoSuchElementException
      case Single(v) =>
        _next = null
        v
      case Repeated(v, tail) =>
        _next = tail
        v
    }
  }

  def toList: List[Opt[String]] = iterator.toList

  def encodeParam(name: String): String =
    encodeParam(name, new StringBuilder).result()

  def encodeParam(name: String, sb: StringBuilder): StringBuilder = {
    val encName = urlEncode(name)
    def append(enc: Opt[String]): StringBuilder = enc match {
      case Opt.Empty => sb.append(encName)
      case Opt(v) => sb.append(encName).append("=").append(v)
    }
    @tailrec def loop(rqv: RawQueryValue): StringBuilder = rqv match {
      case Single(v) => append(v)
      case Repeated(h, t) =>
        append(h)
        sb.append("&")
        loop(t)
    }
    loop(this)
  }

  def exploded: RawQueryValue = RawQueryValue.fromRaw {
    val it = iterator.flatMap {
      case Opt.Empty | Opt("") => Iterator.empty
      case Opt(v) => v.split(",").iterator.map(Opt(_))
    }
    if (it.nonEmpty) it else Iterator(Opt.Empty)
  }

  def unexploded: RawQueryValue =
    Single(Opt(iterator.flatten.mkString(",")))

  def firstPlainValue: PlainValue =
    PlainValue(first.fold("")(urlDecode))
}
object RawQueryValue {
  def fromRaw(values: TraversableOnce[Opt[String]]): RawQueryValue =
    if (values.isEmpty) throw new IllegalArgumentException("no values")
    else values.foldRight(null: RawQueryValue) {
      case (v, null) => Single(v)
      case (v, tail) => Repeated(v, tail)
    }

  def plain(value: String): RawQueryValue =
    Single(Opt(urlEncode(value)))

  /**
    * @param encoded If non-empty, contains an already URL-encoded string (it may contain unencoded commas to indicate
    *                a list of values). `Opt.Empty` indicates a "flag" query parameter (without `=` and value).
    */
  // not using plain List in order to statically ensure non-emptiness and avoid too much boxing of Opts
  case class Single(encoded: Opt[String]) extends RawQueryValue
  case class Repeated(encoded: Opt[String], tail: RawQueryValue) extends RawQueryValue

  def urlEncode(str: String): String =
    URLEncoder.encode(str, spaceAsPlus = true)

  def urlDecode(str: String): String =
    URLEncoder.decode(str, plusAsSpace = true)

  def encodeQuery(query: Mapping[RawQueryValue]): String =
    encodeQuery(query, new StringBuilder).result()

  def encodeQuery(query: Mapping[RawQueryValue], builder: StringBuilder): StringBuilder = {
    val it = query.entries.iterator
    if (it.hasNext) {
      val (name, rqv) = it.next()
      rqv.encodeParam(name, builder)
      it.foreach { case (name, rqv) =>
        rqv.encodeParam(name, builder.append("&"))
      }
    }
    builder
  }

  def decodeQuery(queryString: String): Mapping[RawQueryValue] = {
    def splitPart(part: String): (String, String) = part.split("=", 2) match {
      case Array(name, value) => urlDecode(name) -> value
      case Array(name) => urlDecode(name) -> null
    }
    val entries = queryString.split("&").foldRight(List.empty[(String, RawQueryValue)]) {
      case (part, Nil) => splitPart(part) match {
        case (n, v) => List(n -> Single(v.opt))
      }
      case (part, (nextName, nextRqv) :: tail) => splitPart(part) match {
        // only grouping adjacent params with the same name into Multiple TODO improve?
        case (`nextName`, v) => (nextName, Repeated(v.opt, nextRqv)) :: tail
        case (name, v) => (name, Single(v.opt)) :: (nextName, nextRqv) :: tail
      }
    }
    Mapping(entries)
  }

  implicit def plainValueBasedAsRaw[T](implicit asPlain: AsRaw[PlainValue, T]): AsRaw[RawQueryValue, T] =
    AsRaw.create(real => Single(urlEncode(asPlain.asRaw(real).value).opt))

  implicit def plainValueBasedAsReal[T](implicit fromPlain: AsReal[PlainValue, T]): AsReal[RawQueryValue, T] =
    AsReal.create(raw => fromPlain.asReal(raw.firstPlainValue))

  // comma-separated representation of collections (OpenAPI Parameter: explode = false)
  // FIXME: a collection containing single empty string is unrepresentable (even with explode = true)
  implicit def plainValueBasedIterableAsRaw[C[X] <: BIterable[X], T](
    implicit asPlain: AsRaw[PlainValue, T]
  ): AsRaw[RawQueryValue, C[T]] =
    AsRaw.create(real => Single(real.iterator.map(v => urlEncode(asPlain.asRaw(v).value)).mkString(",").opt))

  implicit def plainValueBasedIterableAsReal[C[X] <: BIterable[X], T](
    implicit fromPlain: AsReal[PlainValue, T], cbf: CanBuildFrom[Nothing, T, C[T]]
  ): AsReal[RawQueryValue, C[T]] =
    AsReal.create(_.iterator.flatMap { vopt =>
      vopt.filter(_.nonEmpty).map(_.split(",").iterator).getOrElse(Iterator.empty)
        .map(v => fromPlain.asReal(PlainValue(urlDecode(v))))
    }.to[C])

  @implicitNotFound("#{forPlain}")
  implicit def asRealNotFound[T](
    implicit forPlain: ImplicitNotFound[AsReal[PlainValue, T]]
  ): ImplicitNotFound[AsReal[RawQueryValue, T]] = ImplicitNotFound()

  @implicitNotFound("#{forPlain}")
  implicit def asRawNotFound[T](
    implicit forJson: ImplicitNotFound[AsRaw[PlainValue, T]]
  ): ImplicitNotFound[AsRaw[RawQueryValue, T]] = ImplicitNotFound()

  @implicitNotFound("#{forPlain}")
  implicit def iterableAsRealNotFound[C[X] <: BIterable[X], T](
    implicit forPlain: ImplicitNotFound[AsReal[PlainValue, T]]
  ): ImplicitNotFound[AsReal[RawQueryValue, C[T]]] = ImplicitNotFound()

  @implicitNotFound("#{forPlain}")
  implicit def iterableAsRawNotFound[C[X] <: BIterable[X], T](
    implicit forJson: ImplicitNotFound[AsRaw[PlainValue, T]]
  ): ImplicitNotFound[AsRaw[RawQueryValue, C[T]]] = ImplicitNotFound()
}
