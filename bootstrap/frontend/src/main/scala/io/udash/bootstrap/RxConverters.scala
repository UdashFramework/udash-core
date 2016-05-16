package io.udash.bootstrap

import io.udash._
import io.udash.properties.{ModelValue, PropertyCreator}
import rx.{Ctx, Rx, Var}

import scala.concurrent.ExecutionContext

/**
  * Provides implicit conversion between [[rx.Rx]] and [[Property]].
  */
trait RxConverters {

  implicit protected val context: Ctx.Owner = Ctx.Owner.Unsafe.Unsafe

  implicit class PropertyVar[T](property: Property[T]) extends Var[T](property.get) {
    private val registration = property.listen(update)

    override def update(newValue: T): Unit = {
      property.set(newValue)
      super.update(newValue)
    }

    override def kill(): Unit = {
      registration.cancel()
      super.kill()
    }
  }

  implicit def var2Property[T: ModelValue : PropertyCreator](variable: Var[T])(implicit ec: ExecutionContext): CastableProperty[T] = {
    val res = rx2readableProp(variable)
    res.listen(variable.update)
    res
  }

  private def rx2readableProp[T: ModelValue : PropertyCreator](variable: Rx[T])(implicit ec: ExecutionContext): CastableProperty[T] = {
    val res = Property[T]
    variable.trigger(res.set(variable.now))
    res
  }

  implicit def rx2Property[T: ModelValue : PropertyCreator](variable: Rx[T])(implicit ec: ExecutionContext): CastableReadableProperty[T] =
    rx2readableProp(variable)

}
