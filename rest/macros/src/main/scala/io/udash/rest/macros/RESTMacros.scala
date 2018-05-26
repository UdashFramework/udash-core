package io.udash.rest.macros

import com.avsystem.commons.macros.rpc.RPCMacros

import scala.reflect.macros.blackbox

class RESTMacros(val ctx: blackbox.Context) {
  val rpcm = new RPCMacros(ctx)

  import rpcm._
  import rpcm.c.universe._

  sealed trait BodyArgumentsState
  object BodyArgumentsState {
    case object None extends BodyArgumentsState
    case object HasBody extends BodyArgumentsState
    case object HasBodyValue extends BodyArgumentsState
  }

  val FrameworkObj: Tree = c.prefix.tree
  val RestPackage = q"_root_.io.udash.rest"

  val RESTFrameworkType = getType(tq"$RestPackage.UdashRESTFramework")
  val ValidRESTCls = tq"$FrameworkObj.ValidREST"
  val ValidServerRESTCls = tq"$FrameworkObj.ValidServerREST"

  val RpcNameCls = tq"_root_.io.udash.rpc.RPCName"
  val RestNameCls = tq"$RestPackage.RESTName"
  val RestParamNameCls = tq"$RestPackage.RESTParamName"
  val SkipRestNameCls = tq"$RestPackage.SkipRESTName"

  val RestMethodCls = tq"$RestPackage.RESTMethod"
  val GetCls = tq"$RestPackage.GET"
  val PostCls = tq"$RestPackage.POST"
  val PutCls = tq"$RestPackage.PUT"
  val PatchCls = tq"$RestPackage.PATCH"
  val DeleteCls = tq"$RestPackage.DELETE"

  val ArgumentTypeCls = tq"$RestPackage.ArgumentType"
  val BodyCls = tq"$RestPackage.Body"
  val BodyValueCls = tq"$RestPackage.BodyValue"
  val HeaderCls = tq"$RestPackage.Header"
  val QueryCls = tq"$RestPackage.Query"
  val URLPartCls = tq"$RestPackage.URLPart"

  def hasAnnot(annotations: List[Annot], annotation: Type) = annotations.exists(_.tree.tpe <:< annotation)
  def hasRestMethodAnnot(annotations: List[Annot]) = hasAnnot(annotations, getType(RestMethodCls))
  def hasArgumentTypeAnnot(annotations: List[Annot]) = hasAnnot(annotations, getType(ArgumentTypeCls))
  def hasSkipRestNameAnnot(annotations: List[Annot]) = hasAnnot(annotations, getType(SkipRestNameCls))
  def hasRESTNameOverride(annotations: List[Annot]) = hasAnnot(annotations, getType(RestNameCls))

  def countAnnot(annotations: List[Annot], annotation: Type) = annotations.count(_.tree.tpe <:< annotation)
  def countRestMethodAnnot(annotations: List[Annot]) = countAnnot(annotations, getType(RestMethodCls))
  def countArgumentTypeAnnot(annotations: List[Annot]) = countAnnot(annotations, getType(ArgumentTypeCls))

  def isNameAnnotArgumentValid(annotations: List[Annot], annotation: Type) = {
    val count: Int = countAnnot(annotations, annotation)
    if (count == 1) {
      val children = annotations.find(_.tree.tpe <:< annotation).get.tree.children
      val Literal(Constant(name: String)) = children(1)
      children.size == 2 && name.nonEmpty
    } else count == 0
  }

  def checkNameOverride(annotations: List[Annot], errorMsg: Tree => String) =
    Seq(RpcNameCls, RestNameCls, RestParamNameCls).foreach(cls =>
      if (!isNameAnnotArgumentValid(annotations, getType(cls)))
        abort(errorMsg(cls))
    )

  def checkMethodNameOverride(method: RealMethod) =
    checkNameOverride(allAnnotations(method.symbol),
      cls => s"@$cls annotation argument has to be non empty string, value on ${method.description} is not.")

  def checkParameterNameOverride(parameter: RealParam) =
    checkNameOverride(allAnnotations(parameter.symbol),
      cls => s"@$cls annotation argument has to be non empty string, value on ${parameter.description} is not.")

  def checkParameterTypeAnnots(parameter: RealParam) =
    if (countArgumentTypeAnnot(allAnnotations(parameter.symbol)) > 1)
      abort(s"REST method argument has to be annotated with at most one argument type annotation, ${parameter.description} has not.")

