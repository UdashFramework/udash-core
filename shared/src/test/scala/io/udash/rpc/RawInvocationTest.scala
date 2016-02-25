package io.udash.rpc

import io.udash.testing.UdashSharedTest
import upickle.Js

class RawInvocationTest extends UdashSharedTest {
  "RawInvocation uPickle Serializers" should {
    "serialize and deserialize invocation" in {
      import RawInvocation._

      val inv = RawInvocation("rpcName", List(List(Js.Str("Test"), Js.Num(7)), List(Js.Obj("k" -> Js.Str("v")))))
      val serialized = RawInvocationWriter.write(inv)
      val deserialized = RawInvocationReader.read(serialized)

      deserialized should be(inv)
    }

    "serialize and deserialize invocation without args" in {
      import RawInvocation._

      val inv = RawInvocation("rpcName", List())
      val serialized = RawInvocationWriter.write(inv)
      val deserialized = RawInvocationReader.read(serialized)

      deserialized should be(inv)
    }
  }
}
