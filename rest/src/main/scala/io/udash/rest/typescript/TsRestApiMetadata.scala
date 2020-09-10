package io.udash.rest
package typescript

import com.avsystem.commons.meta._
import com.avsystem.commons.rpc._
import com.avsystem.commons.serialization.json.JsonStringOutput
import com.avsystem.commons.serialization.optionalParam
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
  @paramTag[RestParamTag]
  @unmatchedParam[RestParamTag](TsRestApiMetadata.PrefixParamsNotAllowed)
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
  jsonBodyMethods: List[TsHttpJsonBodyMethod[_]]
) extends TypedMetadata[T] with TsApiType { apiMetadata =>
  val module: TsModule =
    moduleTag.module

  val methods: List[TsHttpMethod[_]] =
    (List.empty[TsHttpMethod[_]] ++ gets ++ customBodyMethods ++ jsonBodyMethods ++ formBodyMethods)
      .sortBy(_.info.pos.index)

  def resolve(gen: TsGeneratorCtx): String = name

  def definition(pathPrefix: Vector[String]): TsDefinition = new TsDefinition {
    def module: TsModule = moduleTag.module
    def name: String = apiMetadata.name
    def contents(gen: TsGeneratorCtx): String = {
      val methodDecls = methods.iterator.map(_.declaration(gen, apiMetadata)).mkString("\n")
      s"""export class $name {
         |    private static readonly basePath = ${pathPrefix.iterator.map(quote).mkString("[", ", ", "]")}
         |
         |    constructor(
         |        private _handle: ${gen.raw}.HandleRequest,
         |    ) {}
         |
         |$methodDecls
         |}
         |""".stripMargin
    }
  }

  def subApis: List[(Seq[String], TsApiType)] = prefixes.map { prefix =>
    (prefix.path, prefix.typeTag.tsType)
  }
}
object TsRestApiMetadata extends RpcMetadataCompanion[TsRestApiMetadata] {
  final val PrefixParamsNotAllowed = "In APIs that support TypeScript client generation, prefix method cannot take parameters"
  final val CookieParamsNotAllowed = "Cannot send cookie parameters from browser based TypeScript client"
}

sealed abstract class TsRestMethod[T] extends TypedMetadata[T] {
  def methodTag: RestMethodTag

  val path: List[String] = PlainValue.decodePath(methodTag.path).map(_.value)
}

final case class TsMethodInfo[+Tag <: RestMethodTag](
  @reifyName name: String,
  @reifyPosition pos: MethodPosition,
  @reifyAnnot methodTag: Tag,
  @multi @rpcParamMetadata @tagged[Path] pathParams: List[TsRestParameter[Path, TsPlainType, _]],
  @multi @rpcParamMetadata @tagged[Query] queryParams: List[TsRestParameter[Query, TsPlainType, _]],
  @multi @rpcParamMetadata @tagged[Header] headerParams: List[TsRestParameter[Header, TsPlainType, _]]
)

final case class TsPrefixMethod[T](
  @reifyAnnot methodTag: Prefix,
  @infer @checked typeTag: TsApiTypeTag[T]
  // Note: no cookies! can't send them from browser based client
) extends TsRestMethod[T]

sealed abstract class TsHttpMethod[T] extends TsRestMethod[T] {
  def info: TsMethodInfo[HttpMethodTag]
  def result: TsResultTypeTag[T]
  def bodyParams: List[TsRestParameter[Body, TsType, _]]

  def methodTag: HttpMethodTag = info.methodTag

  protected def bodyDecl(gen: TsGeneratorCtx): String

  lazy val params: List[TsRestParameter[RestParamTag, TsType, _]] =
    (info.pathParams ++ info.queryParams ++ info.headerParams ++ bodyParams).sortBy(_.pos.index)