  def checkGetterParameter(param: RealParam): Unit = {
    checkParameterNameOverride(param)
    checkParameterTypeAnnots(param)

    if (param.annot(getType(BodyCls)).nonEmpty)
      abort(s"Subinterface getter cannot contain arguments annotated with @Body annotation, ${param.owner.description} does.")
    if (param.annot(getType(BodyValueCls)).nonEmpty)
      abort(s"Subinterface getter cannot contain arguments annotated with @BodyValue annotation, ${param.owner.description} does.")
  }

  def checkMethodParameter(param: RealParam, bodyArgsState: BodyArgumentsState): BodyArgumentsState = {
    checkParameterNameOverride(param)
    checkParameterTypeAnnots(param)

    if (param.annot(getType(BodyCls)).nonEmpty) {
      if (bodyArgsState != BodyArgumentsState.None)
        abort(s"REST method cannot contain more than one argument annotated with @Body annotation, ${param.owner.description} does.")
      if (param.owner.annot(getType(GetCls)).nonEmpty)
        abort(s"GET HTTP request cannot contain body argument, ${param.owner.description} does.")
      BodyArgumentsState.HasBody
    } else if (param.annot(getType(BodyValueCls)).nonEmpty) {
      if (bodyArgsState == BodyArgumentsState.HasBody)
        abort(s"REST method cannot contain arguments annotated with both @Body and @BodyValue, ${param.owner.description} does.")
      if (param.owner.annot(getType(GetCls)).nonEmpty)
        abort(s"GET HTTP request cannot contain body argument, ${param.owner.description} does.")
      BodyArgumentsState.HasBodyValue
    } else bodyArgsState
  }

  private def validRest(restType: Type, isServer: Boolean): c.Tree = {
    val realRpc = RealRpcTrait(restType)
    val proxyables: List[RealMethod] = realRpc.realMethods
    val (subinterfaces, methods) = proxyables.partition(rm =>
      c.inferImplicitValue(getType(q"$FrameworkObj.RPCMetadata[${rm.resultType}]")) != EmptyTree)

    val subinterfacesImplicits = subinterfaces.map { getter =>
      checkMethodNameOverride(getter)
      if (isServer && hasRESTNameOverride(allAnnotations(getter.symbol)))
        abort(s"Subinterface getter cannot be annotated with RESTName annotation in server-side interface, ${getter.description} does.")
      if (hasRestMethodAnnot(allAnnotations(getter.symbol)))
        abort(s"Subinterface getter cannot be annotated with REST method annotation, ${getter.description} does.")
      if (isServer && hasSkipRestNameAnnot(allAnnotations(getter.symbol)))
        abort(s"Subinterface getter in server-side REST interface cannot be annotated with @SkipRESTName annotation, ${getter.description} does.")

      getter.paramLists.foreach(paramsList =>
        paramsList.foreach(param => checkGetterParameter(param))
      )

      if (!isServer)
        q"""implicitly[$ValidRESTCls[${getter.resultType}]]"""
      else
        q"""implicitly[$ValidServerRESTCls[${getter.resultType}]]"""
    }

    val methodsImplicits = methods.map { method =>
      var alreadyContainsBodyArgument: BodyArgumentsState = BodyArgumentsState.None
      checkMethodNameOverride(method)
      if (isServer && hasRESTNameOverride(allAnnotations(method.symbol)))
        abort(s"REST method cannot be annotated with RESTName annotation in server-side interface, ${method.description} does.")
      if (countRestMethodAnnot(allAnnotations(method.symbol)) > 1)
        abort(s"REST method has to be annotated with at most one REST method annotation, ${method.description} has not.")
      if (isServer && hasSkipRestNameAnnot(allAnnotations(method.symbol)))
        abort(s"REST method in server-side REST interface cannot be annotated with @SkipRESTName annotation, ${method.description} does.")

      method.paramLists.foreach(paramsList =>
        paramsList.foreach(param =>
          alreadyContainsBodyArgument = checkMethodParameter(param, alreadyContainsBodyArgument)
        )
      )
      q"""null"""
    }

    val cls = if (!isServer) ValidRESTCls else ValidServerRESTCls
    q"""
      new $cls[$restType] {
        implicit def ${c.freshName(TermName("self"))}: $cls[$restType] = this

        def subInterfaces = Seq(..$subinterfacesImplicits)
        def methods = Seq(..$methodsImplicits)
      }
      """
  }

  def asValidRest[T: c.WeakTypeTag]: c.Tree =
    validRest(weakTypeOf[T], isServer = false)

  def asValidServerRest[T: c.WeakTypeTag]: c.Tree =
    validRest(weakTypeOf[T], isServer = true)
}
