package io.udash.rest

import monix.execution.Scheduler
import monix.reactive.Observable
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Fork, Mode, Scope, State}
import com.avsystem.commons.concurrent.ObservableExtensions.*

import java.io.ByteArrayOutputStream
import scala.util.Random

@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class MergeArraysBenchmark {

  import Scheduler.Implicits.global

  private final val data: Observable[Array[Byte]] =
    Observable.repeatEval(Random.nextBytes(1024)).take(32)

  @Benchmark
  def mergeByteArrayOutputStream: Array[Byte] =
    data.foldLeftL(new ByteArrayOutputStream()) { case (acc, elem) =>
      acc.write(elem)
      acc
    }.map(_.toByteArray).runSyncUnsafe()

  @Benchmark
  def mergeByteArrayToL: Array[Byte] =
    data.flatMap(Observable.fromIterable(_)).toL(Array).runSyncUnsafe()
}