package io.udash.utils

import org.scalajs.dom.Console

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

@deprecated("Use `io.udash.logging.CrossLogger` instead.", "0.6.0")
trait Logger {
  def info(message: String, params: js.Any*): Unit

  def warn(message: String, params: js.Any*): Unit

  def error(message: String, params: js.Any*): Unit

  def log(message: String, params: js.Any*): Unit
}

/**
  * Global JS logger.
  */
@deprecated("Use `io.udash.logging.CrossLogger` instead.", "0.6.0")
private object ConsoleLogger extends Logger {
  private val console: Console = global.console.asInstanceOf[Console]

  def info(message: String, params: js.Any*): Unit = console.info(message, params:_*)

  def warn(message: String, params: js.Any*): Unit = console.warn(message, params:_*)

  def error(message: String, params: js.Any*): Unit = console.error(message, params:_*)

  def log(message: String, params: js.Any*): Unit = console.log(message, params:_*)
}

/**
  * Provides `logger` reference to io.udash.utils.ConsoleLogger.
  */
@deprecated("Use `io.udash.logging.CrossLogging` instead.", "0.6.0")
trait StrictLogging {
  val logger: Logger = ConsoleLogger
}
