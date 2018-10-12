package io.udash.properties

import scala.annotation.tailrec
import scala.concurrent.Future

trait ValidationError {
  def message: String
}
case class DefaultValidationError(override val message: String) extends ValidationError

sealed trait ValidationResult
case object Valid extends ValidationResult
case class Invalid[ErrorType <: ValidationError](errors: Seq[ErrorType]) extends ValidationResult
object Invalid {
  def apply[ErrorType <: ValidationError](error: ErrorType, errors: ErrorType*): Invalid[ErrorType] =
    Invalid(error +: errors.toSeq)

  def apply(error: String, errors: String*): Invalid[ValidationError] =
    this((error +: errors).map(DefaultValidationError))
}

trait Validator[ArgumentType] {
  def apply(element: ArgumentType): Future[ValidationResult]
}

object Validator {
  class FunctionValidator[ArgumentType](f: (ArgumentType) => ValidationResult) extends Validator[ArgumentType] {
    import scala.concurrent.ExecutionContext.Implicits.global

    override def apply(element: ArgumentType): Future[ValidationResult] =
      Future(f(element))
  }

  def apply[ArgumentType](f: (ArgumentType) => ValidationResult): Validator[ArgumentType] =
    new FunctionValidator(f)

  implicit class FutureOps[T](private val future: Future[T]) extends AnyVal {
    import scala.concurrent.ExecutionContext.Implicits.global
    def foldValidationResult(implicit ev: T =:= Seq[ValidationResult]): Future[ValidationResult] = {
      @tailrec
      def reduce(acc: Seq[ValidationError], results: Seq[ValidationResult]): ValidationResult = results match {
        case Seq() =>
          if (acc.isEmpty) Valid
          else Invalid(acc)
        case Seq(Valid, tl@_*) => reduce(acc, tl)
        case Seq(Invalid(errors), tl@_*) => reduce(acc ++ errors, tl)
      }

      future.map(s => reduce(Nil, s))
    }
  }
}