package io.udash.properties

private[properties] object PropertyIdGenerator {
  private var last = -1L

  def next(): PropertyId = {
    last += 1
    PropertyId(last)
  }
}