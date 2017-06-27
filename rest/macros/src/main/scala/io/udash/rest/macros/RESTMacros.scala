package io.udash.rest.macros

import com.avsystem.commons.macros.rpc.RPCMacros

import scala.reflect.macros.blackbox

class RESTMacros(override val c: blackbox.Context) extends RPCMacros(c) {

  import c.universe._

  sealed trait BodyArgumentsState
  object BodyArgumentsState {
    case object None extends BodyArgumentsState
    case object HasBody extends BodyArgumentsState
    case object HasBodyValue extends BodyArgumentsState
  }

  val RestPackage = q"io.udash.rest"

  val RESTFrameworkType = getType(tq"$RestPackage.UdashRESTFramework")
  val ValidRESTCls = tq"$FrameworkObj.ValidREST"
  val ValidServerRESTCls = tq"$FrameworkObj.ValidServerREST"

  val RpcNameCls = tq"io.udash.rpc.RPCName"
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

  def hasAnnotation(annotations: List[Annotation], annotation: Type) = annotations.exists(_.tree.tpe <:< annotation)
  def hasRestMethodAnnotation(annotations: List[Annotation]) = hasAnnotation(annotations, getType(RestMethodCls))
  def hasArgumentTypeAnnotation(annotations: List[Annotation]) = hasAnnotation(annotations, getType(ArgumentTypeCls))
  def hasSkipRestNameAnnotation(annotations: List[Annotation]) = hasAnnotation(annotations, getType(SkipRestNameCls))
  def hasRESTNameOverride(annotations: List[Annotation]) = hasAnnotation(annotations, getType(RestNameCls))

  def countAnnotation(annotations: List[Annotation], annotation: Type) = annotations.count(_.tree.tpe <:< annotation)
  def countRestMethodAnnotation(annotations: List[Annotation]) = countAnnotation(annotations, getType(RestMethodCls))
  def countArgumentTypeAnnotation(annotations: List[Annotation]) = countAnnotation(annotations, getType(ArgumentTypeCls))

  def isNameAnnotationArgumentValid(annotations: List[Annotation], annotation: Type) = {
    val count: Int = countAnnotation(annotations, annotation)
    if (count == 1) {
      val children = annotations.find(_.tree.tpe <:< annotation).get.tree.children
      val Literal(Constant(name: String)) = children(1)
      children.size == 2 && name.nonEmpty
    } else count == 0
  }

  def checkNameOverride(annotations: List[Annotation], errorMsg: Tree => String) =
    Seq(RpcNameCls, RestNameCls, RestParamNameCls).foreach(cls =>
      if (!isNameAnnotationArgumentValid(annotations, getType(cls)))
        abort(errorMsg(cls))
    )

  def checkMethodNameOverride(method: ProxyableMember, restType: Type) =
    checkNameOverride(method.method.annotations,
      cls => s"@$cls annotation argument has to be non empty string, value on ${method.rpcName} in $restType is not.")

  def checkParameterNameOverride(parameter:  Symbol, method: ProxyableMember, restType: Type) =
    checkNameOverride(parameter.annotations,
      cls => s"@$cls annotation argument has to be non empty string, value on ${parameter.name} from ${method.rpcName} in $restType is not.")

  def checkParameterTypeAnnotations(parameter:  Symbol, method: ProxyableMember, restType: Type) =
    if (countArgumentTypeAnnotation(parameter.annotations) != 1)
      abort(s"REST method argument has to be annotated with exactly one argument type annotation, ${parameter.name} from ${method.rpcName} in $restType has not.")

  def checkGetterParameter(param: Symbol, getter: ProxyableMember, restType: Type): Unit = {
    checkParameterNameOverride(param, getter, restType)
    checkParameterTypeAnnotations(param, getter, restType)

    if (hasAnnotation(param.annotations, getType(BodyCls)))
      abort(s"Subinterface getter cannot contain arguments annotated with @Body annotation, ${getter.rpcName} in $restType does.")
    if (hasAnnotation(param.annotations, getType(BodyValueCls)))
      abort(s"Subinterface getter cannot contain arguments annotated with @BodyValue annotation, ${getter.rpcName} in $restType does.")
  }

