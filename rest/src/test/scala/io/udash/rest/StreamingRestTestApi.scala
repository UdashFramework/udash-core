package io.udash
package rest

import monix.execution.Scheduler
import monix.reactive.Observable

trait StreamingRestTestApi {
  @GET def simpleStream: Observable[String]

  @GET def jsonStream: Observable[RestEntity]
}
object StreamingRestTestApi extends DefaultRestApiCompanion[StreamingRestTestApi] {

  import Scheduler.Implicits.global

  final class Impl extends StreamingRestTestApi {
    override def simpleStream: Observable[String] = Observable("a", "b", "c")

    override def jsonStream: Observable[RestEntity] = Observable(
      RestEntity(RestEntityId("1"), "first"),
      RestEntity(RestEntityId("2"), "second"),
      RestEntity(RestEntityId("3"), "third")
    )
  }
}
