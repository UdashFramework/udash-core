package io.udash.guide.views

import io.udash._
import io.udash.guide._
import io.udash.guide.styles.partials.GuideStyles
import io.udash.guide.Context._

import org.scalajs.dom.Element

import scalacss.ScalatagsCss._
import scalatags.JsDom.all._

object IntroViewPresenter extends DefaultViewPresenterFactory[RootState.type](() => new IntroView)

class IntroView extends View {
  private val content = div(
    h1("Udash Developer's Guide"),
    p(
      "This guide provides knowledge essential for implementing web applications with the Udash framework. ",
      "The quick start guide below will introduce you to Udash basics."
    ),
    h2("Quick start guide"),
    (new ImageFactory("assets/images/quick"))("generator.png", "Generator example", GuideStyles.imgRight, GuideStyles.imgMedium),
    p(
      "A good starting point is a generation of a project base with the Udash project generator. You can download it from ",
      a(href := References.udashGeneratorDownload)("here"), " ",
      "The generator provides a command line interface which will collect some information about the project and ",
      "prepare the project base for you. "
    ),
    p(
      "Follow the below steps:",
      ol(GuideStyles.stepsList)(
        li("Download the generator zip package and unzip it."),
        li("Run it using the ", i("run.sh"), " or ", i("run.bat"), " script."),
        li("Provide required data and start the project generation."),
        li("Switch to a project directory in the command line. "),
        li("Open the SBT interpreter using the ", i("sbt"), " command."),
        li(
          "Compile the project and run the Jetty server, if you selected the standard project version ",
          "and asked the generator to create the Jetty launcher."
        ),
        li("If you selected only the frontend project, you can find static files in ", i("target/UdashStatic."))
      )
    ),
    p(
      "You can read more about the project generator in the ",
      a(href := BootstrappingGeneratorsState.url)("Udash Generator"), " chapter."
    ),
    h3("Frontend"),
    p(
      "While working on the frontend application you can run the ", i("~compile"), " command in SBT to make it recompile on ",
      "every source change. "
    ),
    p(
      "If you want to add a new view, then you need to: ",
      ol(GuideStyles.stepsList)(
        li("Add a new state in ", b("states.scala"), ""),
        li("Add route pointing to this state in ", b("RoutingRegistryDef.scala"), ""),
        li("Create ", a(href := FrontendMVPState.url)("Model, View, Presenter and  ViewPresenter"), " in the ", b("views/YourView.scala"), ""),
        li("Add mapping from the new state to your ViewPresenter in ", b("StatesToViewPresenterDef.scala"), "")
      )
    ),
    p("If you selected the ScalaCSS demo in the generator settings, you can modify those styles in ", b("styles/DemoStyles.scala"), ""),
    h3("RPC communication"),
    p(
      "Files related to the RPC system are placed in three modules:",
      ul(GuideStyles.defaultList)(
        li(b("shared"), " - contains RPC interfaces for both the client and the server, it may also contain a data models passed through RPC."),
        li(b("frontend"), " - contains the client RPC interface implementation in ", b("rpc/ExposedRpcInterfaces.scala"), ""),
        li(b("backend"), " - contains the server RPC interface implementation in ", b("rpc/RPCService.scala"), "")
      )
    ),
    p(
      "When you want to add a new method to the RPC interface, declare it in trait from the ", i("shared"), " module and then ",
      "implement it inside the ", i("backend"), " or ", i("frontend"), " module."
    ),
    h3("Backend"),
    p(
      "If you selected the Jetty launcher in the generator settings, you can run the application server using the main method of the prepared ",
      b("Launcher"), " object. The application server will handle static files requests and RPC connections."
    ),
    h2("What's next?"),
    p(
      "Now you know the basics of the Udash framework. You can learn more about the Udash project configuration in the ",
      a(href := BootstrappingIntroState.url)("Udash bootstraping"), " chapter. ",
      "Check also: ",
      a(href := FrontendIntroState.url)("Frontend application development"),
      " and ",
      a(href := RpcIntroState.url)("RPC in Udash"), ""
    )
  ).render

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}