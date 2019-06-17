package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.jumbotron.UdashJumbotron
import io.udash.bootstrap.utils.BootstrapStyles.{Color, Size}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object JumbotronDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    UdashJumbotron()(_ => Seq[Modifier](
      h1("Jumbo poem!"),
      p("One component to rule them all, one component to find them, " +
        "one component to bring them all and in the darkness bind them."
      ),
      UdashButton(
        buttonStyle = Color.Info.toProperty,
        size = Some(Size.Large).toProperty[Option[Size]]
      )(_ => "Click")
    ))
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

