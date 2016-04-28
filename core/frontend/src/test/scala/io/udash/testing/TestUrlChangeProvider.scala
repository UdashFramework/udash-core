package io.udash.testing

import io.udash._

import scala.collection.mutable

class TestUrlChangeProvider(init: Url) extends UrlChangeProvider {
  var currUrl: Url = init
  var urlsHistory: mutable.ArrayBuffer[Url] = mutable.ArrayBuffer.empty
  val changeListeners: mutable.Set[(Url) => Unit] = mutable.Set.empty

  override def changeFragment(url: Url): Unit = {
    currUrl = url
    urlsHistory.append(url)
    changeListeners.foreach(_(url))
  }

  override def changeUrl(url: String): Unit = changeFragment(Url(url))

  override def currentFragment: Url = currUrl

  override def onFragmentChange(callback: (Url) => Unit): Unit = changeListeners.add(callback)
}
