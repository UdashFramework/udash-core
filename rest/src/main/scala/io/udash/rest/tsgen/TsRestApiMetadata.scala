package io.udash.rest
package tsgen

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
  @tagged[JsonBody](whenUntagged = new JsonBody)
  @paramTag[RestParamTag](defaultTag = new Body)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  jsonBodyMethods: List[TsHttpJsonBodyMethod[_]],

  @multi @rpcMethodMetadata
  @tagged[BodyMethodTag](whenUntagged = new POST)
  @tagged[FormBody]
  @paramTag[RestParamTag](defaultTag = new Body)
  @paramTag[RestParamTag](defaultTag = new Body)
  @unmatchedParam[Cookie](TsRestApiMetadata.CookieParamsNotAllowed)
  formBodyMethods: List[TsHttpFormBodyMethod[_]]
) extends TypedMetadata[T] with TsDefinition {
  val methods: List[TsRestMethod[_]] =
    (List.empty[TsRestMethod[_]] ++ prefixes ++ gets ++ customBodyMethods ++ jsonBodyMethods ++ formBodyMethods)
      .sortBy(_.info.pos.index)

  def definition(ctx: TsGenerationCtx): String = {
    val methodDecls = methods.iterator.map(_.declaration(ctx)).mkString("\n")
    s"""export class $name {
       |    constructor(
       |        private _handle: ${ctx.rawModule}.HandleRequest,
       |        private _prefixParams?: ${ctx.rawModule}.RestParameters
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
  def declaration(ctx: TsGenerationCtx): String

  val path: List[String] = PlainValue.decodePath(info.methodTag.path).map(_.value)

  val params: List[TsRestParameter[RestParamTag, TsType, _]] =
    (info.pathParams ++ info.queryParams ++ info.headerParams ++ bodyParams).sortBy(_.pos.index)

  protected def quote(str: String): String = JsonStringOutput.write(str)

  protected def mkWritePlain(ctx: TsGenerationCtx, codecRef: Opt[TsReference], value: String): String =
    codecRef.fold(value)(c => s"${c.resolve(ctx)}.writePlain($value)")

  protected def mkPair(ctx: TsGenerationCtx, p: TsRestParameter[_, TsPlainType, _]): String = {
    s"[${quote(p.rawName)}, ${mkWritePlain(ctx, p.typeTag.tsType.plainCodecRef, p.name)}]"
  }

  protected def restParamsDefn(ctx: TsGenerationCtx): String = {
    val pathValues =
      (path.iterator.map(quote) ++ info.pathParams.iterator.flatMap { p =>
        Iterator(mkWritePlain(ctx, p.typeTag.tsType.plainCodecRef, p.name)) ++
          PlainValue.decodePath(p.paramTag.pathSuffix).iterator.map(pv => quote(pv.value))
      }).mkStringOrEmpty("\n        _params.path.push(", ", ", ")")

    val queryValues = info.queryParams.iterator.map(mkPair(ctx, _))
      .mkStringOrEmpty("\n        _params.query.push(", ", ", ")")

    val headerValues = info.headerParams.iterator.map(mkPair(ctx, _))
      .mkStringOrEmpty("\n        _params.header.push(", ", ", ")")

    s"const _params = ${ctx.rawModule}.newParameters(this._prefixParams)$pathValues$queryValues$headerValues"
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

  def declaration(ctx: TsGenerationCtx): String = {
    val returnType = result.value.resolve(ctx)
    val paramDecls = params.iterator.map(_.declaration(ctx)).mkString("(", ", ", ")")

    s"""    ${info.name}$paramDecls: $returnType {
       |        ${restParamsDefn(ctx)}
       |        return new $returnType(this._handle, _params)
       |    }
       |""".stripMargin
  }
}

sealed abstract class TsHttpMethod[T] extends TsRestMethod[T] {
  def info: TsMethodInfo[HttpMethodTag]
  def result: TsResultTypeTag[T]

  protected def bodyDecl(ctx: TsGenerationCtx): String

  def declaration(ctx: TsGenerationCtx): String = {
    val paramDecls = params.iterator.map(_.declaration(ctx)).mkString("(", ", ", ")")
    val returnType = result.tsType.resolve(ctx)
    val methodStr = quote(info.methodTag.method.name)

    val readerRef = result.tsType.responseReaderRef
      .fold(s"${ctx.codecsModule}.responseFromBody<$returnType>()")(_.resolve(ctx))

    s"""    ${info.name}$paramDecls: Promise<$returnType> {
       |        ${restParamsDefn(ctx)}
       |        ${bodyDecl(ctx)}
       |        return this._handle({method: $methodStr, parameters: _params, body: _body})
       |            .then($readerRef.readResponse)
       |    }
       |""".stripMargin
  }
}

final case class TsHttpGetMethod[T](
  @composite info: TsMethodInfo[GET],
  @infer @checked result: TsResultTypeTag[T],
) extends TsHttpMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = Nil

  protected def bodyDecl(ctx: TsGenerationCtx): String =
    "const _body = null"
}

final case class TsHttpJsonBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsJsonType, _]],
) extends TsHttpMethod[T] {
  protected def bodyDecl(ctx: TsGenerationCtx): String = {
    val bodyObj = bodyParams.iterator.map { p =>
      val encoded = p.typeTag.tsType.jsonCodecRef.fold(p.name)(cr => s"${cr.resolve(ctx)}.writeJson(${p.name})")
      s"${quote(p.rawName)}: $encoded"
    }.mkString("{", ", ", "}")
    s"const _body = ${ctx.rawModule}.mkJsonBody($bodyObj)"
  }
}

final case class TsHttpFormBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @multi @rpcParamMetadata @tagged[Body] bodyParams: List[TsRestParameter[Body, TsPlainType, _]],
) extends TsHttpMethod[T] {
  protected def bodyDecl(ctx: TsGenerationCtx): String = {
    val formFields = bodyParams.iterator.map(mkPair(ctx, _)).mkString(", ")
    s"const _body = ${ctx.rawModule}.mkFormBody($formFields)"
  }
}

final case class TsHttpCustomBodyMethod[T](
  @composite info: TsMethodInfo[BodyMethodTag],
  @infer @checked result: TsResultTypeTag[T],
  @encoded @rpcParamMetadata @tagged[Body] bodyParam: TsRestParameter[Body, TsBodyType, _],
) extends TsHttpMethod[T] {
  def bodyParams: List[TsRestParameter[Body, TsType, _]] = List(bodyParam)

  protected def bodyDecl(ctx: TsGenerationCtx): String = {
    val tsType = bodyParam.typeTag.tsType
    val codecRef = tsType.bodyCodecRef
      .fold(s"${ctx.codecsModule}.bodyFromJson<${tsType.resolve(ctx)}>()")(_.resolve(ctx))
    s"const _body = $codecRef.writeBody(${bodyParam.name})"
  }
}

//TODO: default values and stuff
final case class TsRestParameter[+Tag <: RestParamTag, +TsT <: TsType, T](
  @reifyName name: String,
  @reifyName(useRawName = true) rawName: String,
  @reifyPosition pos: ParamPosition,
  @reifyAnnot paramTag: Tag,
  @infer typeTag: TsTypeTag[TsT, T]
) extends TypedMetadata[T] {
  def declaration(ctx: TsGenerationCtx): String =
    s"$name: ${typeTag.tsType.resolve(ctx)}"
}
