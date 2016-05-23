package io.udash.bootstrap

import io.udash._
import io.udash.properties.{ModelValue, PropertyCreator}
import rx.{Ctx, Rx, Var}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

/**
  * Provides implicit conversion between rx.Rx and [[Property]].
  */
trait RxConverters {

  implicit protected val context: Ctx.Owner = Ctx.Owner.Unsafe.Unsafe

  //cache views to avoid redundant updates
  private val rxCache = mutable.Map.empty[ReadableProperty[_], Rx[_]]
  private val varCache = mutable.Map.empty[Property[_], Var[_]]
  private val readablePropertyCache = mutable.Map.empty[Rx[_], ReadableProperty[_]]
  private val propertyCache = mutable.Map.empty[Var[_], Property[_]]

  implicit def property2Var[T](property: Property[T]): Var[T] =
    varCache.getOrElseUpdate(property, new PropertyVar(property)).asInstanceOf[Var[T]]

  implicit def rx2Property[T: ModelValue : PropertyCreator](rx: Rx[T])(implicit ec: ExecutionContext): CastableReadableProperty[T] =
    rx match {
      case variable: Var[T] => var2Property(variable)
      case _ => readablePropertyCache.getOrElseUpdate(rx, createReadableProp(rx)).asInstanceOf[CastableReadableProperty[T]]
    }

  implicit def var2Property[T: ModelValue : PropertyCreator](variable: Var[T])(implicit ec: ExecutionContext): CastableProperty[T] =
    propertyCache.getOrElseUpdate(variable, {
      val res = createReadableProp(variable)
      res.listen(variable.update)
      res
    }).asInstanceOf[CastableProperty[T]]

  private def createReadableProp[T: ModelValue : PropertyCreator](rx: Rx[T])(implicit ec: ExecutionContext): CastableProperty[T] = {
    val res = Property[T]
    rx.trigger(res.set(rx.now))
    res
  }

  implicit def rxSeq2Property[T: ModelValue](rx: Rx[Seq[T]])(implicit ec: ExecutionContext): ReadableSeqProperty[T] =
    rx match {
      case variable: Var[Seq[T]] => varSeq2Property(variable)
      case _ => readablePropertyCache.getOrElseUpdate(rx, createReadableSeqProp(rx)).asInstanceOf[ReadableSeqProperty[T]]
    }

  implicit def varSeq2Property[T: ModelValue](variable: Var[Seq[T]])(implicit ec: ExecutionContext): SeqProperty[T] =
    propertyCache.getOrElseUpdate(variable, {
      val res = createReadableSeqProp(variable)
      res.listen(t => variable.update(t))
      res
    }).asInstanceOf[SeqProperty[T]]

  private def createReadableSeqProp[T: ModelValue](rx: Rx[Seq[T]])(implicit ec: ExecutionContext): SeqProperty[T] = {
    val res = SeqProperty[T]
    rx.trigger(res.set(rx.now))
    res
  }

  implicit def readableProp2Rx[T](property: ReadableProperty[T]): Rx[T] = {
    property match {
      case prop: Property[T] => property2Var(prop)
      case _ => rxCache.getOrElseUpdate(property, new ReadablePropertyRx[T](property).r).asInstanceOf[Rx[T]]
    }
  }

  private class ReadablePropertyRx[T](property: ReadableProperty[T]) extends Var[T](property.get) {
    private val registration = property.listen(update)

    override def kill(): Unit = {
      registration.cancel()
      super.kill()
    }
  }

  private class PropertyVar[T](property: Property[T]) extends ReadablePropertyRx[T](property) {
    override def update(newValue: T): Unit = {
      property.set(newValue)
      super.update(newValue)
    }
  }
}
