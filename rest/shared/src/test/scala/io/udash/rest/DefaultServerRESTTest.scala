package io.udash.rest

import com.avsystem.commons.serialization.GenCodec
import io.udash.rest.internal.RESTConnector
import io.udash.rest.internal.RESTConnector.HTTPMethod
import io.udash.rpc.serialization.JsonStr
import io.udash.testing.AsyncUdashSharedTest
import org.scalatest.Succeeded

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

class DefaultServerRESTTest extends AsyncUdashSharedTest {
  implicit val testExecutionContext: ExecutionContext = ExecutionContext.Implicits.global

  class ConnectorMock extends RESTConnector {
    var url: String = null
    var method: HTTPMethod = null
    var queryArguments: Map[String, String] = null
    var headers: Map[String, String] = null
    var body: String = null

    var response = "{}"

    override def send(url: String, method: HTTPMethod, queryArguments: Map[String, String], headers: Map[String, String], body: String): Future[String] = {
      this.url = url
      this.method = method
      this.queryArguments = queryArguments
      this.headers = headers
      this.body = body
      Future.successful(response)
    }
  }

  "DefaultServerREST" should {
    val r = TestRESTRecord(None, "Bla bla")
    val r2 = TestRESTRecord(Some(2), "Bla bla 2")
    val r3 = TestRESTRecord(Some(3), "Bla bla 3")

    val connector = new ConnectorMock
    val rest: DefaultServerREST[TestRESTInterface] = new DefaultServerREST[TestRESTInterface](connector)
    import rest.framework._
    val restServer = rest.remoteRpc

    "send valid REST requests via RESTConnector" in {
      val responses = Seq.newBuilder[Future[org.scalatest.Assertion]]

      connector.response = write(r3).json
      for {
        _ <- retrying {
          responses += restServer.serviceOne().create(r).map(_ should be(r3))
          connector.url should be("/serviceOne/create")
          connector.method should be(RESTConnector.POST)
          connector.queryArguments should be(Map.empty)
          connector.headers should be(Map.empty)
          read[TestRESTRecord](JsonStr(connector.body)) should be(r)
        }

        _ <- Future {
          connector.response = write(r2).json
        }
        _ <- retrying {
          responses += restServer.serviceOne().update(r2.id.get)(r2).map(_ should be(r2))
          connector.url should be(s"/serviceOne/update/${r2.id.get}")
          connector.method should be(RESTConnector.PUT)
          connector.queryArguments should be(Map.empty)
          connector.headers should be(Map.empty)
          read[TestRESTRecord](JsonStr(connector.body)) should be(r2)
        }

        _ <- Future {
          connector.response = write(r3).json
        }
        _ <- retrying {
          responses += restServer.serviceOne().modify(r2.id.get)("test", 5).map(_ should be(r3))
          connector.url should be(s"/serviceOne/change/${r2.id.get}")
          connector.method should be(RESTConnector.PATCH)
          connector.queryArguments should be(Map.empty)
          connector.headers should be(Map.empty)
          read[Map[String, RawValue]](JsonStr(connector.body)) should
            be(Map("s" -> JsonStr("\"test\""), "i" -> JsonStr("5"))) // in this framework RawValue = String
        }

        _ <- Future {
          connector.response = write(r).json
        }
        r <- retrying {
          responses += restServer.serviceOne().delete(r2.id.get).map(_ should be(r))
          connector.url should be(s"/serviceOne/remove/${r2.id.get}")
          connector.method should be(RESTConnector.DELETE)
          connector.queryArguments should be(Map.empty)
          connector.headers should be(Map.empty)
          connector.body should be(null)
          Future.sequence(responses.result()).map(_.fold(Succeeded)((x, y) => Succeeded))
        }
      } yield r
    }

    "handle deep interfaces" in {
      restServer.serviceOne().deeper().load(r2.id.get)
      retrying {
        connector.url should be(s"/serviceOne/deeper/load/${r2.id.get}")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
      }
    }

    "handle methods with skipped REST name" in {
      restServer.serviceSkip().deeper().load(r2.id.get)
      retrying {
        connector.url should be(s"/deeper/load/${r2.id.get}")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
      }
    }

    "handle overloaded methods" in {
      val s = Seq(r, r2, r3)
      connector.response = write(s).json
      val resp = restServer.serviceOne().load()
      retrying {
        connector.url should be(s"/serviceOne/load")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
        connector.body should be(null)
        resp.map(_ should be(s))
      }
    }

    "handle query arguments" in {
      restServer.serviceOne().load(r3.id.get, "trashValue", "thrashValue 123")
      retrying {
        connector.url should be(s"/serviceOne/load/${r3.id.get}")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments("trash") should be("trashValue")
        connector.queryArguments("trash_two") should be("thrashValue 123")
        connector.headers should be(Map.empty)
        connector.body should be(null)
      }
    }

    "handle header arguments" in {
      restServer.serviceTwo("token_123", "pl").create(r)
      retrying {
        connector.url should be("/serviceTwo/create")
        connector.method should be(RESTConnector.POST)
        connector.queryArguments should be(Map.empty)
        connector.headers("X_AUTH_TOKEN") should be("token_123")
        connector.headers("lang") should be("pl")
        read[TestRESTRecord](JsonStr(connector.body)) should be(r)
      }
    }

    "handle overrided method name" in {
      restServer.serviceThree("abc").create(r)
      retrying {
        connector.url should be("/service_three/abc/create")
        connector.method should be(RESTConnector.POST)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
        read[TestRESTRecord](JsonStr(connector.body)) should be(r)
      }
    }

    "handle broken HTTP response" in {
      connector.response = write(r2).json
      val resp = restServer.serviceOne().load()
      retrying {
        connector.url should be(s"/serviceOne/load")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
        connector.body should be(null)
        resp.value.get should matchPattern { case Failure(_: GenCodec.ReadFailure) => }
      }
    }

    "handle RPC fires" in {
      restServer.serviceOne().fireAndForget(123)
      for {
        _ <- retrying {
          connector.url should be(s"/serviceOne/fireAndForget")
          connector.method should be(RESTConnector.POST)
          connector.queryArguments should be(Map.empty)
          connector.headers should be(Map.empty)
          connector.body should be("123")
        }
        _ <- Future {
          restServer.serviceTwo("token_123", "pl").deeper().fire(123)
        }
        r <- retrying {
          connector.url should be(s"/serviceTwo/deeper/fire/123")
          connector.method should be(RESTConnector.GET)
          connector.queryArguments should be(Map.empty)
          connector.headers("X_AUTH_TOKEN") should be("token_123")
          connector.headers("lang") should be("pl")
          connector.body should be(null)
        }
      } yield r
    }

    "encode URL part arguments" in {
      restServer.serviceThree("a b /? @#$%^&+").fireAndForget(123)
      retrying {
        connector.url should be("/service_three/a%20b%20%2F%3F%20%40%23%24%25%5E%26%2B/fireAndForget")
        connector.method should be(RESTConnector.POST)
        connector.queryArguments should be(Map.empty)
        connector.headers should be(Map.empty)
        connector.body should be("123")
      }
    }

    "not encode query arguments (connector encodes these parameters)" in {
      restServer.serviceOne().load(123, "a b /? @#$%^&+", "a b /? @#$%^&+")
      retrying {
        connector.url should be("/serviceOne/load/123")
        connector.method should be(RESTConnector.GET)
        connector.queryArguments("trash") should be("a b /? @#$%^&+")
        connector.queryArguments("trash_two") should be("a b /? @#$%^&+")
        connector.headers should be(Map.empty)
        connector.body should be(null)
      }
    }

    "not encode headers" in {
      restServer.serviceTwo("a b /? @#$%^&+", "a b /? @#$%^&+").create(r)
      retrying {
        connector.url should be("/serviceTwo/create")
        connector.method should be(RESTConnector.POST)
        connector.queryArguments should be(Map.empty)
        connector.headers("X_AUTH_TOKEN") should be("a b /? @#$%^&+")
        connector.headers("lang") should be("a b /? @#$%^&+")
        read[TestRESTRecord](JsonStr(connector.body)) should be(r)
      }
    }

    "compile recursive interface" in {
      """import io.udash.rpc.RPCName
        |case class TestRESTRecord(id: Option[Int], s: String)
        |implicit val x: GenCodec[TestRESTRecord] = null
        |
        |@REST
        |trait NotBrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): NotBrokenRESTInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): NotBrokenRESTInterface
        |  @RESTName("service_three") def serviceThree(@URLPart arg: String): NotBrokenRESTInterface
        |  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |}
        |
        |val rest: DefaultServerREST[NotBrokenRESTInterface] = new DefaultServerREST[NotBrokenRESTInterface](connector)
      """.stripMargin should compile

      """import io.udash.rpc.RPCName
        |case class TestRESTRecord(id: Option[Int], s: String)
        |implicit val x: GenCodec[TestRESTRecord] = null
        |
        |@REST
        |trait NotBrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): NotBrokenRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): NotBrokenRESTInternalInterface
        |  @RESTName("service_three") def serviceThree(@URLPart arg: String): NotBrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait NotBrokenRESTInternalInterface extends HasFakeInstances {
        |  @RESTName("load") @RPCName("loadAll") def load(): NotBrokenRESTInterface
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PATCH @RESTName("change") def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[NotBrokenRESTInterface] = new DefaultServerREST[NotBrokenRESTInterface](connector)
      """.stripMargin should compile
    }

    "not compile with interface without @REST annotation" in {
      """import io.udash.rpc.RPCName
        |case class TestRESTRecord(id: Option[Int], s: String)
        |implicit val x: GenCodec[TestRESTRecord] = null
        |
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): BrokenRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |  @RESTName("service_three") def serviceThree(@URLPart arg: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PATCH @RESTName("change") def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot compile
    }

    "not compile with internal interface without @REST annotation" in {
      """import io.udash.rpc.RPCName
        |case class TestRESTRecord(id: Option[Int], s: String)
        |implicit val x: GenCodec[TestRESTRecord] = null
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): BrokenRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |  @RESTName("service_three") def serviceThree(@URLPart arg: String): BrokenRESTInternalInterface
        |}
        |
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PATCH @RESTName("change") def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot compile
    }

    "not compile with @Body argument in getter" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(@Body bodyArg: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "not compile with REST method annotation on getter" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  @GET def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  @POST def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  @PUT def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  @PATCH def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  @DELETE def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "not compile with more than one @Body argument" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): BrokenRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |  @RESTName("service_three") def serviceThree(@URLPart arg: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord, @Body record2: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PATCH @RESTName("change") def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "not compile with @Body argument in @GET annotated method" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Body @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "not compile with more than one REST method annotation" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceOne(): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @POST @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "compile without REST method annotation (@GET or @POST as default)" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait RESTInterface extends HasFakeInstances {
        |  def serviceOne(): RESTInternalInterface
        |}
        |
        |@REST
        |trait RESTInternalInterface extends HasFakeInstances {
        |  def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[RESTInterface] = new DefaultServerREST[RESTInterface](connector)
      """.stripMargin should compile
    }

    "not compile with more than one argument type annotation" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Query @Header lang: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "compile without argument type annotation (use @Query as default)" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait RESTInterfaceWithDefaultArgType extends HasFakeInstances {
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, lang: String): RESTInternalInterface
        |}
        |
        |@REST
        |trait RESTInternalInterface extends HasFakeInstances {
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |}
        |
        |val rest: DefaultServerREST[RESTInterfaceWithDefaultArgType] = new DefaultServerREST[RESTInterfaceWithDefaultArgType](connector)
      """.stripMargin should compile
    }

    "not compile with empty @RESTName or @RPCName or @RESTParamName" in {
      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceTwo(@RESTParamName("") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceTwo(@RESTParamName("token_x") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("load") @RPCName("") def load(): Future[Seq[TestRESTRecord]]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc.RPCName
        |implicit val x: GenCodec[TestRESTRecord] = null
        |case class TestRESTRecord(id: Option[Int], s: String)
        |
        |@REST
        |trait BrokenRESTInterface extends HasFakeInstances {
        |  def serviceTwo(@RESTParamName("token_x") @Header token: String, @Header lang: String): BrokenRESTInternalInterface
        |}
        |
        |@REST
        |trait BrokenRESTInternalInterface extends HasFakeInstances {
        |  @GET @RESTName("") @RPCName("load") def load(): Future[Seq[TestRESTRecord]]
        |}
        |
        |val rest: DefaultServerREST[BrokenRESTInterface] = new DefaultServerREST[BrokenRESTInterface](connector)
      """.stripMargin shouldNot typeCheck
    }

    "not mix @Body and @BodyValue" in {
      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |@REST
        |trait TestServerRESTInterface extends HasFakeInstances {
        |  def serviceOne(): TestServerRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
        |  def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
        |}
        |
        |@REST
        |trait TestServerRESTInternalInterface extends HasFakeInstances {
        |  @GET @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin should compile

      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |@REST
        |trait TestServerRESTInterface extends HasFakeInstances {
        |  def serviceOne(): TestServerRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
        |  def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
        |}
        |
        |@REST
        |trait TestServerRESTInternalInterface extends HasFakeInstances {
        |  @GET @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@BodyValue id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |@REST
        |trait TestServerRESTInterface extends HasFakeInstances {
        |  def serviceOne(): TestServerRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
        |  def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
        |}
        |
        |@REST
        |trait TestServerRESTInternalInterface extends HasFakeInstances {
        |  @GET @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@Body id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc._
        |import scala.concurrent.Future
        |
        |@REST
        |trait TestServerRESTInterface extends HasFakeInstances {
        |  def serviceOne(): TestServerRESTInternalInterface
        |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
        |  def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
        |}
        |
        |@REST
        |trait TestServerRESTInternalInterface extends HasFakeInstances {
        |  @GET @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@Body s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |  def deeper(): TestServerRESTDeepInterface
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck
    }
  }
}
