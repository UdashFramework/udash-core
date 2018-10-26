package io.udash
package rest.examples

import io.udash.rest._

import scala.concurrent.Future

case class User(id: String, name: String, birthYear: Int)
object User extends RestDataCompanion[User]

trait UserApi {
  /** Returns newly created user */
  def createUser(name: String, birthYear: Int): Future[User]
}
object UserApi extends DefaultRestApiCompanion[UserApi]