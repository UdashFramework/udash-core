package io.udash.rest

import com.avsystem.commons.meta.MacroInstances

/**
 * Bundles all REST API-trait companion base classes ([[ApiCompanion]], [[NoDocApiCompanion]],
 * [[ServerApiCompanion]], [[ClientApiCompanion]]) pre-bound to a custom `Implicits` bundle. This is the
 * packaged, reusable form of the "convenience companion base class" pattern described in the Udash REST
 * guide (section *Plugging in entirely custom serialization*): instead of hand-writing one base class per
 * companion shape, you instantiate this once (see [[RestApisWithCustomImplicits]]) and get every shape as
 * an inner class, all sharing the same injected implicits.
 *
 * The `Implicits` type parameter (typically an object extending [[DefaultRestImplicits]] with extra
 * serialization/schema instances) is threaded into macro materialization, so custom serialization for your
 * own types is picked up automatically without explicit imports.
 *
 * Also provides the data-type companions from [[ApiDataWithCustomImplicits]] (for the ADTs used by these
 * APIs). Defined as a `trait` so applications may mix it into their own base and add further companions.
 *
 * @see [[DefaultRestApiCompanion]] and friends for the equivalents pre-bound to [[DefaultRestImplicits]].
 */
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

/**
 * Ready-to-use entry point for [[AbstractRestApisWithCustomImplicits]]. Extend it with an `object`,
 * fixing the implicits bundle, e.g.
 * {{{
 *   object MyImplicits extends DefaultRestImplicits
 *   object MyRestApis extends RestApisWithCustomImplicits[MyImplicits.type](MyImplicits)
 *
 *   trait MyApi { ... }
 *   object MyApi extends MyRestApis.ApiCompanion[MyApi]
 * }}}
 */
abstract class RestApisWithCustomImplicits[Implicits](override protected val implicits: Implicits)
  extends AbstractRestApisWithCustomImplicits[Implicits]
