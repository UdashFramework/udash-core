package io.udash
package macros

import scala.reflect.macros.blackbox

class ComponentIdMacro(val c: blackbox.Context) {

  import c.universe._

  final def IdObj: Tree = q"io.udash.component.ComponentId"

  def impl(): c.Tree = {
    val fqn = Iterator.iterate(c.internal.enclosingOwner)(_.owner).find(_.isClass).get.fullName.replace('.', '-').replace('$', '__')
    q"$IdObj.forName($fqn)"
  }
}
