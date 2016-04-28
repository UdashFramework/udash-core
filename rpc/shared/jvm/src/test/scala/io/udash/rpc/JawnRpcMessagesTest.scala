package io.udash.rpc

import scala.language.higherKinds

class JawnRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default serializers" should tests(DefaultUdashRPCFramework)
  "RPCMessages default serializers" should hugeTests(DefaultUdashRPCFramework)
}