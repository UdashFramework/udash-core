package io.udash.guide

import com.typesafe.scalalogging.LazyLogging
import com.avsystem.commons.spring.HoconBeanDefinitionReader
import com.typesafe.config.{Config, ConfigFactory}
import io.udash.guide.server.ApplicationServer
import org.springframework.context.support.GenericApplicationContext

object Launcher extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime

    val ctx = createApplicationContext()
    ctx.getBean(classOf[ApplicationServer]).start()
    
    val duration: Long = (System.nanoTime - startTime) / 1000000000
    logger.info(s"Udash Homepage & Dev's Guide started in ${duration}s.")
  }

  def createApplicationContext(): GenericApplicationContext = {
    val ctx = new GenericApplicationContext()
    val bdr = new HoconBeanDefinitionReader(ctx)
    bdr.loadBeanDefinitions(ConfigFactory.load("beans.conf"))
    ctx.refresh()
    ctx
  }
}
