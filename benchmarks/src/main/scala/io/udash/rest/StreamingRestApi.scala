package io.udash.rest

import io.udash.rest.RestExampleData.RestResponseSize
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
    @GET def exampleEndpoint(size: RestResponseSize): Observable[RestExampleData]

    @streamingResponseBatchSize(10)
    @GET def exampleEndpointBatch10(size: RestResponseSize): Observable[RestExampleData]

    @streamingResponseBatchSize(500)
    @GET def exampleEndpointBatch500(size: RestResponseSize): Observable[RestExampleData]

    @GET def exampleEndpointWithoutStreaming(size: RestResponseSize): Task[List[RestExampleData]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {
      private var responses: Map[RestResponseSize, List[RestExampleData]] =
        Map.empty

      def exampleEndpoint(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(responses(size))

      def exampleEndpointBatch10(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(responses(size))

      def exampleEndpointBatch500(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(responses(size))

      def exampleEndpointWithoutStreaming(size: RestResponseSize): Task[List[RestExampleData]] =
        Task.eval(responses(size))

      def generateResponses(): Unit = {
        this.responses = RestResponseSize.values.map(size => size -> RestExampleData.generateRandomList(size)).toMap
      }
    }
  }

  private def creteApiProxy(): (RestTestApi.Impl, RestTestApi) = {
    val apiImpl = new RestTestApi.Impl()
    val streamingServerHandle = RawRest.asHandleRequestWithStreaming[RestTestApi](apiImpl)
    val streamingClientHandler = new RawRest.RestRequestHandler {
      override def handleRequest(request: RestRequest): Task[RestResponse] =
        streamingServerHandle(request).map(_.asInstanceOf[RestResponse])

      override def handleRequestStream(request: RestRequest): Task[StreamedRestResponse] =
        streamingServerHandle(request).map(_.asInstanceOf[StreamedRestResponse])
    }
    (apiImpl, RawRest.fromHandleRequestWithStreaming[RestTestApi](streamingClientHandler))
  }
}


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
class StreamingRestApi {
  implicit def scheduler: Scheduler = Scheduler.global
  private final val (impl, proxy) = StreamingRestApi.creteApiProxy()

  @Setup(Level.Trial)
  def setup(): Unit = {
    this.impl.generateResponses()
  }

  @Benchmark
  def smallArray(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArray(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArray(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Huge)
  }

  @Benchmark
  def smallArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(RestResponseSize.Small).toListL)
  }

  @Benchmark
  def mediumArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(RestResponseSize.Medium).toListL)
  }

  @Benchmark
  def hugeArrayBatch10(): Unit = {
    wait(this.proxy.exampleEndpointBatch10(RestResponseSize.Huge).toListL)
  }

  @Benchmark
  def smallArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(RestResponseSize.Small).toListL)
  }

  @Benchmark
  def mediumArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(RestResponseSize.Medium).toListL)
  }

  @Benchmark
  def hugeArrayBatch500(): Unit = {
    wait(this.proxy.exampleEndpointBatch500(RestResponseSize.Huge).toListL)
  }

  @Benchmark
  def smallArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArrayWithoutStreaming(): Unit = {
    waitEndpointWithoutStreaming(RestResponseSize.Huge)
  }

  private def waitEndpointWithoutStreaming(samples: RestResponseSize): Unit = {
    wait(this.proxy.exampleEndpointWithoutStreaming(samples))
  }

  private def waitStreamingEndpoint(samples: RestResponseSize): Unit = {
    wait(this.proxy.exampleEndpoint(samples).toListL)
  }

  private def wait[T](task: Task[List[T]]): Unit = {
    Await.result(task.runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}