package io.udash.rest.server

import io.udash.rpc.RPCName
import io.udash.rest._

import scala.concurrent.Future

@REST
trait TestServerRESTInterface {
  def serviceOne(): TestServerRESTInternalInterface
  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
  @RPCName("service_three") def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface
}

@REST
trait TestServerRESTInternalInterface {
  @GET @RPCName("loadAll") def load(): Future[Seq[TestRESTRecord]]
  @GET def load(@URLPart id: Int, @Query trash: String, /*@Query */@RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
  @POST def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def update(id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
  @DELETE @RPCName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
  def deeper(): TestServerRESTDeepInterface
}

@REST
trait TestServerRESTDeepInterface {
  @GET def load(@URLPart id: Int): Future[TestRESTRecord]
}
