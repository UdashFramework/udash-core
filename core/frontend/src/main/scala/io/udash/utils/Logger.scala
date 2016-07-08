package io.udash.utils

import org.scalajs.dom.Console

import scala.scalajs.js.Dynamic.global

trait Logger {
  def info(message: String, params: String*): Unit

  def warn(message: String, params: String*): Unit

  def error(message: String, params: String*): Unit

  def log(message: String, params: String*): Unit
}

/**
  * Global JS logger.
  */
private object ConsoleLogger extends Logger {
  private val console: Console = global.console.asInstanceOf[Console]

  def info(message: String, params: String*): Unit = console.info(message, params)

  def warn(message: String, params: String*): Unit = console.warn(message, params)

  def error(message: String, params: String*): Unit = console.error(message, params)

  def log(message: String, params: String*): Unit = console.log(message, params)
}

/**
  * Provides `logger` reference to io.udash.utils.ConsoleLogger.
  */
trait StrictLogging {
  val logger: Logger = ConsoleLogger
}
