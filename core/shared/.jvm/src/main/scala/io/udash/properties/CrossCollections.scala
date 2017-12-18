package io.udash.properties

import scala.collection.mutable

private[properties]
object CrossCollections {
  def toCrossArray[T](t: Traversable[T]): mutable.Buffer[T] = t.to[mutable.ArrayBuffer]
  def createArray[T]: mutable.Buffer[T] = new mutable.ArrayBuffer[T]
  def createDictionary[T]: mutable.Map[String, T] = new mutable.HashMap[String, T]
  def copyArray[T](a: mutable.Buffer[T]): mutable.Buffer[T] = a.to[mutable.ArrayBuffer] // creates copy
  def slice[T](a: mutable.Buffer[T], from: Int, to: Int): mutable.Buffer[T] = a.slice(from, to)
  def replace[T](a: mutable.Buffer[T], idx: Int, count: Int, items: T*): Unit = {
    a.remove(idx, count)
    a.insertAll(idx, items)
  }
}
