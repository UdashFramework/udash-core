package io.udash
package rpc.utils

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class TimeoutConfig(sendRetryTimeout: FiniteDuration, sendRetriesLimit: Int, callResponseTimeout: FiniteDuration)
object TimeoutConfig {
  val Default = TimeoutConfig(500 millis, 60, 30 seconds)
}
