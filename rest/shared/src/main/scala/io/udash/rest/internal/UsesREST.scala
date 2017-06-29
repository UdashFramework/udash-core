package io.udash.rest.internal

import com.avsystem.commons.rpc.MetadataAnnotation
import io.udash.rest._
import io.udash.rpc.serialization.URLEncoder

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

abstract class UsesREST[ServerRPCType : UdashRESTFramework#AsRealRPC : UdashRESTFramework#ValidREST]
                       (implicit val ec: ExecutionContext) {

  val framework: UdashRESTFramework

  import framework._

  /**
    * Proxy for remote RPC implementation. Use this to perform RPC calls.
    */
  lazy val remoteRpc = remoteRpcAsReal.asReal(new RawRemoteRPC(Nil))

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

  private val pendingCalls = mutable.Map.empty[String, Promise[RawValue]]
  private var cid: Int = 0

  private def newCallId() = {
    cid += 1
    cid.toString
  }

  private def callRemote(callId: String, getterChain: List[RawInvocation], invocation: RawInvocation): Unit = {
    val urlBuilder = Seq.newBuilder[String]
    val queryArgsBuilder = Map.newBuilder[String, String]
    val headersArgsBuilder = Map.newBuilder[String, String]
    val bodyArgsBuilder = Map.newBuilder[String, RawValue]
    var body: String = null

    def shouldSkipRestName(annotations: Seq[MetadataAnnotation]): Boolean =
      annotations.exists(_.isInstanceOf[SkipRESTName])

    def findRestName(annotations: Seq[MetadataAnnotation]): Option[String] =
      annotations.find(_.isInstanceOf[RESTName]).map(_.asInstanceOf[RESTName].restName)

    def findRestParamName(annotations: Seq[MetadataAnnotation]): Option[String] =
      annotations.find(_.isInstanceOf[RESTParamName]).map(_.asInstanceOf[RESTParamName].restName)

    def parseInvocation(inv: framework.RawInvocation, metadata: RPCMetadata[_]): Unit = {
      val rpcMethodName: String = inv.rpcName
      val methodMetadata = metadata.signatures(rpcMethodName)

      if (!shouldSkipRestName(methodMetadata.annotations))
        urlBuilder += findRestName(methodMetadata.annotations).getOrElse(rpcMethodName)
      methodMetadata.paramMetadata.zip(inv.argLists).foreach { case (params, values) =>
        params.zip(values).foreach { case (param, value) =>
          val paramName: String = findRestParamName(param.annotations).getOrElse(param.name)
          val argTypeAnnotations = param.annotations.filter(_.isInstanceOf[ArgumentType])
          if (argTypeAnnotations.size > 1) throw new RuntimeException(s"Too many parameter type annotations! ($argTypeAnnotations)")
          argTypeAnnotations.headOption match {
            case Some(_: Header) =>
              headersArgsBuilder.+=((paramName, rawToHeaderArgument(value)))
            case Some(_: URLPart) =>
              urlBuilder += rawToURLPart(value)
            case Some(_: Body) =>
              body = rawToString(value)
            case Some(_: BodyValue) =>
              bodyArgsBuilder.+=((paramName, value))
            case _ => // Query is a default argument type
              queryArgsBuilder.+=((paramName, rawToQueryArgument(value)))
          }
        }
      }
    }

    def findRestMethod(inv: framework.RawInvocation, metadata: RPCMetadata[_]): RESTConnector.HTTPMethod = {
      val rpcMethodName: String = inv.rpcName
      val methodMetadata = metadata.signatures(rpcMethodName)
      val methodAnnotations = methodMetadata.annotations.filter(_.isInstanceOf[RESTMethod])
      if (methodAnnotations.size > 1) throw new RuntimeException(s"Too many method type annotations! ($methodAnnotations)")
      methodAnnotations.headOption match {
        case Some(_: GET) => RESTConnector.GET
        case Some(_: POST) => RESTConnector.POST
        case Some(_: PATCH) => RESTConnector.PATCH
        case Some(_: PUT) => RESTConnector.PUT
        case Some(_: DELETE) => RESTConnector.DELETE
        case _ => throw new RuntimeException(s"Missing method type annotations! ($methodAnnotations)")
      }
    }

    var metadata: RPCMetadata[_] = rpcMetadata
    getterChain.reverse.foreach { inv =>
      parseInvocation(inv, metadata)
      metadata = metadata.getterResults(inv.rpcName)
    }
    parseInvocation(invocation, metadata)

    val bodyArgs = bodyArgsBuilder.result()
    if (body != null && bodyArgs.nonEmpty) throw new IllegalStateException("@Body and @BodyValue annotations used at the same time!")
    else if (body == null && bodyArgs.nonEmpty) {
      body = rawToString(framework.write(bodyArgs)(framework.bodyValuesWriter))
    }

    connector.send(
      url = s"/${urlBuilder.result().map(URLEncoder.encode).mkString("/")}",
      method = findRestMethod(invocation, metadata),
      queryArguments = queryArgsBuilder.result(),
      headers = headersArgsBuilder.result(),
      body = body
    ) onComplete {
      case Success(text) => pendingCalls.remove(callId).foreach(p => p.success(stringToRaw(text)))
      case Failure(ex) => pendingCalls.remove(callId).foreach(p => p.failure(ex))
    }
  }

  protected class RawRemoteRPC(getterChain: List[RawInvocation]) extends RawRPC {
    def call(rpcName: String, argLists: List[List[RawValue]]): Future[RawValue] = {
      val callId = newCallId()
      val promise = Promise[RawValue]()
      pendingCalls.put(callId, promise)
      callRemote(callId, getterChain, RawInvocation(rpcName, argLists))
      promise.future
    }

    def get(rpcName: String, argLists: List[List[RawValue]]): RawRPC =
      new RawRemoteRPC(RawInvocation(rpcName, argLists) :: getterChain)
  }
}
