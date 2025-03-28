package io.udash
package rest

import io.udash.rest.raw.HttpErrorException
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.duration._

trait StreamingRestTestApi {
  @GET def simpleStream(size: Int): Observable[Int]

  @GET def jsonStream: Observable[RestEntity]

  @POST def binaryStream(): Observable[Array[Byte]]

  @POST def errorStream(@Query immediate: Boolean): Observable[RestEntity]
}
object StreamingRestTestApi extends DefaultRestApiCompanion[StreamingRestTestApi] {

  import Scheduler.Implicits.global

  final class Impl extends StreamingRestTestApi {

    override def simpleStream(size: Int): Observable[Int] =
      Observable.fromIterable(Range(0, size))

    override def jsonStream: Observable[RestEntity] = Observable(
      RestEntity(RestEntityId("1"), "first"),
      RestEntity(RestEntityId("2"), "second"),
      RestEntity(RestEntityId("3"), "third")
    )

    override def binaryStream(): Observable[Array[Byte]] =
      Observable("abc".getBytes, "xyz".getBytes)

    override def errorStream(immediate: Boolean): Observable[RestEntity] =
      if (immediate)
        Observable.raiseError(HttpErrorException.plain(400, "bad"))
      else
        Observable.fromIterable(Range(0, 3)).map { i =>
          if (i < 2) RestEntity(RestEntityId(i.toString), "first")
          else throw HttpErrorException.plain(400, "later bad")
        }
  }
}
