package io.udash.routing

import io.udash.core.{Url, Window}
import org.scalajs.dom

/**
  * Provides information about current URL.
  */
trait UrlChangeProvider {
  /** Changes URL fragment following # to passed [[io.udash.core.Url]]. */
  def changeFragment(url: Url): Unit

  /** Returns URL fragment following #. */
  def currentFragment: Url

  /** Registers listener for URL fragment change. */
  def onFragmentChange(callback: (Url) => Unit): Unit

  /** Changes whole URL. */
  def changeUrl(url: String): Unit
}

object WindowUrlChangeProvider extends UrlChangeProvider {

  import dom.document

  private var callbacks: List[Url => Unit] = List()

  override def onFragmentChange(callback: Url => Unit): Unit = {
    callbacks = callback :: callbacks
  }

  def currentFragment: Url = Url(document.location.hash.stripPrefix("#"))

  Window.onFragmentChange(() => {
    callbacks.foreach(_.apply(currentFragment))
  })

  def changeFragment(url: Url): Unit = document.location.hash = url.value

  def changeUrl(url: String): Unit = document.location.replace(url)
}
