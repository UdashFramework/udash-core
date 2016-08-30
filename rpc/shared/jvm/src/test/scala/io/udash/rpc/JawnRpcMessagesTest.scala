package io.udash.rpc

class JawnRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default serializers" should tests(DefaultServerUdashRPCFramework)
  "RPCMessages default serializers" should hugeTests(DefaultServerUdashRPCFramework)
}