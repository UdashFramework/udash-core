package io.udash
package macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.blackbox

// Placed under "tests" directory to avoid publishing and yet provide access across all Udash tests.
class AllValuesMacro(override val c: blackbox.Context) extends AbstractMacroCommons(c) {
  import c.universe._

  /**
    * Accessible members include methods, modules, val/var setters and getters and Java fields.
    */
  private def accessibleMembers(tpe: Type): Iterable[TermSymbol] =
    tpe.members.collect { case s if s.isPublic && s.isTerm &&
      (s.isJava || (!s.asTerm.isVal && !s.asTerm.isVar)) && !s.isImplementationArtifact => s.asTerm
    }

  /**
    * Returns all public members (including inherited ones) evaluating to type `V` (or its subtype) and obtainable from
    * provided `obj` by calling a no-arg method.
    */
  def ofType[V: WeakTypeTag](obj: Tree): Tree = {
    val valueType = weakTypeOf[V]
    val names = accessibleMembers(obj.tpe).iterator.filter { s =>
      (s.isMethod || s.isModule) &&
        s.typeSignature.paramLists.iterator.flatten.isEmpty &&
        s.typeSignature.typeParams.isEmpty &&
        s.typeSignatureIn(obj.tpe).finalResultType <:< valueType
    }.map(_.name.toTermName).toVector

    val objName = c.freshName(TermName("obj"))
    q"""
       val $objName = $obj
       $ListObj[$valueType](..${names.map(n => q"$objName.$n")})
     """
  }
}
