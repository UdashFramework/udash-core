package io.udash.selenium.views.demos.frontend

import io.udash._
import io.udash.css.CssView
import org.scalajs.dom
import scalatags.JsDom

class BindDemoComponent extends CssView {
  import JsDom.all._

  val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
  val name: Property[String] = Property[String](names.next())

  dom.window.setInterval(() => name.set(names.next()), 500)

  def getTemplate: Modifier = div(id := "bind-demo")(
    p("Name: ", bind(name))
  )
}
