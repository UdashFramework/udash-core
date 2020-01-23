package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._
import com.avsystem.commons._

object CheckboxButtonsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.UdashButtonGroup
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    val options = SeqProperty("Checkbox 1", "Checkbox 2", "Checkbox 3")
    val selected = SeqProperty(options.get.head)

    div(
      div(Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      ))(
        UdashButtonGroup.checkboxes(selected, options)().render
      ),
      h4("Is active: "),
      div(Card.card, Card.body, Background.color(Color.Light))(
        repeatWithNested(options) { (option, nested) =>
          val checked = selected.transform(_.contains(option.get))
          div(
            nested(bind(option)), ": ",
            nested(bind(checked))
          ).render
        }
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

