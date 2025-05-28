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
    @GET def exampleEndpoint(size: Int): Observable[RestExampleData]

    @streamingResponseBatchSize(10)
    @GET def exampleEndpointBatch10(size: Int): Observable[RestExampleData]

    @streamingResponseBatchSize(500)
    @GET def exampleEndpointBatch500(size: Int): Observable[RestExampleData]

    @GET def exampleEndpointWithoutStreaming(size: Int): Task[List[RestExampleData]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {

      def exampleEndpoint(size: Int): Observable[RestExampleData] =
        RestExampleData.generateRandomObservable(size)

      def exampleEndpointBatch10(size: Int): Observable[RestExampleData] =
        RestExampleData.generateRandomObservable(size)

      def exampleEndpointBatch500(size: Int): Observable[RestExampleData] =
        RestExampleData.generateRandomObservable(size)

      def exampleEndpointWithoutStreaming(size: Int): Task[List[RestExampleData]] =
        RestExampleData.generateRandomList(size)
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
  def smallArray(): Unit = {
    waitStreamingEndpoint(10)
  }

  @Benchmark
  def mediumArray(): Unit = {
    waitStreamingEndpoint(200)
  }

  @Benchmark
  def hugeArray(): Unit = {
    waitStreamingEndpoint(5000)
  }

  @Benchmark
  def smallArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(10).toListL)
  }

  @Benchmark
  def mediumArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(200).toListL)
  }

  @Benchmark
  def hugeArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(5000).toListL)
  }

  @Benchmark
  def smallArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(10).toListL)
  }

  @Benchmark
  def mediumArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(200).toListL)
  }

  @Benchmark
  def hugeArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(5000).toListL)
  }

  @Benchmark
  def smallArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(10)
  }

  @Benchmark
  def mediumArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(200)
  }

  @Benchmark
  def hugeArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(5000)
  }

  private def waitEndpointWithoutStreaming(samples: Int): Unit = {
    wait(this.proxy.exampleEndpointWithoutStreaming(samples))
  }

  private def waitStreamingEndpoint(samples: Int): Unit = {
    wait(this.proxy.exampleEndpoint(samples).toListL)
  }

  private def wait[T](task: Task[List[T]]): Unit = {
    Await.result(task.runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}