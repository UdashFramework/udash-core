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

class IndexView(url: Property[String]) extends View {
  import scalatags.JsDom.all._

  private lazy val content = div(
    IndexView.sectionIntro,
    IndexView.sectionFeatures,
    IndexView.sectionMore,
    IndexView.sectionDemo(url)
  ).render

  override def getTemplate: Element = content

  override def renderChild(view: View): Unit = {}
}

private[views] object IndexView {
  import scalatags.JsDom.tags2._
  import scalatags.JsDom.all._

  val sectionIntro = section(HomepageStyles.sectionIntro)(
    div(GlobalStyles.body)(
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

  val sectionFeatures = section(HomepageStyles.section)(
    div(GlobalStyles.body)(
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
          i()(HomepageStyles.featuresListIcon)(Image("features_typesafe.png", "Type safe", GlobalStyles.centerBlock)),
          h2(HomepageStyles.featuresListHead)(span("Type safe"), br(), span(HomepageStyles.featuresListHeadInner)("HTML CSS JS")),
          p(HomepageStyles.moreListDescription)("In cooperation with Scalatags and ScalaCSS libraries, Udash provides a type safe layer over HTML, CSS and JS with powerful data binding into DOM templates.")
        ),
        li(HomepageStyles.featuresListItem)(
          i(HomepageStyles.featuresListIcon)(Image("features_compiled.png", "Compiled to JS", GlobalStyles.centerBlock)),
          h2(HomepageStyles.featuresListHead)("Compiled to JS"),
          p(HomepageStyles.moreListDescription)("Scala is compiled to highly efficient JavaScript with no need to maintain js. It is also easy to use it with good, old JavaScript libraries like Twitter Bootstrap or jQuery.")
        )
      )
    )
  )

  val sectionMore = section(HomepageStyles.section)(
    div(GlobalStyles.body)(
      h1(
        "What's more?"
      ),
      ul(HomepageStyles.boxList)(
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("Routing"),
          p(HomepageStyles.moreListDescription)("Udash serves a frontend routing mechanism. Just define matching from URL to view.")
        ),
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("Asynchronous"),
          p(HomepageStyles.moreListDescription)("The whole framework is asynchronous by default – implementing reactive websites is much easier.")
        ),
        li(HomepageStyles.moreListItem)(
          h2(HomepageStyles.moreListHead)("IDE support"),
          p(HomepageStyles.moreListDescription)("With any IDE supporting the Scala language. No extra plugin needed.")
        )
      )
    )
  )

  def sectionDemo(url: Property[String]) = section(HomepageStyles.sectionDemo)(
    div(GlobalStyles.body)(
      h1("Have a code preview"),
      new DemoComponent(url).getTemplate,
      p(HomepageStyles.demoDescription)("It's free, try it now!"),
      Buttons.blackBorderButton(ExternalUrls.guide, "Start your project")
    )
  )
}