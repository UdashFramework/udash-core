package io.udash.web.guide.views.frontend.demos

import io.udash._
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom
import scalacss.ScalatagsCss._
import io.udash.web.commons.views.Component

class BindDemoComponent extends Component {
  import io.udash.web.guide.Context._

  import JsDom.all._

  val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
  val name: Property[String] = Property[String](names.next())

  dom.window.setInterval(() => name.set(names.next()), 500)

  override def getTemplate: Modifier = div(id := "bind-demo", GuideStyles.get.frame)(
    p("Name: ", bind(name))
  )
}
