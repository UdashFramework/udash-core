package io.udash
package rest.monix

import monix.eval.Task

trait MonixTestApi {
  def method(arg: Int, param: String): Task[String]
}
object MonixTestApi extends MonixRestApiCompanion[MonixTestApi]
