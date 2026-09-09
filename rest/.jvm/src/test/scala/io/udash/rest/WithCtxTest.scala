package io.udash
package rest

import io.udash.rest.WithCtx.NoCtx
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class WithCtxTest extends AnyFunSuite with ScalaFutures with Matchers {
  implicit def scheduler: Scheduler = Scheduler.global

  private val CtxTask = CtxTaskCompanion[UserCtx]
  private val alice = UserCtx("alice")

  test("WithCtx.apply and result compute the value for a context") {
    val wc = WithCtx[UserCtx, String](_.user.toUpperCase)
    wc(alice) shouldBe "ALICE"
    wc.result(alice) shouldBe "ALICE"
  }

  test("CtxTaskCompanion factory methods") {
    CtxTask.now(1).apply(alice).runToFuture.futureValue shouldBe 1
    CtxTask.eval(2).apply(alice).runToFuture.futureValue shouldBe 2
    CtxTask.sync(_.user).apply(alice).runToFuture.futureValue shouldBe "alice"
    CtxTask(ctx => Task.now(ctx.user + "!")).apply(alice).runToFuture.futureValue shouldBe "alice!"
    CtxTask.readCtx.apply(alice).runToFuture.futureValue shouldBe alice
    CtxTask.readCtx(_.user).apply(alice).runToFuture.futureValue shouldBe "alice"
    CtxTask.unit.apply(alice).runToFuture.futureValue shouldBe (())
    CtxTask.defer(CtxTask.now(3)).apply(alice).runToFuture.futureValue shouldBe 3
    CtxTask.raiseError[Int](new RuntimeException("boom"))
      .apply(alice).runToFuture.failed.futureValue.getMessage shouldBe "boom"
  }

  test("CtxTaskOps.map and flatMap thread the same context through") {
    val base = CtxTask.readCtx(_.user) // yields ctx.user
    base.map(_.length).apply(alice).runToFuture.futureValue shouldBe 5
    base.flatMap(u => CtxTask.now(u + "-x")).apply(alice).runToFuture.futureValue shouldBe "alice-x"
  }

  test("CtxTaskOps.onErrorRecover and failed") {
    val failing = CtxTask.raiseError[Int](new RuntimeException("nope"))
    failing.onErrorRecover { case _ => 42 }.apply(alice).runToFuture.futureValue shouldBe 42
    failing.failed.apply(alice).runToFuture.futureValue.getMessage shouldBe "nope"
  }

  test("NoCtx makes result resolvable from the implicit scope") {
    val wc = WithCtx[NoCtx, Int](_ => 7)
    wc.result shouldBe 7 // uses the implicit NoCtx.noContext
  }
}
