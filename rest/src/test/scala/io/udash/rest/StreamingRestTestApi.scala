package io.udash
package rest

import com.avsystem.commons.rpc.{AsRaw, AsRawReal, AsReal}
import io.udash.rest.openapi.RestSchema
import io.udash.rest.raw.*
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.duration.*

final case class DataStream(source: Observable[Int], metadata: Map[String, String])

object DataStream extends GenCodecRestImplicits {
  implicit def schema: RestSchema[DataStream] =
    RestSchema.create(res => RestSchema.seqSchema[Seq, Int].createSchema(res), "DataStream")

  implicit val dataStreamAsRawReal: AsRawReal[StreamedBody, DataStream] =
    AsRawReal.create(
      stream => StreamedBody.JsonList(stream.source.map(AsRaw[JsonValue, Int].asRaw)),
      rawBody => {
        val list = StreamedBody.castOrFail[StreamedBody.JsonList](rawBody)
        DataStream(list.elements.map(AsReal[JsonValue, Int].asReal), Map.empty)
      },
    )
}

final case class CustomStream(source: Observable[Int], code: Int)
object CustomStream extends GenCodecRestImplicits {
  implicit def schema: RestSchema[CustomStream] =
    RestSchema.create(res => RestSchema.seqSchema[Seq, Int].createSchema(res), "CustomStream")

  implicit val customStreamAsRawReal: AsRawReal[StreamedRestResponse, CustomStream] =
    AsRawReal.create(
      stream => StreamedRestResponse(
        code = stream.code,
        headers = IMapping.empty,
        body = StreamedBody.JsonList(stream.source.map(AsRaw[JsonValue, Int].asRaw)),
      ),
      rawResponse => {
        val list = StreamedBody.castOrFail[StreamedBody.JsonList](rawResponse.body)
        CustomStream(list.elements.map(AsReal[JsonValue, Int].asReal), rawResponse.code)
      },
    )
}

trait StreamingRestTestApi {
  @GET def simpleStream(size: Int): Observable[Int]

  @GET def jsonStream: Observable[RestEntity]

  @POST def binaryStream(): Observable[Array[Byte]]

  @streamingResponseBatchSize(3)
  @POST def errorStream(@Query immediate: Boolean): Observable[RestEntity]

  @GET def delayedStream(@Query size: Int, @Query delayMillis: Long): Observable[Int]

  @GET def streamTask(@Query size: Int): Task[Observable[Int]]

  @GET def customStreamTask(@Query size: Int): Task[DataStream]

  @GET def customStream(@Query size: Int): Task[CustomStream]
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
          else throw HttpErrorException.plain(400, "bad stream")
        }

    override def delayedStream(size: Int, delayMillis: Long): Observable[Int] =
      Observable.fromIterable(Range(0, size))
        .zip(Observable.intervalAtFixedRate(delayMillis.millis, delayMillis.millis))
        .map(_._1)

    override def streamTask(size: Int): Task[Observable[Int]] =
      Task.eval(Observable.fromIterable(Range(0, size)))

    override def customStreamTask(size: Int): Task[DataStream] = Task {
      DataStream(
        source = Observable.fromIterable(Range(0, size)),
        metadata = Map.empty
      )
    }

    override def customStream(size: Int): Task[CustomStream] = Task {
      CustomStream(
        source = Observable.fromIterable(Range(0, size)),
        code = 200,
      )
    }
  }
}
