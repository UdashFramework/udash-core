package io.udash.properties

import io.udash.properties.single.Property

import scala.collection.mutable

/**
 * <b>Note: It can be used only in one-thread environment!</b>
  *
 * This sequencer is used in order to fire callback listeners ONCE during making many updates to [[Property]].
 * Property implementation uses this CallbackSequencer in order to queue callbacks and invoke them after
 * running commit().
 * In code you should use sequence method to group operation over the Property.
 */
object CallbackSequencer {
  type Id = String

  private var starts: Int = 0
  private var queue: mutable.ArrayBuffer[(Id, () => Any)] = mutable.ArrayBuffer()
  private var endCallbacks: mutable.ArrayBuffer[() => Any] = mutable.ArrayBuffer()

  private def start(): Unit =
    starts += 1

  private def end(): Unit = {
    if (starts == 1) {
      endCallbacks.foreach(c => c())
      endCallbacks.clear()
    }
    starts -= 1
  }

  private def commit(): Boolean = {
    if (starts == 1) {
      val used = mutable.HashSet[Id]()
      val waiting = queue.reverseIterator.collect {
        case (id, callback) if !used.contains(id) =>
          used += id
          callback
      }.toSeq.reverseIterator
      queue.clear()
      waiting.foreach(c => c())
      used.nonEmpty
    } else false
  }

  def queue(id: Id, fireListeners: () => Any): Unit = {
    if (starts == 0) fireListeners()
    else queue += Tuple2(id, fireListeners)
  }

  def finalCallback(finalListeners: () => Any): Unit = {
    if (starts == 0) finalListeners()
    else endCallbacks += finalListeners
  }

  def sequence(code: => Any): Unit = {
    start()
    try {
      code
      while (commit()) {}
    } finally {
      end()
    }
  }
}