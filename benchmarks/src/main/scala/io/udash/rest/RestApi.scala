package io.udash.rest

import io.udash.rest.raw.RawRest
import monix.eval.Task
import monix.execution.Scheduler
import org.openjdk.jmh.annotations.*

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

private object RestApi {
  trait RestTestApi {
    @GET def exampleEndpoint(size: Int): Task[List[RestExampleData]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {

      def exampleEndpoint(size: Int): Task[List[RestExampleData]] =
        RestExampleData.generateRandomList(size)
    }
  }

  private def creteApiProxy(): RestTestApi = {
    val apiImpl = new RestTestApi.Impl()
    val handler = RawRest.asHandleRequest[RestTestApi](apiImpl)
    RawRest.fromHandleRequest[RestTestApi](handler)
  }
}


@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
class RestApi {
  implicit def scheduler: Scheduler = Scheduler.global
  private final val proxy = RestApi.creteApiProxy()

  @Benchmark
  def smallArray(): Unit = {
    waitEndpoint(10)
  }

  @Benchmark
  def mediumArray(): Unit = {
    waitEndpoint(200)
  }

  @Benchmark
  def hugeArray(): Unit = {
    waitEndpoint(5000)
  }

  private def waitEndpoint(samples: Int): Unit = {
    Await.result(this.proxy.exampleEndpoint(samples).runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}