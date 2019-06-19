package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ButtonToolbarDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import scalatags.JsDom.all._

    val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))

    div(
      UdashButtonToolbar.reactive(groups)((p, nested) => {
        val group = UdashButtonGroup.reactive(
          p.transformToSeq(identity),
          size = Some(Size.Large).toProperty[Option[Size]]
        ) {
          case (element, nested) =>
            val btn = UdashButton()(_ => nested(bind(element)))
            nested(btn)
            btn.render
        }
        nested(group)
        group.render
      })
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.lines)
  }
}

