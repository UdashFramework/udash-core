package io.udash.bootstrap

object UdashBootstrap {
  private var cid = -1

  /**
    * Generates unique element ID
    */
  def newId(): String = {
    cid += 1
    s"bs-auto-$cid"
  }
}