  protected def declareParams(gen: TsGeneratorCtx): String =
    params.foldRight((List.empty[String], true)) { case (param, (acc, optionalAllowed)) =>
      (param.declaration(gen, optionalAllowed) :: acc, param.optional && optionalAllowed)
    }._1.mkString("(", ", ", ")")

  protected def quote(str: String): String = JsonStringOutput.write(str)

  protected def mkPair(gen: TsGeneratorCtx, p: TsRestParameter[_, TsPlainType, _]): String =
    s"[${quote(p.rawName)}, ${p.tsType.mkOptionalPlainWrite(gen, p.name, p.optional)}]"

  protected def restParamsDefn(gen: TsGeneratorCtx, owner: TsRestApiMetadata[_]): String = {
    val pathValues =
      (path.iterator.map(quote) ++ info.pathParams.iterator.flatMap { p =>
        Iterator(p.tsType.mkOptionalPlainWrite(gen, p.name, p.optional)) ++
          PlainValue.decodePath(p.paramTag.pathSuffix).iterator.map(pv => quote(pv.value))
      }).mkString(s"[...${owner.resolve(gen)}.basePath, ", ", ", "]")

    val queryValues = info.queryParams.iterator.map(mkPair(gen, _))
      .mkString("[", ", ", "]")

    val headerValues = info.headerParams.iterator.map(mkPair(gen, _))
      .mkString("[", ", ", "]")

    s"""const _params: ${gen.raw}.RestParameters = {
       |            path: $pathValues,
       |            query: $queryValues,
       |            header: $headerValues
       |        }""".stripMargin
  }

  def declaration(gen: TsGeneratorCtx, owner: TsRestApiMetadata[_]): String = {
    val methodStr = quote(info.methodTag.method.name)
    s"""    ${info.name}${declareParams(gen)}: ${result.tsType.resolve(gen)} {
       |        ${restParamsDefn(gen, owner)}
       |        ${bodyDecl(gen)}
       |        const _result = this._handle({method: $methodStr, parameters: _params, body: _body})
       |        return ${result.tsType.mkFromPromise(gen, "_result")}
       |    }
       |""".stripMargin
  }
}

final case class TsHttpGetMethod[T](
  @composite info: TsMethodInfo[GET],
  @infer @checked result: TsResultTypeTag[T]
) extends TsHttpMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = Nil

  protected def bodyDecl(gen: TsGeneratorCtx): String =
    "const _body = null"
}

final case class TsHttpJsonBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsJsonType, _]]
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
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsPlainType, _]]
) extends TsHttpMethod[T] {
  protected def bodyDecl(gen: TsGeneratorCtx): String = {
    val formFields = bodyParams.iterator.map(mkPair(gen, _)).mkString(", ")
    s"const _body = ${gen.codecs}.formToBody($formFields)"
  }
}

final case class TsHttpCustomBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @encoded @rpcParamMetadata @tagged[Body] bodyParam: TsRestParameter[Body, TsBodyType, _]
) extends TsHttpMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = List(bodyParam)

  protected def bodyDecl(gen: TsGeneratorCtx): String =
    s"const _body = ${bodyParam.tsType.mkBodyWrite(gen, bodyParam.name)}"
}

final case class TsRestParameter[+Tag <: RestParamTag, +TsT <: TsType, T](
  @reifyName name: String,
  @reifyName(useRawName = true) rawName: String,
  @reifyPosition pos: ParamPosition,
  @reifyAnnot paramTag: Tag,
  @isAnnotated[optionalParam] optional: Boolean,
  @infer typeTag: TsTypeTag[TsT, T]
) extends TypedMetadata[T] {
  def tsType: TsT = typeTag.tsType

  def declaration(gen: TsGeneratorCtx, optionalAllowed: Boolean): String = {
    val qmark = if (optional && optionalAllowed) "?" else ""
    val orUndefined = if (optional && !optionalAllowed) " | undefined" else ""
    s"$name$qmark: ${tsType.resolve(gen)}$orUndefined"
  }
}
