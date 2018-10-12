package io.udash.utils

import io.udash.testing.UdashSharedTest

import scala.collection.mutable

class CallbacksHandlerTest extends UdashSharedTest {
  "CallbacksHandler" should {
    "fire all callbacks and swallow exceptions" in {
      val orderCheck = mutable.ArrayBuffer.empty[Int]
      var counter = 0
      def callback(idx: Int): PartialFunction[Int, Any] = {
        case v: Int =>
          counter += v
          orderCheck += idx
          throw new NullPointerException
      }

      val handler = new CallbacksHandler[Int]
      handler.register(callback(1))
      handler.register(callback(2))
      handler.register(callback(3))
      handler.register(callback(4))

      counter should be(0)
      orderCheck should be(Seq.empty)

      handler.fire(5)
      counter should be(20)
      orderCheck should be(Seq(1, 2, 3, 4))

      handler.fire(1)
      counter should be(24)
      orderCheck should be(Seq(1, 2, 3, 4, 1, 2, 3, 4))
    }

    "handle callbacks creating new callback" in {
      val handler = new CallbacksHandler[Int]

      val orderCheck = mutable.ArrayBuffer.empty[Int]
      def callback(idx: Int): PartialFunction[Int, Any] = {
        case _: Int =>
          orderCheck += idx
          handler.register(callback(idx + 1))
      }

      handler.register(callback(1))

      orderCheck should be(Seq.empty)

      handler.fire(0)
      orderCheck should be(Seq(1))

      handler.fire(0)
      orderCheck should be(Seq(1, 1, 2))
    }
  }
}
