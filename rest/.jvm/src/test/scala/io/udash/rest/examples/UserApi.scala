package io.udash
package rest.examples

import io.udash.rest._
import io.udash.rest.openapi.adjusters._

import scala.concurrent.Future

trait UserApi {
  def createUser(name: String): Future[User]
  @GET def getUser(id: UserId): Future[User]
}
object UserApi extends DefaultRestApiCompanion[UserApi]

case class UserId(id: Long) extends AnyVal
object UserId extends RestDataWrapperCompanion[Long, UserId]

@description("Representation of system user")
@example(User(UserId(0), "Fred"))
case class User(id: UserId, @description("User name") name: String)
object User extends RestDataCompanion[User]

trait GroupApi
object GroupApi extends DefaultRestApiCompanion[GroupApi]

trait SystemApi {
  def users: UserApi
  def groups: GroupApi
}
object SystemApi extends DefaultRestApiCompanion[SystemApi]

trait AuthApi {
  @Prefix("") def auth(@Header("Authorization") authToken: String): SystemApi
}
object AuthApi extends DefaultRestApiCompanion[AuthApi]

object PrintOpenAPI {
  def main(args: Array[String]): Unit = {
    import io.udash.rest.openapi._

    val userOpenApi: OpenApi =
      UserApi.openapiMetadata.openapi(
        Info("User API", "0.1", description = "User management API"),
        servers = List(Server("http://www.userapi.com"))
      )

    import com.avsystem.commons.serialization.json._

    val openApiJson: String =
      JsonStringOutput.write(userOpenApi)

    println(openApiJson)
  }
}
