package io.udash.rest

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.RestExampleData.RestResponseSize
import io.udash.rest.raw.{RawRest, RestRequest, RestResponse, StreamedRestResponse}
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.openjdk.jmh.annotations.*

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

private object StreamingRestApiBenchmark {
  trait RestTestApi {
    @GET def exampleEndpoint(size: RestResponseSize): Observable[RestExampleData]
    @GET def exampleEndpointBinary(size: RestResponseSize): Observable[Array[Byte]]

    @streamingResponseBatchSize(10)
    @GET def exampleEndpointBatch10(size: RestResponseSize): Observable[RestExampleData]

    @streamingResponseBatchSize(10)
    @GET def exampleEndpointBatch10Binary(size: RestResponseSize): Observable[Array[Byte]]

    @streamingResponseBatchSize(500)
    @GET def exampleEndpointBatch500(size: RestResponseSize): Observable[RestExampleData]

    @streamingResponseBatchSize(500)
    @GET def exampleEndpointBatch500Binary(size: RestResponseSize): Observable[Array[Byte]]

    @GET def exampleEndpointWithoutStreaming(size: RestResponseSize): Task[List[RestExampleData]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {
      private var responses: Map[RestResponseSize, List[RestExampleData]] = Map.empty

      def exampleEndpoint(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(getResponse(size))

      def exampleEndpointBinary(size: RestResponseSize): Observable[Array[Byte]] =
        getResponseBinary(size)

      def exampleEndpointBatch10(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(getResponse(size))

      def exampleEndpointBatch10Binary(size: RestResponseSize): Observable[Array[Byte]] =
        getResponseBinary(size)

      def exampleEndpointBatch500(size: RestResponseSize): Observable[RestExampleData] =
        Observable.fromIterable(getResponse(size))

      def exampleEndpointBatch500Binary(size: RestResponseSize): Observable[Array[Byte]] =
        getResponseBinary(size)

      def exampleEndpointWithoutStreaming(size: RestResponseSize): Task[List[RestExampleData]] =
        Task.eval(getResponse(size))

      private def getResponse(size: RestResponseSize): List[RestExampleData] =
        responses(size)

      private def getResponseBinary(size: RestResponseSize): Observable[Array[Byte]] =
        Observable.fromIterable(getResponse(size)).map(JsonStringOutput.write(_).getBytes(StandardCharsets.UTF_8))

      def generateResponses(): Unit =
        this.responses = RestResponseSize.values.map(size => size -> RestExampleData.generateRandomList(size)).toMap
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
@Fork(1)
class StreamingRestApiBenchmark {
  implicit def scheduler: Scheduler = Scheduler.global
  private final val (impl, proxy) = StreamingRestApiBenchmark.creteApiProxy()

  @Setup(Level.Trial)
  def setup(): Unit = {
    this.impl.generateResponses()
  }

  @Benchmark
  def smallArrayJsonList(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArrayJsonList(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArrayJsonList(): Unit = {
    waitStreamingEndpoint(RestResponseSize.Huge)
  }

  @Benchmark
  def smallArrayBinary(): Unit = {
    waitStreamingEndpointBinary(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArrayBinary(): Unit = {
    waitStreamingEndpointBinary(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArrayBinary(): Unit = {
    waitStreamingEndpointBinary(RestResponseSize.Huge)
  }

  @Benchmark
  def smallArrayBatch10JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10(RestResponseSize.Small))
  }

  @Benchmark
  def mediumArrayBatch10JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10(RestResponseSize.Medium))
  }

  @Benchmark
  def hugeArrayBatch10JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10(RestResponseSize.Huge))
  }

  @Benchmark
  def smallArrayBatch10Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10Binary(RestResponseSize.Small))
  }

  @Benchmark
  def mediumArrayBatch10Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10Binary(RestResponseSize.Medium))
  }

  @Benchmark
  def hugeArrayBatch10Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch10Binary(RestResponseSize.Huge))
  }

  @Benchmark
  def smallArrayBatch500JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500(RestResponseSize.Small))
  }

  @Benchmark
  def mediumArrayBatch500JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500(RestResponseSize.Medium))
  }

  @Benchmark
  def hugeArrayBatch500JsonList(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500(RestResponseSize.Huge))
  }

  @Benchmark
  def smallArrayBatch500Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500Binary(RestResponseSize.Small))
  }

  @Benchmark
  def mediumArrayBatch500Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500Binary(RestResponseSize.Medium))
  }

  @Benchmark
  def hugeArrayBatch500Binary(): Unit = {
    waitObservable(this.proxy.exampleEndpointBatch500Binary(RestResponseSize.Huge))
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

  private def waitEndpointWithoutStreaming(samples: RestResponseSize): Unit =
    wait(this.proxy.exampleEndpointWithoutStreaming(samples))

  private def waitStreamingEndpoint(samples: RestResponseSize): Unit =
    wait(this.proxy.exampleEndpoint(samples).completedL)

  private def waitStreamingEndpointBinary(samples: RestResponseSize): Unit =
    wait(this.proxy.exampleEndpointBinary(samples).completedL)

  private def wait[T](task: Task[T]): Unit =
    Await.result(task.runToFuture, Duration.apply(15, TimeUnit.SECONDS))

  private def waitObservable[T](obs: Observable[T]): Unit =
    Await.result(obs.completedL.runToFuture, Duration.apply(15, TimeUnit.SECONDS))
}
