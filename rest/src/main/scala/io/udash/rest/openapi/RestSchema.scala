package io.udash
package rest.openapi

import java.util.UUID

import com.avsystem.commons._
import com.avsystem.commons.misc.{ImplicitNotFound, NamedEnum, NamedEnumCompanion, Timestamp}
import io.udash.rest.raw.RawRest.AsyncEffect
import io.udash.rest.raw._

import scala.annotation.implicitNotFound
import scala.collection.mutable

@implicitNotFound("RestSchema for ${T} not found")
trait RestSchema[T] { self =>
  /**
    * Creates a [[Schema]] object or external schema reference.
    * May use [[SchemaResolver]] to resolve any dependent `RestSchema` instances.
    */
  def createSchema(resolver: SchemaResolver): RefOr[Schema]

  /**
    * Optional name of the schema. When `RestSchema` is named, schema created by [[createSchema]] will be registered
    * under that name in [[SchemaRegistry]] and ultimately included into [[Components]] of the [[OpenApi]] document.
    * All direct usages of the schema in OpenAPI document will be replaced by a reference to
    * the registered schema, i.e. `{"$$ref": "#/components/schemas/<schema-name>"}`.
    *
    * If schema is unnamed, it will be always inlined instead of being replaced by a reference.
    */
  def name: Opt[String]

  def map[S](fun: RefOr[Schema] => Schema, newName: OptArg[String] = OptArg.Empty): RestSchema[S] =
    RestSchema.create(resolver => RefOr(fun(resolver.resolve(self))), newName)
  def named(name: String): RestSchema[T] =
    RestSchema.create(createSchema, name)
  def unnamed: RestSchema[T] =
    RestSchema.create(createSchema)
}
object RestSchema {
  def apply[T](implicit rt: RestSchema[T]): RestSchema[T] = rt

  def create[T](creator: SchemaResolver => RefOr[Schema], schemaName: OptArg[String] = OptArg.Empty): RestSchema[T] =
    new RestSchema[T] {
      def createSchema(resolver: SchemaResolver): RefOr[Schema] = creator(resolver)
      def name: Opt[String] = schemaName.toOpt
    }

  def named[T](name: String)(creator: SchemaResolver => RefOr[Schema]): RestSchema[T] =
    create(creator, name)

  def plain[T](schema: Schema): RestSchema[T] =
    RestSchema.create(_ => RefOr(schema))

  def ref[T](refstr: String): RestSchema[T] =
    RestSchema.create(_ => RefOr.ref(refstr))

  def lazySchema[T](actual: => RestSchema[T]): RestSchema[T] =
    new RestSchema[T] {
      private lazy val actualSchema = actual
      def createSchema(resolver: SchemaResolver): RefOr[Schema] = actualSchema.createSchema(resolver)
      def name: Opt[String] = actualSchema.name
    }

  implicit lazy val NothingSchema: RestSchema[Nothing] =
    RestSchema.create(_ => throw new NotImplementedError("RestSchema[Nothing]"))

  implicit lazy val UnitSchema: RestSchema[Unit] = plain(Schema(nullable = true))
  implicit lazy val NullSchema: RestSchema[Null] = plain(Schema(nullable = true))
  implicit lazy val VoidSchema: RestSchema[Void] = plain(Schema(nullable = true))

  implicit lazy val BooleanSchema: RestSchema[Boolean] = plain(Schema.Boolean)
  implicit lazy val CharSchema: RestSchema[Char] = plain(Schema.Char)
  implicit lazy val ByteSchema: RestSchema[Byte] = plain(Schema.Byte)
  implicit lazy val ShortSchema: RestSchema[Short] = plain(Schema.Short)
  implicit lazy val IntSchema: RestSchema[Int] = plain(Schema.Int)
  implicit lazy val LongSchema: RestSchema[Long] = plain(Schema.Long)
  implicit lazy val FloatSchema: RestSchema[Float] = plain(Schema.Float)
  implicit lazy val DoubleSchema: RestSchema[Double] = plain(Schema.Double)
  implicit lazy val BigIntSchema: RestSchema[BigInt] = plain(Schema.Integer)
  implicit lazy val BigDecimalSchema: RestSchema[BigDecimal] = plain(Schema.Number)

