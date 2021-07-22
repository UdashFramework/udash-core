package io.udash.web.guide.views

import com.avsystem.commons.universalOps
import io.udash._
import io.udash.wrappers.jquery.EasingFunction
import org.scalajs.dom._

import scala.scalajs.js

abstract class ViewContainer extends ContainerView {
  protected val child: Element

  override def renderChild(view: Option[View]): Unit = {
    import io.udash.wrappers.jquery.jQ
    val jqChild = jQ(child)

    jqChild.animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing, { _ =>
      view match {
        case Some(view) =>
          jqChild.children().remove()
          view.getTemplate.applyTo(jqChild.toArray.head)
          jqChild.animate(Map[String, Any]("opacity" -> 1), 200, EasingFunction.swing,
            _ => js.Dynamic.global.Prism.highlightAll()
          )
        case None =>
          jqChild.html(null)
            .animate(Map[String, Any]("opacity" -> 1), 200)
      }
    }).discard
  }
}
