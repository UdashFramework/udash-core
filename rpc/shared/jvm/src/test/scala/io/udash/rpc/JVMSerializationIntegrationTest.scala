package io.udash.rpc

import scala.language.higherKinds

class JVMSerializationIntegrationTest extends SerializationIntegrationTestBase {
  "DefaultUdashRPCFramework -> UPickleUdashRPCFramework default serialization" should tests(DefaultUdashRPCFramework, UPickleUdashRPCFramework)
  "UPickleUdashRPCFramework -> DefaultUdashRPCFramework default serialization" should tests(UPickleUdashRPCFramework, DefaultUdashRPCFramework)
}