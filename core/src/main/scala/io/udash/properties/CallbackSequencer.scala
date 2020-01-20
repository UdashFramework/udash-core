package io.udash.properties

import com.github.ghik.silencer.silent

import scala.collection.mutable

/**
 * <b>Note: It can be used only in one-thread environment!</b>
 *
 * This sequencer is used in order to fire callback listeners ONCE during making many updates to [[io.udash.properties.single.Property]].
 * Property implementation uses this CallbackSequencer in order to queue callbacks and invoke them after
 * running commit().
 * In code you should use sequence method to group operation over the Property.
 */
final class CallbackSequencer {
  type Id = String

  private var starts: Int = 0
  private val queue: mutable.LinkedHashMap[Id, () => Any] = mutable.LinkedHashMap.empty

  private def start(): Unit =
    starts += 1

  private def end(): Unit = {
    starts -= 1
  }

  private def commit(): Unit = {
    if (starts == 1) {
      val used = mutable.HashSet[Id]()
      while (queue.nonEmpty) {
        queue.retain { case (id, callback) =>
          if (used.add(id)) {
            callback()
          }
          false //removes
        }: @silent("deprecated")
      }
    }
  }

  def queue(id: Id, fireListeners: () => Any): Unit = {
    sequence(queue += id -> fireListeners)
  }

  def sequence(code: => Any): Unit = {
    start()
    try {
      code
      commit()
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