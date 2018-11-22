package io.udash
package rpc.utils

import scala.concurrent.duration.FiniteDuration

case class CallTimeout(callTimeout: FiniteDuration) extends RuntimeException(s"Response missing after $callTimeout.")