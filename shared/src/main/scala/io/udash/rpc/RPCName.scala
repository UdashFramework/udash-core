package io.udash.rpc

import scala.annotation.StaticAnnotation

/**
 * You can use this annotation on overloaded RPC methods to give them unique identifiers for RCP serialization.
 */
class RPCName(val name: String) extends StaticAnnotation