  implicit lazy val JBooleanSchema: RestSchema[JBoolean] = plain(Schema.Boolean.copy(nullable = true))
  implicit lazy val JCharacterSchema: RestSchema[JCharacter] = plain(Schema.Char.copy(nullable = true))
  implicit lazy val JByteSchema: RestSchema[JByte] = plain(Schema.Byte.copy(nullable = true))
  implicit lazy val JShortSchema: RestSchema[JShort] = plain(Schema.Short.copy(nullable = true))
  implicit lazy val JIntegerSchema: RestSchema[JInteger] = plain(Schema.Int.copy(nullable = true))
  implicit lazy val JLongSchema: RestSchema[JLong] = plain(Schema.Long.copy(nullable = true))
  implicit lazy val JFloatSchema: RestSchema[JFloat] = plain(Schema.Float.copy(nullable = true))
  implicit lazy val JDoubleSchema: RestSchema[JDouble] = plain(Schema.Double.copy(nullable = true))
  implicit lazy val JBigIntegerSchema: RestSchema[JBigInteger] = plain(Schema.Integer)
  implicit lazy val JBigDecimalSchema: RestSchema[JBigDecimal] = plain(Schema.Number)

  implicit lazy val TimestampSchema: RestSchema[Timestamp] = plain(Schema.DateTime)
  implicit lazy val JDateSchema: RestSchema[JDate] = plain(Schema.DateTime)
  implicit lazy val StringSchema: RestSchema[String] = plain(Schema.String)
  implicit lazy val SymbolSchema: RestSchema[Symbol] = plain(Schema.String)
  implicit lazy val UuidSchema: RestSchema[UUID] = plain(Schema.Uuid)

  implicit def arraySchema[T: RestSchema]: RestSchema[Array[T]] =
    RestSchema[T].map(Schema.arrayOf(_))
  implicit def seqSchema[C[X] <: BSeq[X], T: RestSchema]: RestSchema[C[T]] =
    RestSchema[T].map(Schema.arrayOf(_))
  implicit def setSchema[C[X] <: BSet[X], T: RestSchema]: RestSchema[C[T]] =
    RestSchema[T].map(Schema.arrayOf(_, uniqueItems = true))
  implicit def jCollectionSchema[C[X] <: JCollection[X], T: RestSchema]: RestSchema[C[T]] =
    RestSchema[T].map(Schema.arrayOf(_))
  implicit def jSetSchema[C[X] <: JSet[X], T: RestSchema]: RestSchema[C[T]] =
    RestSchema[T].map(Schema.arrayOf(_, uniqueItems = true))
  implicit def mapSchema[M[X, Y] <: BMap[X, Y], K, V: RestSchema]: RestSchema[M[K, V]] =
    RestSchema[V].map(Schema.mapOf)
  implicit def jMapSchema[M[X, Y] <: JMap[X, Y], K, V: RestSchema]: RestSchema[M[K, V]] =
    RestSchema[V].map(Schema.mapOf)

  implicit def optionSchema[T: RestSchema]: RestSchema[Option[T]] =
    RestSchema[T].map(Schema.nullable)
  implicit def optSchema[T: RestSchema]: RestSchema[Opt[T]] =
    RestSchema[T].map(Schema.nullable)
  implicit def optArgSchema[T: RestSchema]: RestSchema[OptArg[T]] =
    RestSchema[T].map(Schema.nullable)
  implicit def optRefSchema[T >: Null : RestSchema]: RestSchema[OptRef[T]] =
    RestSchema[T].map(Schema.nullable)
  implicit def nOptSchema[T: RestSchema]: RestSchema[NOpt[T]] =
    RestSchema[T].map(Schema.nullable)

  implicit def eitherSchema[A: RestSchema, B: RestSchema]: RestSchema[Either[A, B]] =
    RestSchema.create { resolver =>
      RefOr(Schema(oneOf = List(
        RefOr(Schema(`type` = DataType.Object, properties =
          Map("Left" -> resolver.resolve(RestSchema[A])), required = List("Left"))),
        RefOr(Schema(`type` = DataType.Object, properties =
          Map("Right" -> resolver.resolve(RestSchema[B])), required = List("Right")))
      )))
    }

  implicit def namedEnumSchema[E <: NamedEnum](implicit comp: NamedEnumCompanion[E]): RestSchema[E] =
    RestSchema.plain(Schema.enumOf(comp.values.iterator.map(_.name).toList))
  implicit def jEnumSchema[E <: Enum[E]](implicit ct: ClassTag[E]): RestSchema[E] =
    RestSchema.plain(Schema.enumOf(ct.runtimeClass.getEnumConstants.iterator.map(_.asInstanceOf[E].name).toList))
}

