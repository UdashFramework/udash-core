package io.udash.rest

import io.udash.rpc.RPCName

import scala.concurrent.Future

case class TestRESTRecord(id: Option[Int], s: String)

@REST
trait TestRESTInterface {
  def serviceOne(): TestRESTInternalInterface
  def serviceTwo(@RESTName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestRESTInternalInterface
  @RESTName("service_three") def serviceThree(@URLPart arg: String): TestRESTInternalInterface
  @SkipRESTName def serviceSkip(): TestRESTInternalInterface
}

@REST
trait TestRESTInternalInterface {
  @GET @RESTName("load") @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
  @GET def load(@URLPart id: Int, @Query trash: String, @Query @RESTName("trash_two") trash2: String): Future[TestRESTRecord]
  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def update(@URLPart id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PATCH @RESTName("change") def modify(@URLPart id: Int)(@Body s: String): Future[TestRESTRecord]
  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
  def deeper(): TestRESTDeepInterface
}

@REST
trait TestRESTDeepInterface {
  @GET def load(@URLPart id: Int): Future[TestRESTRecord]
}
