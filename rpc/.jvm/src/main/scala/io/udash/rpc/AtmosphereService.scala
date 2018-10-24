package io.udash.rpc

import java.util.UUID

import com.avsystem.commons._
import com.avsystem.commons.serialization.json.{JsonStringInput, JsonStringOutput}
import com.typesafe.scalalogging.{Logger, StrictLogging}
import io.udash.rpc.internals._
import io.udash.rpc.serialization.ExceptionCodecRegistry
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletResponse
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT
import org.atmosphere.cpr._

import scala.concurrent.Future
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.{Failure, Success, Try}

trait AtmosphereServiceConfig[ServerRPCType] {
  /**
    * Called after AtmosphereResource gets closed.
    *
    * @param resource Closed resource
    */
  def onClose(resource: AtmosphereResource): Unit

  /** Should return RPC for provided resource. */
  def resolveRpc(resource: AtmosphereResource): ExposesServerRPC[ServerRPCType]

  /**
    * Initialize RPC for resource.
    * This is called on every request from resource!
    */
  def initRpc(resource: AtmosphereResource): Unit

  /** @return If a filter returns failure, RPC method is not called. */
  def filters: ISeq[AtmosphereResource => Try[Unit]]
}

/**
  * Integration between Atmosphere framework and Udash RPC system.
  *
  * @param config Configuration of AtmosphereService.
  * @tparam ServerRPCType Main server side RPC interface
  */
class AtmosphereService[ServerRPCType](
  config: AtmosphereServiceConfig[ServerRPCType],
  exceptionsRegistry: ExceptionCodecRegistry,
  sseSuspendTime: FiniteDuration = 1 minute,
  onRequestHandlingFailure: (Throwable, Logger) => Unit = (ex, logger) => logger.error("RPC request handling failed", ex)
) extends AtmosphereServletProcessor with StrictLogging {

  private var brodcasterFactory: BroadcasterFactory = _

  override def init(config: AtmosphereConfig): Unit = {
    brodcasterFactory = config.getBroadcasterFactory
    BroadcastManager.init(brodcasterFactory, config.metaBroadcaster())
  }

  override def onRequest(resource: AtmosphereResource): Unit = {
    resource.transport match {
      case TRANSPORT.WEBSOCKET =>
        config.initRpc(resource)
        onWebsocketRequest(resource)
      case TRANSPORT.SSE =>
        config.initRpc(resource)
        onSSERequest(resource)
      case TRANSPORT.POLLING =>
        onPollingRequest(resource)
      case _ =>
        logger.error(s"Transport ${resource.transport} is not supported!")
        resource.getResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
  }

  private def onWebsocketRequest(resource: AtmosphereResource): Unit = {
    resource.suspend()
    val uuid: String = resource.uuid()
    BroadcastManager.registerResource(resource, uuid)

    try {
      handleRequest(resource,
        data => BroadcastManager.sendToClient(uuid, data.json),
        () => ()
      )
    } catch {
      case e: Exception =>
        logger.error("Error occurred while handling websocket data.", e)
    }
  }

  private def onSSERequest(resource: AtmosphereResource): Unit = {
    resource.suspend(sseSuspendTime.toMillis)
    BroadcastManager.registerResource(resource, resource.uuid())
  }

  private def onPollingRequest(resource: AtmosphereResource): Unit = {
    try {
      resource.setBroadcaster(brodcasterFactory.lookup(s"polling-tmp-${resource.uuid()}-${UUID.randomUUID()}", true))
      resource.suspend()
      handleRequest(resource,
        data => {
          resource.getResponse.write(data.json)
          resource.resume()
        },
        () => resource.resume()
      )
    } catch {
      case e: Exception =>
        resource.getResponse.sendError(HttpServletResponse.SC_BAD_REQUEST)
        logger.error("Error occurred while handling polling data.", e)
    }
  }

  private def handleRequest(resource: AtmosphereResource, onCall: JsonStr => Unit, onFire: () => Unit): Unit = {
    val rpc = config.resolveRpc(resource)

    implicit val ecr: ExceptionCodecRegistry = exceptionsRegistry

    val input = readInput(resource.getRequest.getInputStream)
    if (input.json.nonEmpty) {
      val rpcRequest = readRequest(input, rpc)
      (rpcRequest, handleRpcRequest(rpc)(resource, rpcRequest)) match {
        case (call: RpcCall, Some(response)) =>
          response.onCompleteNow {
            case Success(r) =>
              onCall(JsonStr(JsonStringOutput.write[RpcServerMessage](RpcResponseSuccess(r, call.callId))))
            case Failure(ex) =>
              onRequestHandlingFailure(ex, logger)
              val exceptionName = exceptionsRegistry.name(ex)
              onCall(JsonStr(JsonStringOutput.write[RpcServerMessage](
                if (exceptionsRegistry.contains(exceptionName)) {
                  RpcResponseException(exceptionName, ex, call.callId)
                } else {
                  val cause: String = if (ex.getCause != null) ex.getCause.getMessage else exceptionName
                  RpcResponseFailure(cause, Option(ex.getMessage).getOrElse(""), call.callId)
                }
              )))
          }
        case _ => onFire()
      }
    }
  }

  override def onStateChange(event: AtmosphereResourceEvent): Unit = {
    val resource = event.getResource
    val response = resource.getResponse

    def writeMsg(): Unit = response.getWriter.write(event.getMessage.toString)

    if (event.isCancelled || event.isClosedByApplication || event.isClosedByClient) {
      config.onClose(event.getResource)
    } else if (event.getMessage != null) {
      resource.transport() match {
        case TRANSPORT.LONG_POLLING | TRANSPORT.POLLING | TRANSPORT.JSONP =>
          writeMsg()
          resource.resume()
        case TRANSPORT.WEBSOCKET | TRANSPORT.STREAMING | TRANSPORT.SSE =>
          writeMsg()
          response.getWriter.flush()
        case unknownTransport =>
          logger.error(s"Unknown transport type: $unknownTransport")
      }
    }
  }

  override def destroy(): Unit = {}

  private def handleRpcRequest(rpc: ExposesServerRPC[ServerRPCType])(
    resource: AtmosphereResource, request: RpcRequest
  ): Option[Future[JsonStr]] = {

    val filterResult = config.filters.foldLeft[Try[Unit]](Success(()))((result, filter) => result match {
      case Success(_) => filter.apply(resource)
      case failure: Failure[_] => failure
    })
    filterResult match {
      case Success(_) =>
        request match {
          case call: RpcCall =>
            Some(rpc.handleRpcCall(call))
          case fire: RpcFire =>
            rpc.handleRpcFire(fire)
            None
        }
      case Failure(ex) => request match {
        case _: RpcCall =>
          Some(Future.failed(ex))
        case _: RpcFire =>
          None
      }
    }
  }

  private def readRequest(input: JsonStr, rpc: ExposesServerRPC[ServerRPCType]): RpcRequest =
    JsonStringInput.read[RpcRequest](input.json)

  private def readInput(inputStream: ServletInputStream): JsonStr =
    JsonStr(scala.io.Source.fromInputStream(inputStream).mkString)
}
