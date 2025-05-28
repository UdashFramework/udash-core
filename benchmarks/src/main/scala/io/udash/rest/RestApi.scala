package io.udash.rest

import io.udash.rest.RestExampleData.RestResponseSize
import io.udash.rest.raw.RawRest
import monix.eval.Task
import monix.execution.Scheduler
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

private object RestApi {
  trait RestTestApi {
    @GET def exampleEndpoint(size: RestResponseSize): Task[List[RestExampleData]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {
      private var responses: Map[RestResponseSize, List[RestExampleData]] =
        Map.empty

      def exampleEndpoint(size: RestResponseSize): Task[List[RestExampleData]] =
        Task.eval(responses(size))

      def generateResponses(): Unit = {
        this.responses = RestResponseSize.values.map(size => size -> RestExampleData.generateRandomList(size)).toMap
      }
    }
  }

  private def creteApiProxy(): (RestTestApi.Impl, RestTestApi) = {
    val apiImpl = new RestTestApi.Impl()
    val handler = RawRest.asHandleRequest[RestTestApi](apiImpl)
    (apiImpl, RawRest.fromHandleRequest[RestTestApi](handler))
  }
}


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
class RestApi {
  implicit def scheduler: Scheduler = Scheduler.global

  private final val (impl, proxy) = RestApi.creteApiProxy()

  @Setup(Level.Trial)
  def setup(): Unit = {
    this.impl.generateResponses()
  }

  @Benchmark
  def smallArray(): Unit = {
    waitEndpoint(RestResponseSize.Small)
  }

  @Benchmark
  def mediumArray(): Unit = {
    waitEndpoint(RestResponseSize.Medium)
  }

  @Benchmark
  def hugeArray(): Unit = {
    waitEndpoint(RestResponseSize.Huge)
  }

  private def waitEndpoint(size: RestResponseSize): Unit = {
    Await.result(this.proxy.exampleEndpoint(size).runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}