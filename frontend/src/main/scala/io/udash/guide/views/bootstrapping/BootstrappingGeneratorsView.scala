package io.udash.guide.views.bootstrapping

import io.udash.core.{DefaultViewPresenterFactory, View}
import io.udash.guide.styles.partials.GuideStyles
import io.udash.guide.views.References
import io.udash.guide.{Context, _}
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._

case object BootstrappingGeneratorsViewPresenter extends DefaultViewPresenterFactory[BootstrappingGeneratorsState.type](() => new BootstrappingGeneratorsView)

class BootstrappingGeneratorsView extends View {
  import Context._

  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h1("Udash project generator"),
    p(
      "If you want to start developing with Udash as soon as possible, you may find the Udash project generator very helpful. ",
      "Basing on a few configuration settings, it will generate a complete, runnable Udash project ready to work with."
    ),
    h2("Command line interface"),
    p(
      "To use the project generator in the command line, download it from ",
      a(href := References.udashGeneratorDownload)("here"), ". Then use ", i("run.sh"), " or ", i("run.bat"), " script to start it."
    ),
    p(
      "Now you will have to configure a few settings: ",
      ul(GuideStyles.defaultList)(
        li(b("Project root directory"), " - a directory where project files will be placed."),
        li(b("Clear root directory"), " - if true, the generator will remove all files and directories from the selected project root directory."),
        li(b("Project name"), " - a name of your project."),
        li(b("Organization"), " - a domain of your organization like: ", i("io.udash"), " or ", i("com.avsystem"), "."),
        li(b("Root package"), " - a root package of project source files like: ", i("io.udash.project"), " or ", i("com.avsystem.project.name"), "."),
        li(b("Project type"), " - you can select a frontend-only project without modules or a standard Udash project with three modules: \"backend\", \"shared\", \"frontend\"."),
        li(b("Module names"), " - if you selected the default Udash project, then you should select names of the modules."),
        li(
          span(b("Create basic frontend application"), " - decide if you want to generate a base of the frontend application."),
          ul(GuideStyles.innerList)(
            li(b("Create frontend demo views"), " - decide if you want to generate frontend demo views."),
            li(b("Create ScalaCSS demo views"), " - decide if you want to add ScalaCSS to your project and generate demo views.")
          )
        ),
        li(
          span(b("Create Jetty launcher"), " - decide if you want to use a Jetty server for serving the frontend files."),
          ul(GuideStyles.innerList)(
            li(b("Create RPC communication layer"), " - decide if you want to generate the RPC base for your project."),
            li(b("Create RPC communication layer demos"), " - decide if you want to generate RPC demo views.")
          )
        ),
        li(b("Start generation"), " - decide if you want to start project generation based on the above configuration.")
      )
    ),
    h2("Project structure"),
    p(
      "The basic Udash project contains three modules: ",
      strong("shared"), ", ", strong("backend"), " and ", strong("frontend"), "."
    ),
    p(
      "Let's go through the frontend module files first:",
      ul(GuideStyles.defaultList)(
        li(
          b("init.scala"), " - consists of the main function which starts whole application and the ", i("Context"),
          " object which contains useful vals like ", i("serverRpc"), " (for communication with server) and implicit execution context."
        ),
        li(b("states.scala"), " - contains application states definition."),
        li(b("RoutingRegistryDef.scala"), " - contains the application routing definition."),
        li(b("StatesToViewPresenterDef.scala"), " - contains mapping from the application state to ViewPresenter."),
        li(b("rpc/RPCService.scala"), " - contains implementation of the main client RPC interface."),
        li(b("styles/DemoStyles.scala"), " - contains example ScalaCSS styles."),
        li(b("views/*.scala"), " - contains application views.")
      )
    ),
    p(
      "The ", i("shared"), " module contains files releated to the RPC system:",
      ul(GuideStyles.defaultList)(
        li(b("MainServerRPC.scala"), " - contains the main server RPC interface implemented in the ", i("backend"), " module."),
        li(b("MainClientRPC.scala"), " - contains the main client RPC interface implemented in the ", i("frontend"), " module.")
      )
    ),
    p(
      "The ", i("backend"), " module contains:",
      ul(GuideStyles.defaultList)(
        li(b("jetty/ApplicationServer.scala"), " - Jetty based server serving static files and handling RPC connections."),
        li(b("Launcher.scala"), " - creates and starts the application server."),
        li(b("rpc/ExposedRpcInterfaces.scala"), " - implementation of the main server RPC interface."),
        li(b("rpc/ClientRPC.scala"), " - helper object for communication with the client application.")
      )
    ),
    h2("Project compilation and running"),
    p(
      "In case of the frontend-only project, you can use the ", i("sbt compile"), " command to compile sources of your web application. ",
      "When compilation is finished, you can find generated files in the ", i("target/UdashStatic"), " directory."
    ),
    p(
      "If you decided to create the standard project with the Jetty launcher, you can use the ", i("sbt run"), " command to ",
      "compile sources of your web application and start the Jetty server. ",
      "When the server is started, you can see your web application on ",
      a(href := "http://127.0.0.1:8080/")("http://127.0.0.1:8080/"), "."
    ),
    p("While developing, you can use the ", i("~compile"), " task, which will automatically recompile changed source files."),
    h2("What's next?"),
    p(
      "If you want to prepare a custom project, you might be interested in ",
      a(href := BootstrappingSBTState.url)("SBT configuration"), ". "
    ),
    p(
      "You can also learn more about ",
      a(href := FrontendIntroState.url)("Frontend application development"),
      " or ",
      a(href := RpcIntroState.url)("RPC in Udash"), "."
    )
  ).render

  override def renderChild(view: View): Unit = ()
}