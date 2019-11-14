package io.udash
package web.guide.views.bootstrapping

import com.avsystem.commons._
import io.udash.css.CssView
import io.udash.rest.SttpRestClient
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

import scala.concurrent.Future

case object AdvancedBootstrappingSbtViewFactory extends StaticViewFactory[AdvancedBootstrappingSbtState.type](() => new AdvancedBootstrappingSbtView)

final class AdvancedBootstrappingSbtView extends View {

  import Context._
  import JsDom.all._
  import io.udash.web.guide.views.References._

  private val g8buildContent = Property.blank[String]
  private val g8PluginsContent = Property.blank[String]
  private val g8IndexContent = Property.blank[String]

  locally {
    import com.softwaremill.sttp._
    implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()
    sttp.get(uri"${ExternalUrls.udashG8Build}").send().foreachNow(_.body.foreach(g8buildContent.set(_)))
    sttp.get(uri"${ExternalUrls.udashG8Plugins}").send().foreachNow(_.body.foreach(g8PluginsContent.set(_)))
    sttp.get(uri"${ExternalUrls.udashG8Index}").send().foreachNow(_.body.foreach(g8IndexContent.set(_)))
  }

  override def getTemplate: Modifier =
    div(
      h2("Advanced project setup"),
      p(
        "You can see the content of the build templates below. They're also available on ", a(href := UdashG8Repo, target := "_blank")("udash.g8"), " repository."
      ),
      h3("build.sbt"),
      CodeBlock.reactive(g8buildContent, "Loading...")(GuideStyles),
      h3("plugins.sbt"),
      CodeBlock.reactive(g8PluginsContent, "Loading...")(GuideStyles),
      h3("index.html"),
      CodeBlock.reactive(g8IndexContent, "Loading...", "html")(GuideStyles),
      p(
        "You don't have to use ", a(href := JettyHomepage, target := "_blank")("Jetty"), " as webserver nor to use ",
        a(href := BootstrapHomepage, target := "_blank")("Twitter bootstrap"), " for frontend components. Anyway it is recommended to use ",
        "Jetty for an easy start, since you can embed it in your code and deploy anywhere easily."
      ),
      h2("What's next?"),
      p(
        "sbt configuration is ready, now it is time to prepare ", a(href := BootstrappingRpcState.url)("RPC interfaces"),
        " in the ", b("shared"), " module."
      )
    )
}