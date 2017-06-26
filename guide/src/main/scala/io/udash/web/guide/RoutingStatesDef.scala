package io.udash.web.guide

import io.udash._

sealed abstract class RoutingState(val parentState: RoutingState) extends State {
  def url(implicit application: Application[RoutingState]): String = s"/#${application.matchState(this).value}"
}

case object RootState extends RoutingState(null)

case object ContentState extends RoutingState(RootState)

case object ErrorState extends RoutingState(RootState)

case object IntroState extends RoutingState(ContentState)

case object FAQState extends RoutingState(ContentState)

case object LicenseState extends RoutingState(ContentState)

/** Bootstrapping chapters */
case object BootstrappingState extends RoutingState(ContentState)

case object BootstrappingIntroState extends RoutingState(BootstrappingState)

case object BootstrappingSBTState extends RoutingState(BootstrappingState)

case object BootstrappingRpcState extends RoutingState(BootstrappingState)

case object BootstrappingBackendState extends RoutingState(BootstrappingState)

case object BootstrappingFrontendState extends RoutingState(BootstrappingState)

case object BootstrappingGeneratorsState extends RoutingState(ContentState)

/** Frontend chapters */
case object FrontendState extends RoutingState(ContentState)

case object FrontendIntroState extends RoutingState(FrontendState)

case class FrontendRoutingState(additionalArgument: Option[String]) extends RoutingState(FrontendState)

case object FrontendMVPState extends RoutingState(FrontendState)

case object FrontendTemplatesState extends RoutingState(FrontendState)

case object FrontendPropertiesState extends RoutingState(FrontendState)

case object FrontendBindingsState extends RoutingState(FrontendState)

case object FrontendFormsState extends RoutingState(FrontendState)

case object FrontendFilesState extends RoutingState(FrontendState)

/** RPC communication chapters */
case object RpcState extends RoutingState(ContentState)

case object RpcIntroState extends RoutingState(RpcState)

case object RpcInterfacesState extends RoutingState(RpcState)

case object RpcSerializationState extends RoutingState(RpcState)

case object RpcClientServerState extends RoutingState(RpcState)

case object RpcServerClientState extends RoutingState(RpcState)

/** REST communication chapters */
case object RestState extends RoutingState(ContentState)

case object RestIntroState extends RoutingState(RestState)

case object RestInterfacesState extends RoutingState(RestState)

case object RestClientServerState extends RoutingState(RestState)

case object RestServerState extends RoutingState(RestState)

/** Extensions **/
case object BootstrapExtState extends RoutingState(ContentState)

case object ChartsExtState extends RoutingState(ContentState)

case object JQueryExtState extends RoutingState(ContentState)

case object I18NExtState extends RoutingState(ContentState)

case object UserActivityExtState extends RoutingState(ContentState)


