package io.udash.web.guide.demos.rpc

import com.typesafe.scalalogging.LazyLogging
import io.udash.web.guide.demos.rpc.GenCodecServerRPC.{DemoCaseClass, DemoClass, Fruit}

import scala.concurrent.Future

class GenCodecServer extends GenCodecServerRPC with LazyLogging {
  import io.udash.web.Implicits._

  override def sendInt(el: Int): Future[Int] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendDouble(el: Double): Future[Double] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendSealedTrait(el: Fruit): Future[Fruit] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendString(el: String): Future[String] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendMap(el: Map[String, Int]): Future[Map[String, Int]] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendClass(el: DemoClass): Future[DemoClass] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendSeq(el: Seq[String]): Future[Seq[String]] = Future {
    logger.debug(el.toString)
    el
  }

  override def sendCaseClass(el: DemoCaseClass): Future[DemoCaseClass] = Future {
    logger.debug(el.toString)
    el
  }
}
