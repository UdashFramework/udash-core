package io.udash.web.guide.views

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.Context._
import io.udash.web.guide._

import scalatags.JsDom.all._

object IntroViewFactory extends StaticViewFactory[RootState.type](() => new IntroView)

class IntroView extends FinalView with CssView {
  private val content = div(
    h1("Udash Developer's Guide"),
    p(
      "This guide provides knowledge essential for implementing web applications with the Udash framework. ",
      "The quick start guide below will introduce you to Udash basics."
    ),
    h2("Quick start guide"),
    p(
      "A good starting point is a generation of a project base with the ",
      a(href := References.SbtTemplates)("Giter8"),
      " project generator. The generator is a built-in mechanism of the SBT since the ",
      i("0.13.13"), " version. "
    ),
    p("To start a new project just type: ", i("sbt new UdashFramework/udash.g8"), ". "),
    p(
      "The generator allows you to customize some basic properties of the target project. ",
      "The generated sources contain comprehensive READMEs, these provide guidance around the code ",
      "and links to the useful sources of knowledge about development with Udash. "
    ),
    h3("What's inside?"),
    p(
      "The generator creates a simple chat application, which presents the most important features of the Udash framework. ",
      "It uses properties, Bootstrap components, RPC with notifications from the server, translations, etc. ",
    ),
    p(
      "When you open the application in your browser you should see the login page form. In the top-right corner you can ",
      "change the page language. Below you can type user credentials and go to the chat window. ",
    ),
    p(
      "On the chat view you can type, send and read messages. You should try to open another browser window and check ",
      "that messages and connections count refresh automatically. Server notifies authenticated clients about these events ",
      "via Server -> Client RPC notification. ",
    ),
    p(
      "This demo presents the usage of the other useful tools for building and deploying web applications. ",
      "In the backend module the application uses Jetty and Spring to setup the server. ",
      "In every module you can find tests based on ScalaTest and ScalaMock. ",
      "The frontend and shared modules use ", i("scalajs-env-selenium"),
      " in order to run the tests compiled to JavaScript in a web browser. ",
      "The SBT configuration embraces ", i("SBT Native Packager"), " to provide easy deployment process. "
    ),
    h2("What's next?"),
    p(
      "It's time to learn the basics of the Udash framework. You can learn more about the Udash project configuration in the ",
      a(href := BootstrappingIntroState.url)("Udash bootstrapping"), " chapter. ",
      "Check also ",
      a(href := FrontendIntroState.url)("Frontend application development"),
      " and ",
      a(href := RpcIntroState.url)("RPC in Udash"), ". "
    )
  )

  override def getTemplate: Modifier = content
}