package io.udash.testing

import io.udash._
import io.udash.utils.{Registration, SetRegistration}

import scala.collection.mutable

class TestUrlChangeProvider(init: Url) extends UrlChangeProvider {
  var currUrl: Url = init
  var urlsHistory: mutable.ArrayBuffer[Url] = mutable.ArrayBuffer.empty
  val changeListeners: mutable.Set[Url => Unit] = mutable.Set.empty

  override def initialize(): Unit = ()

  override def changeFragment(url: Url, changeHistory: Boolean): Unit = {
    currUrl = url
    if (changeHistory || urlsHistory.isEmpty) urlsHistory.append(url)
    else urlsHistory.update(urlsHistory.length - 1, url)
    changeListeners.foreach(_(url))
  }

  override def changeUrl(url: Url): Unit = changeFragment(url)

  override def currentFragment: Url = currUrl

  override def onFragmentChange(callback: Url => Unit): Registration = {
    changeListeners.add(callback)
    new SetRegistration(changeListeners, callback)
  }
}
