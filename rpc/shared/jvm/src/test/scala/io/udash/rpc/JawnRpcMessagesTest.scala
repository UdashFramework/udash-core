package io.udash.rpc

import scala.language.higherKinds

class JawnRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default serializers" should tests(DefaultServerUdashRPCFramework)
  "RPCMessages default serializers" should hugeTests(DefaultServerUdashRPCFramework)
}