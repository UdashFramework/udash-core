package io.udash.bindings.modifiers

import io.udash.StrictLogging
import io.udash.bindings.Bindings._
import io.udash.properties._
import io.udash.properties.single.ReadableProperty
import org.scalajs.dom._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private[bindings]
class ValidationValueModifier[T](property: ReadableProperty[T],
                                 initBuilder: Option[(Future[ValidationResult], Binding => Binding) => Seq[Element]],
                                 completeBuilder: (ValidationResult, Binding => Binding) => Seq[Element],
                                 errorBuilder: Option[(Throwable, Binding => Binding) => Seq[Element]])
                                (implicit ec: ExecutionContext)
  extends Binding with StrictLogging {

  def this(property: ReadableProperty[T], initBuilder: Option[Future[ValidationResult] => Seq[Element]],
           completeBuilder: ValidationResult => Seq[Element], errorBuilder: Option[Throwable => Seq[Element]])
          (implicit ec: ExecutionContext) = {
    this(property, initBuilder.map(c => (d, _) => c(d)), (d, _) => completeBuilder(d), errorBuilder.map(c => (d, _) => c(d)))
  }

  override def applyTo(root: Element): Unit = {
    var elements: Seq[Element] = Seq.empty

    def rebuild[R](result: R, builder: (R, Binding => Binding) => Seq[Element]) = {
      killNestedBindings()

      val oldEls = elements
      elements = builder.apply(result, nestedInterceptor)
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
          errorBuilder.foreach(b => rebuild(error, b))
      }
    }

    listener(property.get)
    propertyListeners += property.listen(listener)
  }
}




