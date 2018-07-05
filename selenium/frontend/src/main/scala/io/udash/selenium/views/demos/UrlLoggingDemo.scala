package io.udash.selenium.views.demos

import io.udash._
import io.udash.bootstrap.BootstrapStyles
import org.scalajs.dom
import scalatags.JsDom.all._

import scala.collection.mutable.ListBuffer

object UrlLoggingDemo {
  import io.udash.css.CssView._

  val enabled = Property(false)
  val history = SeqProperty[(String, Option[String])](ListBuffer.empty)
  enabled.listen(b => if (!b) history.set(ListBuffer.empty))

  def log(url: String, referrer: Option[String]): Unit =
    if (enabled.get) history.append((url, referrer))

  def apply(): dom.Element =
    span(
      form(BootstrapStyles.containerFluid)(
        div(BootstrapStyles.row)(
          div(BootstrapStyles.Grid.colMd4)(
            div(BootstrapStyles.Form.inputGroup)(
              div(BootstrapStyles.Form.inputGroupAddon)("Turn on logging:"),
              div(BootstrapStyles.Form.inputGroupAddon)(Checkbox(UrlLoggingDemo.enabled)(cls := "checkbox-demo-a"))
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
