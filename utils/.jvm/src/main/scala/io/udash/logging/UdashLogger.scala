package io.udash.logging

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

class UdashLogger extends CrossLogger {
  private val internalLogger = Logger(LoggerFactory.getLogger(getClass.getName))

  override def debug(message: String, params: Any*): Unit =
    internalLogger.debug(message, params)

  override def debug(message: String, cause: Throwable): Unit =
    internalLogger.debug(message, cause)

  override def info(message: String, params: Any*): Unit =
    internalLogger.info(message, params)

  override def info(message: String, cause: Throwable): Unit =
    internalLogger.info(message, cause)

  override def warn(message: String, params: Any*): Unit =
    internalLogger.warn(message, params)

  override def warn(message: String, cause: Throwable): Unit =
    internalLogger.warn(message, cause)

  override def error(message: String, params: Any*): Unit =
    internalLogger.error(message, params)

  override def error(message: String, cause: Throwable): Unit =
    internalLogger.error(message, cause)
}
