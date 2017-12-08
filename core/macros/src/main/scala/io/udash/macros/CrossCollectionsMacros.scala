package io.udash.macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.blackbox

class CrossCollectionsMacros(val ctx: blackbox.Context) extends AbstractMacroCommons(ctx) {
  import c.universe._

  val ArrayBufferCls = tq"scala.collection.mutable.ArrayBuffer"
  val JsArrayCls = tq"scala.scalajs.js.Array"
  val JsArrayObj = q"scala.scalajs.js.Array"
  val WrappedArrayCls = tq"scala.scalajs.js.WrappedArray"

  val HashMapCls = tq"scala.collection.mutable.HashMap"
  val JsDictionaryObj = q"scala.scalajs.js.Dictionary"

  def toCrossArray[T: c.WeakTypeTag](t: c.Tree): c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) {
      q"""{
        import scala.scalajs.js.JSConverters._
        $t.toJSArray
      }"""
    }
    else q"$t.to[$ArrayBufferCls]"
  }

  def createArray[T: c.WeakTypeTag]: c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) q"new $WrappedArrayCls($JsArrayObj[$tpe]())" else q"new $ArrayBufferCls[$tpe]"
  }

  def createDictionary[T: c.WeakTypeTag]: c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) q"$JsDictionaryObj[$tpe]()" else q"new $HashMapCls[String, $tpe]"
  }

  def copyArray[T: c.WeakTypeTag](a: c.Tree): c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) q"$a.asInstanceOf[$WrappedArrayCls[$tpe]].jsSlice()" else q"$a.to[$ArrayBufferCls]"
  }

  def slice[T: c.WeakTypeTag](a: c.Tree, from: c.Tree, to: c.Tree): c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) q"$a.asInstanceOf[$WrappedArrayCls[$tpe]].jsSlice($from, $to)" else q"$a.slice($from, $to)"
  }

  def replace[T: c.WeakTypeTag](a: c.Tree, idx: c.Tree, count: c.Tree, items: c.Tree*): c.Tree = {
    val tpe = weakTypeOf[T]
    if (isScalaJs) q"$a.asInstanceOf[$WrappedArrayCls[$tpe]].splice($idx, $count, ..$items)"
    else q"""{
      $a.remove($idx, $count)
      $a.insertAll($idx, Seq(..$items))
    }"""
  }
}