  def checkMethodParameter(param: Symbol, method: ProxyableMember, restType: Type, bodyArgsState: BodyArgumentsState): BodyArgumentsState = {
    checkParameterNameOverride(param, method, restType)
    checkParameterTypeAnnotations(param, method, restType)

    if (hasAnnotation(param.annotations, getType(BodyCls))) {
      if (bodyArgsState != BodyArgumentsState.None)
        abort(s"REST method cannot contain more than one argument annotated with @Body annotation, ${method.rpcName} in $restType does.")
      if (hasAnnotation(method.method.annotations, getType(GetCls)))
        abort(s"GET HTTP request cannot contain body argument, ${method.rpcName} in $restType does.")
      BodyArgumentsState.HasBody
    } else if (hasAnnotation(param.annotations, getType(BodyValueCls))) {
      if (bodyArgsState == BodyArgumentsState.HasBody)
        abort(s"REST method cannot contain arguments annotated with both @Body and @BodyValue, ${method.rpcName} in $restType does.")
      if (hasAnnotation(method.method.annotations, getType(GetCls)))
        abort(s"GET HTTP request cannot contain body argument, ${method.rpcName} in $restType does.")
      BodyArgumentsState.HasBodyValue
    } else bodyArgsState
  }

  private def validRest(restType: Type, isServer: Boolean): c.Tree = {
    val proxyables: List[ProxyableMember] = proxyableMethods(restType)
    val subinterfaces = proxyables.filter(proxyable => hasRpcAnnot(proxyable.returnType))
    val methods = proxyables.filterNot(proxyable => hasRpcAnnot(proxyable.returnType))

    val subinterfacesImplicits = subinterfaces.map(getter => {
      checkMethodNameOverride(getter, restType)
      if (isServer && hasRESTNameOverride(getter.method.annotations))
        abort(s"Subinterface getter cannot be annotated with RESTName annotation in server-side interface, ${getter.rpcName} in $restType does.")
      if (hasRestMethodAnnotation(getter.method.annotations))
        abort(s"Subinterface getter cannot be annotated with REST method annotation, ${getter.rpcName} in $restType does.")
      if (isServer && hasSkipRestNameAnnotation(getter.method.annotations))
        abort(s"Subinterface getter in server-side REST interface cannot be annotated with @SkipRESTName annotation, ${getter.rpcName} in $restType does.")

      getter.paramLists.foreach(paramsList =>
        paramsList.foreach(param => checkGetterParameter(param, getter, restType))
      )

      if (!isServer) q"""implicitly[$ValidRESTCls[${getter.returnType}]]"""
      else q"""implicitly[$ValidServerRESTCls[${getter.returnType}]]"""
    })

    val methodsImplicits = methods.map(method => {
      var alreadyContainsBodyArgument: BodyArgumentsState = BodyArgumentsState.None
      checkMethodNameOverride(method, restType)
      if (isServer && hasRESTNameOverride(method.method.annotations))
        abort(s"REST method cannot be annotated with RESTName annotation in server-side interface, ${method.rpcName} in $restType does.")
      if (countRestMethodAnnotation(method.method.annotations) != 1)
        abort(s"REST method has to be annotated with exactly one REST method annotation, ${method.rpcName} in $restType has not.")
      if (isServer && hasSkipRestNameAnnotation(method.method.annotations))
        abort(s"REST method in server-side REST interface cannot be annotated with @SkipRESTName annotation, ${method.rpcName} in $restType does.")

      method.paramLists.foreach(paramsList =>
        paramsList.foreach(param =>
          alreadyContainsBodyArgument = checkMethodParameter(param, method, restType, alreadyContainsBodyArgument)
        )
      )
      q"""null"""
    })

    val cls = if (!isServer) ValidRESTCls else ValidServerRESTCls
      q"""
      new $cls[$restType] {
        implicit def ${c.freshName(TermName("self"))}: $cls[$restType] = this

        val subInterfaces = Seq(..$subinterfacesImplicits)
        val methods = Seq(..$methodsImplicits)
      }
      """
  }

  def asValidRest[T: c.WeakTypeTag]: c.Tree =
    validRest(weakTypeOf[T], isServer = false)

  def asValidServerRest[T: c.WeakTypeTag]: c.Tree =
    validRest(weakTypeOf[T], isServer = true)
}
