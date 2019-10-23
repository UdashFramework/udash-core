package io.udash.web.guide.views.ext.demo

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.Color
import io.udash.web.guide.demos.activity.Call
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom._
import scalatags.JsDom.all._

object RpcLoggingDemo {
  import io.udash.css.CssView._
  def apply(model: ReadableSeqProperty[Call], loadCalls: () => Any): dom.Element =
    span(GuideStyles.frame, GuideStyles.useBootstrap)(
      button(
        id := "call-logging-demo", BootstrapStyles.Button.btn, BootstrapStyles.Button.color(Color.Primary),
        onclick :+= ((_: MouseEvent) => loadCalls(), true)
      )("Load call list"),
      produce(model)(seq =>
        ul(
          seq.map(call => li(call.toString)).toSeq: _*
        ).render
      )
    ).render
}
