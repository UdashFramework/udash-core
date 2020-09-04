package io.udash.rest.typescript

import java.io.File

import com.avsystem.commons._
import com.avsystem.commons.misc.Timestamp
import com.avsystem.commons.serialization.{transparent, whenAbsent}
import io.udash.rest._
import io.udash.rest.typescript.other.{Enumik, OtherApi, Tree}

import scala.concurrent.Future

case class Ajdi(id: String) extends AnyVal
object Ajdi extends RestDataWrapperCompanion[String, Ajdi]

@tsMutable
case class MajFriend(
  name: String,
  @tsMutable(false) age: Int,
  skills: List[String],
  @tsOptional(Opt.Empty) extra: Opt[Double],
  @tsOptional(Opt.Empty) updateTime: Opt[Timestamp] = Opt.Empty,
  @tsOptional(Wrappy("")) wrappy: Wrappy = whenAbsent.value
)
object MajFriend extends TsRestDataCompanion[MajFriend]

@transparent
case class Wrappy(stringu: String) extends AnyVal
object Wrappy extends TsRestDataCompanion[Wrappy]

trait TsExampleApi {
  @POST def postStuff(int: Int, @tsOptional(Opt.Empty) optstr: Opt[String]): Future[Boolean]
  @Prefix("fuu/bar") def prefix(@Path("after/paf") paf: Boolean): OtherApi
  @CustomBody def postMe(@Path id: Ajdi, body: MajFriend, @Query("tink") @tsOptional(0) thing: Int): Future[Unit]
  @PUT def create(name: String, age: Int, skills: List[String], extra: Opt[Double]): Future[String]
  @GET def find(id: Ajdi): Future[Opt[MajFriend]]
  @GET def allFriends: Future[Map[Ajdi, MajFriend]]
}
object TsExampleApi extends TsRestApiCompanion[TsExampleApi]

object TsExampleApiImpl extends TsExampleApi {
  def prefix(paf: Boolean): OtherApi = new OtherApi {
    def echo(frjend: MajFriend, opcja: Enumik): Future[MajFriend] = Future.successful(frjend)
    def gimmeTree: Future[Tree] = Future.successful(other.Leaf)
  }

  def postMe(id: Ajdi, body: MajFriend, thing: Int): Future[Unit] =
    Future.unit
  def create(name: String, age: Int, skills: List[String], extra: Opt[Double]): Future[String] =
    Future.successful("ajdi")
  def find(id: Ajdi): Future[Opt[MajFriend]] =
    Future.successful(Opt(MajFriend("Fred", 18, List("doing"), Opt(3.14))))
  def allFriends: Future[Map[Ajdi, MajFriend]] =
    Future.successful(Map.empty)
  def postStuff(int: Int, optstr: Opt[String]): Future[Boolean] =
    Future.successful(true)
}

object test {
  def main(args: Array[String]): Unit = {
    val ctx = new TsGenerator
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
