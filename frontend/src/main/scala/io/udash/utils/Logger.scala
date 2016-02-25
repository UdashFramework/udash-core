package io.udash.utils

import org.scalajs.dom.Console

import scala.scalajs.js.Dynamic.global

/** Global JS logger. */
object Logger {
  private val console: Console = global.console.asInstanceOf[Console]

  def info(message: String): Unit = console.info(message)

  def warn(message: String): Unit = console.warn(message)

  def error(message: String): Unit = console.error(message)

  def log(message: String): Unit = console.log(message)
}

/** Provides `logger` reference to [[io.udash.utils.Logger]]. */
trait StrictLogging {
  val logger = Logger
}
