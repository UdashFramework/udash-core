package io.udash
package rest

import com.avsystem.commons.meta.MacroInstances

/**
  * Base class for companions of REST API traits used only for REST clients to external services.
  * Injects `GenCodec` and `GenKeyCodec` based serialization.
  */
abstract class DefaultRestClientApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, ClientInstances[Real]]
) extends RestClientApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)

/**
  * Base class for companions of REST API traits used only for REST servers exposed to external world.
  * Injects `GenCodec` and `GenKeyCodec` based serialization and forces derivation of
  * [[io.udash.rest.openapi.OpenApiMetadata OpenApiMetadata]].
  */
abstract class DefaultRestServerApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, OpenApiServerInstances[Real]]
) extends RestServerOpenApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)

/**
  * Base class for companions of REST API traits used for both REST clients and servers.
  * Injects `GenCodec` and `GenKeyCodec` based serialization and forces derivation of
  * [[io.udash.rest.openapi.OpenApiMetadata OpenApiMetadata]].
  */
abstract class DefaultRestApiCompanion[Real](implicit
  inst: MacroInstances[DefaultRestImplicits, OpenApiFullInstances[Real]]
) extends RestOpenApiCompanion[DefaultRestImplicits, Real](DefaultRestImplicits)
