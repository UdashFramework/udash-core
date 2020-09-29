package io.udash.routing

import io.udash._
import io.udash.testing._

import scala.collection.mutable.ListBuffer

class UrlLoggingTest extends AsyncUdashFrontendTest with TestRouting {
  "UrlLogging" should {
    "call logging impl on url change" in {
      val urlWithRef = ListBuffer.empty[(String, Option[String])]

      new TestViewFactory[TestState]: ViewFactory[_ <: TestState]

      initTestRouting(default = () => new TestViewFactory[TestState])
      val initUrl = "/"
      val urlProvider: TestUrlChangeProvider = new TestUrlChangeProvider(initUrl)
      val app = new Application[TestState](routing, vpRegistry, urlProvider) with UrlLogging[TestState] {
        override protected def log(url: String, referrer: Option[String]): Unit = {
          urlWithRef += ((url, referrer))
        }
      }
      app.run(emptyComponent())

      val urls = Seq("/", "/next", "/abc/1", "/next")
      val expected = (urls.head, Some("")) :: urls.sliding(2).map { case Seq(prev, current) => (current, Some(prev)) }.toList
      urls.foreach(str => app.goTo(routing.matchUrl(str)))
      retrying(urlWithRef.toList shouldBe expected)
    }
  }
}
