package io.udash.testing

import io.udash.i18n.Translated
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.Future

trait UdashSharedTest extends WordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfter {
  def getTranslatedString(tr: Future[Translated]): String =
    tr.value.get.get.string
}
