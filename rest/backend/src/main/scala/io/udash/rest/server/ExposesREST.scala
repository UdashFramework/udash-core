package io.udash.rest.server

import com.avsystem.commons.concurrent.RunNowEC
import com.avsystem.commons.misc.Opt
import io.udash.rest.{UdashRESTFramework, _}
import io.udash.rpc.serialization.JsonStr
import javax.servlet.http.HttpServletRequest

import scala.concurrent.{ExecutionContext, Future}

/**
  * Base trait for anything that exposes REST interface.
  */
abstract class ExposesREST[ServerRPCType: UdashRESTFramework#ValidServerREST](localRest: ServerRPCType) {
  val framework: UdashRESTFramework

  import framework._

  /**
    * This allows the RPC implementation to be wrapped in raw RPC which will translate raw calls coming from network
    * into calls on actual RPC implementation.
    */
  protected def localRpcAsRaw: AsRawRPC[ServerRPCType]

  protected lazy val rawLocalRpc: framework.RawRPC = localRpcAsRaw.asRaw(localRest)

  protected val rpcMetadata: RPCMetadata[ServerRPCType]

  /** Transform `String` from HTTP request header into `RawValue`. */
  def headerArgumentToRaw(raw: String, isStringArg: Boolean): RawValue
  /** Transform `String` from HTTP request query argument into `RawValue`. */
  def queryArgumentToRaw(raw: String, isStringArg: Boolean): RawValue
  /** Transform `String` from URL part into `RawValue`. */
  def urlPartToRaw(raw: String, isStringArg: Boolean): RawValue

  def handleRestCall(req: HttpServletRequest, httpMethod: Class[_ <: RESTMethod])(implicit ec: ExecutionContext): Future[String] = {
    val invocations = List.newBuilder[RawInvocation]
    val path: Array[String] =
      Opt(req.getRequestURI)
        .map(_.stripPrefix(req.getServletPath).stripPrefix("/").split("/"))
        .getOrElse(Array.empty[String])
    lazy val bodyContent = req.getReader.lines().toArray.mkString("\n")
    lazy val bodyValues = read[Map[String, framework.RawValue]](JsonStr(bodyContent))(bodyValuesReader)

    def findRestParamName(data: framework.ParamMetadata[_]): String =
      data.annotations.collectFirst {
        case rpn: RESTParamName => rpn.restName
      }.getOrElse(data.name)

    def parseInvocations(path: Seq[String], metadata: RPCMetadata[_]): RPCMetadata[_] = {
      if (path.isEmpty) throw ExposesREST.NotFound(req.getPathInfo)

      val allSignatures: PartialFunction[String, Signature] =
        metadata.getterSignatures orElse metadata.functionSignatures orElse metadata.procedureSignatures

      val methodName = path.head
      var nextParts = path.tail

      if (!allSignatures.isDefinedAt(methodName))
        throw ExposesREST.NotFound(req.getPathInfo)

      val methodMetadata = allSignatures(methodName)
      var hasBodyArgs = false

      val args: List[RawValue] = methodMetadata.paramMetadata.map { arg =>
        val argTypeAnnotation = arg.annotations.collectFirst {
          case at: ArgumentType => at
        }
        argTypeAnnotation match {
          case Some(_: Header) =>
            val argName = findRestParamName(arg)
            val headerValue = req.getHeader(argName)
            if (headerValue == null) throw ExposesREST.MissingHeader(argName)
            headerArgumentToRaw(headerValue, arg.typeMetadata == framework.SimplifiedType.StringType)
          case Some(_: URLPart) =>
            if (nextParts.isEmpty) throw ExposesREST.MissingURLPart(arg.name)
            val v = nextParts.head
            nextParts = nextParts.tail
            urlPartToRaw(v, arg.typeMetadata == framework.SimplifiedType.StringType)
          case Some(_: BodyValue) =>
            val argName = findRestParamName(arg)
            if (!bodyValues.contains(argName)) throw ExposesREST.MissingBodyValue(argName)
            hasBodyArgs = true
            bodyValues(argName)
          case Some(_: Body) =>
            if (bodyContent.isEmpty) throw ExposesREST.MissingBody(arg.name)
            hasBodyArgs = true
            JsonStr(bodyContent)
          case Some(_: Query) | None => // Query is a default argument type
            val argName = findRestParamName(arg)
            val param = req.getParameter(argName)
            if (param == null) throw ExposesREST.MissingQueryArgument(argName)
            queryArgumentToRaw(param, arg.typeMetadata == framework.SimplifiedType.StringType)
        }
      }

      invocations += RawInvocation(methodName, args)

      if (metadata.getterSignatures.contains(methodName))
        parseInvocations(nextParts, metadata.getterSignatures(methodName).resultMetadata.value)
      else {
        val methodAnnotation = methodMetadata.annotations.find(_.isInstanceOf[RESTMethod])
          .getOrElse(if (hasBodyArgs) new POST else new GET)
        if (methodAnnotation.getClass != httpMethod)
          throw new ExposesREST.MethodNotAllowed()
        metadata
      }
    }

    Future {
      val metadata = parseInvocations(path, rpcMetadata)
      val result = invocations.result().reverse
      val invocation = result.head
      val receiver = rawLocalRpc.resolveGetterChain(result.tail)
      val function = metadata.functionSignatures.contains(invocation.rpcName)
      (receiver, function, result.head)
    }.flatMap { case (receiver, function, invocation) =>
      if (function)
        receiver.call(invocation.rpcName)(invocation.args).map(_.json)(RunNowEC)
      else {
        receiver.fire(invocation.rpcName)(invocation.args)
        Future.successful("")
      }
    }
  }
}

object ExposesREST {
  case class NotFound(path: String) extends RuntimeException(s"Resource `$path` not found.")
  class MethodNotAllowed extends RuntimeException("Method not allowed.")
  case class Unauthorized(msg: String) extends RuntimeException(msg)

  abstract class BadRequestException(msg: String) extends RuntimeException(msg)
  case class MissingHeader(name: String) extends BadRequestException(s"Header `$name` not found.")
  case class MissingQueryArgument(name: String) extends BadRequestException(s"Query argument `$name` not found.")
  case class MissingURLPart(name: String) extends BadRequestException(s"URL argument `$name` not found.")
  case class MissingBody(name: String) extends BadRequestException(s"Body argument `$name` not found.")
  case class MissingBodyValue(name: String) extends BadRequestException(s"Body argument value `$name` not found.")
}
