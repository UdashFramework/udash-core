package io.udash.web.guide.views.frontend.demos

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object BindDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import org.scalajs.dom.window
    import scalatags.JsDom.all._

    val names = Iterator.continually(Seq("John", "Amy", "Bryan", "Diana")).flatten

    val name = Property(names.next())
    window.setInterval(() => name.set(names.next()), 500)

    p("Name: ", bind(name))
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (
      div(
        id := "bind-demo",
        GuideStyles.frame
      )(rendered),
      source.linesIterator
    )
  }
}
