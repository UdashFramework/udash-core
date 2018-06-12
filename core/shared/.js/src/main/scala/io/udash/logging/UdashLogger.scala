package io.udash.logging

import org.scalajs.dom.Console

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global

class UdashLogger extends CrossLogger {
  private val console: Console = global.console.asInstanceOf[Console]

  private def printWithParams(method: (js.Any, Seq[js.Any]) => Unit)(message: String, params: Any*): Unit =
    method(message, params.map(_.asInstanceOf[js.Any]))

  private def printWithCause(method: (js.Any, Seq[js.Any]) => Unit)(message: String, cause: Throwable): Unit = {
    method(message, Seq.empty)
    if (cause.getMessage != null) method(cause.getMessage, Seq.empty)
    method(cause.getStackTrace.map(_.toString).mkString("\n\t"), Seq.empty)
  }

  def debug(message: String, params: Any*): Unit =
    printWithParams(console.log)(message, params: _*)

  def debug(message: String, cause: Throwable): Unit =
    printWithCause(console.log)(message, cause)

  def info(message: String, params: Any*): Unit =
    printWithParams(console.info)(message, params: _*)

  def info(message: String, cause: Throwable): Unit =
    printWithCause(console.info)(message, cause)

  def warn(message: String, params: Any*): Unit =
    printWithParams(console.warn)(message, params: _*)

  def warn(message: String, cause: Throwable): Unit =
    printWithCause(console.warn)(message, cause)

  def error(message: String, params: Any*): Unit =
    printWithParams(console.error)(message, params: _*)

  def error(message: String, cause: Throwable): Unit =
    printWithCause(console.error)(message, cause)
}