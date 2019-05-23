package io.udash
package bootstrap.form

import com.avsystem.commons._

trait ValidationError {
  def message: String
}
final case class DefaultValidationError(override val message: String) extends ValidationError

sealed trait ValidationResult
case object Valid extends ValidationResult
final case class Invalid[ErrorType <: ValidationError](errors: Seq[ErrorType]) extends ValidationResult
object Invalid {
  def apply[ErrorType <: ValidationError](error: ErrorType, errors: ErrorType*): Invalid[ErrorType] =
    Invalid(error +: errors.toSeq)

  def apply(error: String, errors: String*): Invalid[ValidationError] =
    this ((error +: errors).map(DefaultValidationError))
}

trait Validator[-ArgumentType] {
  def apply(element: ArgumentType): Future[ValidationResult]
}

object Validator {
  final val Default: Validator[Any] = _ => Future.successful(Valid)

  final class FunctionValidator[ArgumentType](f: ArgumentType => ValidationResult) extends Validator[ArgumentType] {
    override def apply(element: ArgumentType): Future[ValidationResult] = f(element).evalFuture
  }

  def apply[ArgumentType](f: ArgumentType => ValidationResult): Validator[ArgumentType] =
    new FunctionValidator(f)

  implicit final class FutureValidationOps[T](private val future: Future[Seq[ValidationResult]]) extends AnyVal {
    def foldValidationResult: Future[ValidationResult] = {
      future.mapNow { s =>
        val errors = s.iterator.flatCollect { case Invalid(results) => results }
        if (errors.isEmpty) Valid else Invalid(errors.toSeq)
      }
    }
  }
}