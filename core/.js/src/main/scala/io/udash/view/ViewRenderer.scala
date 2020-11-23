package io.udash.view

import com.avsystem.commons._
import io.udash.core.{ContainerView, View}
import io.udash.utils.FilteringUtils._
import org.scalajs.dom.Element

import scala.scalajs.js

/**
 * ViewRenderer is used to provide mechanism to render nested [[View]] within provided [[rootElement]].
 */
private[udash] class ViewRenderer(rootElement: => Element) {
  private lazy val endpoint = rootElement
  private val views: MBuffer[View] = js.Array[View]()

  private def mergeViews(path: Iterator[View]): Option[View] = {
    def renderChild(parent: View, child: Option[View]): Unit =
      parent match {
        case p: ContainerView =>
          p.renderChild(child)
        case rest =>
          throw new RuntimeException(s"Only instances of ContainerView can render a child view! Check the states hierarchy of view $rest.")
      }
    path.nextOpt.setup(_.foreach { top =>
      val lastElement = path.fold(top) { case (parent, child) =>
        renderChild(parent, Some(child))
        views.append(parent)
        child
      }
      views.append(lastElement)
    }).toOption
  }

  /**
   * Updates views hierarchy.
   * <br/><br/>
   * Example: <br/>
   * Current views: A -> B -> C -> D <br/>
   * subPathToLeave: A -> B <br/>
   * pathToAdd: E -> F <br/>
   * <br/>
   * Calls:<br/>
   * A - nothing<br/>
   * B - renderChild(E)<br/>
   * E - getTemplate(); renderChild(F)<br/>
   * F - getTemplate()<br/>
   *
   * @param subPathToLeave prefix of views hierarchy, which will not be removed
   * @param pathToAdd      views list, which will be added to hierarchy
   */
  def renderView(subPathToLeave: Iterator[View], pathToAdd: Iterable[View]): Unit = {
    //technically e.g. B from docs is left in the hierarchy, but we run it through the algorithm for proper cleanup
    val unmodifiedViews = findEqPrefix(subPathToLeave, views.iterator).size - 1
    views.drop(unmodifiedViews).foreach {
      case c: ContainerView => c.renderChild(None)
      case _ =>
    }
    val rootView = views.applyOpt(unmodifiedViews)
    views.takeInPlace(unmodifiedViews)
    val rootViewToAttach = mergeViews(rootView.iterator ++ pathToAdd.iterator)
    if (rootView.isEmpty) {
      while (endpoint.firstChild != null) endpoint.removeChild(endpoint.firstChild)
      rootViewToAttach.foreach(_.getTemplate.applyTo(endpoint))
    }
  }
}
