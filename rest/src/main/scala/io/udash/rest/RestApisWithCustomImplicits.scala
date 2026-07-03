package io.udash.rest

import com.avsystem.commons.meta.MacroInstances

/** TODO doc: why trait: allow client apps to create more custom companions */
trait AbstractRestApisWithCustomImplicits[Implicits] extends ApiDataWithCustomImplicits[Implicits] {

  /**
   * Companion for REST API traits that are used both on server and client.
   * This is typically used for inter-service APIs.
   */
  abstract class ApiCompanion[Real](implicit inst: MacroInstances[Implicits, OpenApiFullInstances[Real]])
    extends RestOpenApiCompanion[Implicits, Real](implicits)

  /**
   * Like [[ApiCompanion]] but without OpenAPI generation.
   */
  abstract class NoDocApiCompanion[Real](implicit inst: MacroInstances[Implicits, FullInstances[Real]])
    extends RestApiCompanion[Implicits, Real](implicits)

  /**
   * Companion for REST API traits that are used only on the server side.
   * This is typically used for APIs consumed by external systems (e.g. client specific).
   * Using [[ServerApiCompanion]] instead of [[ApiCompanion]] requires less macro generated implicits.
   * This makes compilation faster and makes it easier to write custom serialization for parameters and results.
   */
  abstract class ServerApiCompanion[Real](implicit inst: MacroInstances[Implicits, OpenApiServerInstances[Real]])
    extends RestServerOpenApiCompanion[Implicits, Real](implicits)

  /**
   * Companion for REST API traits used only as client facades for external APIs
   * (typically not written in Udash REST). These traits do not have any (human written) implementations and serve
   * only as convenient, typesafe proxies to external APIs.
   * Using [[ClientApiCompanion]] instead of [[ApiCompanion]] requires less macro generated implicits.
   * This makes compilation faster and makes it easier to write custom serialization for parameters and results.
   */
  abstract class ClientApiCompanion[Real](implicit inst: MacroInstances[Implicits, ClientInstances[Real]])
    extends RestClientApiCompanion[Implicits, Real](implicits)
}

/** TODO doc */
abstract class RestApisWithCustomImplicits[Implicits](override protected val implicits: Implicits)
  extends AbstractRestApisWithCustomImplicits[Implicits]
