package io.udash
package rest

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.avsystem.commons.*
import com.avsystem.commons.misc.ScalaDurationExtensions.durationIntOps
import io.udash.rest.raw.RawRest
import io.udash.testing.AsyncUdashSharedTest
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.scalactic.source.Position
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Assertion, BeforeAndAfterEach}

import scala.concurrent.duration.FiniteDuration

abstract class RestApiTest extends AsyncUdashSharedTest with BeforeAndAfterEach {
  implicit def scheduler: Scheduler = Scheduler.global

  protected final val MaxConnections: Int = 1 // to timeout quickly
  protected final val Connections: Int = 10 // > MaxConnections
  protected final val CallTimeout: FiniteDuration = 300.millis // << idle timeout
  protected final val IdleTimout: FiniteDuration = CallTimeout * 100

  protected val impl: RestTestApi.Impl = new RestTestApi.Impl
  protected val streamingImpl: StreamingRestTestApi.Impl = new StreamingRestTestApi.Impl

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    impl.resetCounter()
  }

  final val serverHandle: RawRest.HandleRequest =
    RawRest.asHandleRequest[RestTestApi](impl)

  final val streamingServerHandle: RawRest.HandleRequestWithStreaming =
    RawRest.asHandleRequestWithStreaming[StreamingRestTestApi](streamingImpl)

  def clientHandle: RawRest.HandleRequest

  def streamingClientHandler: RawRest.RestRequestHandler =
    throw new UnsupportedOperationException(s"Streaming not supported in ${getClass.getSimpleName}")

  lazy val proxy: RestTestApi =
    RawRest.fromHandleRequest[RestTestApi](clientHandle)

  lazy val streamingProxy: StreamingRestTestApi =
    RawRest.fromHandleRequestWithStreaming[StreamingRestTestApi](streamingClientHandler)

  def testCall[T](call: RestTestApi => Future[T])(implicit pos: Position): Future[Assertion] =
    (call(proxy).wrapToTry, call(impl).catchFailures.wrapToTry).mapN { (proxyResult, implResult) =>
      assert(proxyResult.map(mkDeep) == implResult.map(mkDeep))
    }

  def testStream[T](call: StreamingRestTestApi => Observable[T])(implicit pos: Position): Future[Assertion] =
    (call(streamingProxy).toListL.materialize, call(streamingImpl).toListL.materialize).mapN { (proxyResult, implResult) =>
      assert(proxyResult.map(mkDeep) == implResult.map(mkDeep))
    }.runToFuture

  def mkDeep(value: Any): Any = value match {
    case arr: Array[_] => IArraySeq.empty[AnyRef] ++ arr.iterator.map(mkDeep)
    case _ => value
  }
}

trait RestApiTestScenarios extends RestApiTest {
  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(scaled(Span(10, Seconds)), scaled(Span(50, Millis)))

  "trivial GET" in {
    testCall(_.trivialGet)
  }

  "failing GET" in {
    testCall(_.failingGet)
  }

  "JSON failing GET" in {
    testCall(_.jsonFailingGet)
  }

  "more failing GET" in {
    testCall(_.moreFailingGet)
  }

  "complex GET" in {
    testCall(_.complexGet(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", Opt(3), 4, "ó /&f"))
    testCall(_.complexGet(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", Opt.Empty, 3, "ó /&f"))
  }

  "multi-param body POST" in {
    testCall(_.multiParamPost(0, "a/ +&", 1, "b/ +&", 2, "ć/ +&", 3, "l\"l"))
  }

  "single body PUT" in {
    testCall(_.singleBodyPut(RestEntity(RestEntityId("id"), "señor")))
  }

  "form POST" in {
    testCall(_.formPost("ó", "ą=ę", 42))
  }

  "prefixed GET" in {
    testCall(_.prefix("p0", "h0", "q0").subget(0, 1, 2))
  }

  "transparent prefix GET" in {
    testCall(_.transparentPrefix.subget(0, 1, 2))
  }

  "custom response with headers" in {
    testCall(_.customResponse("walue"))
  }

  "binary request and response" in {
    testCall(_.binaryEcho(Array.fill[Byte](5)(5)))
  }

  "large binary request and response" in {
    testCall(_.binaryEcho(Array.fill[Byte](1024 * 1024)(5)))
  }

  "body using third party type" in {
    testCall(_.thirdPartyBody(HasThirdParty(ThirdParty(5))))
  }

  "close connection on monix task timeout" in {
    Task
      .traverse(List.range(0, Connections))(_ => Task.deferFuture(proxy.neverGet).timeout(CallTimeout).failed)
      .map(_ => assertResult(expected = Connections)(actual = impl.counterValue())) // neverGet should be called Connections times
      .runToFuture
  }

  "close connection on monix task cancellation" in {
    Task
      .traverse(List.range(0, Connections)) { i =>
        val cancelable = Task.deferFuture(proxy.neverGet).runAsync(_ => ())
        Task.sleep(100.millis)
          .restartUntil(_ => impl.counterValue() >= i)
          .map(_ => cancelable.cancel())
      }
      .map(_ => assertResult(expected = Connections)(actual = impl.counterValue())) // neverGet should be called Connections times
      .runToFuture
  }
}

// TODO streaming MORE tests: cancellation, timeouts, errors, errors after sending a few elements, custom format, slow source observable
trait StreamingRestApiTestScenarios extends RestApiTest {

  "trivial GET stream" in {
    testStream(_.simpleStream)
  }

  "json GET stream" in {
    testStream(_.jsonStream)
  }
}
