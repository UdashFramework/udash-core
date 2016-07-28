package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic._

private[bindings] class ValidationValueModifier[T](property: ReadableProperty[T],
                                 initBuilder: Future[ValidationResult] => Element,
                                 completeBuilder: ValidationResult => Element,
                                 errorBuilder: Throwable => Element)(implicit ec: ExecutionContext) extends Modifier[dom.Element] {

  override def applyTo(root: dom.Element): Unit = {
    var element: Element = null

    def rebuild[R](result: R, builder: R => Element) = {
      val oldEl = element
      element = builder.apply(result)
      if (oldEl == null) root.appendChild(element)
      else root.replaceChild(element, oldEl)
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




