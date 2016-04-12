package io.udash.guide.views.frontend.demos

import io.udash._
import io.udash.guide.styles.partials.GuideStyles
import org.scalajs.dom
import org.scalajs.dom.Element

import scalatags.JsDom
import scalacss.ScalatagsCss._

class BindDemoComponent extends Component {
  import io.udash.guide.Context._

  import JsDom.all._

  val names = Stream.continually(Stream("John", "Amy", "Bryan", "Diana")).flatten.iterator
  val name: Property[String] = Property[String](names.next())

  dom.window.setInterval(() => name.set(names.next()), 500)

  override def getTemplate: Element = div(id := "bind-demo", GuideStyles.frame)(
    p(
      "Name: ",
      bind(name)
    )
  ).render
}
