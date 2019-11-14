package io.udash.web.guide.views.frontend.demos

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ShowIfDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val visible = Property(true)
    window.setInterval(() => visible.set(!visible.get), 1000)

    p(
      span("Visible: ", bind(visible), " -> "),
      showIf(visible)(span("Show/hide").render)
    )
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (div(id := "show-if-demo", GuideStyles.frame)(rendered), source.linesIterator)
  }
}
