package io.udash.web.guide.views

import io.udash.core.View
import io.udash.wrappers.jquery.EasingFunction
import org.scalajs.dom._

import scala.scalajs.js

/**
  * Created by malchik on 2016-03-30.
  */
abstract class ViewContainer extends View {
  protected val child: Element

  override def renderChild(view: View): Unit = {
    import io.udash.wrappers.jquery.jQ
    val jqChild = jQ(child)

    jqChild
      .animate(Map[String, Any]("opacity" -> 0), 150, EasingFunction.swing,
        (el: Element) => {
          jqChild
            .html(if (view != null) view.getTemplate else null)
            .animate(Map[String, Any]("opacity" -> 1), 200)

          js.Dynamic.global.Prism.highlightAll()
        })
  }
}
