package io.udash.rest
package typescript

import com.avsystem.commons._
import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._
import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.rest.raw.PlainValue

import scala.annotation.implicitNotFound

/**
 * Metadata class that captures all information about a REST API necessary to generate a TypeScript based client.
 * NOTE: this metadata does not thoroughly validate the REST API, assuming that another macro has already done
 * that (e.g. the one related to [[io.udash.rest.raw.RawRest RawRest]]).
 */
@implicitNotFound("TsRestApiMetadata for ${T} not found, " +
  "is it a valid REST API trait with properly defined companion object?")
@methodTag[RestMethodTag]
@methodTag[BodyTypeTag]
final case class TsRestApiMetadata[T](
  @reifyName name: String,
  @infer moduleTag: TsModuleTag[T],

  @multi @rpcMethodMetadata
  @tagged[Prefix](whenUntagged = new Prefix)
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Path)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  prefixes: List[TsPrefixMethod[_]],

  @multi @rpcMethodMetadata
  @tagged[GET]
  @tagged[NoBody](whenUntagged = new NoBody)
  @paramTag[RestParamTag](defaultTag = new Query)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  gets: List[TsHttpGetMethod[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[CustomBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  customBodyMethods: List[TsHttpCustomBodyMethod[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[FormBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  formBodyMethods: List[TsHttpFormBodyMethod[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[JsonBody](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  jsonBodyMethods: List[TsHttpJsonBodyMethod[_]],
) extends TypedMetadata[T] with TsDefinition {
  val module: TsModule =
    moduleTag.module

  val methods: List[TsRestMethod[_]] =
    (List.empty[TsRestMethod[_]] ++ prefixes ++ gets ++ customBodyMethods ++ jsonBodyMethods ++ formBodyMethods)
      .sortBy(_.info.pos.index)

  def contents(gen: TsGeneratorCtx): String = {
    val methodDecls = methods.iterator.map(_.declaration(gen)).mkString("\n")
    s"""export class $name {
       |    constructor(
       |        private _handle: ${gen.raw}.HandleRequest,
       |        private _prefixParams?: ${gen.raw}.RestParameters
       |    ) {}
       |
       |$methodDecls
       |}
       |""".stripMargin
  }
}
object TsRestApiMetadata extends RpcMetadataCompanion[TsRestApiMetadata] {
  final val CookieParamsNotAllowed = "Cannot send cookie parameters from browser based TypeScript client"
}

sealed abstract class TsRestMethod[T] extends TypedMetadata[T] {
  def info: TsMethodInfo[RestMethodTag]
  def bodyParams: List[TsRestParameter[Body, TsType, _]]
  def declaration(gen: TsGeneratorCtx): String

  val path: List[String] = PlainValue.decodePath(info.methodTag.path).map(_.value)

  lazy val params: List[TsRestParameter[RestParamTag, TsType, _]] =
    (info.pathParams ++ info.queryParams ++ info.headerParams ++ bodyParams).sortBy(_.pos.index)

  protected def quote(str: String): String = JsonStringOutput.write(str)

  protected def mkPair(gen: TsGeneratorCtx, p: TsRestParameter[_, TsPlainType, _]): String =
    s"[${quote(p.rawName)}, ${p.tsType.mkOptionalPlainWrite(gen, p.name, p.optional)}]"

  protected def restParamsDefn(gen: TsGeneratorCtx): String = {
    val pathValues =
      (path.iterator.map(quote) ++ info.pathParams.iterator.flatMap { p =>
        Iterator(p.tsType.mkOptionalPlainWrite(gen, p.name, p.optional)) ++
          PlainValue.decodePath(p.paramTag.pathSuffix).iterator.map(pv => quote(pv.value))
      }).mkString("[", ", ", "]")

    val queryValues = info.queryParams.iterator.map(mkPair(gen, _))
      .mkString("[", ", ", "]")

    val headerValues = info.headerParams.iterator.map(mkPair(gen, _))
      .mkString("[", ", ", "]")

    s"""const _params = ${gen.raw}.newParameters(
       |            this._prefixParams,
       |            $pathValues, $queryValues, $headerValues
       |        )""".stripMargin
  }
}

final case class TsMethodInfo[+Tag <: RestMethodTag](
  @reifyName name: String,
  @reifyPosition pos: MethodPosition,
  @reifyAnnot methodTag: Tag,
  @multi @rpcParamMetadata @tagged[Path] pathParams: List[TsRestParameter[Path, TsPlainType, _]],
  @multi @rpcParamMetadata @tagged[Query] queryParams: List[TsRestParameter[Query, TsPlainType, _]],
  @multi @rpcParamMetadata @tagged[Header] headerParams: List[TsRestParameter[Header, TsPlainType, _]],
)

final case class TsPrefixMethod[T](
  @composite info: TsMethodInfo[Prefix],
  @infer @checked result: TsRestApiMetadata.Lazy[T],
  // Note: no cookies! can't send them from browser based client
) extends TsRestMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = Nil

  def declaration(gen: TsGeneratorCtx): String = {
    val returnType = result.value.resolve(gen)
    val paramDecls = params.iterator.map(_.declaration(gen)).mkString("(", ", ", ")")

    s"""    ${info.name}$paramDecls: $returnType {
       |        ${restParamsDefn(gen)}
       |        return new $returnType(this._handle, _params)
       |    }
       |""".stripMargin
  }
}

sealed abstract class TsHttpMethod[T] extends TsRestMethod[T] {
  def info: TsMethodInfo[HttpMethodTag]
  def result: TsResultTypeTag[T]

  protected def bodyDecl(gen: TsGeneratorCtx): String

  def declaration(gen: TsGeneratorCtx): String = {
    val paramDecls = params.iterator.map(_.declaration(gen)).mkString("(", ", ", ")")
    val returnType = result.tsType.resolve(gen)
    val methodStr = quote(info.methodTag.method.name)

    s"""    ${info.name}$paramDecls: $returnType {
       |        ${restParamsDefn(gen)}
       |        ${bodyDecl(gen)}
       |        const _result = this._handle({method: $methodStr, parameters: _params, body: _body})
       |        return ${result.tsType.mkFromPromise(gen, "_result")}
       |    }
       |""".stripMargin
  }
}

final case class TsHttpGetMethod[T](
  @composite info: TsMethodInfo[GET],
  @infer @checked result: TsResultTypeTag[T],
) extends TsHttpMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = Nil

  protected def bodyDecl(gen: TsGeneratorCtx): String =
    "const _body = null"
}

final case class TsHttpJsonBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsJsonType, _]],
) extends TsHttpMethod[T] {
  protected def bodyDecl(gen: TsGeneratorCtx): String = {
    val bodyObj = bodyParams.iterator
      .map(p => s"${quote(p.rawName)}: ${p.tsType.mkOptionalJsonWrite(gen, p.name, p.optional)}")
      .mkString("{", ", ", "}")
    s"const _body = ${gen.codecs}.jsonToBody($bodyObj)"
  }
}

final case class TsHttpFormBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsPlainType, _]],
) extends TsHttpMethod[T] {
  protected def bodyDecl(gen: TsGeneratorCtx): String = {
    val formFields = bodyParams.iterator.map(mkPair(gen, _)).mkString(", ")
    s"const _body = ${gen.codecs}.formToBody($formFields)"
  }
}

