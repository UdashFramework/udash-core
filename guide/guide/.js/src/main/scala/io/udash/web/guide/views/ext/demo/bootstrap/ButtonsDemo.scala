package io.udash.web.guide.views.ext.demo.bootstrap


import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object ButtonsDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssStyle
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

    import scala.util.Random

    val smallBtn = Some(Size.Small).toProperty[Option[Size]]
    val disabledButtons = Property(Set.empty[Int])

    def bottomMargin: CssStyle = {
      Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      )
    }

    val buttons = Color.values.map(color =>
      UdashButton(
        color.toProperty,
        smallBtn,
        disabled = disabledButtons.transform(_.contains(color.ordinal))
      )(_ => Seq[Modifier](
        color.name,
        Spacing.margin(size = SpacingSize.ExtraSmall)
      ))
    )

    val clicks = SeqProperty[String](Seq.empty)
    buttons.foreach(_.listen {
      case UdashButton.ButtonClickEvent(source, _) =>
        clicks.append(source.render.textContent)
    })

    val push = UdashButton(
      size = Some(Size.Large).toProperty,
      block = true.toProperty
    )("Disable random buttons!")
    push.listen {
      case UdashButton.ButtonClickEvent(_, _) =>
        clicks.set(Seq.empty)

        val maxDisabledCount = Random.nextInt(buttons.size + 1)
        disabledButtons.set(Seq.fill(maxDisabledCount)(
          Random.nextInt(buttons.size)
        ).toSet)
    }

    div(
      div(bottomMargin)(push),
      div(
        Display.flex(),
        Flex.justifyContent(FlexContentJustification.Center),
        bottomMargin
      )(buttons),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(Card.card, Card.body, Background.color(Color.Light))(seq.map(li(_))).render
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

