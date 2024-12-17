package io.udash
package rest

import com.avsystem.commons.*
import com.avsystem.commons.misc.ScalaDurationExtensions.durationIntOps
import io.udash.rest.raw.RawRest
import io.udash.rest.raw.RawRest.HandleRequest
import monix.eval.Task
import monix.execution.Scheduler
import org.scalactic.source.Position
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.duration.FiniteDuration

abstract class RestApiTest extends AnyFunSuite with ScalaFutures {
  protected final val MaxConnections: Int = 1 // to timeout quickly
  protected final val Connections: Int = 10 // > MaxConnections
  protected final val CallTimeout: FiniteDuration = 300.millis // << idle timeout

  implicit def scheduler: Scheduler = Scheduler.global

  private val impl: RestTestApi = RestTestApi.Impl

  final val serverHandle: RawRest.HandleRequest =
    RawRest.asHandleRequest[RestTestApi](impl)

  def clientHandle: RawRest.HandleRequest

  lazy val proxy: RestTestApi =
    RawRest.fromHandleRequest[RestTestApi](clientHandle)

  def testCall[T](call: RestTestApi => Future[T])(implicit pos: Position): Unit =
    assert(
      call(proxy).wrapToTry.futureValue.map(mkDeep) ==
        call(impl).catchFailures.wrapToTry.futureValue.map(mkDeep)
    )

  def mkDeep(value: Any): Any = value match {
    case arr: Array[_] => IArraySeq.empty[AnyRef] ++ arr.iterator.map(mkDeep)
    case _ => value
  }

  def getNeverGetCounter(): Int = impl.neverGetCounter.get()
  def resetNeverGetCounter(): Unit = impl.neverGetCounter.set(0)
}

trait RestApiTestScenarios extends RestApiTest {
  test("trivial GET") {
    testCall(_.trivialGet)
  }

  test("failing GET") {
    testCall(_.failingGet)
  }

  test("JSON failing GET") {
    testCall(_.jsonFailingGet)
  }

  test("more failing GET") {
    testCall(_.moreFailingGet)
  }

  test("complex GET") {
    testCall(_.complexGet(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", Opt(3), 4, "ó /&f"))
    testCall(_.complexGet(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", Opt.Empty, 3, "ó /&f"))
  }

  test("multi-param body POST") {
    testCall(_.multiParamPost(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", 3, "l\"l"))
  }

  test("single body PUT") {
    testCall(_.singleBodyPut(RestEntity(RestEntityId("id"), "señor")))
  }

  test("form POST") {
    testCall(_.formPost("ó", "ą=ę", 42))
  }

  test("prefixed GET") {
    testCall(_.prefix("p0", "h0", "q0").subget(0, 1, 2))
  }

  test("transparent prefix GET") {
    testCall(_.transparentPrefix.subget(0, 1, 2))
  }

  test("custom response with headers") {
    testCall(_.customResponse("walue"))
  }

  test("binary request and response") {
    testCall(_.binaryEcho(Array.fill[Byte](5)(5)))
  }

  test("large binary request and response") {
    testCall(_.binaryEcho(Array.fill[Byte](1024 * 1024)(5)))
  }

  test("body using third party type") {
    testCall(_.thirdPartyBody(HasThirdParty(ThirdParty(5))))
  }

  test("close connection on monix task timeout") {
    resetNeverGetCounter()
    Task
      .traverse(List.range(0, Connections))(_ => Task.deferFuture(proxy.neverGet).timeout(CallTimeout).failed)
      .map(_ => assertResult(expected = Connections)(actual = getNeverGetCounter())) // neverGet should be called Connections times
      .runToFuture
      .futureValue(Timeout(30.seconds))
  }

  test("close connection on monix task cancellation") {
    resetNeverGetCounter()
    Task
      .traverse(List.range(0, Connections)) { i =>
        val cancelable = Task.deferFuture(proxy.neverGet).runAsync(_ => ())
        Task.sleep(100.millis)
          .restartUntil(_ => getNeverGetCounter() >= i)
          .map(_ => cancelable.cancel())
      }
      .map(_ => assertResult(expected = Connections)(actual = getNeverGetCounter())) // neverGet should be called Connections times
      .runToFuture
      .futureValue(Timeout(30.seconds))
  }
}

class DirectRestApiTest extends RestApiTestScenarios {
  def clientHandle: HandleRequest = serverHandle
}
