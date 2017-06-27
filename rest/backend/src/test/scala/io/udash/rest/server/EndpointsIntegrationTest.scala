package io.udash.rest.server

import fr.hmil.roshttp.exceptions.HttpException
import fr.hmil.roshttp.response.{HttpResponse, SimpleHttpResponse}
import io.udash.rest._
import io.udash.rest.internal.RESTConnector
import io.udash.testing.UdashSharedTest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

class EndpointsIntegrationTest extends UdashSharedTest with BeforeAndAfterAll {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val port = 44598
  val contextPrefix = "/rest_api/"
  val server = new Server(port)
  val context = new ServletContextHandler()
  context.setSessionHandler(new SessionHandler)
  context.setGzipHandler(new GzipHandler)

  val holder = new ServletHolder(new RestServlet(new DefaultExposesREST[TestServerRESTInterface](new TestServerRESTInterfaceImpl)))
  holder.setAsyncSupported(true)
  context.addServlet(holder, s"${contextPrefix}*")
  server.setHandler(context)

  val restServer = DefaultServerREST[TestServerRESTInterface]("127.0.0.1", port, contextPrefix)

  def await[T](f: Future[T]): T =
    Await.result(f, 5 seconds)

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    server.start()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }

  val serverConnector = new DefaultRESTConnector("127.0.0.1", port, contextPrefix)

  "REST endpoint" should {
    "work with Udash REST client (1)" in {
      await(restServer.serviceOne().deeper().load(5)) should be(TestRESTRecord(Some(5), "one/deeper"))
    }
    "work with Udash REST client (2)" in {
      await(restServer.serviceThree("qwe/asd").deeper().load(321)) should be(TestRESTRecord(Some(321), "three/qwe/asd/deeper"))
    }
    "work with Udash REST client (3)" in {
      await(restServer.serviceOne().load()).size should be(3)
    }
    "work with Udash REST client (4)" in {
      await(restServer.serviceOne().load(42, "a\\bc", "q:/we")) should be(TestRESTRecord(Some(42), "one/load/a\\bc/q:/we"))
    }
    "work with Udash REST client (5)" in {
      await(restServer.serviceTwo("tok:/\\en123", "en_GB").create(TestRESTRecord(None, "test123"))) should be(TestRESTRecord(None, "two/tok:/\\en123/en_GB/create/None/test123"))
    }
    "work with Udash REST client (6)" in {
      await(restServer.serviceOne().update(123321)(TestRESTRecord(Some(52), "test123"))) should be(TestRESTRecord(Some(123321), "one/update/Some(52)/test123"))
    }
    "work with Udash REST client (7)" in {
      await(restServer.serviceThree("asd").modify(321)("mod", 5)) should be(TestRESTRecord(Some(321), "three/asd/modify/mod/5"))
    }
    "work with Udash REST client (8)" in {
      await(restServer.serviceTwo("token123", "en_GB").delete(222)) should be(TestRESTRecord(Some(222), "two/token123/en_GB/delete"))
    }
    "report valid HTTP codes (1)" in {
      intercept[HttpException[SimpleHttpResponse]](
        await(serverConnector.send("/non/existing/path", RESTConnector.POST, Map.empty, Map.empty, null))
      ).response.statusCode should be(404)
    }
    "report valid HTTP codes (2)" in {
      intercept[HttpException[SimpleHttpResponse]](
        await(serverConnector.send("/serviceOne/loadAll", RESTConnector.POST, Map.empty, Map.empty, null))
      ).response.statusCode should be(405)
    }
    "report valid HTTP codes (3)" in {
      intercept[HttpException[SimpleHttpResponse]](
        await(serverConnector.send("/serviceTwo/loadAll", RESTConnector.GET, Map.empty, Map.empty, null))
      ).response.statusCode should be(400)
    }
    "report valid HTTP codes (4)" in {
      intercept[HttpException[SimpleHttpResponse]](
        await(serverConnector.send("/serviceThree/loadAll", RESTConnector.GET, Map.empty, Map.empty, null))
      ).response.statusCode should be(404)
      intercept[HttpException[SimpleHttpResponse]](
        await(serverConnector.send("/service_three/loadAll", RESTConnector.GET, Map.empty, Map.empty, null))
      ).response.statusCode should be(404) // "loadAll" is interpreted as URL argument from `serviceThree` getter
    }
  }

  private class TestServerRESTInterfaceImpl extends TestServerRESTInterface {
    override def serviceOne(): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl("one")

    override def serviceTwo(token: String, lang: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"two/$token/$lang")

    override def serviceThree(arg: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"three/$arg")
  }

  private class TestServerRESTInternalInterfaceImpl(data: String) extends TestServerRESTInternalInterface {
    override def load(): Future[Seq[TestRESTRecord]] =
      Future.successful(Seq(
        TestRESTRecord(Some(1), s"$data/load"),
        TestRESTRecord(Some(2), s"$data/load"),
        TestRESTRecord(Some(3), s"$data/load")
      ))

    override def load(id: Int, trash: String, trash2: String): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), s"$data/load/$trash/$trash2"))

    override def create(record: TestRESTRecord): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(None, s"$data/create/${record.id}/${record.s}"))

    override def update(id: Int)(record: TestRESTRecord): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), s"$data/update/${record.id}/${record.s}"))

    override def modify(id: Int)(s: String, i: Int): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), s"$data/modify/$s/$i"))

    override def delete(id: Int): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), s"$data/delete"))

    override def deeper(): TestServerRESTDeepInterface =
      new TestServerRESTDeepInterfaceImpl(s"$data/deeper")
  }

  private class TestServerRESTDeepInterfaceImpl(data: String) extends TestServerRESTDeepInterface {
    override def load(id: Int): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), data))
  }
}
