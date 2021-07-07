package io.udash.macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.blackbox

class RestMacros(val ctx: blackbox.Context) extends AbstractMacroCommons(ctx) {

  import c.universe._

  def UdashRestPkg: Tree = q"_root_.io.udash.rest"

  def materializeImplMetadata[Real: c.WeakTypeTag]: Tree =
    q"$CommonsPkg.rpc.RpcMetadata.materializeForApi[$UdashRestPkg.raw.RestMetadata, ${weakTypeOf[Real]}]"

  def materializeImplOpenApiMetadata[Real: c.WeakTypeTag]: Tree =
    q"$CommonsPkg.rpc.RpcMetadata.materializeForApi[$UdashRestPkg.openapi.OpenApiMetadata, ${weakTypeOf[Real]}]"
}
