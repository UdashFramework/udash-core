package io.udash.web.guide.views.bootstrapping

import com.avsystem.commons._
import io.udash._
import io.udash.css.CssView
import io.udash.rest.SttpRestClient
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.Versions
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

import scala.concurrent.Future

case object BootstrappingSbtViewFactory extends StaticViewFactory[BootstrappingSbtState.type](() => new BootstrappingSbtView)

final class BootstrappingSbtView extends FinalView with CssView {

  import Context._
  import JsDom.all._
  import io.udash.web.guide.views.References._

  private val g8buildContent = Property.blank[String]
  locally {
    import com.softwaremill.sttp._
    implicit val backend: SttpBackend[Future, Nothing] = SttpRestClient.defaultBackend()
    sttp.get(uri"${ExternalUrls.udashG8Build}").send().foreachNow(_.body.foreach(g8buildContent.set(_)))
  }

  override def getTemplate: Modifier =
    div(
      h2("SBT configuration"),
      p(
        a(href := SbtHomepage)("SBT"),
        " is the recommended build tool for ScalaJS. The excellent ",
        a(href := SbtScalaJsPluginHomepage)("Scala.js SBT plugin"),
        " provides tools for configuring cross-compiled modules and JS dependencies management. If you want to use it, " +
          "add the following line into the ", i("project/plugins.sbt"), " file: "
      ),
      CodeBlock(
        s"""addSbtPlugin("org.scala-js" % "sbt-scalajs" % "${Versions.scalaJSPluginVersion}")""".stripMargin
      )(GuideStyles),
      p(
        "We recommend testing your code compiled to JavaScript in a browser. The ",
        a(href := ScalaJsEnvSeleniumHomepage)("scalajs-env-selenium"), " plugin enables you to do that. ",
        "Put the following line on top of the ", i("project/plugins.sbt"), " file. "
      ),
      CodeBlock(
        s"""libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "${Versions.scalaJSSeleniumPluginVersion}" """.stripMargin
      )(GuideStyles),
      h3("Advanced project setup"),
      p(
        "You can see the generated ", a(href := ExternalUrls.udashG8Build)(i("build.sbt"), " template"), " below."
      ),
      CodeBlock.reactive(g8buildContent, "Loading...")(GuideStyles),
      p(
        "You don't have to use ", a(href := JettyHomepage)("Jetty"), " as webserver nor to use ",
        a(href := BootstrapHomepage)("Twitter bootstrap"), " for frontend components. Anyway it is recommended to use ",
        a(href := JettyHomepage)("Jetty"), " for an easy start, since you can embed it in your code and deploy anywhere easily."
      ),
      h3("SBT commands"),
      p("You project configuration is ready. Take a look at sbt commands which you will use:"),
      ul(GuideStyles.defaultList)(
        li("compile - compiles all your Scala sources (it does not produce JS files)."),
        li("copyAssets - copies all your assets into target directory."),
        li("compileStatics - produces whole frontend application with full JS optimization (includes copyAssets)."),
        li("compileAndOptimizeStatics  - as above but with full JS optimization."),
        li("run  - starts backend server."),
      ),
      p("Notice that you can prefix commands with ~ to automatically rerun them on sources change."),
      h2("What's next?"),
      p(
        "SBT configuration is ready, now it is time to prepare ", a(href := BootstrappingRpcState.url)("RPC interfaces"),
        " in the ", b("shared"), " module."
      )
    )
}