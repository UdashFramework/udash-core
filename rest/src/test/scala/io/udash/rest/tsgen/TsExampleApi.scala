package io.udash.rest.tsgen

import com.avsystem.commons.Opt
import com.avsystem.commons.meta.MacroInstances
import com.avsystem.commons.serialization.GenCodec
import io.udash.rest._
import io.udash.rest.openapi.{OpenApiMetadata, RestSchema}
import io.udash.rest.raw.{RawRest, RestMetadata}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}

import scala.concurrent.Future

trait TsRestImplicits extends DefaultRestImplicits {

}
object TsRestImplicits extends TsRestImplicits

trait TsRestDataInstances[T] extends CodecWithStructure[T] {
  def tsTypeMetadata: TsTypeMetadata[T]
}

abstract class TsRestDataCompanion[T](implicit instances: MacroInstances[TsRestImplicits, TsRestDataInstances[T]]) {
  implicit lazy val tsTypeTag: TsJsonTypeTag[T] = instances(TsRestImplicits, this).tsTypeMetadata.tsTypeTag
  implicit lazy val codec: GenCodec[T] = instances(TsRestImplicits, this).codec
  implicit lazy val restSchema: RestSchema[T] = instances(TsRestImplicits, this).structure.standaloneSchema

  def tsType: TsJsonType = tsTypeTag.tsType
}

trait TsRestApiInstances[Real] extends OpenApiFullInstances[Real] {
  def tsRestApiMetadata: TsRestApiMetadata[Real]
}

abstract class TsRestApiCompanion[Real](
  implicit inst: MacroInstances[TsRestImplicits, TsRestApiInstances[Real]]
) {
  implicit final lazy val restMetadata: RestMetadata[Real] = inst(TsRestImplicits, this).metadata
  implicit final lazy val restAsRaw: RawRest.AsRawRpc[Real] = inst(TsRestImplicits, this).asRaw
  implicit final lazy val restAsReal: RawRest.AsRealRpc[Real] = inst(TsRestImplicits, this).asReal
  implicit final lazy val openapiMetadata: OpenApiMetadata[Real] = inst(TsRestImplicits, this).openapiMetadata
  implicit final lazy val tsRestApiMetadata: TsRestApiMetadata[Real] = inst(TsRestImplicits, this).tsRestApiMetadata
}

case class MajFriend(
  name: String,
  age: Int,
  skills: Seq[String],
  extra: Opt[Double],
)
object MajFriend extends TsRestDataCompanion[MajFriend]

trait TsExampleApi {
  @Prefix("fuu/bar") def prefix(@Path("after/paf") paf: Boolean): OtherApi
  @CustomBody def postMe(@Path id: String, @Query("tink") thing: Int, body: MajFriend): Future[Unit]
  @PUT def create(name: String, age: Int, skills: Seq[String], extra: Opt[Double]): Future[String]
  @GET def find(id: String): Future[Opt[MajFriend]]
}
object TsExampleApi extends TsRestApiCompanion[TsExampleApi]

trait OtherApi {

}
object OtherApi extends TsRestApiCompanion[OtherApi]

object TsExampleApiImpl extends TsExampleApi {
  def prefix(paf: Boolean): OtherApi = new OtherApi {}
  def postMe(id: String, thing: Int, body: MajFriend): Future[Unit] = Future.unit
  def create(name: String, age: Int, skills: Seq[String], extra: Opt[Double]): Future[String] =
    Future.successful("ajdi")
  def find(id: String): Future[Opt[MajFriend]] =
    Future.successful(Opt(MajFriend("Fred", 18, Seq("doing"), Opt(3.14))))
}

object test {
  def main(args: Array[String]): Unit = {
    val ctx = new TsGenerationCtx("codecs", "raw")
    ctx.add(TsExampleApi.tsRestApiMetadata)
    println(ctx.allDefinitions)

    val server = new Server(9090)
    val handler = new ServletContextHandler
    handler.addServlet(new ServletHolder(RestServlet[TsExampleApi](TsExampleApiImpl)), "/*")
    server.setHandler(handler)
    server.start()
    server.join()
  }
}
