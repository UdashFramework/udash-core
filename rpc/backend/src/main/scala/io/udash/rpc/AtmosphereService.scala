package io.udash.rpc

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletResponse

import com.typesafe.scalalogging.LazyLogging
import io.udash.rpc.internals._
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT
import org.atmosphere.cpr.{AtmosphereConfig, AtmosphereResource, AtmosphereResourceEvent, AtmosphereServletProcessor}

import scala.concurrent.{ExecutionContext, Future}
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
  def filters: Seq[AtmosphereResource => Try[Any]]
}

/**
  * Integration between Atmosphere framework and Udash RPC system.
  *
  * @param config Configuration of AtmosphereService.
  * @tparam ServerRPCType Main server side RPC interface
  */
class AtmosphereService[ServerRPCType](config: AtmosphereServiceConfig[ServerRPCType])
                                      (implicit val executionContext: ExecutionContext)
  extends AtmosphereServletProcessor with LazyLogging {

  override def init(config: AtmosphereConfig): Unit =
    BroadcastManager.init(config.getBroadcasterFactory, config.metaBroadcaster())

  override def onRequest(resource: AtmosphereResource): Unit = {
    config.initRpc(resource)

    resource.transport match {
      case TRANSPORT.WEBSOCKET => onWebsocketRequest(resource)
      case TRANSPORT.SSE => onSSERequest(resource)
      case TRANSPORT.POLLING => onPollingRequest(resource)
      case _ =>
        logger.error(s"Transport ${resource.transport} is not supported!")
        resource.getResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
  }

  private def onWebsocketRequest(resource: AtmosphereResource) = {
    resource.suspend()
    val uuid: String = resource.uuid()
    BroadcastManager.registerResource(resource, uuid)

    try {
      val rpc = config.resolveRpc(resource)
      import rpc.framework._
      val input: String = readInput(resource.getRequest.getInputStream)
      val rpcRequest = readRequest(input, rpc)
      (rpcRequest, handleRpcRequest(rpc)(resource, rpcRequest)) match {
        case (call: RPCCall, Some(response)) =>
          response onComplete {
            case Success(r) =>
              BroadcastManager.sendToClient(uuid, rawToString(write[RPCResponse](RPCResponseSuccess(r, call.callId))))
            case Failure(ex) =>
              logger.error("RPC request handling failed", ex)
              val cause: String = if (ex.getCause != null) ex.getCause.getMessage else ex.getClass.getName
              BroadcastManager.sendToClient(uuid, rawToString(write[RPCResponse](RPCResponseFailure(cause, Option(ex.getMessage).getOrElse(""), call.callId))))
          }
        case (_, _) =>
      }
    } catch {
      case e: Exception =>
        logger.debug("Error occurred while handling websocket data.", e)
    }
  }

  private def onSSERequest(resource: AtmosphereResource) = {
    resource.suspend()
    BroadcastManager.registerResource(resource, resource.uuid())
  }

  private def onPollingRequest(resource: AtmosphereResource) = {
    try {
      resource.suspend()
      val rpc = config.resolveRpc(resource)
      import rpc.framework._
      val input: String = readInput(resource.getRequest.getInputStream)
      val rpcRequest = readRequest(input, rpc)
      (rpcRequest, handleRpcRequest(rpc)(resource, rpcRequest)) match {
        case (call: RPCCall, Some(response)) =>
          response onComplete {
            case Success(r) =>
              resource.getResponse.write(rawToString(write[RPCResponse](RPCResponseSuccess(r, call.callId))))
              resource.resume()
            case Failure(ex) =>
              val cause: String = if (ex.getCause != null) ex.getCause.getMessage else ""
              resource.getResponse.write(rawToString(write[RPCResponse](RPCResponseFailure(cause, Option(ex.getMessage).getOrElse(""), call.callId))))
              resource.resume()
          }
        case (_, _) =>
          resource.resume()
      }
    } catch {
      case e: Exception =>
        resource.getResponse.sendError(HttpServletResponse.SC_BAD_REQUEST)
        logger.debug("Error occurred while handling polling data.", e)
    }
  }

  override def onStateChange(event: AtmosphereResourceEvent): Unit = {
    val resource = event.getResource
    val response = resource.getResponse

    def writeMsg() = response.getWriter.write(event.getMessage.toString)

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

  private def handleRpcRequest(rpc: ExposesServerRPC[ServerRPCType])
                              (resource: AtmosphereResource,
                               request: rpc.framework.RPCRequest): Option[Future[rpc.framework.RawValue]] = {

    val filterResult = config.filters.foldLeft[Try[Any]](Success(()))((result, filter) => result match {
      case Success(_) => filter.apply(resource)
      case failure: Failure[_] => failure
    })

    import rpc.framework._
    filterResult match {
      case Success(_) =>
        request match {
          case call: RPCCall =>
            Some(rpc.handleRpcCall(call))
          case fire: RPCFire =>
            rpc.handleRpcFire(fire)
            None
        }
      case Failure(ex) => request match {
        case call: RPCCall =>
          Some(Future.failed(ex))
        case fire: RPCFire =>
          None
      }
    }
  }

  private def readRequest(input: String, rpc: ExposesServerRPC[ServerRPCType]): rpc.framework.RPCRequest = {
    import rpc.framework._
    val raw: RawValue = stringToRaw(input)
    read[RPCRequest](raw)
  }

  private def readInput(inputStream: ServletInputStream): String = {
    scala.io.Source.fromInputStream(inputStream).mkString
  }
}