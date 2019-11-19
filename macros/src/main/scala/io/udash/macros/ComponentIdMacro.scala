package io.udash
package macros

import scala.reflect.macros.blackbox

class ComponentIdMacro(val c: blackbox.Context) {

  import c.universe._

  def impl(): c.Tree = {
    val trimmedName = Iterator.iterate(c.internal.enclosingOwner)(_.owner).find(_.isClass).get.fullName
    q"ComponentId.forName($trimmedName)"
  }
}