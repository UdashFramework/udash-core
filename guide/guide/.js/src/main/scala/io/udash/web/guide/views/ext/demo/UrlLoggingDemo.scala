package io.udash.web.guide.views.ext.demo

import io.udash.bootstrap.utils.BootstrapStyles._
import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object UrlLoggingDemo extends AutoDemo with CssView {

  private val ((enabled, history, add), source) = {
    import io.udash._

    val enabled = Property(false)
    val history = SeqProperty.blank[(String, Option[String])]
    enabled.listen(b => if (!b) history.set(Seq.empty))

    def log(url: String, referrer: Option[String]): Unit = {
      if (enabled.get) history.append((url, referrer))
    }

    (enabled, history, log _)
  }.withSourceCode

  def log(url: String, referrer: Option[String]): Unit = add(url, referrer)

  override protected final def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash._
    val rendered =
      span(GuideStyles.frame, GuideStyles.useBootstrap)(
        form(containerFluid)(
          div(Grid.row)(
            div(Grid.col(4, ResponsiveBreakpoint.Medium))(
              div(InputGroup.inputGroup)(
                Display.flex(),
                Flex.alignItems(FlexAlign.Center),
                div(InputGroup.prepend, Spacing.margin(size = SpacingSize.Small, side = Side.Right))("Turn on logging: "),
                div(InputGroup.append)(Checkbox(enabled)(cls := "checkbox-demo-a"))
              )
            )
          )
        ), br,
        form(containerFluid)(
          div(Grid.row)(
            div(Grid.col(4, ResponsiveBreakpoint.Medium))(
              b("Url")
            ),
            div(Grid.col(4, ResponsiveBreakpoint.Medium))(
              b("Referrer")
            )
          ),
          produce(history)(seq =>
            div(seq.map { case (url, refOpt) =>
              div(Grid.row)(
                div(Grid.col(4, ResponsiveBreakpoint.Medium))(
                  url
                ),
                div(Grid.col(4, ResponsiveBreakpoint.Medium))(
                  refOpt
                )
              )
            }).render
          )
        )
      ).render

    (rendered, source.linesIterator.take(source.linesIterator.size - 2))
  }
}
