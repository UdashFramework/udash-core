package io.udash.macros

import scala.concurrent.Future
import scala.reflect.macros.blackbox

/**
 * Author: ghik
 * Created: 27/05/15.
 */
class RPCMacros(val c: blackbox.Context) {

  import c.universe._

  val Upickle = q"_root_.upickle"
  val RpcPackage = q"_root_.io.udash.rpc"
  val ListObj = q"_root_.scala.collection.immutable.List"
  val ListCls = tq"_root_.scala.collection.immutable.List"
  val FutureSym = typeOf[Future[_]].typeSymbol
  val RunNowEC = q"$RpcPackage.RunNowExecutionContext"
  val RawRPCCls = tq"$RpcPackage.RawRPC"
  val AsRawRPCObj = q"$RpcPackage.AsRawRPC"
  val AsRawRPCCls = tq"$RpcPackage.AsRawRPC"
  val AsRealRPCObj = q"$RpcPackage.AsRealRPC"
  val AsRealRPCCls = tq"$RpcPackage.AsRealRPC"
  val RPCNameAnnotType = c.mirror.staticClass("io.udash.rpc.RPCName").toType
  val RPCType = c.mirror.staticClass("io.udash.rpc.RPC").toType
  val ClientRPCType = c.mirror.staticClass("io.udash.rpc.ClientRPC").toType

  sealed trait MemberType

  case object Procedure extends MemberType

  case object Function extends MemberType

  case object Getter extends MemberType

  case object Invalid extends MemberType

  case class ProxyableMember(method: MethodSymbol, signature: Type) {
    val returnType = returnTypeOf(signature)
    val typeParams = typeParamsOf(signature)
    val paramLists = paramListsOf(signature)

    val memberType =
      if (returnType =:= typeOf[Unit]) Procedure
      else if (returnType.typeSymbol == FutureSym) Function
      else if (returnType <:< RPCType) Getter
      else Invalid

    val rpcName = (method :: method.overrides).flatMap(_.annotations).find(_.tree.tpe <:< RPCNameAnnotType).map { annot =>
      annot.tree.children.tail match {
        case List(Literal(Constant(name: String))) => TermName(name)
        case _ => c.abort(annot.tree.pos, "The argument of @RPCName must be a string literal.")
      }
    }.getOrElse(method.name)
  }

  def proxyableMethods(tpe: Type) = {
    val isClientRpc = tpe <:< ClientRPCType

    def invalidProxyableMsg(pm: ProxyableMember): String = {
      if (isClientRpc) {
        s"All abstract members in ClientRPC interface must be non-generic methods that return Unit or another ClientRPC interface, ${pm.method} in $tpe does not."
      } else {
        s"All abstract members in RPC interface must be non-generic methods that return Unit, Future or another RPC interface, ${pm.method} in $tpe does not."
      }
    }

    if (!tpe.typeSymbol.isClass || tpe <:< typeOf[AnyVal] || tpe <:< typeOf[Null]) {
      c.abort(c.enclosingPosition, s"RPC type must be a class or trait with abstract members, $tpe is not.")
    }

    val proxyables = tpe.members.filter(m => m.isTerm && m.isAbstract).map { m =>
      val signature = m.typeSignatureIn(tpe)
      ProxyableMember(m.asMethod, signature)
    }.groupBy(_.memberType).withDefaultValue(Iterable.empty)

    proxyables(Invalid).foreach { pm =>
      c.error(c.enclosingPosition, invalidProxyableMsg(pm))
    }

    def validateShapes(members: Iterable[ProxyableMember]): Unit = {
      def shape(pm: ProxyableMember) = (pm.rpcName, pm.paramLists.map(_.map(_ => ())))
      members.groupBy(shape).values.filter(_.size > 1).foreach { overloads =>
        val name = overloads.head.method.name.decodedName.toString
        c.abort(c.enclosingPosition, s"Overloaded variants of $name in $tpe have the same shape " +
          s"(the same number of arguments in every parameter list)\n" +
          s"You can give each variant different @RPCName to resolve such conflicts.")
      }
    }

    val procedures = proxyables(Procedure)
    val functions = proxyables(Function)
    val getters = proxyables(Getter)

    if (isClientRpc && functions.nonEmpty) {
      c.error(c.enclosingPosition, invalidProxyableMsg(functions.head))
    }

    if (isClientRpc) {
      getters.foreach(getter =>
        if (!(getter.returnType <:< ClientRPCType)) {
          c.error(c.enclosingPosition, invalidProxyableMsg(getter))
        }
      )
    }

    validateShapes(procedures)
    validateShapes(functions)
    validateShapes(getters)

    if (proxyables.isEmpty) {
      c.warning(c.enclosingPosition, s"$tpe has no abstract members that could represent remote methods.")
    }

    (procedures, functions, getters)
  }

  def reifyList(args: List[Tree]) = q"$ListObj(..$args)"

  def reifyListPat(args: List[Tree]) = pq"$ListObj(..$args)"

  def paramListsOf(t: Type): List[List[Symbol]] = t match {
    case PolyType(_, resultType) => paramListsOf(resultType)
    case MethodType(params, returnType) =>
      params :: paramListsOf(returnType)
    case _ =>
      Nil
  }

  def returnTypeOf(t: Type): Type = t match {
    case PolyType(_, resultType) => returnTypeOf(resultType)
    case MethodType(_, rt) => returnTypeOf(rt)
    case NullaryMethodType(rt) => rt
    case _ => t
  }

  def typeParamsOf(t: Type): List[Symbol] = t match {
    case PolyType(typeParams, _) => typeParams
    case _ => Nil
  }

