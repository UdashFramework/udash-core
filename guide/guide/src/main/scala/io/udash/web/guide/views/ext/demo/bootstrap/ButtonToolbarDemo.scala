package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup, UdashButtonToolbar}
import io.udash.bootstrap.utils.BootstrapStyles.Size
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object ButtonToolbarDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))
    div(
      UdashButtonToolbar.reactive(groups)((p: CastableProperty[Seq[Int]], nested) => {
        val range = p.asSeq[Int]
        val group = UdashButtonGroup.reactive(range, size = Some(Size.Large).toProperty[Option[Size]]) {
          case (element, nested) =>
            val btn = UdashButton()(_ => nested(bind(element)))
            nested(btn)
            btn.render
        }
        nested(group)
        group.render
      })
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