/**
  * Typeclass which defines how an OpenAPI [[io.udash.rest.openapi.Responses Responses]] Object will look like for a
  * given type. By default, [[io.udash.rest.openapi.RestResponses RestResponses]] is derived based on
  * [[io.udash.rest.openapi.RestSchema RestSchema]] for that type.
  */
@implicitNotFound("RestResponses instance for ${T} not found")
trait RestResponses[T] {
  /**
    * @param schemaTransform Should be used if [[RestResponses]] is being built based on [[RestSchema]] for
    *                        the same type. The transformation may adjust the schema and give it a different name.
    */
  def responses(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Responses
}
object RestResponses {
  def apply[T](implicit r: RestResponses[T]): RestResponses[T] = r

  final val SuccessDescription = "Success"

  implicit val UnitResponses: RestResponses[Unit] =
    new RestResponses[Unit] {
      def responses(resolver: SchemaResolver, schemaTransform: RestSchema[Unit] => RestSchema[_]): Responses =
        Responses(byStatusCode = Map(
          204 -> RefOr(Response(description = SuccessDescription))
        ))
    }

  implicit val ByteArrayResponses: RestResponses[Array[Byte]] =
    new RestResponses[Array[Byte]] {
      def responses(resolver: SchemaResolver, schemaTransform: RestSchema[Array[Byte]] => RestSchema[_]): Responses = {
        val schema = resolver.resolve(schemaTransform(RestSchema.plain(Schema.Binary)))
        Responses(byStatusCode = Map(
          200 -> RefOr(Response(
            description = SuccessDescription,
            content = Map(HttpBody.OctetStreamType -> MediaType(schema = schema))
          ))
        ))
      }
    }

  implicit def fromSchema[T: RestSchema]: RestResponses[T] =
    new RestResponses[T] {
      def responses(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Responses =
        Responses(byStatusCode = Map(
          200 -> RefOr(Response(
            description = SuccessDescription,
            content = Map(HttpBody.JsonType ->
              MediaType(schema = resolver.resolve(schemaTransform(RestSchema[T])))
            )
          ))
        ))
    }

  @implicitNotFound("RestResponses instance for ${T} not found, because:\n#{forSchema}")
  implicit def notFound[T](implicit forSchema: ImplicitNotFound[RestSchema[T]]): ImplicitNotFound[RestResponses[T]] =
    ImplicitNotFound()
}

/**
  * Just like [[io.udash.rest.openapi.RestResponses RestResponses]],
  * [[io.udash.rest.openapi.RestResultType RestResultType]] is a typeclass that defines how an OpenAPI
  * Responses Object will look like for a HTTP method which returns given type. The difference between
  * [[io.udash.rest.openapi.RestResultType RestResultType]] and [[io.udash.rest.openapi.RestResponses RestResponses]]
  * is that [[io.udash.rest.openapi.RestResultType RestResultType]] is defined for full result
  * type which usually is some kind of asynchronous wrapper over actual result type (e.g. `Future`).
  * In such situation, [[io.udash.rest.openapi.RestResponses RestResponses]] must be provided for `T` while
  * [[io.udash.rest.openapi.RestResultType RestResultType]] is provided
  * for `Future[T]` (or whatever async wrapper is used), based on the [[io.udash.rest.openapi.RestResponses RestResponses]]
  * instance of `T`. You can see an example of this in [[io.udash.rest.FutureRestImplicits FutureRestImplicits]].
  *
  * [[io.udash.rest.openapi.RestResultType RestResultType]] for [[io.udash.rest.openapi.OpenApiMetadata OpenApiMetadata]]
  * is analogous to [[io.udash.rest.raw.HttpResponseType HttpResponseType]]
  * for [[io.udash.rest.raw.RestMetadata RestMetadata]].
  */
final case class RestResultType[T](responses: SchemaResolver => Responses)
object RestResultType {
  implicit def forAsyncEffect[F[_] : AsyncEffect, T: RestResponses]: RestResultType[F[T]] =
    RestResultType(RestResponses[T].responses(_, identity))

  @implicitNotFound("#{forResponseType}")
  implicit def notFound[T](
    implicit forResponseType: ImplicitNotFound[HttpResponseType[T]]
  ): ImplicitNotFound[RestResultType[T]] = ImplicitNotFound()

  @implicitNotFound("#{forRestResponses}")
  implicit def notFoundForAsyncEffect[F[_] : AsyncEffect, T](
    implicit forRestResponses: ImplicitNotFound[RestResponses[T]]
  ): ImplicitNotFound[RestResultType[F[T]]] = ImplicitNotFound()
}

@implicitNotFound("RestRequestBody instance for ${T} not found")
trait RestRequestBody[T] {
  /**
    * @param schemaTransform Should be used if [[RestRequestBody]] is being built based on [[RestSchema]] for
    *                        the same type. The transformation may adjust the schema and give it a different name.
    */
  def requestBody(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Opt[RefOr[RequestBody]]
}
object RestRequestBody {
  def apply[T](implicit r: RestRequestBody[T]): RestRequestBody[T] = r

