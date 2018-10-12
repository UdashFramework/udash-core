package io.udash.logging

trait CrossLogger {
  def debug(message: String, params: Any*): Unit
  def debug(message: String, cause: Throwable): Unit

  def info(message: String, params: Any*): Unit
  def info(message: String, cause: Throwable): Unit

  def warn(message: String, params: Any*): Unit
  def warn(message: String, cause: Throwable): Unit

  def error(message: String, params: Any*): Unit
  def error(message: String, cause: Throwable): Unit
}
