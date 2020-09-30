package io.udash.routing

import com.avsystem.commons._
import io.udash.core.Url
import io.udash.properties.MutableBufferRegistration
import io.udash.utils.Registration
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLAnchorElement, HashChangeEvent}
import org.scalajs.dom.{Element, Location}

import scala.scalajs.js

/** Provides information about current URL. */
trait UrlChangeProvider {
  /** Enables all required event listeners.
   * [[io.udash.Application]] initializes the provider automatically. */
  def initialize(): Unit

  /** Changes the URL part representing the frontend routing state. */
  def changeFragment(url: Url, replaceCurrent: Boolean = false): Unit

  /** Returns the URL part representing the frontend routing state. */
  def currentFragment: Url

  /** Registers listener for URL changes. */
  def onFragmentChange(callback: Url => Unit): Registration

  /** Changes the whole URL. */
  def changeUrl(url: Url): Unit = dom.window.location.assign(url.value)
}

/** Used for routing based on the URL part following # sign. */
final class WindowUrlFragmentChangeProvider extends UrlChangeProvider {

  import dom.window

  private val callbacks: js.Array[Url => Unit] = js.Array()

  override def initialize(): Unit = {
    window.onhashchange = (_: HashChangeEvent) => callbacks.foreach(_.apply(currentFragment))
  }

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback, Opt.Empty)
  }

  override def currentFragment: Url = Url(window.location.hash.stripPrefix("#"))

  override def changeFragment(url: Url, replaceCurrent: Boolean): Unit = {
    if (replaceCurrent) window.location.replace(window.location.href.takeWhile(_ != '#') + "#" + url.value)
    else window.location.hash = url.value
  }
}

/**
 * Used for routing based on the URL path.
 * Don't forget to configure your web server to handle frontend routes. You may find "rewrite rules" mechanism useful.
 */
final class WindowUrlPathChangeProvider extends UrlChangeProvider {

  import dom.window
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
      event.target.opt
        .collect { case node: Node => node }
        .flatMap(Iterator.iterate(_)(_.parentNode).takeWhile(_ != null).collectFirstOpt { case a: HTMLAnchorElement => a })
        .filter(_.getAttribute("href") != null)
        .foreach { target =>
          val href = target.getAttribute("href")
          val location = window.location
          val newUrl = new JSUrl(href, location.toString)
          val (samePath, sameHash, sameOrigin) =
            (isSamePath(location, newUrl), isSameHash(location, newUrl), isSameOrigin(location, newUrl))
          if (!shouldIgnoreClick(event, target, href, samePath, sameHash, sameOrigin)) {
            if (!samePath) changeFragment(Url(href))
            event.preventDefault()
          }
        }
    })

    window.addEventListener("popstate", (_: PopStateEvent) => callbacks.foreach(_.apply(currentFragment)))
  }

  override def onFragmentChange(callback: Url => Unit): Registration = {
    callbacks.push(callback)
    new MutableBufferRegistration(callbacks, callback, Opt.Empty)
  }

  override def changeFragment(url: Url, replaceCurrent: Boolean): Unit = {
    (null, "", url.value) |> (
      if (replaceCurrent) window.history.replaceState(_: js.Any, _: String, _: String)
      else window.history.pushState(_: js.Any, _: String, _: String)
      ).tupled
    val withoutHash = Url(url.value.takeWhile(_ != '#'))
    callbacks.foreach(_.apply(withoutHash))
  }

  override def currentFragment: Url =
    Url(window.history.state.opt.map(_.asInstanceOf[js.Dynamic].url.toString).getOrElse(window.location.pathname))
}