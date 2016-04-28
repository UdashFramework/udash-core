package io.udash.macros

import scala.reflect.macros.blackbox

class BidirectionalMacro(val c: blackbox.Context) {

  import c.universe._

  val SELECTOR_DUMMY = TermName("<unapply-selector>")
  val UNAPPLY = TermName("unapply")
  val UNAPPLY_SEQ = TermName("unapplySeq")
  val APPLY = TermName("apply")

  def findAliasCompanion(tpe: Type, name: TypeName) = tpe match {
    case MethodType(_, resultType) =>
      val resultSymbol = resultType.typeSymbol

      val possibleResultSymbols =
        if (resultSymbol.isClass && resultSymbol.asClass.isSealed)
          resultSymbol.asClass.knownDirectSubclasses + resultSymbol
        else
          Set(resultSymbol)

      possibleResultSymbols.find(_.name == name)
        .map(_.companion).filter(_ != NoSymbol)

    case _ => None
  }

  object SelectOrTypeApplySelect {
    def unapply(tree: Tree) = tree match {
      case TypeApply(Select(fun, name), _) => Some((fun, name))
      case Select(fun, name) => Some((fun, name))
      case _ => None
    }
  }

  object ApplyOperator {
    def unapply(tree: Tree): Option[(Symbol, Tree, Tree)] = tree match {
      // left associative operator
      case Apply(prefix, List(rhs)) =>
        prefix match {
          case SelectOrTypeApplySelect(lhs, opName) =>
            findAliasCompanion(prefix.tpe, opName.toTypeName).map((_, lhs, rhs))
          case _ => None
        }

      // right associative operator
      case Block(List(ValDef(mods, valName, _, lhs)), Apply(prefix, List(Ident(lhsName))))
        if valName == lhsName && mods.hasFlag(Flag.ARTIFACT | Flag.SYNTHETIC) =>

        prefix match {
          case SelectOrTypeApplySelect(rhs, opName) =>
            findAliasCompanion(prefix.tpe, opName.toTypeName).map((_, lhs, rhs))
          case _ => None
        }

      case _ =>
        None
    }
  }

  def impl[A: c.WeakTypeTag, B: c.WeakTypeTag](
    pf: c.Expr[PartialFunction[A, B]]): c.Expr[(PartialFunction[A, B], PartialFunction[B, A])] = {

    // To find out WTF is this, please learn how Scala compiler represents (typechecked) partial functions
    val Typed(Block(List(ClassDef(_, _, _, Template(_, _, List(_, DefDef(_, _, _, _, _, Match(_, cases)), _)))), _), _) = pf.tree

    def patternToExpr(pattern: Tree): Tree = pattern match {
      case Apply(tt@TypeTree(), args) if tt.original != null =>
        Apply(tt.original, args.map(patternToExpr))
      case Apply(fun, args) =>
        Apply(fun, args.map(patternToExpr))
      case UnApply(TypeApply(Apply(Select(prefix, UNAPPLY | UNAPPLY_SEQ), tpt), List(Ident(SELECTOR_DUMMY))), args) =>
        Apply(TypeApply(prefix, tpt), args.map(patternToExpr))
      case UnApply(Apply(Select(prefix, UNAPPLY | UNAPPLY_SEQ), List(Ident(SELECTOR_DUMMY))), args) =>
        Apply(prefix, args.map(patternToExpr))
      case Bind(name, Typed(Ident(termNames.WILDCARD), tpt)) =>
        Typed(Ident(name), tpt)
      case Bind(name, Ident(termNames.WILDCARD)) =>
        Ident(name)
      case _: Select | _: Ident | _: Literal =>
        pattern
      case _ =>
        c.abort(pattern.pos, "Could not translate pattern to expression: " + show(pattern))
    }

    def boundNames(pattern: Tree) = pattern.collect {
      case Bind(name, Typed(Ident(termNames.WILDCARD), _)) =>
        name
      case Bind(name, Ident(termNames.WILDCARD)) =>
        name
    }

    def exprToPattern(expr: Tree, boundNamesSet: Set[Name]): Tree = {
      def toPattern(expr: Tree): Tree = expr match {
        case Apply(TypeApply(Select(prefix, APPLY), _), args) =>
          Apply(prefix, args.map(toPattern))
        case Apply(Select(prefix, APPLY), args) =>
          Apply(prefix, args.map(toPattern))
        case ApplyOperator(companion, lhs, rhs) =>
          Apply(Ident(companion), List(lhs, rhs).map(toPattern))
        case Ident(name) if boundNamesSet.contains(name) =>
          Bind(name, Ident(termNames.WILDCARD))
        case _: Select | _: Ident | _: Literal =>
          expr
        case _ =>
          c.abort(expr.pos, "Could not translate expression to pattern: " + show(expr))
      }
      toPattern(expr)
    }

    val bodies = scala.collection.mutable.Set[Tree]()
    def reverseCaseDef(caseDef: CaseDef) = caseDef match {
      case CaseDef(pattern, guard, body) =>
        val boundNamesSet = boundNames(pattern).toSet
        if (bodies.exists(b => body.equalsStructure(b))) c.error(pattern.pos, "Body should be unique.")
        bodies.add(body)
        CaseDef(c.untypecheck(exprToPattern(body, boundNamesSet)), guard, c.untypecheck(patternToExpr(pattern)))
    }

    val reversed = c.typecheck(Match(EmptyTree, cases.init.map(reverseCaseDef)), pt = weakTypeOf[PartialFunction[B, A]])
    c.Expr(q"($pf, $reversed)")
  }

  def indent(str: String) =
    str.split("\n").toStream.map("  " + _).mkString("\n")

  def prettyPrint(v: Any): String = v match {
    case tt: TypeTree if tt.original != null =>
      "TypeTree().setOriginal(" + prettyPrint(tt.original) + ")"
    case tt: TypeTree if tt.tpe != null =>
      "TypeTree(): " + prettyPrint(tt.tpe)
    case l: List[Any] if l.size > 1 =>
      "List(\n" + l.iterator.map(prettyPrint).map(indent).mkString(",\n") + "\n)"
    case l: List[Any] =>
      "List(" + l.iterator.map(prettyPrint).mkString + ")"
    case p: Product if p.productArity > 1 =>
      p.productPrefix + "(\n" + p.productIterator.map(prettyPrint).map(indent).mkString(",\n") + "\n)"
    case p: Product =>
      p.productPrefix + "(" + p.productIterator.map(prettyPrint).mkString + ")"
    case _ => showRaw(v)
  }
}