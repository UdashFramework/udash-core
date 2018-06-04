package io.udash.rest.server

import com.avsystem.commons.rpc.rpcName
import io.udash.rest.DefaultRESTFramework._
import io.udash.rest._

import scala.concurrent.Future

trait TestServerRESTInterface {
  def serviceOne(): TestServerRESTInternalInterface
  def serviceTwo(@RESTParamName("X_AUTH_TOKEN") @Header token: String, @Header lang: String): TestServerRESTInternalInterface
  @rpcName("service_three") def serviceThree(@URLPart arg: String): TestServerRESTInternalInterface

  def auth(@Header @RESTParamName("X-Password") pass: String): TestServerRESTInternalInterface
}
object TestServerRESTInterface extends RPCCompanion[TestServerRESTInterface]

trait TestServerRESTInternalInterface {
  @GET @rpcName("loadAll") def load(): Future[Seq[TestRESTRecord]]
  /*@GET */ def load(@URLPart id: Int, @Query trash: String, /*@Query */ @RESTParamName("trash_two") trash2: String): Future[TestRESTRecord]
  /*@POST */ def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def update(id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def modify(@URLPart id: Int)(@BodyValue s: String, @BodyValue i: Int): Future[TestRESTRecord]
  @DELETE @rpcName("remove") def delete(@URLPart id: Int): Future[TestRESTRecord]
  def fireAndForget(@Body id: Int): Unit
  def deeper(): TestServerRESTDeepInterface
}
object TestServerRESTInternalInterface extends RPCCompanion[TestServerRESTInternalInterface]

trait TestServerRESTDeepInterface {
  @GET def load(@URLPart id: Int): Future[TestRESTRecord]
  @GET def fire(@URLPart id: Int): Unit
}
object TestServerRESTDeepInterface extends RPCCompanion[TestServerRESTDeepInterface]
