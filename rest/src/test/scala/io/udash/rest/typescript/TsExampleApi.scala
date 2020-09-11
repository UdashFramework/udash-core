package io.udash.rest.typescript

import java.io.File

import com.avsystem.commons._
import com.avsystem.commons.misc.Timestamp
import com.avsystem.commons.serialization.transparent
import io.udash.rest._
import io.udash.rest.typescript.other.OtherApi

import scala.concurrent.Future

case class Ajdi(id: String) extends AnyVal
object Ajdi extends RestDataWrapperCompanion[String, Ajdi]

@tsMutable
case class MajFriend(
  name: String,
  @tsMutable(false) age: Int,
  skills: List[String],
  @tsOptional extra: Opt[Double],
  @tsOptional updateTime: Opt[Timestamp] = Opt.Empty,
  bytes: Array[Byte] = Array.empty,
)
object MajFriend extends TsRestDataCompanion[MajFriend]

@transparent
case class Wrappy(stringu: String) extends AnyVal
object Wrappy extends TsRestDataCompanion[Wrappy]

trait TsExampleApi {
  @POST def postStuff(int: Int, @OptBodyField optstr: Opt[String], nono: Boolean): Future[Boolean]
  @Prefix("fuu/bar") def prefix: OtherApi
  @CustomBody def postMe(@Path id: Ajdi, body: MajFriend, @OptQuery("tink") thing: Opt[Int]): Future[Unit]
  @PUT def create(name: String, age: Int, skills: List[String], extra: Opt[Double]): Future[String]
  @GET def find(id: Ajdi): Future[Opt[MajFriend]]
  @GET def allFriends: Future[Map[Ajdi, MajFriend]]
}
object TsExampleApi extends TsRestApiCompanion[TsExampleApi]

object test {
  def main(args: Array[String]): Unit = {
    val gen = new TsGenerator
    gen.add[TsExampleApi](Vector("api"))
    gen.write(new File("./rest/ts-test/src"))

    //    val server = new Server(9090)
    //    val handler = new ServletContextHandler
    //    handler.addServlet(new ServletHolder(RestServlet[TsExampleApi](TsExampleApiImpl)), "/*")
    //    server.setHandler(handler)
    //    server.start()
    //    server.join()
  }
}