final case class TsHttpCustomBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @encoded @rpcParamMetadata @tagged[Body] rawBodyParam: TsRestParameter[Body, TsBodyType, _],
) extends TsHttpMethod[T] {
  // body param in @CustomBody method can't be TS-optional
  val bodyParam: TsRestParameter[Body, TsBodyType, _] =
    rawBodyParam.copy(tsOptional = Opt.Empty)

  def bodyParams: List[TsRestParameter[Body, TsType, _]] = List(bodyParam)

  protected def bodyDecl(gen: TsGeneratorCtx): String =
    s"const _body = ${bodyParam.tsType.mkBodyWrite(gen, bodyParam.name)}"
}

final case class TsRestParameter[+Tag <: RestParamTag, +TsT <: TsType, T](
  @reifyName name: String,
  @reifyName(useRawName = true) rawName: String,
  @reifyPosition pos: ParamPosition,
  @reifyAnnot paramTag: Tag,
  @optional @reifyAnnot tsOptional: Opt[tsOptional[T]],
  @infer typeTag: TsTypeTag[TsT, T]
) extends TypedMetadata[T] {
  def optional: Boolean = tsOptional.isDefined

  def tsType: TsT =
    tsOptional.fold(typeTag.tsType)(to => typeTag.optionalParamType(to.fallbackValue))

  def declaration(gen: TsGeneratorCtx): String = {
    val qmark = if (optional) "?" else ""
    s"$name$qmark: ${tsType.resolve(gen)}"
  }
}
