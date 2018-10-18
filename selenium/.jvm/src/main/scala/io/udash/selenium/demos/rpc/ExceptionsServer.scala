package io.udash.selenium.demos.rpc

import io.udash.selenium.rpc.GuideExceptions
import io.udash.selenium.rpc.demos.i18n.Translations
import io.udash.selenium.rpc.demos.rpc.ExceptionsRPC

import scala.concurrent.Future

class ExceptionsServer extends ExceptionsRPC {
  override def example(): Future[Unit] =
    Future.failed(GuideExceptions.ExampleException("Exception from server"))

  override def exampleWithTranslatableError(): Future[Unit] =
    Future.failed(GuideExceptions.TranslatableExampleException(Translations.exceptions.example))

  override def unknownError(): Future[Unit] =
    Future.failed(new RuntimeException("RuntimeException from server"))
}
