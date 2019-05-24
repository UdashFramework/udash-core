package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.UdashButtonGroup
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.components.BootstrapUtils.wellStyles
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object CheckboxButtonsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._

  private val (rendered, source) = {
    val options = SeqProperty[String]("Checkbox 1", "Checkbox 2", "Checkbox 3")
    val selected = SeqProperty[String](options.get.head)

    div(
      div(BootstrapStyles.Spacing.margin(side = Side.Bottom, size = SpacingSize.Normal))(
        UdashButtonGroup.checkboxes(selected, options)().render
      ),
      h4("Is active: "),
      div(wellStyles)(
        repeatWithNested(options) { (option, nested) =>
          val checked = selected.transform((_: Seq[String]).contains(option.get))
          div(nested(bind(option)), ": ", nested(bind(checked))).render
        }
      )
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

