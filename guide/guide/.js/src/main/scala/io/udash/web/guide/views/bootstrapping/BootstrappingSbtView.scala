package io.udash.web.guide.views.bootstrapping

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{Context, _}
import scalatags.JsDom

case object BootstrappingSbtViewFactory extends StaticViewFactory[BootstrappingSbtState.type](() => new BootstrappingSbtView)

final class BootstrappingSbtView extends FinalView with CssView {

  import Context._
  import JsDom.all._

  override def getTemplate: Modifier =
    div(
      h2("SBT commands"),
      p("If you generated your project using the template, there are few sbt commands ready for you to use:"),
      ul(GuideStyles.defaultList)(
        li(i("compile"), " - compiles all your Scala sources (it does not produce JS files)."),
        li(i("copyAssets"), " - copies all your assets into target directory."),
        li(i("compileStatics"), " - produces whole frontend application with full JS optimization (includes copyAssets)."),
        li(i("compileAndOptimizeStatics"), " - as above but with full JS optimization."),
        li(i("run"), " - starts backend server."),
      ),
      p("Notice that you can prefix commands with ~ to automatically rerun them on sources change."),
      h2("Advanced project setup"),
      p(
        "You can go to the ", a(href := AdvancedBootstrappingSbtState.url)("advanced bootstrapping section"),
        " to take a detailed look at the content of the build template."
      ),
      h2("What's next?"),
      p(
        "SBT configuration is ready, now it is time to prepare ", a(href := BootstrappingRpcState.url)("RPC interfaces"),
        " in the ", b("shared"), " module."
      )
    )
}