package io.udash.web.guide.views

import io.udash._
import io.udash.wrappers.jquery.EasingFunction
import org.scalajs.dom._

import scala.scalajs.js

/**
  * Created by malchik on 2016-03-30.
  */
abstract class ViewContainer extends ContainerView {
  protected val child: Element

  override def renderChild(view: Option[View]): Unit = {
    import io.udash.wrappers.jquery.jQ
    val jqChild = jQ(child)

    jqChild
      .animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing,
        (el: Element) => {
          view match {
            case Some(view) =>
              jqChild.children().remove()
              view.getTemplate.applyTo(jqChild.toArray.head)
              jqChild.animate(Map[String, Any]("opacity" -> 1), 200, EasingFunction.swing,
                (_) => js.Dynamic.global.Prism.highlightAll()
              )
            case None =>
              jqChild.html(null)
                .animate(Map[String, Any]("opacity" -> 1), 200)
          }
        }
      )
  }
}
