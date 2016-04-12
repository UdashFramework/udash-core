package io.udash.guide.views.bootstrapping

import io.udash.core.{DefaultViewPresenterFactory, View}
import io.udash.guide.{Context, _}
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom

case object BootstrappingIntroViewPresenter extends DefaultViewPresenterFactory[BootstrappingIntroState.type](() => new BootstrappingIntroView)

class BootstrappingIntroView extends View {
  import Context._

  import JsDom.all._
  import scalacss.Defaults._
  import scalacss.ScalatagsCss._

  override def getTemplate: dom.Element = div(
    h2("Project structure"),
    p("The basic Udash project contains three modules: "),
    BootstrappingImage("modules_basic.png", "Basic modules structure.", GuideStyles.imgRight, GuideStyles.imgSmall),
    ul(GuideStyles.defaultList)(
      li(
        "shared - contains ", a(href := RpcIntroState.url)("RPC"), " interfaces, shared model and logic (e.g model validation). ",
        "It is cross-compiled into JavaScript and JVM bytecode, so you can use it in the frontend and backend code. ",
        "It is useful when you want to use data models both in frontend and backend. Udash uses this module to share RPC interfaces."
      ),
      li(
        "backend - contains ", a(href := RpcIntroState.url)("RPC"), " and the whole backend application logic. ",
        "It is compiled only to bytecode. You can write your backend logic like database access, services layer etc. there."
      ),
      li(
        "frontend - contains frontend application code. It is compiled only to JS, it is packed into three files such as: ",
        i("frontend-deps.js"), ", ", i("frontend-impl.js"), " and ", i("frontend-init.js"), "."
      )
    ),
    p(
      "You can split the backend module into a more complex structure to keep a code cleaner - it is all up to you. This guide " +
      "keeps the basic structure with three modules."
    ),
    BootstrappingImage("modules_extended.png", "More complex modules structure.", GuideStyles.imgBig, GuideStyles.frame),
    h2("What's next?"),
    p(
      "If you want to prepare a custom project, you might be interested in ",
      a(href := BootstrappingSBTState.url)("SBT configuration"),  ". "
    )
  ).render

  override def renderChild(view: View): Unit = ()
}