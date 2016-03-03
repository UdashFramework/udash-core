package io.udash.rpc

import scala.language.higherKinds
import scala.util.Random

class NativeRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default JS serializers" should tests(DefaultUdashRPCFramework)
  "RPCMessages default JS serializers" should hugeTests(DefaultUdashRPCFramework)
}
