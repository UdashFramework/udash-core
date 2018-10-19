package io.udash
package rest

import io.udash.legacyrest.TestRESTRecord

import scala.concurrent.Future

trait TestServerRESTInterface {
  def serviceOne(): TestServerRESTInternalInterface
  def serviceTwo(@Header("X_AUTH_TOKEN") token: String, @Header("Lang") lang: String): TestServerRESTInternalInterface
  @Prefix("service_three") def serviceThree(@Path arg: String): TestServerRESTInternalInterface

  def auth(@Header("X-Password") pass: String): TestServerRESTInternalInterface
}
object TestServerRESTInterface extends DefaultRestApiCompanion[TestServerRESTInterface]

trait TestServerRESTInternalInterface {
  @GET("loadAll") def load(): Future[Seq[TestRESTRecord]]
  @GET def load(@Path id: Int, trash: String, @Query("trash_two") trash2: String): Future[TestRESTRecord]
  def create(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def update(@Query id: Int)(@Body record: TestRESTRecord): Future[TestRESTRecord]
  @PUT def modify(@Path id: Int)(s: String, i: Int): Future[TestRESTRecord]
  @DELETE("remove") def delete(@Path id: Int): Future[TestRESTRecord]
  def deeper(): TestServerRESTDeepInterface
}
object TestServerRESTInternalInterface extends DefaultRestApiCompanion[TestServerRESTInternalInterface]

trait TestServerRESTDeepInterface {
  @GET def load(@Path id: Int): Future[TestRESTRecord]
}
object TestServerRESTDeepInterface extends DefaultRestApiCompanion[TestServerRESTDeepInterface]
