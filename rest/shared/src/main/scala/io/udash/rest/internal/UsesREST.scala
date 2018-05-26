package io.udash.rest.internal

import com.avsystem.commons.rpc.MetadataAnnotation
import io.udash.rest._
import io.udash.rpc.serialization.{JsonStr, URLEncoder}

import scala.concurrent.{ExecutionContext, Future}

abstract class UsesREST[ServerRPCType: UdashRESTFramework#AsRealRPC : UdashRESTFramework#ValidREST]
(implicit val ec: ExecutionContext) {

  val framework: UdashRESTFramework

  import framework._

  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc: ServerRPCType = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

  /**
    * This allows for generation of proxy which translates RPC calls into raw calls that
    * can be sent through the network.
    */
  protected def remoteRpcAsReal: AsRealRPC[ServerRPCType]

  val rpcMetadata: RPCMetadata[ServerRPCType]

  protected val connector: RESTConnector

  /** Transform `RawValue` into String used in HTTP request header. */
  def rawToHeaderArgument(raw: RawValue): String
  /** Transform `RawValue` into String used as HTTP request query argument. */
  def rawToQueryArgument(raw: RawValue): String
  /** Transform `RawValue` into String used as URL part. */
  def rawToURLPart(raw: RawValue): String

  private def callRemote(getterChain: List[RawInvocation], invocation: RawInvocation, function: Boolean): Future[RawValue] = {
    val urlBuilder = Seq.newBuilder[String]
    val queryArgsBuilder = Map.newBuilder[String, String]
    val headersArgsBuilder = Map.newBuilder[String, String]
    val bodyArgsBuilder = Map.newBuilder[String, RawValue]
    var body: String = null

    def shouldSkipRestName(annotations: Seq[MetadataAnnotation]): Boolean =
      annotations.exists(_.isInstanceOf[SkipRESTName])

    def findRestName(annotations: Seq[MetadataAnnotation]): Option[String] =
      annotations.collectFirst {
        case a: RESTName => a.restName
      }

    def findRestParamName(data: framework.ParamMetadata[_]): String =
      data.annotations.collectFirst {
        case rpn: RESTParamName => rpn.restName
      }.getOrElse(data.name)

    def parseInvocation(inv: framework.RawInvocation, signatures: Map[String, Signature]): Unit = {
      val rpcMethodName: String = inv.rpcName
      val methodMetadata = signatures(rpcMethodName)

      if (!shouldSkipRestName(methodMetadata.annotations))
        urlBuilder += URLEncoder.encode(findRestName(methodMetadata.annotations).getOrElse(rpcMethodName))
      methodMetadata.paramMetadata.zip(inv.args).foreach { case (param, value) =>
        val paramName: String = findRestParamName(param)
        val argTypeAnnotations = param.annotations.collectFirst { case x: ArgumentType => x }
        argTypeAnnotations match {
          case Some(_: Header) =>
            headersArgsBuilder += (paramName -> rawToHeaderArgument(value))
          case Some(_: URLPart) =>
            urlBuilder += rawToURLPart(value)
          case Some(_: Body) =>
            body = value.json
          case Some(_: BodyValue) =>
            bodyArgsBuilder += (paramName -> value)
          case Some(_: Query) | None => // Query is a default argument type
            queryArgsBuilder += (paramName -> rawToQueryArgument(value))
        }
      }
    }

    def findRestMethod(inv: framework.RawInvocation, signatures: Map[String, Signature], hasBody: Boolean): RESTConnector.HTTPMethod = {
      val rpcMethodName: String = inv.rpcName
      val methodMetadata = signatures(rpcMethodName)
      val methodAnnotations = methodMetadata.annotations.collect({ case rm: RESTMethod => rm })
      if (methodAnnotations.lengthCompare(1) > 0) throw new RuntimeException(s"Too many method type annotations! ($methodAnnotations)")
      methodAnnotations.headOption match {
        case Some(_: GET) => RESTConnector.GET
        case Some(_: POST) => RESTConnector.POST
        case Some(_: PATCH) => RESTConnector.PATCH
        case Some(_: PUT) => RESTConnector.PUT
        case Some(_: DELETE) => RESTConnector.DELETE
        // default GET/POST method
        case None if hasBody => RESTConnector.POST
        case None => RESTConnector.GET
      }
    }

    var metadata: RPCMetadata[_] = rpcMetadata
    getterChain.reverse.foreach { inv =>
      parseInvocation(inv, metadata.getterSignatures)
      metadata = metadata.getterSignatures(inv.rpcName).resultMetadata.value
    }
    val signatures = if (function) metadata.functionSignatures else metadata.procedureSignatures
    parseInvocation(invocation, signatures)

    val bodyArgs = bodyArgsBuilder.result()
    if (body != null && bodyArgs.nonEmpty) throw new IllegalStateException("@Body and @BodyValue annotations used at the same time!")
    else if (body == null && bodyArgs.nonEmpty) {
      body = framework.write(bodyArgs)(framework.bodyValuesWriter).json
    }

    import com.avsystem.commons._
    connector.send(
      url = s"/${urlBuilder.result().mkString("/")}",
      method = findRestMethod(invocation, signatures, body != null),
      queryArguments = queryArgsBuilder.result(),
      headers = headersArgsBuilder.result(),
      body = body
    ).mapNow(JsonStr)
  }

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def call(rpcName: String)(args: List[RawValue]): Future[RawValue] =
      callRemote(getterChain, RawInvocation(rpcName, args), function = true)

    def fire(rpcName: String)(args: List[RawValue]): Unit =
      callRemote(getterChain, RawInvocation(rpcName, args), function = false)

    def get(rpcName: String)(args: List[RawValue]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, args) :: getterChain)
  }
}
