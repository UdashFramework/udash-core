package io.udash.testing

import io.udash._

import scala.collection.mutable
import scala.util.matching.Regex

class TestRoutingRegistry extends RoutingRegistry[TestState] {
  val classStatePattern: Regex = "/(\\w+)/(\\d+)".r

  var urlsHistory: mutable.ArrayBuffer[Url] = mutable.ArrayBuffer.empty
  var statesHistory: mutable.ArrayBuffer[TestState] = mutable.ArrayBuffer.empty

  override def matchUrl(url: Url): TestState = {
    urlsHistory.append(url)
    url.value match {
      case "/" => ObjectState
      case "/root" => RootState(None)
      case "/root" / v => RootState(Some(v.toInt))
      case "/exception" => ThrowExceptionState
      case "/next" => NextObjectState
      case classStatePattern(arg: String, arg2: String) => ClassState(arg, Integer.parseInt(arg2))
      case _ => ErrorState
    }
  }

  override def matchState(state: TestState): Url = {
    statesHistory.append(state)
    Url(state match {
      case ObjectState => "/"
      case RootState(None) => "/root"
      case RootState(Some(v)) => s"/root/$v"
      case ThrowExceptionState => "/exception"
      case NextObjectState => "/next"
      case ClassState(arg, arg2) => s"/$arg/$arg2"
      case _ => ""
    })
  }
}