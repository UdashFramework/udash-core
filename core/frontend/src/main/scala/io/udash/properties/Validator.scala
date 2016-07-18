package io.udash.properties

import scala.concurrent.{ExecutionContext, Future}

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

trait Validator[T] {
  def apply(element: T)(implicit ec: ExecutionContext): Future[ValidationResult]
}

object Validator {
  implicit class FutureOps[T](private val future: Future[T]) extends AnyVal {
    def foldValidationResult[ErrorType <: ValidationError](implicit ev: T =:= Seq[ValidationResult],
                                                           ec: ExecutionContext): Future[ValidationResult] = {
      future map {
        case s if s.forall(r => r == Valid) => Valid
        case s => Invalid(s.foldLeft(Seq[ValidationError]())((acc: Seq[ValidationError], r: ValidationResult) => r match {
          case Invalid(errors) => acc ++ errors
          case _ => acc
        }))
      }
    }
  }
}