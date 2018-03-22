package io.udash.rpc


class JSSerializationIntegrationTest extends SerializationIntegrationTestBase {
  override val repeats = 3
  "DefaultUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(DefaultClientUdashRPCFramework, DefaultClientUdashRPCFramework)
  "DefaultUdashRPCFramework -> UPickleUdashRPCFramework default serialization" should tests(DefaultClientUdashRPCFramework, ClientUPickleUdashRPCFramework)
  "UPickleUdashRPCFramework -> DefaultUdashRPCFramework  default serialization" should tests(ServerUPickleUdashRPCFramework, DefaultClientUdashRPCFramework)
}