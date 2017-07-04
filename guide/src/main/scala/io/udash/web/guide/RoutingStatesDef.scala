package io.udash.web.guide

import io.udash._

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState
  def url(implicit application: Application[RoutingState]): String = s"/#${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with ContainerState
sealed abstract class FinalRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with FinalState

case object RootState extends ContainerRoutingState(None)

case object ContentState extends ContainerRoutingState(Some(RootState))

case object ErrorState extends FinalRoutingState(Some(RootState))

case object IntroState extends FinalRoutingState(Some(ContentState))

case object FAQState extends FinalRoutingState(Some(ContentState))

case object LicenseState extends FinalRoutingState(Some(ContentState))

/** Bootstrapping chapters */
case object BootstrappingState extends ContainerRoutingState(Some(ContentState))

case object BootstrappingIntroState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingSBTState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingRpcState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingBackendState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingFrontendState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingGeneratorsState extends FinalRoutingState(Some(ContentState))

/** Frontend chapters */
case object FrontendState extends ContainerRoutingState(Some(ContentState))

case object FrontendIntroState extends FinalRoutingState(Some(FrontendState))

case class FrontendRoutingState(additionalArgument: Option[String]) extends FinalRoutingState(Some(FrontendState))

case object FrontendMVPState extends FinalRoutingState(Some(FrontendState))

case object FrontendTemplatesState extends FinalRoutingState(Some(FrontendState))

case object FrontendPropertiesState extends FinalRoutingState(Some(FrontendState))

case object FrontendBindingsState extends FinalRoutingState(Some(FrontendState))

case object FrontendFormsState extends FinalRoutingState(Some(FrontendState))

case object FrontendFilesState extends FinalRoutingState(Some(FrontendState))

/** RPC communication chapters */
case object RpcState extends ContainerRoutingState(Some(ContentState))

case object RpcIntroState extends FinalRoutingState(Some(RpcState))

case object RpcInterfacesState extends FinalRoutingState(Some(RpcState))

case object RpcSerializationState extends FinalRoutingState(Some(RpcState))

case object RpcClientServerState extends FinalRoutingState(Some(RpcState))

case object RpcServerClientState extends FinalRoutingState(Some(RpcState))

/** REST communication chapters */
case object RestState extends ContainerRoutingState(Some(ContentState))

case object RestIntroState extends FinalRoutingState(Some(RestState))

case object RestInterfacesState extends FinalRoutingState(Some(RestState))

case object RestClientServerState extends FinalRoutingState(Some(RestState))

case object RestServerState extends FinalRoutingState(Some(RestState))

/** Extensions **/
case object BootstrapExtState extends FinalRoutingState(Some(ContentState))

case object ChartsExtState extends FinalRoutingState(Some(ContentState))

case object JQueryExtState extends FinalRoutingState(Some(ContentState))

case object I18NExtState extends FinalRoutingState(Some(ContentState))

case object UserActivityExtState extends FinalRoutingState(Some(ContentState))


