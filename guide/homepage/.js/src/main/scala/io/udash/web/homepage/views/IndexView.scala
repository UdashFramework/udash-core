package io.udash.web.homepage.views

import io.udash._
import io.udash.core.Presenter
import io.udash.web.commons.config.ExternalUrls
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.commons.views.{Image, SVG, Size}
import io.udash.web.homepage._
import io.udash.web.homepage.components.Buttons
import io.udash.web.homepage.components.demo.DemoComponent
import io.udash.web.homepage.styles.partials.HomepageStyles

final case class IndexViewFactory()(implicit application: Application[RoutingState]) extends ViewFactory[IndexState] {
  override def create(): (View, Presenter[IndexState]) = {
    (new IndexView, EmptyPresenter)
  }
}

final class IndexView(implicit application: Application[RoutingState]) extends View {

  import scalatags.JsDom.all._

  private lazy val content = div(
    IndexView.sectionIntro,
    IndexView.sectionFeatures,
    IndexView.sectionMore,
    IndexView.sectionDemo,
  )

  override def getTemplate: Modifier = content
}

private object IndexView {
  import io.udash.css.CssView._
  import scalatags.JsDom.all._
  import scalatags.JsDom.tags2._

  private val sectionIntro = section(HomepageStyles.sectionIntro)(
    div(GlobalStyles.body, HomepageStyles.body)(
      div(HomepageStyles.introInner)(
        a(href := ExternalUrls.scalajs, HomepageStyles.introScala, target := "_blank")(
          i(HomepageStyles.introScalaIcon)(
            SVG("based.svg#based", Size(10, 10))
          ),
          span("Based on Scala.js")
        ),
        p(HomepageStyles.introHead)(
          span("Udash is a "), span(GlobalStyles.red)("Scala", br()),
          span("framework for building", br(), "beautiful and maintainable", br()),
          span(GlobalStyles.grey)("Web Applications")
        ),
        Buttons.whiteBorderButton(ExternalUrls.guide, "Start your project")
      )
    )
  )

  private val sectionFeatures = section(HomepageStyles.section)(
    div(GlobalStyles.body, HomepageStyles.body)(
      h1(
        "Combined forces", br(), "of Scala & JavaScript"
      ),
      ul(HomepageStyles.boxList)(
        li(HomepageStyles.featuresListItem)(
          i(HomepageStyles.featuresListIcon)(Image("features_shared.png", "Shared scala code", GlobalStyles.centerBlock)),
          h2(HomepageStyles.featuresListHead)("Shared scala code"),
          p(HomepageStyles.moreListDescription)("Udash brings out of the box the RPC system with a shared data model and interfaces between frontend and backend, which boosts development and keeps code bases consistent.")
        ),
        li(HomepageStyles.featuresListItem)(
          i()(HomepageStyles.featuresListIcon)(Image("features_typesafe.png", "Type-safe", GlobalStyles.centerBlock)),
          h2(HomepageStyles.featuresListHead)(span("Type-safe"), br(), span(HomepageStyles.featuresListHeadInner)("HTML CSS JS")),
          p(HomepageStyles.moreListDescription)("In cooperation with Scalatags and ScalaCSS libraries, Udash provides a type-safe layer over HTML, CSS and JS with powerful data binding into DOM templates.")
        ),
        li(HomepageStyles.featuresListItem)(
          i(HomepageStyles.featuresListIcon)(Image("features_compiled.png", "Compiled to JS", GlobalStyles.centerBlock)),
          h2(HomepageStyles.featuresListHead)("Compiled to JS"),
          p(HomepageStyles.moreListDescription)("Scala is compiled to highly efficient JavaScript with no need to maintain js. It is also easy to use it with good, old JavaScript libraries like Twitter Bootstrap or jQuery.")
        )
      )
    )
  )

  private val sectionMore = section(HomepageStyles.section)(
    div(GlobalStyles.body, HomepageStyles.body)(
      h1("Why Udash?"),
      ul(HomepageStyles.moreList)(
        li(HomepageStyles.moreListItem, HomepageStyles.moreListItemTwoLineTitle)(
          h2(HomepageStyles.moreListHead)("Reactive", br(), "Data Bindings"),
          p(HomepageStyles.moreListDescription)("Automatically synchronise user interface with your data model.")
        ),
        li(HomepageStyles.moreListItem, HomepageStyles.moreListItemTwoLineTitle)(
          h2(HomepageStyles.moreListHead)("Type-safe ", br(), "RPC & REST"),
          p(HomepageStyles.moreListDescription)("A client↔server communication based on typed interfaces. Bidirectional RPC via WebSockets out of the box.")
        ),
        li(HomepageStyles.moreListItem, HomepageStyles.moreListItemTwoLineTitle)(
          h2(HomepageStyles.moreListHead)("User Interface Components"),
          p(HomepageStyles.moreListDescription)("Twitter Bootstrap components enriched by Udash features.")
        ),

        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("CSS"),
          p(HomepageStyles.moreListDescription)("Type-safe CSS definition in you Scala code with minimal JavaScript size footprint and server-side rendering.")
        ),
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("i18n"),
          p(HomepageStyles.moreListDescription)("Translations served by the backend or compiled into JavaScript.")
        ),
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("Generator"),
          p(HomepageStyles.moreListDescription)("Generate a customized application, compile and\u00A0try it out in 5 minutes.")
        ),

        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("Open Source"),
          p(HomepageStyles.moreListDescription)("The whole framework code is available on GitHub under ", i("Apache v2"), " license.")
        ),
        li(HomepageStyles.moreListItem, HomepageStyles.moreListItemTwoLineTitle)(
          h2(HomepageStyles.moreListHead)("Backend independent"),
          p(HomepageStyles.moreListDescription)("Udash provides a complete support for your web application and the communication with the server but does not influence your backend implementation.")
        ),
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("IDE support"),
          p(HomepageStyles.moreListDescription)("With any IDE supporting the Scala language. No extra plugin needed.")
        )
      )
    )
  )

  private def sectionDemo(implicit application: Application[RoutingState]) = section(HomepageStyles.sectionDemo)(
    div(GlobalStyles.body, HomepageStyles.body)(
      h1("Have a code preview"),
      new DemoComponent().getTemplate,
      p(HomepageStyles.demoDescription)("It's free, try it now!"),
      Buttons.blackBorderButton(ExternalUrls.guide, "Start your project")
    )
  )
}