package io.udash.auth

import io.udash.{Application, GState, RoutingRegistry, UrlChangeProvider, ViewFactoryRegistry}
import io.udash.properties.ImmutableValue
import io.udash.routing.WindowUrlChangeProvider

import scala.reflect.ClassTag

object AuthApplication {
  implicit class ApplicationAuthExt[HierarchyRoot <: GState[HierarchyRoot]](val application: Application[HierarchyRoot]) extends AnyVal {
    /**
      * Adds the default listener of authorization failure in routing (redirects to provided state).
      *
      * @param authFailedRedirectState application will redirect user to this state after auth fail
      */
    def withDefaultRoutingFailureListener(authFailedRedirectState: HierarchyRoot): Application[HierarchyRoot] = {
      application.onRoutingFailure {
        case _: UnauthorizedException | _: UnauthenticatedException if application.currentState != authFailedRedirectState =>
          application.goTo(authFailedRedirectState)
      }
      application
    }
  }
}
