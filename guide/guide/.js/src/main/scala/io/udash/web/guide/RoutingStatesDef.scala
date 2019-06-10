package io.udash.web.guide

import io.udash._
import io.udash.web.commons.views.MarkdownPageState
import io.udash.web.guide.markdown.MarkdownPage

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState

  def url(implicit application: Application[RoutingState]): String =
    s"${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with ContainerState
sealed abstract class FinalRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState) with FinalState
sealed abstract class MarkdownState(final val page: MarkdownPage) extends FinalRoutingState(Some(ContentState)) with MarkdownPageState
object MarkdownState {
  def chapterFragment(chapterTitle: String): String =
    chapterTitle.replaceAll("[^A-Za-z0-9_ ]+", "").trim.replaceAll("\\s+", "-").toLowerCase
}

case object RootState extends ContainerRoutingState(None)

case object ContentState extends ContainerRoutingState(Some(RootState))

case object ErrorState extends FinalRoutingState(Some(RootState))

case object IntroState extends MarkdownState(MarkdownPage.Intro)

case object FaqState extends FinalRoutingState(Some(ContentState))

case object LicenseState extends MarkdownState(MarkdownPage.License)

/** Bootstrapping chapters */
case object BootstrappingState extends ContainerRoutingState(Some(ContentState))

case object BootstrappingIntroState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingSbtState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingRpcState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingBackendState extends FinalRoutingState(Some(BootstrappingState))

case object BootstrappingFrontendState extends FinalRoutingState(Some(BootstrappingState))

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
case object RestState extends MarkdownState(MarkdownPage.Rest)

/** Extensions **/
case object BootstrapExtState extends FinalRoutingState(Some(ContentState))

case object AuthorizationExtState extends FinalRoutingState(Some(ContentState))

case object ChartsExtState extends FinalRoutingState(Some(ContentState))

case object JQueryExtState extends FinalRoutingState(Some(ContentState))

case object I18NExtState extends FinalRoutingState(Some(ContentState))

case object UserActivityExtState extends FinalRoutingState(Some(ContentState))


