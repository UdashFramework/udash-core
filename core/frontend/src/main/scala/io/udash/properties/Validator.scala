package io.udash.properties

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

trait ValidationError {
  def message: String
}
object ValidationError {
  implicit val propertyCreatorValidationError: PropertyCreator[ValidationError] = PropertyCreator.propertyCreator[ValidationError]
  implicit val propertyCreatorSValidationError: PropertyCreator[Seq[ValidationError]] = PropertyCreator.propertyCreator[Seq[ValidationError]]
  implicit val propertyCreatorOValidationError: PropertyCreator[Option[ValidationError]] = PropertyCreator.propertyCreator[Option[ValidationError]]
}

case class DefaultValidationError(override val message: String) extends ValidationError

sealed trait ValidationResult
object ValidationResult {
  implicit val propertyCreatorValidationResult: PropertyCreator[ValidationResult] = PropertyCreator.propertyCreator[ValidationResult]
  implicit val propertyCreatorSValidationResult: PropertyCreator[Seq[ValidationResult]] = PropertyCreator.propertyCreator[Seq[ValidationResult]]
  implicit val propertyCreatorOValidationResult: PropertyCreator[Option[ValidationResult]] = PropertyCreator.propertyCreator[Option[ValidationResult]]
}

case object Valid extends ValidationResult
case class Invalid[ErrorType <: ValidationError](errors: Seq[ErrorType]) extends ValidationResult
object Invalid {
  def apply[ErrorType <: ValidationError](error: ErrorType, errors: ErrorType*): Invalid[ErrorType] =
    Invalid(error +: errors.toSeq)

  def apply(error: String, errors: String*): Invalid[ValidationError] =
    this((error +: errors).map(DefaultValidationError))
}

trait Validator[ArgumentType] {
  def apply(element: ArgumentType)(implicit ec: ExecutionContext): Future[ValidationResult]
}

object Validator {
  class FunctionValidator[ArgumentType](f: (ArgumentType) => ValidationResult) extends Validator[ArgumentType] {
    override def apply(element: ArgumentType)(implicit ec: ExecutionContext): Future[ValidationResult] =
      Future(f(element))(ec)
  }

  def apply[ArgumentType](f: (ArgumentType) => ValidationResult): Validator[ArgumentType] =
    new FunctionValidator(f)

  implicit class FutureOps[T](private val future: Future[T]) extends AnyVal {
    def foldValidationResult(implicit ev: T =:= Seq[ValidationResult], ec: ExecutionContext): Future[ValidationResult] = {
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