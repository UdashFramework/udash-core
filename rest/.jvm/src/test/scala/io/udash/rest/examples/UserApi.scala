package io.udash
package rest.examples

import io.udash.rest._

import scala.concurrent.Future

case class User(id: String, name: String)
object User extends RestDataCompanion[User]

trait UserApi {
  /** Returns newly created user */
  def createUser(user: User): Future[Unit]
}
object UserApi extends DefaultRestApiCompanion[UserApi]