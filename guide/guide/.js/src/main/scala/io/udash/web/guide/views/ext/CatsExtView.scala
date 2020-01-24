package io.udash
package web.guide.views.ext

import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide.{BootstrapExtState, CatsExtState, Context, I18NExtState}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.{References, Versions}
import scalatags.JsDom

case object CatsExtViewFactory extends StaticViewFactory[CatsExtState.type](() => new CatsExtView)



class CatsExtView extends View with CssView {

  import Context._
  import JsDom.all._

  override def getTemplate: Modifier = div(
    h1("Udash Cats compatibility module"),
    p(
      "To use ", a(href := References.CatsHomepage, target := "_blank")("cats"), " with Udash, add the following line in you frontend module dependencies: "
    ),
    CodeBlock(
      s""""io.udash" %%% "udash-cats" % "${Versions.udashVersion}"""".stripMargin
    )(GuideStyles),
    h2("ReadableProperty typeclasses"),
  )
}