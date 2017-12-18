package io.udash.properties

import scala.collection.mutable

private[properties]
object CrossCollections {
  type Array[T] = mutable.ArrayBuffer[T]
  type Dictionary[T] = mutable.HashMap[String, T]

  def toCrossArray[T](t: Traversable[T]): Array[T] = t.to[mutable.ArrayBuffer]
  def createArray[T]: Array[T] = new mutable.ArrayBuffer[T]
  def createDictionary[T]: Dictionary[T] = new mutable.HashMap[String, T]
  def copyArray[T](a: Array[T]): Array[T] = a.to[mutable.ArrayBuffer] // creates copy
  def slice[T](a: Array[T], from: Int, to: Int): Array[T] = a.slice(from, to)
  def replace[T](a: Array[T], idx: Int, count: Int, items: T*): Unit = {
    a.remove(idx, count)
    a.insertAll(idx, items)
  }
}
