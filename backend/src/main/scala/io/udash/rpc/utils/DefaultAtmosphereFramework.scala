package io.udash.rpc.utils

import io.udash.rpc.{AtmosphereService, AtmosphereServiceConfig, RPC}
import org.atmosphere.cpr.{ApplicationConfig, AtmosphereFramework}

/** AtmosphereFramework with default configuration for Udash. */
class DefaultAtmosphereFramework(config: AtmosphereServiceConfig[_ <: RPC]) extends AtmosphereFramework {
  addAtmosphereHandler("/*", new AtmosphereService(config))
  addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true")
  addInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true")
  addInitParameter(ApplicationConfig.DEFAULT_CONTENT_TYPE, "application/json")
  addInitParameter(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "30")
  addInitParameter(ApplicationConfig.CLIENT_HEARTBEAT_INTERVAL_IN_SECONDS, "30")
  addInitParameter(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "4")
  addInitParameter(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "4")
  addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true")
  addInitParameter(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY, "IDLE_DESTROY")
  addInitParameter(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY_IDLETIME, "60")
};