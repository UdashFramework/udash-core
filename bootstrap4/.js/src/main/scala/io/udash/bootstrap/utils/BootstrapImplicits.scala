package io.udash.bootstrap.utils

import io.udash.Url
import io.udash.bindings.modifiers.Binding
import org.scalajs.dom.Element
import scalatags.JsDom
import scalatags.JsDom.GenericAttr
import scalatags.JsDom.all._


trait BootstrapImplicits {
  implicit val urlAttrValue: AttrValue[Url] = new AttrValue[Url] {
    def apply(t: Element, a: JsDom.Attr, v: Url): Unit =
      new GenericAttr[String].apply(t, a, v.value)
  }

  implicit def withoutNested(modifier: Modifier): Binding.NestedInterceptor => Modifier = _ => modifier
  implicit def stringWithoutNested(modifier: String): Binding.NestedInterceptor => Modifier = _ => modifier
}

object BootstrapImplicits extends BootstrapImplicits