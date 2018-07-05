package io.udash.routing

import com.avsystem.commons.misc.Opt
import io.udash.core.Url
import io.udash.properties.MutableBufferRegistration
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom.raw.HashChangeEvent
import org.scalajs.dom.{Element, Location}

import scala.scalajs.js

/** Provides information about current URL. */
trait UrlChangeProvider {
  /** Enables all required event listeners.
    * [[io.udash.Application]] initializes the provider automatically. */
  def initialize(): Unit

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

  override def initialize(): Unit = {
    window.onhashchange = (_: HashChangeEvent) => callbacks.foreach(_.apply(currentFragment))
  }

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback, Opt.empty)
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

  @inline
  private def isSameOrigin(loc: Location, url: JSUrl): Boolean =
    loc.protocol == url.protocol && loc.hostname == url.hostname && loc.port == url.port

  @inline
  private def isSamePath(loc: Location, url: JSUrl): Boolean =
    loc.pathname == url.pathname && loc.search == url.search

  @inline
  private def isSameHash(loc: Location, url: JSUrl): Boolean =
    loc.hash == url.hash

  @inline
  private def shouldIgnoreClick(
    event: MouseEvent, target: Element, href: String,
    samePath: Boolean, sameHash: Boolean, sameOrigin: Boolean
  ): Boolean = {
    // handle only links in the same browser card
    event.button != 0 || event.metaKey || event.ctrlKey || event.shiftKey ||
      // ignore click if default already prevented
      event.defaultPrevented ||
      // ignore special link types
      target.hasAttribute("download") || target.getAttribute("rel") == "external" || href.contains("mailto:") ||
      // ignore if links to different domain
      !sameOrigin ||
      // ignore if only the URL fragment changed, but path is the same
      (samePath && !sameHash)
  }

  override def initialize(): Unit = {
    window.document.addEventListener("click", (event: MouseEvent) => {
      def findLink(el: Node): Element =
        if (el == null) null
        else if (el.nodeName.toLowerCase() == "a") el.asInstanceOf[Element]
        else findLink(el.parentNode)

      val target = findLink(event.target.asInstanceOf[Node])
      if (target != null) {
        val href = target.getAttribute("href")
        val location = window.location
        val newUrl = new JSUrl(href, location.toString)
        val (samePath, sameHash, sameOrigin) =
          (isSamePath(location, newUrl), isSameHash(location, newUrl), isSameOrigin(location, newUrl))
        val ignore = shouldIgnoreClick(event, target, href, samePath, sameHash, sameOrigin)

        if (!ignore) {
          if (!samePath) {
            val url = Url(href)
            changeFragment(url)
          }
          event.preventDefault()
        }
      }
    })

    window.addEventListener("popstate", (_: PopStateEvent) => callbacks.foreach(_.apply(currentFragment)))
  }

  override def changeUrl(url: String): Unit = document.location.replace(url)

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback, Opt.empty)
  }

  override def changeFragment(url: Url): Unit = {
    window.history.pushState(js.Dynamic.literal(url = url.value), "", url.value)
    val withoutHash = Url(url.value.takeWhile(_ != '#'))
    callbacks.foreach(_.apply(withoutHash))
  }

  override def currentFragment: Url =
    Url(Option(window.history.state).map(_.asInstanceOf[js.Dynamic].url.asInstanceOf[String]).getOrElse(window.location.pathname))
}