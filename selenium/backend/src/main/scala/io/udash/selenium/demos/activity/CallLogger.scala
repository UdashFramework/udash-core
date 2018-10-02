package io.udash.selenium.demos.activity

import io.udash.selenium.rpc.demos.activity.Call

import scala.collection.mutable.ListBuffer

class CallLogger {
  private val _calls = ListBuffer.empty[Call]

  def append(call: Call): Unit = _calls.synchronized {
    _calls += call
    if (_calls.size > 20) _calls.remove(0, _calls.size-20)
  }

  def calls: List[Call] = _calls.synchronized {
    _calls.toList
  }
}