package io.udash.web.guide.views

import io.udash._
import io.udash.web.guide.RootState

object FaqViewFactory extends StaticViewFactory[RootState.type](() => new FaqView)

class FaqView extends FinalView {
  import scalatags.JsDom.all._

  private val content = div(
    h2("FAQ"),
    p("TODO")
  )

  override def getTemplate: Modifier = content
}