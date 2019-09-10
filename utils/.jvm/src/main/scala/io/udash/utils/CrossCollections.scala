package io.udash
package utils

import com.avsystem.commons._

private[udash]
object CrossCollections {
  def toCrossArray[T](t: Traversable[T]): MBuffer[T] = t.to[MArrayBuffer]
  def createArray[T]: MBuffer[T] = new MArrayBuffer[T]
  def createDictionary[T]: MMap[String, T] = new MHashMap[String, T]
  def copyArray[T](a: MBuffer[T]): MBuffer[T] = a.to[MArrayBuffer] // creates copy
  def slice[T](a: MBuffer[T], from: Int, to: Int): MBuffer[T] = a.slice(from, to)
  def replace[T](a: MBuffer[T], idx: Int, count: Int, items: T*): Unit = {
    a.remove(idx, count)
    a.insertAll(idx, items)
  }
}
