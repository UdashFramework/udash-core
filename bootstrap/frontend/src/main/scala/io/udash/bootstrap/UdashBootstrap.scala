package io.udash.bootstrap

import scala.util.Random

object UdashBootstrap {

  /**
    * Generates unique element ID
    */
  def newId(): String = s"bs-auto-${BigInt.probablePrime(100, Random).toString(36)}"

}
