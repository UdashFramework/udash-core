package io.udash.web.guide.views.ext

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles

import scalatags.JsDom

case object AuthorizationExtViewFactory extends StaticViewFactory[AuthorizationExtState.type](() => new AuthorizationExtView)


class AuthorizationExtView extends FinalView with CssView {
  import Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h1("Udash Authorization Utils"),
    p(
      "The Authorization plugin provides utilities for user's context and data access management. ",
      "It contains permissions description tools and useful methods for verifying user's access level. "
    ),
    h2("Permission & UserCtx"),
    p(
      i("Permission"), " is an entity granting atomic access to a part of the system. Permissions may be combined into ",
      i("PermissionCombinator"), ", which describes more complex access requirements. For example:"
    ),
    CodeBlock(
      s"""import io.udash.auth._
         |
         |class Perm(override val id: PermissionId) extends Permission
         |case object P1 extends Perm(PermissionId("1"))
         |case object P2 extends Perm(PermissionId("2"))
         |case object P3 extends Perm(PermissionId("3"))
         |
         |val c1: PermissionCombinator = P1.and(P2)
         |val c2: PermissionCombinator = P1.or(P3)
         |val c3: PermissionCombinator = PermissionCombinator.allOf(P1, P3)
         |val c4: PermissionCombinator = PermissionCombinator.anyOf(P1, P2, P3)""".stripMargin
    )(GuideStyles),
    p(i("UserCtx"), " provides information about user's permissions. Take a look at an example implementation: "),
    CodeBlock(
      s"""import io.udash.auth._
         |
         |case class User(perms: Set[Permission]) extends UserCtx {
         |  override def has(permission: Permission): Boolean =
         |    perms.contains(permission)
         |
         |  override def isAuthenticated: Boolean = true
         |}""".stripMargin
    )(GuideStyles),
    p(
      "You can verify user against ", i("PermissionCombinator"), " using ", i(".check(ctx: userCtx)"),
      " method as follows:"
    ),
    CodeBlock(
      s"""import io.udash.auth._
         |
         |val userCtx = User(Set(P1, P2))
         |c1.check(userCtx) // == true
         |c2.check(userCtx) // == true
         |c3.check(userCtx) // == false
         |c4.check(userCtx) // == true""".stripMargin
    )(GuideStyles),
    h2("View elements authorization"),
    p("The plugin provides ", i("AuthView"), " mixin, which contains two methods for user authorization: "),
    ul(GuideStyles.defaultList)(
      li(i("require"), " - renders provided view elements only if the user context has required permissions,"),
      li(i("requireAuthenticated"), " - renders provided view elements only if user is authenticated.")
    ),
    CodeBlock(
      s"""import scalatags.JsDom.all._
         |import io.udash._
         |import io.udash.auth._
         |
         |class ExampleView(implicit ctx: UserCtx) extends FinalView with AuthView {
         |  override def getTemplate: Modifier = div(
         |    require(P1.and(P2)) {
         |      span("This elements requires P1 and P2 permissions.")
         |    },
         |    requireAuthenticated {
         |      span("This elements requires authenticated user context.")
         |    }
         |  )
         |}
         |""".stripMargin
    )(GuideStyles),
    h2("RPC endpoints authorization"),
    p(
      "Similar methods are available in ", i("AuthRequires"), " trait, but instead of filtering GUI elements these ",
      "methods throw exceptions: "
    ),
    ul(GuideStyles.defaultList)(
      li(i("require"), " - throws ", i("UnauthorizedException"), " if user does not have reqired premissions,"),
      li(i("requireAuthenticated"), " - throws ", i("UnauthenticatedException"), " if user is not authenticated."),
    ),
    p(
      "These methods are very helpful in RPC endpoints implementation. Remember that you should use ",
      i("DefaultAuthExceptionCodecRegistry"), " instead of ", i("DefaultExceptionCodecRegistry"),
      " - it allows RPC to properly serialize authorization exceptions."
    ),
    CodeBlock(
      s"""import io.udash._
         |import io.udash.auth._
         |import io.udash.rpc._
         |
         |class ExampleEndpoint(implicit ctx: UserCtx, ec: ExecutionContext)
         |  extends ExampleRPC with AurhRequires {
         |
         |  def exampleCall(i: Int): Future[Int] = Future {
         |    require(P1.or(P2))
         |    i + 1
         |  }
         |
         |  def subEndpoint(): AnotherRPC = {
         |    require(P3)
         |    new AnotherEndpoint
         |  }
         |}""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at another extensions like ", a(href := BootstrapExtState.url)("Bootstrap Components"), " or ",
      a(href := I18NExtState.url)("i18n utilities"), "."
    )
  )
}