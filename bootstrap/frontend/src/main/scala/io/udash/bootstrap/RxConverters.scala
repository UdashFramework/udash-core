package io.udash.bootstrap

import io.udash._
import io.udash.properties.{ModelValue, PropertyCreator}
import rx.{Ctx, Rx, Var}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

/**
  * Provides implicit conversion between [[rx.Rx]] and [[Property]].
  */
trait RxConverters {

  implicit protected val context: Ctx.Owner = Ctx.Owner.Unsafe.Unsafe

  //cache views to avoid redundant updates
  private val varCache = mutable.Map.empty[Property[_], Var[_]]
  private val readablePropertyCache = mutable.Map.empty[Rx[_], CastableReadableProperty[_]]
  private val propertyCache = mutable.Map.empty[Var[_], CastableProperty[_]]

  implicit def property2Var[T](property: Property[T]): Var[T] =
    varCache.getOrElseUpdate(property, new PropertyVar(property)).asInstanceOf[Var[T]]

  private class PropertyVar[T](property: Property[T]) extends Var[T](property.get) {
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

  implicit def var2Property[T: ModelValue : PropertyCreator](variable: Var[T])(implicit ec: ExecutionContext): CastableProperty[T] =
    propertyCache.getOrElseUpdate(variable, {
      val res = createReadableProp(variable)
      res.listen(variable.update)
      res
    }).asInstanceOf[CastableProperty[T]]


  private def createReadableProp[T: ModelValue : PropertyCreator](variable: Rx[T])(implicit ec: ExecutionContext): CastableProperty[T] = {
    val res = Property[T]
    variable.trigger(res.set(variable.now))
    res
  }

  implicit def rx2Property[T: ModelValue : PropertyCreator](rx: Rx[T])(implicit ec: ExecutionContext): CastableReadableProperty[T] =
    rx match {
      case variable: Var[T] => var2Property(variable)
      case _ => readablePropertyCache.getOrElseUpdate(rx, createReadableProp(rx)).asInstanceOf[CastableReadableProperty[T]]
    }

}
