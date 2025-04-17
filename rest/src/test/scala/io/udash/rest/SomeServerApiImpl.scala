package io.udash.rest

import monix.reactive.Observable

import scala.concurrent.Future

final class SomeServerApiImpl {
  @GET
  def thingy(param: Int): Future[String] = Future.successful((param - 1).toString)

  @GET
  def streamingNumbers(count: Int): Observable[Int] =
    Observable.fromIterable(1 to count)

  @POST
  def streamEcho(values: List[Int]): Observable[Int] =
    Observable.fromIterable(values)

  @GET
  def streamBinary(chunkSize: Int): Observable[Array[Byte]] = {
    val content = "HelloWorld".getBytes
    Observable.fromIterable(content.grouped(chunkSize).toSeq)
  }

  @GET
  def streamEmpty(): Observable[Array[Byte]] =
    Observable.empty

  val subapi = new SomeServerSubApiImpl
}
object SomeServerApiImpl extends DefaultRestServerApiImplCompanion[SomeServerApiImpl]

final class SomeServerSubApiImpl {
  @POST
  def yeet(data: String): Future[String] = Future.successful(s"yeet $data")
}
object SomeServerSubApiImpl extends DefaultRestServerApiImplCompanion[SomeServerSubApiImpl]
