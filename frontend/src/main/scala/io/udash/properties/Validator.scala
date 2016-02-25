package io.udash.properties

import scala.concurrent.{ExecutionContext, Future}

sealed trait ValidationResult
case object Valid extends ValidationResult
case class Invalid(errors: Seq[String]) extends ValidationResult

trait Validator[T] {
  def apply(element: T)(implicit ec: ExecutionContext): Future[ValidationResult]
}

object Validator {
  implicit class FutureOps[T](private val future: Future[T]) extends AnyVal {
    def foldValidationResult(implicit ev: T =:= Seq[ValidationResult], ec: ExecutionContext): Future[ValidationResult] = {
      future map {
        case s if s.forall(r => r == Valid) => Valid
        case s => Invalid(s.foldLeft(Seq[String]())((acc: Seq[String], r: ValidationResult) => r match {
          case Invalid(errors) => acc ++ errors
          case _ => acc
        }))
      }
    }
  }
}