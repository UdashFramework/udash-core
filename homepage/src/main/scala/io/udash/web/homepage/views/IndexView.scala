package io.udash.web.homepage.views

import io.udash._
import io.udash.core.Presenter
import io.udash.web.homepage._
import io.udash.web.homepage.components.Buttons
import io.udash.web.homepage.components.demo.DemoComponent
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.homepage.styles.partials.{ButtonsStyle, HomepageStyles}
import io.udash.routing.WindowUrlChangeProvider
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.styles.utils.StyleUtils
import io.udash.web.commons.views.{Image, SVG, Size}
import org.scalajs.dom.Element

import scalacss.ScalatagsCss._

case object IndexViewPresenter extends ViewPresenter[IndexState] {
  import Context._

  override def create(): (View, Presenter[IndexState]) = {
    val url = Property[String]
    (new IndexView(url), new IndexPresenter(url))
  }
}

class IndexPresenter(url: Property[String]) extends Presenter[IndexState] {
  override def handleState(state: IndexState): Unit = {
    url.set(WindowUrlChangeProvider.currentFragment.value)
  }
}

class IndexView(url: Property[String]) extends FinalView {
  import scalatags.JsDom.all._

  private lazy val content = div(
    IndexView.sectionIntro,
    IndexView.sectionFeatures,
    IndexView.sectionMore,
    IndexView.sectionDemo(url)
  )

  override def getTemplate: Modifier = content
}

private[views] object IndexView {
  import scalatags.JsDom.tags2._
  import scalatags.JsDom.all._

  val sectionIntro = section(HomepageStyles.get.sectionIntro)(
    div(GlobalStyles.get.body)(
      div(HomepageStyles.get.introInner)(
        a(href := ExternalUrls.scalajs, HomepageStyles.get.introScala, target := "_blank")(
          i(HomepageStyles.get.introScalaIcon)(
            SVG("based.svg#based", Size(10, 10))
          ),
          span("Based on Scala.js")
        ),
        p(HomepageStyles.get.introHead)(
          span("Udash is a "), span(GlobalStyles.get.red)("Scala", br()),
          span("framework for building", br(), "beautiful and maintainable", br()),
          span(GlobalStyles.get.grey)("Web Applications")
        ),
        Buttons.whiteBorderButton(ExternalUrls.guide, "Start your project")
      )
    )
  )

  val sectionFeatures = section(HomepageStyles.get.section)(
    div(GlobalStyles.get.body)(
      h1(
        "Combined forces", br(), "of Scala & JavaScript"
      ),
      ul(HomepageStyles.get.boxList)(
        li(HomepageStyles.get.featuresListItem)(
          i(HomepageStyles.get.featuresListIcon)(Image("features_shared.png", "Shared scala code", GlobalStyles.get.centerBlock)),
          h2(HomepageStyles.get.featuresListHead)("Shared scala code"),
          p(HomepageStyles.get.moreListDescription)("Udash brings out of the box the RPC system with a shared data model and interfaces between frontend and backend, which boosts development and keeps code bases consistent.")
        ),
        li(HomepageStyles.get.featuresListItem)(
          i()(HomepageStyles.get.featuresListIcon)(Image("features_typesafe.png", "Type-safe", GlobalStyles.get.centerBlock)),
          h2(HomepageStyles.get.featuresListHead)(span("Type-safe"), br(), span(HomepageStyles.get.featuresListHeadInner)("HTML CSS JS")),
          p(HomepageStyles.get.moreListDescription)("In cooperation with Scalatags and ScalaCSS libraries, Udash provides a type-safe layer over HTML, CSS and JS with powerful data binding into DOM templates.")
        ),
        li(HomepageStyles.get.featuresListItem)(
          i(HomepageStyles.get.featuresListIcon)(Image("features_compiled.png", "Compiled to JS", GlobalStyles.get.centerBlock)),
          h2(HomepageStyles.get.featuresListHead)("Compiled to JS"),
          p(HomepageStyles.get.moreListDescription)("Scala is compiled to highly efficient JavaScript with no need to maintain js. It is also easy to use it with good, old JavaScript libraries like Twitter Bootstrap or jQuery.")
        )
      )
    )
  )

  val sectionMore = section(HomepageStyles.get.section)(
    div(GlobalStyles.get.body)(
      h1(
        "Why Udash?"
      ),
      ul(HomepageStyles.get.moreList)(
        li(HomepageStyles.get.moreListItem, HomepageStyles.get.moreListItemTwoLineTitle)(
          h2(HomepageStyles.get.moreListHead)("Reactive", br(), "Data Bindings"),
          p(HomepageStyles.get.moreListDescription)("Automatically synchronise user interface with your data model.")
        ),
        li(HomepageStyles.get.moreListItem, HomepageStyles.get.moreListItemTwoLineTitle)(
          h2(HomepageStyles.get.moreListHead)("Type-safe ", br(), "RPC & REST"),
          p(HomepageStyles.get.moreListDescription)("A client↔server communication based on typed interfaces. Bidirectional RPC via WebSockets out of the box.")
        ),
        li(HomepageStyles.get.moreListItem, HomepageStyles.get.moreListItemTwoLineTitle)(
          h2(HomepageStyles.get.moreListHead)("User Interface Components"),
          p(HomepageStyles.get.moreListDescription)("Twitter Bootstrap components enriched by Udash features.")
        ),

        li(HomepageStyles.get.moreListItem)(
          h2(HomepageStyles.get.moreListHead)("Routing"),
          p(HomepageStyles.get.moreListDescription)("Udash serves a frontend routing mechanism. Just define matching from URL to view.")
        ),
        li(HomepageStyles.get.moreListItem)(
          h2(HomepageStyles.get.moreListHead)("i18n"),
          p(HomepageStyles.get.moreListDescription)("Translations served by the backend or compiled into JavaScript.")
        ),
        li(HomepageStyles.get.moreListItem)(
          h2(HomepageStyles.get.moreListHead)("Generator"),
          p(HomepageStyles.get.moreListDescription)("Generate a customized application, compile and\u00A0try it out in 5 minutes.")
        ),

        li(HomepageStyles.get.moreListItem)(
          h2(HomepageStyles.get.moreListHead)("Open Source"),
          p(HomepageStyles.get.moreListDescription)("The whole framework code is available on GitHub under ", i("Apache v2"), " license.")
        ),
        li(HomepageStyles.get.moreListItem, HomepageStyles.get.moreListItemTwoLineTitle)(
          h2(HomepageStyles.get.moreListHead)("Backend independent"),
          p(HomepageStyles.get.moreListDescription)("Udash provides a complete support for your web application and the communication with the server but does not influence your backend implementation.")
        ),
        li(HomepageStyles.get.moreListItem)(
          h2(HomepageStyles.get.moreListHead)("IDE support"),
          p(HomepageStyles.get.moreListDescription)("With any IDE supporting the Scala language. No extra plugin needed.")
        )
      )
    )
  )

  def sectionDemo(url: Property[String]) = section(HomepageStyles.get.sectionDemo)(
    div(GlobalStyles.get.body)(
      h1("Have a code preview"),
      new DemoComponent(url).getTemplate,
      p(HomepageStyles.get.demoDescription)("It's free, try it now!"),
      Buttons.blackBorderButton(ExternalUrls.guide, "Start your project")
    )
  )
}