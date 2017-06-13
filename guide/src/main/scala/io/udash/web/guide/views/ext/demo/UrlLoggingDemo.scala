package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bindings.Checkbox
import io.udash.bootstrap.BootstrapStyles
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scala.collection.mutable.ListBuffer
import scalatags.JsDom.all._

object UrlLoggingDemo {
  import io.udash.css.CssView._
  import io.udash.web.guide.Context._

  val enabled = Property(false)
  val history = SeqProperty[(String, Option[String])](ListBuffer.empty)
  enabled.listen(b => if (!b) history.set(ListBuffer.empty))

  def log(url: String, referrer: Option[String]): Unit =
    if (enabled.get) history.append((url, referrer))

  def apply(): dom.Element =
    span(GuideStyles.frame, GuideStyles.useBootstrap)(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.row)(
          div(BootstrapStyles.Grid.colMd4)(
            div(BootstrapStyles.Form.inputGroup)(
              div(BootstrapStyles.Form.inputGroupAddon)("Turn on logging:"),
              div(BootstrapStyles.Form.inputGroupAddon)(Checkbox(UrlLoggingDemo.enabled, cls := "checkbox-demo-a"))
            )
          )
        )
      ), br,
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.row)(
          div(BootstrapStyles.Grid.colMd4)(
            b("Url")
          ),
          div(BootstrapStyles.Grid.colMd4)(
            b("Referrer")
          )
        ),
        produce(UrlLoggingDemo.history)(seq =>
          div()(seq.map { case (url, refOpt) =>
            div(BootstrapStyles.row)(
              div(BootstrapStyles.Grid.colMd4)(
                url
              ),
              div(BootstrapStyles.Grid.colMd4)(
                refOpt
              )
            )
          }: _*).render
        )
      )
    ).render
}
