package io.udash.macros.rpc

import com.avsystem.commons.macros.rpc.RPCMacros

import scala.reflect.macros.blackbox

class UdashRPCMacros(override val c: blackbox.Context) extends RPCMacros(c) {
  import c.universe._

  val UdashFrameworkObj = c.prefix.tree
  val AsRawClientRPCCls = tq"$UdashFrameworkObj.AsRawClientRPC"
  val AsRealClientRPCCls = tq"$UdashFrameworkObj.AsRealClientRPC"

  def asRawClientImpl[T: c.WeakTypeTag]: c.Tree = {
    val rpcTpe = weakTypeOf[T]
    val (_, functions, _) = proxyableMethods(rpcTpe)

    if (functions.nonEmpty)
      abort(s"Client RPC interfaces cannot contain functions, $rpcTpe does.")

    q"""new $AsRawClientRPCCls"""
  }

  def asRealClientImpl[T: c.WeakTypeTag]: c.Tree = {
    val rpcTpe = weakTypeOf[T]
    val (_, functions, _) = proxyableMethods(rpcTpe)

    if (functions.nonEmpty)
      abort(s"Client RPC interfaces cannot contain functions, $rpcTpe does.")

    q"""new $AsRealClientRPCCls"""
  }
}
