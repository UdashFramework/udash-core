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

  private def mergeViews(pathIterator: Iterator[View]): Opt[View] = {
    def renderChild(parent: View, child: View): Unit =
      parent match {
        case p: ContainerView =>
          p.renderChild(Some(child))
        case rest =>
          throw new RuntimeException(s"Only instances of ContainerView can render a child view! Check the states hierarchy of view $rest.")
      }

    pathIterator.nextOpt.setup(_.foreach { top =>
      views.append(top)
      pathIterator.foldLeft(top) { case (parent, child) =>
        renderChild(parent, child)
        views.append(child)
        child
      }
    })
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
   * B - renderChild(None); renderChild(E)<br/>
   * C - renderChild(None)<br/>
   * D - renderChild(None) // if D is a ContainerView <br/>
   * E - getTemplate(); renderChild(F)<br/>
   * F - getTemplate()<br/>
   *
   * @param subPathToLeave prefix of views hierarchy, which will not be removed
   * @param pathToAdd      views list, which will be added to hierarchy
   */
  def renderView(subPathToLeave: Iterator[View], pathToAdd: Iterable[View]): Unit = {
    val viewsToLeaveSize = findEqPrefix(subPathToLeave, views.iterator).size
    val rootView = views.applyOpt(viewsToLeaveSize - 1)
    //technically e.g. B from docs stays, but we run it through the algorithm anyway for proper children cleanup
    val unmodifiedViews = rootView.foldLeft(viewsToLeaveSize)((unmodified, _) => unmodified - 1)
    views.drop(unmodifiedViews).foreach { //doesn't mutate views
      case c: ContainerView => c.renderChild(None)
      case _ =>
    }
    views.trimEnd(views.length - unmodifiedViews)
    val rootViewToAttach = mergeViews(rootView.iterator ++ pathToAdd)
    if (rootView.isEmpty) {
      while (endpoint.firstChild != null) endpoint.removeChild(endpoint.firstChild)
      rootViewToAttach.foreach(_.getTemplate.applyTo(endpoint))
    }
  }
}
