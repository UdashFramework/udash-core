package io.udash.utils

import org.scalajs.dom.Console

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

trait Logger {
  def info(message: String, params: js.Any*): Unit

  def warn(message: String, params: js.Any*): Unit

  def error(message: String, params: js.Any*): Unit

  def log(message: String, params: js.Any*): Unit
}

/**
  * Global JS logger.
  */
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
trait StrictLogging {
  val logger: Logger = ConsoleLogger
}
