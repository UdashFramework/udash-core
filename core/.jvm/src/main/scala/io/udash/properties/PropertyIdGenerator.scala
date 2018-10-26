package io.udash.properties

import java.util.concurrent.atomic.AtomicLong

private[properties] object PropertyIdGenerator {
  private val last = new AtomicLong(-1)

  private[properties] def next(): PropertyId = {
    PropertyId(last.incrementAndGet())
  }
}