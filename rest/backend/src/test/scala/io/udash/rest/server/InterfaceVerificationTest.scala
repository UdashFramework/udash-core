package io.udash.rest.server

import io.udash.testing.UdashSharedTest

class InterfaceVerificationTest extends UdashSharedTest {
  "Endpoint interface" should {
    "not use @RESTName annotation" in {
      """import io.udash.rpc._
         |import io.udash.rest._
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
         |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
         |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
         |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
         |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
         |  def deeper(): TestServerRESTDeepInterface
         |}
         |
         |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
       """.stripMargin should compile

      """import io.udash.rpc._
         |import io.udash.rest._
         |import scala.concurrent.Future
         |
         |@REST
         |trait TestServerRESTInterface extends HasFakeInstances {
         |  def serviceOne(): TestServerRESTInternalInterface
         |  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
         |  @RESTName("service_three") def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
         |}
         |
         |@REST
         |trait TestServerRESTInternalInterface extends HasFakeInstances {
         |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
         |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
         |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
         |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
         |  def deeper(): TestServerRESTDeepInterface
         |}
         |
         |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
       """.stripMargin shouldNot typeCheck

      """import io.udash.rpc._
         |import io.udash.rest._
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
         |  @GET @RESTName("test") @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
         |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
         |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
         |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
         |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
         |  def deeper(): TestServerRESTDeepInterface
         |}
         |
         |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
       """.stripMargin shouldNot typeCheck
    }

    "not mix @Body and @BodyValue" in {
      """import io.udash.rpc._
        |import io.udash.rest._
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
        |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |  def deeper(): TestServerRESTDeepInterface
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin should compile

      """import io.udash.rpc._
        |import io.udash.rest._
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
        |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@BodyValue id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |  def deeper(): TestServerRESTDeepInterface
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc._
        |import io.udash.rest._
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
        |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@Body id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |  def deeper(): TestServerRESTDeepInterface
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck

      """import io.udash.rpc._
        |import io.udash.rest._
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
        |  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
        |  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
        |  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
        |  @PUT def modify(@URLPart id: Int)(@Body s: String, @BodyValue i: Int): Future[TestRESTRecord]
        |  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
        |  def deeper(): TestServerRESTDeepInterface
        |}
        |
        |implicit val valid: DefaultRESTFramework.ValidServerREST[TestServerRESTInterface] = DefaultRESTFramework.materializeValidServerREST
      """.stripMargin shouldNot typeCheck
    }
  }
}
