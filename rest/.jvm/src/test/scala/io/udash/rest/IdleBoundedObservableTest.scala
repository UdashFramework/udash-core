package io.udash
package rest

import io.udash.rest.RestServlet.{IdleBoundedObservable, IdleTimeoutException}
import io.udash.testing.UdashSharedTest
import monix.eval.Task
import monix.execution.exceptions.UpstreamTimeoutException
import monix.execution.schedulers.TestScheduler
import monix.reactive.Observable

import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.Success

class IdleBoundedObservableTest extends UdashSharedTest {
  private val Timeout = 1.second

  private def run(source: Observable[?])(implicit scheduler: TestScheduler): Future[Unit] =
    new IdleBoundedObservable(source, Timeout).completedL.runToFuture

  private def failedWithIdleTimeout(result: Future[Unit]): Boolean =
    result.value.exists(_.failed.toOption.exists(_.isInstanceOf[IdleTimeoutException]))

  "IdleBoundedObservable" should {
    "fail a source that never emits once its timeout elapses" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val result = run(Observable.never)
      scheduler.tick(Timeout - 1.milli)
      assert(result.value.isEmpty)
      scheduler.tick(1.milli)
      assert(failedWithIdleTimeout(result))
    }

    "fail a source that went silent after an element within its timeout plus one tick" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val result = run(Observable(1) ++ Observable.never)
      scheduler.tick(Timeout + Timeout / 4 - 1.milli)
      assert(result.value.isEmpty)
      scheduler.tick(1.milli)
      assert(failedWithIdleTimeout(result))
    }

    "keep a source that emits within its timeout" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val result = run(Observable.intervalAtFixedRate(Timeout - 100.millis).take(20))
      scheduler.tick(30.seconds)
      assert(result.value.contains(Success(())))
    }

    "not count the time its downstream spends on an element" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val result = new IdleBoundedObservable(Observable.range(0, 3), Timeout)
        .mapEval(_ => Task.sleep(5.seconds)).completedL.runToFuture
      scheduler.tick(30.seconds)
      assert(result.value.contains(Success(())))
    }

    "pass on the source's own failure, including its own upstream timeout" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val own = UpstreamTimeoutException(Timeout)
      val result = run(Observable.raiseError(own))
      scheduler.tick()
      assert(result.value.exists(_.failed.toOption.contains(own)))
    }

    "not fail a source that completed" in {
      implicit val scheduler: TestScheduler = TestScheduler()
      val result = run(Observable(1))
      scheduler.tick(30.seconds)
      assert(result.value.contains(Success(())))
    }
  }
}
