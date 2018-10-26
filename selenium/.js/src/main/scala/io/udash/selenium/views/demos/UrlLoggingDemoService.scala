package io.udash.selenium.views.demos

import io.udash._

import scala.collection.mutable.ListBuffer

object UrlLoggingDemoService {
  val enabled = Property(false)
  private val history = SeqProperty[(String, Option[String])](ListBuffer.empty)
  enabled.listen(b => if (!b) history.set(ListBuffer.empty))

  def log(url: String, referrer: Option[String]): Unit =
    if (enabled.get) history.append((url, referrer))

  def loadHistory: ReadableSeqProperty[(String, Option[String])] = history.readable
}