  def asRawImpl[T: c.WeakTypeTag]: c.Tree = {
    val rpcTpe = weakTypeOf[T]
    val (procedures, functions, getters) = proxyableMethods(rpcTpe)

    val implName = c.freshName(TermName("impl"))

    def methodCase(member: ProxyableMember) = {
      val paramLists = paramListsOf(member.signature)
      val matchedArgs = reifyListPat(paramLists.map(paramList => reifyListPat(paramList.map(ps => pq"${ps.name.toTermName}"))))
      val methodArgs = paramLists.map(_.map(ps => q"$Upickle.default.readJs[${ps.typeSignature}](${ps.name.toTermName})"))
      cq"(${member.rpcName.toString}, $matchedArgs) => ${adjustResult(member, q"$implName.${member.method}(...$methodArgs)")}"
    }

    def adjustResult(member: ProxyableMember, result: Tree) = member.memberType match {
      case Procedure => result
      case Function =>
        val TypeRef(_, _, List(resultTpe)) = member.returnType
        q"$result.map($Upickle.default.writeJs[$resultTpe])($RunNowEC)"
      case Getter =>
        q"$AsRawRPCObj[${member.returnType}].asRaw($result)"
      case Invalid =>
        sys.error("Not a proxyable member")
    }

    def defaultCase = cq"_ => fail(${rpcTpe.toString}, methodName, args)"
    def methodMatch(methods: Iterable[ProxyableMember]) =
      Match(q"(methodName, args)", (methods.iterator.map(m => methodCase(m)) ++ Iterator(defaultCase)).toList)

    q"""
      new $AsRawRPCCls[$rpcTpe] {
        def asRaw($implName: $rpcTpe) =
          new $RawRPCCls {
            def fire(methodName: String, args: $ListCls[$ListCls[$Upickle.Js.Value]]) = ${methodMatch(procedures)}

            def call(methodName: String, args: $ListCls[$ListCls[$Upickle.Js.Value]]) = ${methodMatch(functions)}

            def get(methodName: String, args: $ListCls[$ListCls[$Upickle.Js.Value]]) = ${methodMatch(getters)}
          }
      }
     """
  }

  def asRealImpl[T: c.WeakTypeTag]: c.Tree = {
    val rpcTpe = weakTypeOf[T]
    val (procedures, functions, getters) = proxyableMethods(rpcTpe)
    val methods = procedures ++ functions ++ getters
    val rawRpcName = c.freshName(TermName("rawRpc"))

    val implementations = methods.map { m =>
      val methodName = m.method.name

      val rawRpcMethod = TermName(m.memberType match {
        case Procedure => "fire"
        case Function => "call"
        case Getter => "get"
        case Invalid => sys.error("Not a proxyable member")
      })

      val paramLists = m.paramLists
      val params = paramLists.map(_.map(ps =>
        ValDef(Modifiers(Flag.PARAM), ps.name.toTermName, TypeTree(ps.typeSignature), EmptyTree)))

      val args = reifyList(paramLists.map(paramList =>
        reifyList(paramList.map(ps => q"$Upickle.default.writeJs[${ps.typeSignature}](${ps.name.toTermName})"))))

      val body = q"$rawRpcName.$rawRpcMethod(${m.rpcName.toString}, $args)"
      val adjustedBody = m.memberType match {
        case Procedure => body
        case Function =>
          val TypeRef(_, _, List(resultTpe)) = m.returnType
          q"$body.map($Upickle.default.readJs[$resultTpe])($RunNowEC)"
        case Getter =>
          q"$AsRealRPCObj[${m.returnType}].asReal($body)"
        case Invalid =>
          sys.error("Not a proxyable member")
      }

      q"def $methodName(...$params) = $adjustedBody"
    }

    q"""
      new $AsRealRPCCls[$rpcTpe] {
        def asReal($rawRpcName: $RawRPCCls) = new $rpcTpe { ..$implementations; () }
      }
     """
  }
}

class RPCClientMacros(val c: blackbox.Context) {

  import c.universe._

  val ClientPackage = "io.udash.client"
  val ServerPackage = "io.udash.server"
  val ClientPackageSym = c.mirror.staticPackage(ClientPackage)
  val ClientPackageTree = q"$ClientPackageSym"
  val JSInitFuncCls = tq"$ClientPackageTree.JSInitFunc"
  val ConnectorWrapperTpe = c.mirror.staticPackage(ClientPackage).moduleClass.asType.toType
    .member(TypeName("ConnectorWrapper")).asType.toType

  def jsInitFuncMaterialize[T: c.WeakTypeTag]: c.Tree = {
    val tpe = weakTypeOf[T]
    val sym = symbolOf[T]

    def isInClientPackage(sym: Symbol): Boolean =
      sym == ClientPackageSym.moduleClass || (sym != NoSymbol && isInClientPackage(sym.owner))

    lazy val cwTpe = tpe.member(termNames.CONSTRUCTOR)
      .alternatives.map(_.asMethod.paramLists).collectFirst {
      case List(List(cwParam)) if cwParam.typeSignature <:< ConnectorWrapperTpe =>
        cwParam.typeSignature
    } getOrElse c.abort(c.enclosingPosition,
      s"$tpe must have a constructor that takes ConnectorWrapper or its subtype as its only argument")

    if (!isInClientPackage(sym)) {
      c.abort(c.enclosingPosition, s"Component implementation must be in io.vsjs.client package, $tpe is not.")
    } else {
      val functionName = (ServerPackage + sym.fullName.stripPrefix(ClientPackage)).replace('.', '_')

      q"new $JSInitFuncCls[$tpe]($functionName, new $tpe(_: $cwTpe))"
    }
  }
}
