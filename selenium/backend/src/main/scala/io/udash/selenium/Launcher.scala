package io.udash.selenium

import com.typesafe.scalalogging.LazyLogging
import io.udash.selenium.server.ApplicationServer

object Launcher extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    new ApplicationServer(8888, "selenium/frontend/target/UdashStatics/WebContent").start()
    
    val duration: Long = (System.nanoTime - startTime) / 1000000000
    logger.info(s"Selenium Demos started in ${duration}s.")
  }
}
