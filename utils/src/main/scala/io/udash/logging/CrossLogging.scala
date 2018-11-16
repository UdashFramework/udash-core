package io.udash.logging

trait CrossLogging {
  protected def logger: CrossLogger = new UdashLogger(this.getClass)
}
