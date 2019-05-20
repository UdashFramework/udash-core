package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.css.CssView
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.ext.demo.AutoDemo
import org.scalajs.dom
import scalatags.JsDom

object ShowIfDemo extends AutoDemo with CssView {
  import JsDom.all._

  private val (rendered, source) = {
    val visible: Property[Boolean] = Property[Boolean](true)
    dom.window.setInterval(() => visible.set(!visible.get), 1000)

    val element = Seq(
      span("Visible: ", bind(visible), " -> "),
      showIf(visible)(span("Show/hide").render)
    )
    element
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(id := "show-if-demo", GuideStyles.frame)(rendered), source.lines.slice(1, source.lines.size - 2))
  }
}
