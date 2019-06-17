package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import scalatags.JsDom

object BindAttributeDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    val visible: Property[Boolean] = Property[Boolean](true)
    dom.window.setInterval(() => visible.set(!visible.get), 1000)

    p(
      span("Visible: ", bind(visible), " -> "),
      span((style := "display: none;").attrIfNot(visible))("Show/hide")
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "bind-attr-demo", GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}
