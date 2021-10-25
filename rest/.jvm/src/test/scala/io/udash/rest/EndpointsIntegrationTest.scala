package io.udash
package rest

import io.udash.rest.raw._
import io.udash.testing.UdashSharedTest
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import sttp.client3.SttpBackend
import sttp.client3.SttpClientException.ConnectException

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, ExecutionContext, Future}

class EndpointsIntegrationTest extends UdashSharedTest with BeforeAndAfterAll with Eventually with ScalaFutures {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val port = 44598
  val contextPrefix = "/rest_api"
  val baseUri = s"http://127.0.0.1:$port$contextPrefix"
  val server = new Server(port)
  val context = new ServletContextHandler()
  context.setSessionHandler(new SessionHandler)
  context.setGzipHandler(new GzipHandler)

  private val servlet = io.udash.rest.RestServlet[TestServerRESTInterface](new TestServerRESTInterfaceImpl)
  val holder = new ServletHolder(servlet)
  holder.setAsyncSupported(true)
  context.addServlet(holder, s"$contextPrefix/*")
  server.setHandler(context)

  def futureHandle(rawHandle: RawRest.HandleRequest): RestRequest => Future[RestResponse] =
    rawHandle.andThen(FutureRestImplicits.futureAsyncEffect.fromAsync)

  def mkRequest(
    url: String,
    method: HttpMethod,
    queryArguments: Map[String, String],
    headers: Map[String, String],
    body: String
  ): RestRequest = RestRequest(
    method,
    RestParameters(
      PlainValue.decodePath(url),
      IMapping(headers.iterator.map { case (k, v) => (k, PlainValue(v)) }.toList),
      Mapping(queryArguments.iterator.map { case (k, v) => (k, PlainValue(v)) }.toList),
    ),
    HttpBody.json(JsonValue(body))
  )

  implicit val backend: SttpBackend[Future, Any] = SttpRestClient.defaultBackend()

  val rawHandler = futureHandle(SttpRestClient.asHandleRequest(baseUri))
  val proxy: TestServerRESTInterface = SttpRestClient[TestServerRESTInterface](baseUri)
  val badRawHandler = futureHandle(SttpRestClient.asHandleRequest(s"http://127.0.0.1:69$contextPrefix"))

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
      await(proxy.serviceOne().deeper().load(5)) should
        be(TestRESTRecord(Some(5), "one/deeper"))
    }
    "work with Udash REST client (2)" in {
      await(proxy.serviceThree("qwe/asd").deeper().load(321)) should
        be(TestRESTRecord(Some(321), "three/qwe/asd/deeper"))
    }
    "work with Udash REST client (3)" in {
      await(proxy.serviceOne().load()).size should be(3)
    }
    "work with Udash REST client (4)" in {
      await(proxy.serviceOne().load(42, "a\\b?x=2&c", "q:/w?x=2e")) should
        be(TestRESTRecord(Some(42), "one/load/a\\b?x=2&c/q:/w?x=2e"))
    }
    "work with Udash REST client (5)" in {
      await(proxy.serviceTwo("tok:/\\en123", "en_GB").create(TestRESTRecord(None, "test123"))) should
        be(TestRESTRecord(None, "two/tok:/\\en123/en_GB/create/None/test123"))
    }
    "work with Udash REST client (6)" in {
      await(proxy.serviceOne().update(123321)(TestRESTRecord(Some(52), "test123"))) should
        be(TestRESTRecord(Some(123321), "one/update/Some(52)/test123"))
    }
    "work with Udash REST client (7)" in {
      await(proxy.serviceThree("asd").modify(321)("mod", 5)) should
        be(TestRESTRecord(Some(321), "three/asd/modify/mod/5"))
    }
    "work with Udash REST client (8)" in {
      await(proxy.serviceTwo("token123", "en_GB").delete(222)) should
        be(TestRESTRecord(Some(222), "two/token123/en_GB/delete"))
    }
    "report valid HTTP codes (1)" in {
      val eventualResponse = rawHandler(mkRequest("/non/existing/path",
        HttpMethod.POST, Map.empty, Map.empty, ""))
      await(eventualResponse).code should be(404)
    }
    "report valid HTTP codes (2)" in {
      val eventualResponse = rawHandler(mkRequest("/serviceOne/loadAll",
        HttpMethod.POST, Map.empty, Map.empty, ""))
      await(eventualResponse).code should be(405)
    }
    "report valid HTTP codes (3)" in {
      val eventualResponse = rawHandler(mkRequest("/serviceTwo/loadAll",
        HttpMethod.GET, Map.empty, Map.empty, ""))
      await(eventualResponse).code should be(400)
    }
    "report valid HTTP codes (4)" in {
      val eventualResponse = rawHandler(mkRequest("/serviceThree/loadAll",
        HttpMethod.GET, Map.empty, Map.empty, ""))
      await(eventualResponse).code should be(404)

      val eventualResponse2 = rawHandler(mkRequest("/service_three/loadAll",
        HttpMethod.GET, Map.empty, Map.empty, ""))
      // "loadAll" is interpreted as URL argument from `serviceThree` getter
      await(eventualResponse2).code should be(404)
    }
    "report valid HTTP codes (5)" in {
      val eventualResponse = proxy.auth("invalid_pass").load()
      intercept[HttpErrorException](await(eventualResponse)).code should be(401)

      val eventualResponse2 = proxy.auth("TurboSecureAPI").load(42, "a\\bc", "q:/we")
      await(eventualResponse2) should be(TestRESTRecord(Some(42), "auth/load/a\\bc/q:/we"))
    }
    "handle connection refused" in {
      implicit val patienceConfig = PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(100, Millis)))

      val eventualResponse =
        badRawHandler(mkRequest("/non/existing/path",
          HttpMethod.POST, Map.empty, Map.empty, ""))
      eventualResponse.failed.futureValue shouldBe a[ConnectException]

      val eventualResponse2 =
        badRawHandler(mkRequest("/non/existing/path",
          HttpMethod.POST, Map.empty, Map.empty, "lol"))
      eventualResponse2.failed.futureValue shouldBe a[ConnectException]
    }
  }

  private class TestServerRESTInterfaceImpl extends TestServerRESTInterface {
    override def serviceOne(): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl("one")

    override def serviceTwo(token: String, lang: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"two/$token/$lang")

    override def serviceThree(arg: String): TestServerRESTInternalInterface =
      new TestServerRESTInternalInterfaceImpl(s"three/$arg")

    override def auth(pass: String): TestServerRESTInternalInterface =
      if (pass == "TurboSecureAPI") new TestServerRESTInternalInterfaceImpl("auth")
      else throw HttpErrorException(401, "Invalid password")
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