  def simpleRequestBody(mediaType: String, schema: RefOr[Schema], required: Boolean): Opt[RefOr[RequestBody]] =
    Opt(RefOr(RequestBody(
      content = Map(
        mediaType -> MediaType(schema = schema)
      ),
      required = required
    )))

  implicit val UnitRequestBody: RestRequestBody[Unit] = new RestRequestBody[Unit] {
    def requestBody(resolver: SchemaResolver, schemaTransform: RestSchema[Unit] => RestSchema[_]): Opt[RefOr[RequestBody]] =
      Opt.Empty
  }

  implicit val ByteArrayRequestBody: RestRequestBody[Array[Byte]] = new RestRequestBody[Array[Byte]] {
    def requestBody(resolver: SchemaResolver, schemaTransform: RestSchema[Array[Byte]] => RestSchema[_]): Opt[RefOr[RequestBody]] = {
      val schema = resolver.resolve(schemaTransform(RestSchema.plain(Schema.Binary)))
      simpleRequestBody(HttpBody.OctetStreamType, schema, required = true)
    }
  }

  implicit def fromSchema[T: RestSchema]: RestRequestBody[T] =
    new RestRequestBody[T] {
      def requestBody(resolver: SchemaResolver, schemaTransform: RestSchema[T] => RestSchema[_]): Opt[RefOr[RequestBody]] = {
        val schema = resolver.resolve(schemaTransform(RestSchema[T]))
        simpleRequestBody(HttpBody.JsonType, schema, required = true)
      }
    }

  @implicitNotFound("RestRequestBody instance for ${T} not found, because:\n#{forSchema}")
  implicit def notFound[T](implicit forSchema: ImplicitNotFound[RestSchema[T]]): ImplicitNotFound[RestRequestBody[T]] =
    ImplicitNotFound()
}

trait SchemaResolver {
  def resolve(schema: RestSchema[_]): RefOr[Schema]
}

final class InliningResolver extends SchemaResolver {
  private[this] val resolving = new MHashSet[String]

  def resolve(schema: RestSchema[_]): RefOr[Schema] =
    try {
      schema.name.foreach { n =>
        if (!resolving.add(n)) {
          throw new IllegalArgumentException(s"Recursive schema reference: $n")
        }
      }
      schema.createSchema(this)
    }
    finally {
      schema.name.foreach(resolving.remove)
    }
}
object InliningResolver {
  def resolve(schema: RestSchema[_]): RefOr[Schema] =
    new InliningResolver().resolve(schema)
}

final class SchemaRegistry(
  nameToRef: String => String = name => s"#/components/schemas/$name",
  initial: Iterable[(String, RefOr[Schema])] = Map.empty
) extends SchemaResolver {

  private[this] case class Entry(source: Opt[RestSchema[_]], schema: RefOr[Schema])

  private[this] val resolving = new MHashSet[String]
  private[this] val registry = new mutable.OpenHashMap[String, MListBuffer[Entry]]
    .setup(_ ++= initial.iterator.map { case (n, s) => (n, MListBuffer[Entry](Entry(Opt.Empty, s))) })

  def registeredSchemas: Map[String, RefOr[Schema]] =
    registry.iterator.map { case (k, entries) =>
      entries.result() match {
        case Entry(_, schema) :: Nil => (k, schema)
        case _ => throw new IllegalArgumentException(
          s"Multiple schemas named $k detected - you may want to disambiguate them using @name annotation"
        )
      }
    }.intoMap[ITreeMap]

  def resolve(restSchema: RestSchema[_]): RefOr[Schema] = restSchema.name match {
    case Opt(name) =>
      if (!resolving.contains(name)) { // handling recursive schemas
        val entries = registry.getOrElseUpdate(name, new MListBuffer)
        if (!entries.exists(_.source.contains(restSchema))) {
          resolving += name
          val newSchema = try restSchema.createSchema(this) finally {
            resolving -= name
          }
          if (!entries.exists(_.schema == newSchema)) {
            entries += Entry(Opt(restSchema), newSchema)
          }
        }
      }
      RefOr.ref(nameToRef(name))
    case Opt.Empty =>
      restSchema.createSchema(this)
  }
}
