package io.udash.rpc.utils

import com.typesafe.scalalogging.Logger
import io.udash.rpc.serialization.{DefaultExceptionCodecRegistry, ExceptionCodecRegistry}
import io.udash.rpc.{AtmosphereService, AtmosphereServiceConfig}
import javax.servlet.ServletConfig
import org.atmosphere.cpr.{ApplicationConfig, AtmosphereFramework}

/** AtmosphereFramework with default configuration for Udash. */
class DefaultAtmosphereFramework(
  config: AtmosphereServiceConfig[_],
  exceptionsRegistry: ExceptionCodecRegistry = new DefaultExceptionCodecRegistry,
  onRequestHandlingFailure: (Throwable, Logger) => Unit = (ex, logger) => logger.error("RPC request handling failed", ex)
) extends AtmosphereFramework {
  addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true")
  addInitParameter(ApplicationConfig.DEFAULT_CONTENT_TYPE, "application/json")
  addInitParameter(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "30")
  addInitParameter(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS, "30")
  addInitParameter(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "4")
  addInitParameter(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "4")
  addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true")
  addInitParameter(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY, "EMPTY_DESTROY")
  addInitParameter(ApplicationConfig.ANALYTICS, "false")

  override def init(sc: ServletConfig): AtmosphereFramework = {
    super.init(sc)
    addAtmosphereHandler("/*", new AtmosphereService(config, exceptionsRegistry, onRequestHandlingFailure = onRequestHandlingFailure))
  }
};
