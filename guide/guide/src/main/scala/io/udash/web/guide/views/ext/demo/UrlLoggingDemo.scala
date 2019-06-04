package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

import scala.collection.mutable.ListBuffer

object UrlLoggingDemo extends AutoDemo {
  import io.udash.css.CssView._

  private val ((enabled, history, add), source) = {
    val enabled = Property(false)
    val history = SeqProperty[(String, Option[String])](ListBuffer.empty)
    enabled.listen(b => if (!b) history.set(ListBuffer.empty))

    def log(url: String, referrer: Option[String]): Unit =
      if (enabled.get) history.append((url, referrer))

    (enabled, history, log _)
    }.withSourceCode

  def log(url: String, referrer: Option[String]): Unit = add(url, referrer)

  override protected final def demoWithSource(): (Modifier, Iterator[String]) = {
    val rendered =
      span(GuideStyles.frame, GuideStyles.useBootstrap)(
        form(BootstrapStyles.containerFluid)(
          div(BootstrapStyles.Grid.row)(
            div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
              div(BootstrapStyles.InputGroup.inputGroup)(
                div(BootstrapStyles.InputGroup.prepend)("Turn on logging:"),
                div(BootstrapStyles.InputGroup.append)(Checkbox(UrlLoggingDemo.enabled)(cls := "checkbox-demo-a"))
              )
            )
          )
        ), br,
        form(BootstrapStyles.containerFluid)(
          div(BootstrapStyles.Grid.row)(
            div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
              b("Url")
            ),
            div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
              b("Referrer")
            )
          ),
          produce(UrlLoggingDemo.history)(seq =>
            div()(seq.map { case (url, refOpt) =>
              div(BootstrapStyles.Grid.row)(
                div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
                  url
                ),
                div(BootstrapStyles.Grid.col(4, ResponsiveBreakpoint.Medium))(
                  refOpt
                )
              )
            }: _*).render
          )
        )
      ).render

    (rendered, source.lines.slice(1, source.lines.size - 2))
  }
}
