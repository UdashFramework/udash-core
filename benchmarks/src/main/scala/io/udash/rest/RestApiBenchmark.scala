package io.udash.rest

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.RestExampleData.RestResponseSize
import io.udash.rest.raw.RawRest
import monix.eval.Task
import monix.execution.Scheduler
import org.openjdk.jmh.annotations.*

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

private object RestApiBenchmark {
  trait RestTestApi {
    @GET def exampleEndpoint(size: RestResponseSize): Task[List[RestExampleData]]
    @GET def exampleBinaryEndpoint(size: RestResponseSize): Task[List[Array[Byte]]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {
      private var responses: Map[RestResponseSize, List[RestExampleData]] = Map.empty

      def exampleEndpoint(size: RestResponseSize): Task[List[RestExampleData]] =
        Task.eval(getResponse(size))

      override def exampleBinaryEndpoint(size: RestResponseSize): Task[List[Array[Byte]]] =
        Task.eval(getResponse(size).iterator.map(JsonStringOutput.write(_).getBytes(StandardCharsets.UTF_8)).toList)

      private def getResponse(size: RestResponseSize): List[RestExampleData] =
        responses(size)

      def generateResponses(): Unit =
        this.responses = RestResponseSize.values.map(size => size -> RestExampleData.generateRandomList(size)).toMap
    }
  }

  private def createApiProxy(): (RestTestApi.Impl, RestTestApi) = {
    val apiImpl = new RestTestApi.Impl()
    val handler = RawRest.asHandleRequest[RestTestApi](apiImpl)
    (apiImpl, RawRest.fromHandleRequest[RestTestApi](handler))
  }
}


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
@Fork(1)
class RestApiBenchmark {
  implicit def scheduler: Scheduler = Scheduler.global

  private final val (impl, proxy) = RestApiBenchmark.createApiProxy()

  @Setup(Level.Trial)
  def setup(): Unit = {
    this.impl.generateResponses()
  }

  @Benchmark
  def smallArrayJsonList(): Unit = {
    waitEndpoint(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArrayJsonList(): Unit = {
    waitEndpoint(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArrayJsonList(): Unit = {
    waitEndpoint(RestResponseSize.Huge)
  }

  @Benchmark
  def smallArrayBinary(): Unit = {
    waitEndpointBinary(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArrayBinary(): Unit = {
    waitEndpointBinary(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArrayBinary(): Unit = {
    waitEndpointBinary(RestResponseSize.Huge)
  }

  private def waitEndpoint(size: RestResponseSize): Unit =
    Await.result(this.proxy.exampleEndpoint(size).runToFuture, Duration.apply(10, TimeUnit.SECONDS))

  private def waitEndpointBinary(size: RestResponseSize): Unit =
    Await.result(this.proxy.exampleBinaryEndpoint(size).runToFuture, Duration.apply(10, TimeUnit.SECONDS))
}
