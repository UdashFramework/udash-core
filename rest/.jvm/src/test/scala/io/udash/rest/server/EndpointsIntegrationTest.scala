package io.udash.rest.server

import java.net.ConnectException

import io.udash.rest._
import io.udash.rest.internal.RESTConnector
import io.udash.rest.internal.RESTConnector.ClientException
import io.udash.testing.UdashSharedTest
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}

import scala.collection.mutable
import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, ExecutionContext, Future}

class EndpointsIntegrationTest extends UdashSharedTest with BeforeAndAfterAll with Eventually with ScalaFutures {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val firesBuffer = mutable.ArrayBuffer.empty[String]

  val port = 44598
  val contextPrefix = "/rest_api/"
  val server = new Server(port)
  val context = new ServletContextHandler()
  context.setSessionHandler(new SessionHandler)
  context.setGzipHandler(new GzipHandler)

  private val servlet = new AuthRestServlet(new DefaultExposesREST[TestServerRESTInterface](new TestServerRESTInterfaceImpl(firesBuffer)))
  val holder = new ServletHolder(servlet)
  holder.setAsyncSupported(true)
  context.addServlet(holder, s"${contextPrefix}*")
  server.setHandler(context)

  val restServer = DefaultServerREST[TestServerRESTInterface](Protocol.http, "127.0.0.1", port, contextPrefix)
  val serverConnector = new DefaultRESTConnector(Protocol.http, "127.0.0.1", port, contextPrefix)
  val inexistentServerConnector = new DefaultRESTConnector(Protocol.http, "127.0.0.1", 69, contextPrefix)

  def await[T](f: Future[T]): T =
    Await.result(f, 3 seconds)

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    server.start()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

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
      await(restServer.serviceOne().load(42, "a\\b?x=2&c", "q:/w?x=2e")) should be(TestRESTRecord(Some(42), "one/load/a\\b?x=2&c/q:/w?x=2e"))
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
    "work with Udash REST client (9)" in {
      firesBuffer.clear()
      restServer.serviceTwo("token123", "en_GB").fireAndForget(321)
      eventually {
        firesBuffer should contain("two/token123/en_GB/fireAndForget/321")
      }
    }
    "work with Udash REST client (10)" in {
      firesBuffer.clear()
      restServer.serviceTwo("token123", "en_GB").deeper().fire(123321)
      eventually {
        firesBuffer should contain("two/token123/en_GB/deeper/fire/123321")
      }
    }
    "work with Udash REST client (11)" in {
      firesBuffer.clear()
      restServer.serviceOne().deeper().fire(123321)
      eventually {
        firesBuffer should contain("one/deeper/fire/123321")
      }
    }
    "report valid HTTP codes (1)" in {
      val eventualResponse = serverConnector.send("/non/existing/path", RESTConnector.HttpMethod.POST, Map.empty, Map.empty, null)
      intercept[ClientException](await(eventualResponse)).code should be(404)
    }
    "report valid HTTP codes (2)" in {
      val eventualResponse = serverConnector.send("/serviceOne/loadAll", RESTConnector.HttpMethod.POST, Map.empty, Map.empty, null)
      intercept[ClientException](await(eventualResponse)).code should be(405)
    }
    "report valid HTTP codes (3)" in {
      val eventualResponse = serverConnector.send("/serviceTwo/loadAll", RESTConnector.HttpMethod.GET, Map.empty, Map.empty, null)
      intercept[ClientException](await(eventualResponse)).code should be(400)
    }
    "report valid HTTP codes (4)" in {
      val eventualResponse = serverConnector.send("/serviceThree/loadAll", RESTConnector.HttpMethod.GET, Map.empty, Map.empty, null)
      intercept[ClientException](await(eventualResponse)).code should be(404)

      val eventualResponse2 = serverConnector.send("/service_three/loadAll", RESTConnector.HttpMethod.GET, Map.empty, Map.empty, null)
      intercept[ClientException](await(eventualResponse2)).code should be(404) // "loadAll" is interpreted as URL argument from `serviceThree` getter
    }
    "report valid HTTP codes (5)" in {
      val eventualResponse = restServer.auth("invalid_pass").load()
      intercept[ClientException](await(eventualResponse)).code should be(401)

      val eventualResponse2 = restServer.auth("TurboSecureAPI").load(42, "a\\bc", "q:/we")
      await(eventualResponse2) should be(TestRESTRecord(Some(42), "auth/load/a\\bc/q:/we"))
    }
    "handle endpoint creation fail" in {
      servlet.throwAuthError = true
      val eventualResponse = restServer.auth("invalid_pass").load()
      intercept[ClientException](await(eventualResponse)).code should be(401)
      servlet.throwAuthError = false
    }
    "handle connection refused" in {
      implicit val patienceConfig = PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(100, Millis)))

      val eventualResponse =
        inexistentServerConnector.send("/non/existing/path", RESTConnector.HttpMethod.POST, Map.empty, Map.empty, null)
      eventualResponse.failed.futureValue shouldBe a [ConnectException]

      val eventualResponse2 =
        inexistentServerConnector.send("/non/existing/path", RESTConnector.HttpMethod.POST, Map.empty, Map.empty, "lol")
      eventualResponse2.failed.futureValue shouldBe a [ConnectException]
    }
  }

  private class TestServerRESTInterfaceImpl(fires: mutable.ArrayBuffer[String]) extends TestServerRESTInterface {
    override def serviceOne(): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl("one", fires)

    override def serviceTwo(token: String, lang: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"two/$token/$lang", fires)

    override def serviceThree(arg: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"three/$arg", fires)

    override def auth(pass: String): TestServerRESTInternalInterface =
      if (pass == "TurboSecureAPI") new TestServerRESTInternalInterfaceImpl("auth", fires)
      else throw ExposesREST.Unauthorized("Invalid password")
  }

  private class TestServerRESTInternalInterfaceImpl(data: String, fires: mutable.ArrayBuffer[String])  extends TestServerRESTInternalInterface {
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
      new TestServerRESTDeepInterfaceImpl(s"$data/deeper", fires)

    override def fireAndForget(id: Int): Unit = {
      fires += s"$data/fireAndForget/$id"
    }
  }

  private class TestServerRESTDeepInterfaceImpl(data: String, fires: mutable.ArrayBuffer[String]) extends TestServerRESTDeepInterface {
    override def load(id: Int): Future[TestRESTRecord] =
      Future.successful(TestRESTRecord(Some(id), data))

    override def fire(id: Int): Unit = {
      fires += s"$data/fire/$id"
    }
  }

  private class AuthRestServlet(exposedInterfaces: ExposesREST[_])(implicit ec: ExecutionContext) extends RestServlet {
    var throwAuthError: Boolean = false

    override protected def createEndpoint(req: HttpServletRequest): ExposesREST[_] =
      if (throwAuthError) throw ExposesREST.Unauthorized("")
      else exposedInterfaces
  }
}
