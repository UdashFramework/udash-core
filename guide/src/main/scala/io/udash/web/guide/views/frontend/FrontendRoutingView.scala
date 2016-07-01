package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import io.udash.routing.WindowUrlChangeProvider
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.{Context, _}
import org.scalajs.dom

import scala.scalajs.js
import scalatags.JsDom

case object FrontendRoutingViewPresenter extends ViewPresenter[FrontendRoutingState] {
  import Context._
  override def create(): (View, Presenter[FrontendRoutingState]) = {
    val url = Property[String]
    (new FrontendRoutingView(url), new FrontendRoutingPresenter(url))
  }
}

class FrontendRoutingPresenter(url: Property[String]) extends Presenter[FrontendRoutingState] {
  override def handleState(state: FrontendRoutingState) = {
    url.set(WindowUrlChangeProvider.currentFragment.value)
  }
}

class FrontendRoutingView(url: Property[String]) extends View {
  import Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  override def getTemplate: dom.Element = div(
    h2("Routing"),
    p(
      "Modern web applications create user friendly URLs and use them to handle the frontend routing. Udash framework ",
      "provides a convenient routing engine. To use it, create:"
    ),
    ul(GuideStyles.get.defaultList)(
      li(i("RoutingRegistry"), " - mapping from a URL to ", i("RoutingState")),
      li(i("ViewPresenterRegistry"), " - mapping from ", i("RoutingState"), " to ", i("ViewPresenter"))
    ),
    h3("URL"),
    p("The Udash routing engine is based on the URL part following the ", b("#"), " sign. To get the current URL, you can use the code presented below:"),
    CodeBlock("val url = io.udash.routing.WindowUrlChangeProvider.currentFragment")(GuideStyles),
    div(GuideStyles.get.frame)(
      p(
        span("The URL of this page is: "),
        span(id := "url-demo-link")(bind(url)), br(), br(),
        span("Click here to change URL: ")
      ),
      a(id := "url-demo-link-apple", href := s"${new FrontendRoutingState(Some("apple")).url}")("Apple"), " | ",
      a(id := "url-demo-link-orange", href := s"${new FrontendRoutingState(Some("orange")).url}")("Orange"), " | ",
      a(id := "url-demo-link-chocolate", href := s"${new FrontendRoutingState(Some("chocolate")).url}")("Chocolate"), " | ",
      a(id := "url-demo-link-pizza", href := s"${new FrontendRoutingState(Some("pizza")).url}")("Pizza"),
      br(), br(),
      input(GlobalStyles.inline, BootstrapStyles.Form.formControl, id := "url-demo-input", placeholder := "Type anything in this field, it should not disappear on a state change...")
    )(),
    h3("RoutingState & RoutingRegistry"),
    p(
      "The URL is resolved to a ", i("RoutingState"), " on every change. The application state can describe displayed view ",
      "and its state. For example:"
    ),
    CodeBlock(
      """case class UsersListState(searchQuery: Option[String]) extends RoutingState(RootState)
        |case class UserDetailsState(username: String) extends RoutingState(RootState)
        |case object Dashboard extends RoutingState(RootState)""".stripMargin
    )(GuideStyles),
    p(i("RoutingRegistry"), " is used to create a new application state on an URL change. For example:"),
    CodeBlock(
      """class RoutingRegistryDef extends RoutingRegistry[RoutingState] {
        |  def matchUrl(url: Url): RoutingState = {
        |    url2State.applyOrElse(url.value.stripSuffix("/"), (x: String) => ErrorState)
        |  }
        |
        |  def matchState(state: RoutingState): Url = Url(state2Url.apply(state))
        |
        |  private val (url2State, state2Url) = Bidirectional[String, RoutingState] {
        |    case "/users" => Dashboard
        |    case "/users/search" => UsersListState(None)
        |    case "/users/search" /:/ query => UsersListState(Some(query))
        |    case "/users/details" /:/ username => UserDetailsState(username)
        |  }
        |}""".stripMargin
    )(GuideStyles),
    p(
      "You can pass URL parts into the application state, just use the ", i("/:/"), " operator like in the example above. ",
      "For ", i("UsersListState"), " it is possible to keep some ",
      "search query in the URL. You can update the URL on every input change and every time the new state will be created."
    ),
    h3("ViewPresenter & ViewPresenterRegistry"),
    p(
      "When the state changes, the application needs to resolve matching ", i("ViewPresenter"), " ",
      "The way this matching is implemented is crucial, because if it returns a different ", i("ViewPresenter"), ", ",
      "new presenter and view will be created and rendered. If the matching returns equal (value, not reference comparison) ",
      i("ViewPresenter"), ", then the previously created presenter will be informed about the state changed through calling the ", i("handleState"), " method."
    ),
    CodeBlock(
      """class StatesToViewPresenterDef extends ViewPresenterRegistry[RoutingState] {
        |  def matchStateToResolver(state: RoutingState): ViewPresenter[_ <: RoutingState] =
        |    state match {
        |      case Dashboard => DashboardViewPresenter
        |      case UsersListState(query) => UsersListViewPresenter
        |      case UserDetailsState(username) => UserDetailsViewPresenter(username)
        |    }
        |}""".stripMargin
    )(GuideStyles),
    p(
      "Notice that matching for ", i("UsersListState"), " always returns the same ", i("UsersListViewPresenter"), " and ",
      "for ", i("UserDetailsState"), " always returns new ", i("UserDetailsViewPresenter"), ""
    ),
    ul(GuideStyles.get.defaultList)(
      li(
        span("The URL change: /users/details/john ➔ /users/details/david"),
        ul(GuideStyles.get.innerList)(
          li("The application state changes: UserDetailsState(\"john\") ➔ UserDetailsState(\"david\")."),
          li("The ViewPresenter changes: UserDetailsViewPresenter(\"john\") ➔ UserDetailsViewPresenter(\"david\")."),
          li("The application creates new view and presenter.")
        )
      ),
      li(
        span("The URL change: /users/search/john ➔ /users/search/david"),
        ul(GuideStyles.get.innerList)(
          li("The application state changes: UsersListState(Some(\"john\")) ➔ UsersListState(Some(\"david\"))."),
          li("The ViewPresenter stays: UsersListViewPresenter ➔ UsersListViewPresenter."),
          li("Presenter's ", i("handleState"), " method is called with the new state as an argument."),
          li("The view is not touched at all. The presenter can update the model or the view.")
        )
      )
    ),
    p(
      "Below you can find input witch changes the URL on every update. This change is handled like ",
      i("UsersListState"), " in the above example, so this view is not refreshed after the URL change."
    ),
    div(GuideStyles.get.frame)(
      input(BootstrapStyles.Form.formControl, id := "url-demo-link-input", value := "", placeholder := "Type something in this field and look at the URL...", onkeyup :+= ((event: dom.Event) => {
        applicationInstance.goTo(FrontendRoutingState(
          Some(js.Dynamic.global
            .encodeURIComponent(event.target.asInstanceOf[dom.html.Input].value)
            .asInstanceOf[String])
        ))
        true
      })),
      p("This view was created with: ", span(id := "url-demo-link-init")(WindowUrlChangeProvider.currentFragment.value))
    ),
    h2("What's next?"),
    p(
      "Take a look at the ", a(href := FrontendMVPState.url)("Model, View, Presenter & ViewPresenter"), " chapter to ",
      "learn more about the ", a(href := References.MvpPattern)("MVP pattern"), " variation used in Udash."
    )
  ).render

  override def renderChild(view: View): Unit = {}
}