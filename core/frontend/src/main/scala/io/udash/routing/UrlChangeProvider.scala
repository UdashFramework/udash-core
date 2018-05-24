package io.udash.routing

import io.udash.core.Url
import io.udash.properties.MutableBufferRegistration
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HashChangeEvent

import scala.scalajs.js

/** Provides information about current URL. */
trait UrlChangeProvider {
  /** Changes the URL part representing the frontend routing state. */
  def changeFragment(url: Url): Unit

  /** Returns the URL part representing the frontend routing state. */
  def currentFragment: Url

  /** Registers listener for URL changes. */
  def onFragmentChange(callback: Url => Unit): Registration

  /** Changes the whole URL. */
  def changeUrl(url: String): Unit
}

/** Used for routing based on the URL part following # sign. */
object WindowUrlFragmentChangeProvider extends UrlChangeProvider {
  import dom.{document, window}

  private val callbacks: js.Array[Url => Unit] = js.Array()

  window.onhashchange = (_: HashChangeEvent) => {
    callbacks.foreach(_.apply(currentFragment))
  }

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback)
  }
  override def currentFragment: Url = Url(document.location.hash.stripPrefix("#"))
  override def changeFragment(url: Url): Unit = document.location.hash = url.value
  override def changeUrl(url: String): Unit = document.location.replace(url)
}

/**
  * Used for routing based on the URL path.
  * Don't forget to configure your web server to handle frontend routes. You may find "rewrite rules" mechanism useful.
  */
object WindowUrlPathChangeProvider extends UrlChangeProvider {
  import dom.{document, window}
  import org.scalajs.dom.experimental.{URL => JSUrl}
  import org.scalajs.dom.raw.{MouseEvent, Node, PopStateEvent}

  private val callbacks: js.Array[Url => Unit] = js.Array()

  window.document.addEventListener("click", (event: MouseEvent) => {
    def findLink(el: Node): Element =
      if (el == null) null
      else if (el.nodeName.toLowerCase() == "a") el.asInstanceOf[Element]
      else findLink(el.parentNode)

    def isSameOrigin(href: String): Boolean = {
      val loc = window.location
      val url = new JSUrl(href, loc.toString)

      loc.protocol == url.protocol && loc.hostname == url.hostname && loc.port == url.port
    }

    def isSamePath(href: String): Boolean = {
      val loc = window.location
      val url = new JSUrl(href, loc.toString)

      loc.pathname == url.pathname && loc.search == url.search
    }

    val target = findLink(event.target.asInstanceOf[Node])
    if (target != null) {
      val href = target.getAttribute("href")
      val ignore = event.button != 0 || event.metaKey || event.ctrlKey || event.shiftKey ||
        event.defaultPrevented || target.hasAttribute("download") ||
        target.getAttribute("rel") == "external" || href.contains("mailto:") || !isSameOrigin(href)

      if (!ignore) {
        if (!isSamePath(href)) {
          val url = Url(href)
          changeFragment(url)
        }
        event.preventDefault()
      }
    }
  })

  window.addEventListener("popstate", (_: PopStateEvent) => callbacks.foreach(_.apply(currentFragment)))

  override def changeUrl(url: String): Unit = document.location.replace(url)

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback)
  }

  override def changeFragment(url: Url): Unit = {
    window.history.pushState(js.Dynamic.literal(url = url.value), "", url.value)
    callbacks.foreach(_.apply(url))
  }

  override def currentFragment: Url =
    Url(Option(window.history.state).map(_.asInstanceOf[js.Dynamic].url.asInstanceOf[String]).getOrElse(window.location.pathname))
}