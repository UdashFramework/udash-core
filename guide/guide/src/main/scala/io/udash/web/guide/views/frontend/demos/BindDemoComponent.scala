package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.commons.views.Component
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom

class BindDemoComponent extends Component {
  import JsDom.all._

  val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
  val name: Property[String] = Property[String](names.next())

  dom.window.setInterval(() => name.set(names.next()), 500)

  override def getTemplate: Modifier = div(id := "bind-demo", GuideStyles.frame)(
    p("Name: ", bind(name))
  )
}
