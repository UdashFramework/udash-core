package io.udash.rpc

import com.avsystem.commons.rpc.rpcName

import scala.annotation.StaticAnnotation

@deprecated("RPC annotation is no longer needed", "0.7.0")
class RPC extends StaticAnnotation

object `package` {
  type RPCName = rpcName
}
