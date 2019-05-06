package io.udash
package rest

import com.avsystem.commons._
import com.avsystem.commons.rpc.AsRawReal
import com.avsystem.commons.serialization.{flatten, whenAbsent}
import io.udash.rest.openapi.adjusters._
import io.udash.rest.openapi.{Header => OASHeader, _}
import io.udash.rest.raw._

@description("Entity identifier")
case class RestEntityId(value: String) extends AnyVal
object RestEntityId extends RestDataWrapperCompanion[String, RestEntityId]

sealed trait BaseEntity
object BaseEntity extends RestDataCompanion[BaseEntity]

@description("Flat sealed entity with some serious cases")
@flatten sealed trait FlatBaseEntity extends BaseEntity
object FlatBaseEntity extends RestDataCompanion[FlatBaseEntity]

@description("REST entity")
case class RestEntity(
  @description("entity id") id: RestEntityId,
  @whenAbsent("anonymous") name: String = whenAbsent.value,
  @description("recursive optional subentity") subentity: OptArg[RestEntity] = OptArg.Empty
) extends FlatBaseEntity
object RestEntity extends RestDataCompanion[RestEntity]

case class RestOtherEntity(fuu: Boolean, kek: List[String]) extends FlatBaseEntity

case object SingletonEntity extends FlatBaseEntity

case class CustomResp(value: String)
object CustomResp {
  implicit val asResponse: AsRawReal[RestResponse, CustomResp] = AsRawReal.create(
    cr => RestResponse(200, IMapping("X-Value" -> PlainValue(cr.value)), HttpBody.plain("Yes")),
    resp => CustomResp(resp.headers("X-Value").value)
  )
  implicit val restResponses: RestResponses[CustomResp] = new RestResponses[CustomResp] {
    def responses(resolver: SchemaResolver, schemaTransform: RestSchema[CustomResp] => RestSchema[_]): Responses =
      Responses(byStatusCode = Map(200 -> RefOr(Response(
        description = "Custom response",
        headers = Map("X-Value" -> RefOr(OASHeader(
          schema = RefOr(Schema.String)
        ))),
        content = Map(HttpBody.PlainType -> MediaType(
          schema = RefOr(Schema.String)
        ))
      ))))
  }
}

@description("binary bytes")
case class Bytes(bytes: Array[Byte]) extends AnyVal
object Bytes extends RestDataWrapperCompanion[Array[Byte], Bytes]

trait RestTestApi {
  @GET def trivialGet: Future[Unit]
  @GET def failingGet: Future[Unit]
  @GET def moreFailingGet: Future[Unit]
  @GET def neverGet: Future[Unit]

  @GET def getEntity(id: RestEntityId): Future[RestEntity]

  @pathDescription("path with a followed by b")
  @description("A really complex GET operation")
  @GET("multi/param") def complexGet(
    @Path("p1") p1: Int, @description("Very serious path parameter") @title("Stri") @Path p2: String,
    @Header("X-H1") h1: Int, @Header("X-H2") h2: String,
    q1: Int, @Query("q=2") @whenAbsent("q2def") q2: String = whenAbsent.value,
    @Cookie c1: Int, @Cookie("coo") c2: String
  ): Future[RestEntity]

  @POST("multi/param") def multiParamPost(
    @Path("p1") p1: Int, @Path p2: String,
    @Header("X-H1") h1: Int, @Header("X-H2") h2: String,
    @Query q1: Int, @Query("q=2") q2: String,
    b1: Int, @Body("b\"2") @description("weird body field") b2: String
  ): Future[RestEntity]

  @CustomBody
  @bodyDescription("Serious body")
  @responseDescription("Serious response")
  @PUT("") def singleBodyPut(
    @description("REST entity description") entity: RestEntity
  ): Future[String]

  @FormBody
  @POST def formPost(
    @Query q1: String,
    p1: String,
    @whenAbsent(42) p2: Int = whenAbsent.value
  ): Future[String]

  @pathSummary("summary for prefix paths")
  def prefix(
    p0: String,
    @Header("X-H0") h0: String,
    @Query @example("q0example") q0: String
  ): RestTestSubApi

  @Prefix("") def transparentPrefix: RestTestSubApi

  def complexParams(
    baseEntity: BaseEntity,
    @whenAbsent(Opt.Empty) flatBaseEntity: Opt[FlatBaseEntity]
  ): Future[Unit]

  @PUT def complexParams(
    flatBaseEntity: FlatBaseEntity,
    @whenAbsent(Opt.Empty) baseEntity: Opt[BaseEntity]
  ): Future[Unit]

  def customResponse(@Query value: String): Future[CustomResp]

  @CustomBody def binaryEcho(bytes: Array[Byte]): Future[Array[Byte]]
  @CustomBody def wrappedBinaryEcho(bytes: Bytes): Future[Bytes]
  @CustomBody def wrappedBody(id: RestEntityId): Future[RestEntityId]
}
object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
  val Impl: RestTestApi = new RestTestApi {
    def trivialGet: Future[Unit] = Future.unit
    def failingGet: Future[Unit] = Future.failed(HttpErrorException(503, "nie"))
    def moreFailingGet: Future[Unit] = throw HttpErrorException(503, "nie")
    def neverGet: Future[Unit] = Promise[Unit].future // Future.never if it wasn't for Scala 2.11
    def getEntity(id: RestEntityId): Future[RestEntity] = Future.successful(RestEntity(id, s"${id.value}-name"))
    def complexGet(p1: Int, p2: String, h1: Int, h2: String, q1: Int, q2: String, c1: Int, c2: String): Future[RestEntity] =
      Future.successful(RestEntity(RestEntityId(s"$p1-$h1-$q1-$c1"), s"$p2-$h2-$q2-$c2"))
    def multiParamPost(p1: Int, p2: String, h1: Int, h2: String, q1: Int, q2: String, b1: Int, b2: String): Future[RestEntity] =
      Future.successful(RestEntity(RestEntityId(s"$p1-$h1-$q1-$b1"), s"$p2-$h2-$q2-$b2"))
    def singleBodyPut(entity: RestEntity): Future[String] =
      Future.successful(entity.toString)
    def formPost(q1: String, p1: String, p2: Int): Future[String] =
      Future.successful(s"$q1-$p1-$p2")
    def prefix(p0: String, h0: String, q0: String): RestTestSubApi =
      RestTestSubApi.impl(s"$p0-$h0-$q0")
    def transparentPrefix: RestTestSubApi = RestTestSubApi.impl("")
    def complexParams(baseEntity: BaseEntity, flatBaseEntity: Opt[FlatBaseEntity]): Future[Unit] = Future.unit
    def complexParams(flatBaseEntity: FlatBaseEntity, baseEntity: Opt[BaseEntity]): Future[Unit] = Future.unit
    def customResponse(value: String): Future[CustomResp] = Future.successful(CustomResp(value))
    def binaryEcho(bytes: Array[Byte]): Future[Array[Byte]] = Future.successful(bytes)
    def wrappedBinaryEcho(bytes: Bytes): Future[Bytes] = Future.successful(bytes)
    def wrappedBody(id: RestEntityId): Future[RestEntityId] = Future.successful(id)
  }
}

trait RestTestSubApi {
  @GET def subget(@Path p1: Int, @Header("X-H1") h1: Int, q1: Int): Future[String]
}
object RestTestSubApi extends DefaultRestApiCompanion[RestTestSubApi] {
  def impl(arg: String): RestTestSubApi = new RestTestSubApi {
    def subget(p1: Int, h1: Int, q1: Int): Future[String] = Future.successful(s"$arg-$p1-$h1-$q1")
  }
}
