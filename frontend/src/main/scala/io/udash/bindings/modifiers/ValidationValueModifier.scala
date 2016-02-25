package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.wrappers.jquery.{JQuery, jQ}
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic._

private[bindings] class ValidationValueModifier[T](property: ReadableProperty[T],
                                 initBuilder: Future[ValidationResult] => Element,
                                 completeBuilder: ValidationResult => Element,
                                 errorBuilder: Throwable => Element)(implicit ec: ExecutionContext) extends Modifier[dom.Element] {

  override def applyTo(t: dom.Element): Unit = {
    val root = jQ(t)
    var element: JQuery = null

    def rebuild[R](result: R, builder: R => Element) = {
      val oldEl = element
      element = jQ(builder.apply(result))
      if (oldEl == null) root.append(element)
      else oldEl.replaceWith(element)
    }

    val listener = (_: T) => {
      val valid: Future[ValidationResult] = property.isValid
      rebuild(valid, initBuilder)
      valid onComplete {
        case Success(result) => rebuild(result, completeBuilder)
        case Failure(errors) => rebuild(errors, errorBuilder)
      }
    }

    listener(property.get)
    property.listen(listener)
  }
}




