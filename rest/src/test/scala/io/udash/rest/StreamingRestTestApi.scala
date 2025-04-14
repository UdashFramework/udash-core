package io.udash
package rest

import com.avsystem.commons.rpc.AsRawReal
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.openapi.RestSchema
import io.udash.rest.raw.{HttpErrorException, JsonValue, StreamedBody}
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.duration.*

case class DataStream(source: Observable[Int], metadata: Map[String, String])

object DataStream {
  implicit def schema: RestSchema[DataStream] = ???
  implicit def dataStreamAsRawReal: AsRawReal[StreamedBody, DataStream] =
    AsRawReal.create(
      stream => StreamedBody.JsonList(stream.source.map(i => JsonValue(JsonStringOutput.write(i)))),
      {
        case StreamedBody.JsonList(e, c) => DataStream(e.map(_.value.toInt), Map.empty)
        case _ => ???
      }
    )
}

trait StreamingRestTestApi {
  @GET def simpleStream(size: Int): Observable[Int]

  @GET def jsonStream: Observable[RestEntity]

  @POST def binaryStream(): Observable[Array[Byte]]

  @POST def errorStream(@Query immediate: Boolean): Observable[RestEntity]

  @GET def delayedStream(@Query size: Int, @Query delayMillis: Long): Observable[Int]

  @GET def delayedStreamTask(@Query size: Int, @Query delayMillis: Long): Task[Observable[Int]]
  @GET def customStreamTask(@Query size: Int): Task[DataStream]

}
object StreamingRestTestApi extends DefaultRestApiCompanion[StreamingRestTestApi] {

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
          else throw HttpErrorException.Streaming
        }

    override def delayedStream(size: Int, delayMillis: Long): Observable[Int] = {
      Observable.fromIterable(Range(0, size))
        .zip(Observable.intervalAtFixedRate(delayMillis.millis, delayMillis.millis))
        .map(_._1)
    }

    override def delayedStreamTask(size: Int, delayMillis: Long): Task[Observable[Int]] =
      Task.delay(delayedStream(size, delayMillis))

    override def customStreamTask(size: Int): Task[DataStream] = Task {
      DataStream(
        Observable.fromIterable(Range(0, size)),
        Map.empty
      )
    }
  }
}