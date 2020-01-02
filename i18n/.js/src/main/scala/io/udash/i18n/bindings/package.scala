package io.udash.i18n

import org.scalajs.dom._

import scala.scalajs.js.{JavaScriptException, SyntaxError}
import scala.util.{Failure, Success, Try}

package object bindings {
  def parseTranslation(rawHtml: Boolean, text: String): Seq[Node] =
    if (rawHtml) Try {
      val wrapper = document.createElement("div")
      wrapper.innerHTML = text
      wrapper.childNodes
    } match {
      case Success(children) if children.length > 0 =>
        (0 until children.length).map(children.item)
      case Success(_) | Failure(JavaScriptException(_: SyntaxError)) =>
        Seq(document.createTextNode(text))
    } else Seq(document.createTextNode(text))
}
