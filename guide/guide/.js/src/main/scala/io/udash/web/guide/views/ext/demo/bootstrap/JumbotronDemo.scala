package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object JumbotronDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.jumbotron.UdashJumbotron
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    UdashJumbotron()(_ => Seq[Modifier](
      h1("Jumbo poem!"),
      p("One component to rule them all, one component to find them, " +
        "one component to bring them all and in the darkness bind them."
      ),
      UdashButton(
        buttonStyle = Color.Info.toProperty,
        size = Some(Size.Large).toProperty
      )(_ => "Click")
    ))
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator)
  }
}

