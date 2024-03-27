package io.udash
package bootstrap.form

import com.avsystem.commons._

trait Validation extends Any {
  def onComplete[U](f: Try[ValidationResult] => U): Unit
}

object Validation {
  implicit final class DirectValidation(private val result: ValidationResult) extends AnyVal with Validation {
    override def onComplete[U](f: Try[ValidationResult] => U): Unit = f(Success(result))
  }

  implicit final class FutureValidation(private val result: Future[ValidationResult]) extends AnyVal with Validation {
    override def onComplete[U](f: Try[ValidationResult] => U): Unit = result.onCompleteNow(f)
  }
}

trait ValidationError {
  def message: String
}
final case class DefaultValidationError(override val message: String) extends ValidationError

sealed trait ValidationResult
case object Valid extends ValidationResult
final case class Invalid[ErrorType <: ValidationError](errors: Seq[ErrorType]) extends ValidationResult
object Invalid {
  def apply[ErrorType <: ValidationError](error: ErrorType, errors: ErrorType*): Invalid[ErrorType] =
    Invalid(error +: errors)

  def apply(error: String, errors: String*): Invalid[ValidationError] =
    this ((error +: errors).map(DefaultValidationError.apply))
}

trait Validator[-ArgumentType] {
  def apply(element: ArgumentType): Validation
}

object Validator {
  final val Default: Validator[Any] = _ => Valid

  implicit final class FutureValidationOps[T](private val future: Future[Seq[ValidationResult]]) extends AnyVal {
    def foldValidationResult: Future[ValidationResult] = {
      future.mapNow { s =>
        val errors = s.iterator.flatCollect { case Invalid(results) => results }
        if (errors.isEmpty) Valid else Invalid(errors.toSeq)
      }
    }
  }
}