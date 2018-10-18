package io.udash.rpc


class NativeRpcMessagesTest extends RpcMessagesTestScenarios {
  "RPCMessages default JS serializers" should tests(DefaultClientUdashRPCFramework)
  "RPCMessages default JS serializers" should hugeTests(DefaultClientUdashRPCFramework)
}
