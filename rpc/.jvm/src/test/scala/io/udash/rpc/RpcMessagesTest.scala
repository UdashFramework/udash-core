package io.udash.rpc

class RpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default serializers" should tests(DefaultServerUdashRPCFramework)
  "RPCMessages default serializers" should hugeTests(DefaultServerUdashRPCFramework)
}