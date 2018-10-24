package io.udash
package rest

import com.avsystem.commons._
import io.udash.rest.raw.RawRest
import io.udash.rest.raw.RawRest.HandleRequest
import org.scalactic.source.Position
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures

abstract class AbstractRestCallTest extends FunSuite with ScalaFutures {
  final val serverHandle: RawRest.HandleRequest =
    RawRest.asHandleRequest[RestTestApi](RestTestApi.Impl)

  def clientHandle: RawRest.HandleRequest

  lazy val proxy: RestTestApi =
    RawRest.fromHandleRequest[RestTestApi](clientHandle)

  def testCall[T](call: RestTestApi => Future[T])(implicit pos: Position): Unit =
    assert(call(proxy).wrapToTry.futureValue == call(RestTestApi.Impl).catchFailures.wrapToTry.futureValue)

  test("trivial GET") {
    testCall(_.trivialGet)
  }

  test("failing GET") {
    testCall(_.failingGet)
  }

  test("more failing GET") {
    testCall(_.moreFailingGet)
  }

  test("complex GET") {
    testCall(_.complexGet(0, "a/+&", 1, "b/+&", 2, "ć/+&"))
  }

  test("multi-param body POST") {
    testCall(_.multiParamPost(0, "a/+&", 1, "b/+&", 2, "ć/+&", 3, "l\"l"))
  }

  test("single body PUT") {
    testCall(_.singleBodyPut(RestEntity("id", "señor")))
  }

  test("form POST") {
    testCall(_.formPost("ó", "ą=ę", 42))
  }

  test("prefixed GET") {
    testCall(_.prefix("", "h0", "q0").subget(0, 1, 2))
  }

  test("custom response with headers") {
    testCall(_.customResponse("walue"))
  }
}

class DirectRestCallTest extends AbstractRestCallTest {
  def clientHandle: HandleRequest = serverHandle
}
