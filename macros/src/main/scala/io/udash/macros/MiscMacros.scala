package io.udash
package macros

import com.avsystem.commons.macros.AbstractMacroCommons

import scala.reflect.macros.{TypecheckException, blackbox}

class MiscMacros(val ctx: blackbox.Context) extends AbstractMacroCommons(ctx) {

  import c.universe._

  private def stringLiteral(tree: Tree): String = tree match {
    case StringLiteral(str) => str
    case Select(StringLiteral(str), TermName("stripMargin")) => str.stripMargin
    case _ => abort(s"expected string literal, got $tree")
  }

  def typeErrorImpl(code: Tree): Tree = {
    val codeTree = c.parse(stringLiteral(code))
    try {
      c.typecheck(codeTree)
      abort("expected typechecking error, none was raised")
    } catch {
      case TypecheckException(_, msg) => q"$msg"
    }
  }
}
