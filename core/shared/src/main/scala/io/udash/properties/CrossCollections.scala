package io.udash.properties

import scala.collection.mutable

private[properties]
object CrossCollections {
  type Array[T] = mutable.Buffer[T]
  type Dictionary[T] = mutable.Map[String, T]

  def toCrossArray[T](t: Traversable[T]): Array[T] = macro io.udash.macros.CrossCollectionsMacros.toCrossArray[T]
  def createArray[T]: Array[T] = macro io.udash.macros.CrossCollectionsMacros.createArray[T]
  def createDictionary[T]: Dictionary[T] = macro io.udash.macros.CrossCollectionsMacros.createDictionary[T]
  def copyArray[T](a: Array[T]): Array[T] = macro io.udash.macros.CrossCollectionsMacros.copyArray[T]
  def slice[T](a: Array[T], from: Int, to: Int): Array[T] = macro io.udash.macros.CrossCollectionsMacros.slice[T]
  def replace[T](a: Array[T], idx: Int, count: Int, items: T*): Unit = macro io.udash.macros.CrossCollectionsMacros.replace[T]
}
