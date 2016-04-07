package io.udash.rpc.utils

import io.udash.rpc.{AtmosphereService, AtmosphereServiceConfig}
import org.atmosphere.cpr.{ApplicationConfig, AtmosphereFramework}

/** AtmosphereFramework with default configuration for Udash. */
class DefaultAtmosphereFramework(config: AtmosphereServiceConfig[_]) extends AtmosphereFramework {
  addAtmosphereHandler("/*", new AtmosphereService(config))
  addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true")
  addInitParameter(ApplicationConfig.DEFAULT_CONTENT_TYPE, "application/json")
  addInitParameter(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "0")
  addInitParameter(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS, "60")
  addInitParameter(ApplicationConfig.OUT_OF_ORDER_BROADCAST, "true")
  addInitParameter(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "10")
  addInitParameter(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "10")
  addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "10")
}
