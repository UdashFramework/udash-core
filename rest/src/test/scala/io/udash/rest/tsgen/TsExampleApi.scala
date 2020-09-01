package io.udash.rest.tsgen

import java.io.File

import com.avsystem.commons._
import io.udash.rest._
import io.udash.rest.tsgen.other.{Enumik, OtherApi}

import scala.concurrent.Future

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

object TsExampleApiImpl extends TsExampleApi {
  def prefix(paf: Boolean): OtherApi = new OtherApi {
    def echo(frjend: MajFriend, opcja: Enumik): Future[MajFriend] = Future.successful(frjend)
  }

  def postMe(id: String, thing: Int, body: MajFriend): Future[Unit] = Future.unit
  def create(name: String, age: Int, skills: Seq[String], extra: Opt[Double]): Future[String] =
    Future.successful("ajdi")
  def find(id: String): Future[Opt[MajFriend]] =
    Future.successful(Opt(MajFriend("Fred", 18, Seq("doing"), Opt(3.14))))
}

object test {
  def main(args: Array[String]): Unit = {
    val ctx = new TsGenerator("codecs", "raw")
    ctx.resolve(TsExampleApi.tsRestApiMetadata)
    ctx.write(new File("./rest/ts-test/src"))

    //    val server = new Server(9090)
    //    val handler = new ServletContextHandler
    //    handler.addServlet(new ServletHolder(RestServlet[TsExampleApi](TsExampleApiImpl)), "/*")
    //    server.setHandler(handler)
    //    server.start()
    //    server.join()
  }
}
