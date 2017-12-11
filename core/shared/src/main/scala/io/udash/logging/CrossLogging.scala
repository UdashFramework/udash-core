package io.udash.logging

trait CrossLogging {
  def logger: CrossLogger = new UdashLogger
}
