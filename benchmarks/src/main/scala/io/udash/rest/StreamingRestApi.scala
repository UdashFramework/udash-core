package io.udash.rest

import io.udash.rest.raw.{RawRest, RestRequest, RestResponse, StreamedRestResponse}
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

private object StreamingRestApi {
  trait RestTestApi {
    @GET def simpleNumbers(size: Int): Observable[Int]
    @GET def simpleNumbersWithoutStreaming(size: Int): Task[List[Int]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {

      def simpleNumbers(size: Int): Observable[Int] =
        Observable.fromIterable(Range(0, size))

      def simpleNumbersWithoutStreaming(size: Int): Task[List[Int]] =
        Task.eval(Range(0, size).toList)
    }
  }

  private def creteApiProxy(): RestTestApi = {
    val apiImpl = new RestTestApi.Impl()
    val streamingServerHandle = RawRest.asHandleRequestWithStreaming[RestTestApi](apiImpl)
    val streamingClientHandler = new RawRest.RestRequestHandler {
      override def handleRequest(request: RestRequest): Task[RestResponse] =
        streamingServerHandle(request).map(_.asInstanceOf[RestResponse])

      override def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
        streamingServerHandle(request).map(_.asInstanceOf[StreamedRestResponse])
    }
    RawRest.fromHandleRequestWithStreaming[RestTestApi](streamingClientHandler)
  }
}


@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
class StreamingRestApi {
  implicit def scheduler: Scheduler = Scheduler.global
  private final val proxy = StreamingRestApi.creteApiProxy()


  @Benchmark
  def smallNumbersArray(): Unit = {
    waitStreamingEndpoint(10)
  }

  @Benchmark
  def mediumNumbersArray(): Unit = {
    waitStreamingEndpoint(200)
  }

  @Benchmark
  def hugeNumbersArray(): Unit = {
    waitStreamingEndpoint(5000)
  }

  @Benchmark
  def smallNumbersArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(10)
  }

  @Benchmark
  def mediumNumbersArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(200)
  }

  @Benchmark
  def hugeNumbersArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(5000)
  }

  private def waitEndpointWithoutStreaming(samples: Int): Unit = {
    wait(this.proxy.simpleNumbersWithoutStreaming(samples))
  }

  private def waitStreamingEndpoint(samples: Int): Unit = {
    wait(this.proxy.simpleNumbers(samples).toListL)
  }

  private def wait[T](task: Task[List[T]]): Unit = {
    Await.result(task.runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}