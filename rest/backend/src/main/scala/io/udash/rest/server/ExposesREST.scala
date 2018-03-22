package io.udash.rest.server

import javax.servlet.http.HttpServletRequest

import io.udash.rest.{UdashRESTFramework, _}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

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
    val path: Array[String] = Option(req.getPathInfo).map(_.stripPrefix("/").split("/")).getOrElse(Array.empty[String])
    lazy val bodyContent = req.getReader.lines().toArray.mkString("\n")
    lazy val bodyValues = read[Map[String, framework.RawValue]](bodyContent)(bodyValuesReader)

    def findRestParamName(data: framework.ParamMetadata): String =
      data.annotations.collectFirst {
        case rpn: RESTParamName => rpn.restName
      }.getOrElse(data.name)

    def parseInvocations(path: Seq[String], metadata: RPCMetadata[_]): Unit = {
      if (path.isEmpty) throw ExposesREST.NotFound(req.getPathInfo)

      val methodName = path.head
      var nextParts = path.tail

      if (!metadata.signatures.contains(methodName))
        throw ExposesREST.NotFound(req.getPathInfo)

      val methodMetadata = metadata.signatures(methodName)
      var hasBodyArgs = false

      val args: List[List[RawValue]] = methodMetadata.paramMetadata.map { argsList =>
        argsList.map { arg =>
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
              bodyContent
            case Some(_: Query) | None => // Query is a default argument type
              val argName = findRestParamName(arg)
              val param = req.getParameter(argName)
              if (param == null) throw ExposesREST.MissingQueryArgument(argName)
              queryArgumentToRaw(param, arg.typeMetadata == framework.SimplifiedType.StringType)
          }
        }
      }

      invocations += RawInvocation(methodName, args)

      if (metadata.getterResults.contains(methodName))
        parseInvocations(nextParts, metadata.getterResults(methodName))
      else {
        val methodAnnotation = methodMetadata.annotations.find(_.isInstanceOf[RESTMethod])
          .getOrElse(if (hasBodyArgs) new POST else new GET)
        if (methodAnnotation.getClass != httpMethod)
          throw new ExposesREST.MethodNotAllowed()
      }
    }

    Future {
      parseInvocations(path, rpcMetadata)
      val result = invocations.result().reverse
      (rawLocalRpc.resolveGetterChain(result.tail), result.head)
    }.flatMap { case (receiver, invocation) =>
      Try(receiver.call(invocation.rpcName, invocation.argLists))
        .recover { case NonFatal(_) =>
          receiver.fire(invocation.rpcName, invocation.argLists)
          Future.successful("")
        }.get
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
