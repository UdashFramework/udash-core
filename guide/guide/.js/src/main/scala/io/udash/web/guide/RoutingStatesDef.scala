package io.udash.web.guide

import com.avsystem.commons.serialization.HasGenCodec
import io.udash._
import io.udash.web.commons.views.MarkdownPageState
import io.udash.web.guide.markdown.MarkdownPage

sealed abstract class RoutingState(val parentState: Option[ContainerRoutingState]) extends State {
  override type HierarchyRoot = RoutingState

  def url(implicit application: Application[RoutingState]): String =
    s"${application.matchState(this).value}"
}
sealed abstract class ContainerRoutingState(parentState: Option[ContainerRoutingState]) extends RoutingState(parentState)
sealed abstract class MarkdownState(final val page: MarkdownPage) extends RoutingState(Some(ContentState)) with MarkdownPageState
object MarkdownState {
  def chapterFragment(chapterTitle: String): String =
    chapterTitle.replaceAll("[^A-Za-z0-9_ ]+", "").trim.replaceAll("\\s+", "-").toLowerCase
}

case object RootState extends ContainerRoutingState(None)

case object ContentState extends ContainerRoutingState(Some(RootState))

case object ErrorState extends RoutingState(Some(RootState))

case object IntroState extends MarkdownState(MarkdownPage.Intro)

case object FaqState extends RoutingState(Some(ContentState))

case object LicenseState extends MarkdownState(MarkdownPage.License)

/** Bootstrapping chapters */
case object BootstrappingState extends ContainerRoutingState(Some(ContentState))

case object BootstrappingIntroState extends RoutingState(Some(BootstrappingState))

case object BootstrappingSbtState extends RoutingState(Some(BootstrappingState))

case object AdvancedBootstrappingSbtState extends RoutingState(Some(BootstrappingState))

case object BootstrappingRpcState extends RoutingState(Some(BootstrappingState))

case object BootstrappingBackendState extends RoutingState(Some(BootstrappingState))

case object BootstrappingFrontendState extends RoutingState(Some(BootstrappingState))

/** Frontend chapters */
case object FrontendState extends ContainerRoutingState(Some(ContentState))

case object FrontendIntroState extends RoutingState(Some(FrontendState))

case class FrontendRoutingState(additionalArgument: Option[String]) extends RoutingState(Some(FrontendState))

case object FrontendMVPState extends RoutingState(Some(FrontendState))

case object FrontendTemplatesState extends RoutingState(Some(FrontendState))

case object FrontendPropertiesState extends RoutingState(Some(FrontendState))

case object FrontendBindingsState extends RoutingState(Some(FrontendState))

case object FrontendFormsState extends RoutingState(Some(FrontendState))

case object FrontendFilesState extends RoutingState(Some(FrontendState))

/** RPC communication chapters */
case object RpcState extends ContainerRoutingState(Some(ContentState))

case object RpcIntroState extends RoutingState(Some(RpcState))

case object RpcInterfacesState extends RoutingState(Some(RpcState))

case object RpcSerializationState extends RoutingState(Some(RpcState))

case object RpcClientServerState extends RoutingState(Some(RpcState))

case object RpcServerClientState extends RoutingState(Some(RpcState))

/** REST communication chapters */
case object RestState extends MarkdownState(MarkdownPage.Rest)

/** Extensions */
case object BootstrapExtState extends RoutingState(Some(ContentState))

case object AuthorizationExtState extends RoutingState(Some(ContentState))

case object JQueryExtState extends RoutingState(Some(ContentState))

case object I18NExtState extends RoutingState(Some(ContentState))

case object UserActivityExtState extends RoutingState(Some(ContentState))

case object BonanzaParentState extends ContainerRoutingState(Some(RootState))
case class PropertiesBonanzaState(currentInputContent: String, lastDistinctInput: String, numberOfValueChanges: Int) extends RoutingState(Some(BonanzaParentState))

object PropertiesBonanzaState extends HasGenCodec[PropertiesBonanzaState] {
  val Default: PropertiesBonanzaState = PropertiesBonanzaState("", "", 0)
}


