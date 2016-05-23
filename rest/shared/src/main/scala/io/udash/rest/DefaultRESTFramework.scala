package io.udash.rest

import io.udash.rpc.{AutoUdashRPCFramework, DefaultUdashSerialization}

object DefaultRESTFramework extends UdashRESTFramework with AutoUdashRPCFramework with DefaultUdashSerialization
