package io.udash.web

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import io.udash.web.server.ApplicationServer

object Launcher extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    createApplicationServer().start()
    
    val duration: Long = (System.nanoTime - startTime) / 1000000000
    logger.info(s"Udash Homepage & Dev's Guide started in ${duration}s.")
  }


  private[udash] def createApplicationServer(): ApplicationServer = {
    val serverConfig = ConfigFactory.load().getConfig("ui.server")
    new ApplicationServer(
      port = serverConfig.getInt("port"),
      homepageResourceBase = serverConfig.getString("homepageResourceBase"),
      guideResourceBase = serverConfig.getString("guideResourceBase")
    )
  }
}
