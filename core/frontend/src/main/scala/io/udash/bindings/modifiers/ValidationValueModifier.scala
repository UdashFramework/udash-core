package io.udash.bindings.modifiers

import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic._

private[bindings] class ValidationValueModifier[T](property: ReadableProperty[T],
                                 initBuilder: Future[ValidationResult] => Seq[Element],
                                 completeBuilder: ValidationResult => Seq[Element],
                                 errorBuilder: Throwable => Seq[Element])(implicit ec: ExecutionContext) extends Modifier[dom.Element] {

  override def applyTo(root: dom.Element): Unit = {
    var elements: Seq[Element] = null

    def rebuild[R](result: R, builder: R => Seq[Element]) = {
      val oldEls = elements
      elements = builder.apply(result)
      if (oldEls == null) elements.foreach(root.appendChild)
      else {
        oldEls.zip(elements).foreach { case (old, fresh) => root.replaceChild(fresh, old) }
        oldEls.drop(elements.size).foreach(root.removeChild)
        elements.drop(oldEls.size - 1).sliding(2).foreach(s =>
          if (s.size == 2) root.insertBefore(s(1), s(0).nextSibling)
        )
      }
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




