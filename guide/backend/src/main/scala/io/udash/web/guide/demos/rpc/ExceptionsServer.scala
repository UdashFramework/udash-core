package io.udash.web.guide.demos.rpc

import io.udash.web.guide.GuideExceptions
import io.udash.web.guide.demos.i18n.Translations

import scala.concurrent.Future

class ExceptionsServer extends ExceptionsRPC {
  override def example(): Future[Unit] =
    Future.failed(GuideExceptions.ExampleException("Exception from server"))

  override def exampleWithTranslatableError(): Future[Unit] =
    Future.failed(GuideExceptions.TranslatableExampleException(Translations.exceptions.example))

  override def unknownError(): Future[Unit] =
    Future.failed(new RuntimeException("RuntimeException from server"))
}
