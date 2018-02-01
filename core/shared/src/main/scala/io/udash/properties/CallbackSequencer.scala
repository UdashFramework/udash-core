package io.udash.properties

import scala.collection.mutable

/**
 * <b>Note: It can be used only in one-thread environment!</b>
  *
 * This sequencer is used in order to fire callback listeners ONCE during making many updates to [[io.udash.properties.single.Property]].
 * Property implementation uses this CallbackSequencer in order to queue callbacks and invoke them after
 * running commit().
 * In code you should use sequence method to group operation over the Property.
 */
class CallbackSequencer {
  type Id = String

  private var starts: Int = 0
  private val queue: mutable.Buffer[(Id, () => Any)] = CrossCollections.createArray

  private def start(): Unit =
    starts += 1

  private def end(): Unit = {
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

object CallbackSequencer {
  private val tl: ThreadLocal[CallbackSequencer] = new ThreadLocal[CallbackSequencer]

  def apply(): CallbackSequencer = {
    if (tl.get() == null) tl.set(new CallbackSequencer)
    tl.get()
  }
}