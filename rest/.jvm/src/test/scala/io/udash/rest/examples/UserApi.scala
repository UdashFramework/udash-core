package io.udash
package rest.examples

import io.udash.rest._

import scala.concurrent.Future

case class User(id: Long, name: String)
object User extends RestDataCompanion[User]

trait UserApi {
  def createUser(name: String): Future[User]
}
object UserApi extends DefaultRestApiCompanion[UserApi]
