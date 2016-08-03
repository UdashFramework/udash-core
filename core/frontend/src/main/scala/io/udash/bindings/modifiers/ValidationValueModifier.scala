package io.udash.bindings.modifiers

import io.udash.StrictLogging
import io.udash.bindings.Bindings
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalatags.generic._

private[bindings] class ValidationValueModifier[T](property: ReadableProperty[T],
                                 initBuilder: Option[Future[ValidationResult] => Seq[Element]],
                                 completeBuilder: ValidationResult => Seq[Element],
                                 errorBuilder: Option[Throwable => Seq[Element]])(implicit ec: ExecutionContext) extends Modifier[dom.Element] with Bindings with StrictLogging {

  override def applyTo(root: dom.Element): Unit = {
    var elements: Seq[Element] = null

    def rebuild[R](result: R, builder: R => Seq[Element]) = {
      val oldEls = elements
      elements = builder.apply(result)
      if (elements.isEmpty) elements = emptyStringNode()
      root.replaceChildren(oldEls, elements)
    }

    val listener = (_: T) => {
      val valid: Future[ValidationResult] = property.isValid
      initBuilder.foreach(b => rebuild(valid, b))
      valid onComplete {
        case Success(result) => rebuild(result, completeBuilder)
        case Failure(error) =>
          logger.error("Validation failed!")
          error.printStackTrace()

          errorBuilder.foreach(b => rebuild(error, b))
      }
    }

    listener(property.get)
    property.listen(listener)
  }
}




