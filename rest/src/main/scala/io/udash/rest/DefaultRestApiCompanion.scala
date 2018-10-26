package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances

/**
  * Base class for companions of REST API traits used only for REST clients to external services.
  * Injects `Future` as the wrapper for asynchronous responses and `GenCodec`/`GenKeyCodec` based serialization
  * for parameters and responses.
  */
abstract class DefaultRestClientApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, ClientInstances[Real]]
) extends RestClientApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)

/**
  * Base class for companions of REST API traits used only for REST servers exposed to external world.
  * Injects `Future` as the wrapper for asynchronous responses and `GenCodec`/`GenKeyCodec` based serialization
  * for parameters and responses.
  * Also, forces derivation of [[io.udash.rest.openapi.OpenApiMetadata OpenApiMetadata]].
  */
abstract class DefaultRestServerApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, OpenApiServerInstances[Real]]
) extends RestServerOpenApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)

/**
  * Base class for companions of REST API traits used for both REST clients and servers.
  * Injects `Future` as the wrapper for asynchronous responses and `GenCodec`/`GenKeyCodec` based serialization
  * for parameters and responses.
  * Also, forces derivation of [[io.udash.rest.openapi.OpenApiMetadata OpenApiMetadata]].
  */
abstract class DefaultRestApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, OpenApiFullInstances[Real]]
) extends RestOpenApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)
