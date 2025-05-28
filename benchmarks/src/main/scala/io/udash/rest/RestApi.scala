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
    @GET def simpleNumbers(size: Int): Task[List[Int]]
  }

  object RestTestApi extends DefaultRestApiCompanion[RestTestApi] {
    final class Impl extends RestTestApi {

      def simpleNumbers(size: Int): Task[List[Int]] =
        Task.eval(Range(0, size).toList)
    }
  }

  private def creteApiProxy(): RestTestApi = {
    val apiImpl = new RestTestApi.Impl()
    val handler = RawRest.asHandleRequest[RestTestApi](apiImpl)
    RawRest.fromHandleRequest[RestTestApi](handler)
  }
}


@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Thread)
class RestApi {
  implicit def scheduler: Scheduler = Scheduler.global
  private final val proxy = RestApi.creteApiProxy()

  @Benchmark
  def smallNumbersArray(): Unit = {
    waitEndpoint(10)
  }

  @Benchmark
  def mediumNumbersArray(): Unit = {
    waitEndpoint(200)
  }

  @Benchmark
  def hugeNumbersArray(): Unit = {
    waitEndpoint(5000)
  }

  private def waitEndpoint(samples: Int): Unit = {
    Await.result(this.proxy.simpleNumbers(samples).runToFuture, Duration.apply(10, TimeUnit.SECONDS))
  }
}