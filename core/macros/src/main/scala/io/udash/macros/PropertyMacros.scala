package io.udash.macros

import scala.reflect.macros.blackbox

class PropertyMacros(val c: blackbox.Context) {
  import c.universe._

  val Package = q"_root_.io.udash.properties"
  val ModelValueCls = tq"$Package.ModelValue"
  val ModelSeqCls = tq"$Package.ModelSeq"
  val ModelPartCls = tq"$Package.ModelPart"
  val ImmutableValueCls = tq"$Package.ImmutableValue"

  val RegistrationCls = tq"$Package.Registration"
  val PropertyCreatorCls = tq"$Package.PropertyCreator"
  val PropertyCreatorCompanion = q"$Package.PropertyCreator"

  val ReadablePropertyCls = tq"$Package.single.ReadableProperty"
  val PropertyCls = tq"$Package.single.Property"
  val DirectPropertyImplCls = tq"$Package.single.DirectPropertyImpl"
  val SeqPropertyCls = tq"$Package.seq.SeqProperty"
  val DirectSeqPropertyImplCls = tq"$Package.seq.DirectSeqPropertyImpl"
  val ModelPropertyCls = tq"$Package.model.ModelProperty"
  val ModelPropertyImplCls = tq"$Package.model.ModelPropertyImpl"
  val CastablePropertyCls = tq"$Package.single.CastableProperty"
  val CallbackSequencerCls = q"$Package.CallbackSequencer"

  val PatchCls = tq"$Package.SeqProperty.Patch"
  val ArrayBufferCls = tq"_root_.scala.collection.mutable.ArrayBuffer"
  val MutableMap = q"_root_.scala.collection.mutable.Map"
  val StringCls = tq"String"

  val ExecutionContextCls = tq"_root_.scala.concurrent.ExecutionContext"

  private lazy val OptionTpe = typeOf[Option[_]]
  private lazy val SeqTpe = typeOf[Seq[_]]
  private lazy val MutableSeqTpe = typeOf[scala.collection.mutable.Seq[_]]
  private lazy val topLevelSymbols = Set(typeOf[Any], typeOf[AnyRef], typeOf[AnyVal], typeOf[Product]).map(_.typeSymbol)

  private def fixOverride(s: Symbol) =
    if (s.isTerm && s.asTerm.isOverloaded) {
      s.alternatives.filterNot(_.isSynthetic).head
    } else s

  private def withOverrides(s: Symbol) =
    s :: s.overrides.map(fixOverride)

  private def isFromTopLevelType(symbol: Symbol) =
    withOverrides(symbol).exists(topLevelSymbols contains _.owner)

  //Checks, if symbol is abstract method
  private def isAbstractMethod(symbol: Symbol): Boolean =
    symbol.isAbstract && symbol.isMethod && !symbol.asMethod.isAccessor

  //Checks, if symbol is method and takes any parameters
  private def takesParameters(symbol: Symbol): Boolean =
    symbol.isMethod && symbol.asMethod.paramLists.nonEmpty

  //Checks, if tpe is case class
  private def isCaseClass(tpe: Type) =
    tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass

  //Checks, if tpe is immutable case class
  private def isImmutableCaseClass(tpe: Type) =
    isCaseClass(tpe) && filterMembers(tpe).filter(m => !m.isPrivate && m.isTerm)
      .forall(m => m.asTerm.isStable && (m.typeSignatureIn(tpe).resultType == tpe || isImmutableValue(m.typeSignatureIn(tpe).resultType)))

  //Checks, if tpe is case class which can be used as ModelProperty template
  private def isModelCaseClass(tpe: Type): Boolean =
    isCaseClass(tpe) && (
      tpe <:< typeOf[scala.Tuple2[_, _]] || //dirty hack for Tuple2 unstable `swap` method
      filterMembers(tpe).filter(!_.isPrivate)
        .forall(m => m.isMethod && m.asMethod.isCaseAccessor && isModelValue(m.typeSignatureIn(tpe).resultType))
    )

  //Checks, if tpe is sealed and children are immutable
  //TODO: Implement this stuff - children checking
  private def isImmutableSealedHierarchy(tpe: Type): Boolean =
    tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isSealed

  //Checks, if tpe is immutable collection and children are immutable
  //TODO: Implement this stuff - children checking
  private def isImmutableCollection(tpe: Type): Boolean =
    tpe <:< SeqTpe && !(tpe <:< MutableSeqTpe)

  //Checks, if tpe is immutable option and children are immutable
  //TODO: Implement this stuff - children checking
  private def isImmutableOption(tpe: Type): Boolean =
    tpe <:< OptionTpe

  //Checks, if return type of method is ModelValue
  private def doesReturnTypeForModelRequirements(symbol: Symbol, signatureType: Type): Boolean =
    symbol.isMethod && isModelValue(symbol.typeSignatureIn(signatureType).resultType)

  //Checks, if method is abstract, without parameters and returns ImmutableValue type
  private def doesMethodMeetModelRequirements(s: Symbol, signatureType: Type): Boolean =
    isAbstractMethod(s) && !takesParameters(s) && doesReturnTypeForModelRequirements(s, signatureType)

  //Returns filtered members without synthetic, top level and constructor of provided type
  private def filterMembers(valueType: Type) =
    valueType.members
      .filterNot(member => member.isSynthetic || isFromTopLevelType(member) || member.isConstructor || member.isType)

  //Returns filtered memebrs which can be treated as Properties
  private def propertyMembers(tpe: Type) =
    filterMembers(tpe)
      .filter(s => doesMethodMeetModelRequirements(s, tpe))

  private def checkModel(tree: c.Tree) =
    c.typecheck(tree, silent = true) != EmptyTree

  private def isModelPart(tpe: Type): Boolean = checkModel(q"""implicitly[$ModelPartCls[$tpe]]""")

  private def isModelValue(tpe: Type): Boolean = checkModel(q"""implicitly[$ModelValueCls[$tpe]]""")

  private def isModelSeq(tpe: Type): Boolean = checkModel(q"""implicitly[$ModelSeqCls[$tpe]]""")

  private def isImmutableValue(tpe: Type): Boolean = checkModel(q"""implicitly[$ImmutableValueCls[$tpe]]""")

  private def getModelPath(tree: Tree) = tree match {
    case f@Function(_, path) => path
    case _ => c.abort(tree.pos, "Only inline lambdas supported. Please use bind(_.path.to.model).")
  }

  def reifyImmutableValue[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    if (isImmutableCaseClass(valueType) || isImmutableSealedHierarchy(valueType) || isImmutableCollection(valueType) || isImmutableOption(valueType)) {
      q"""null"""
    } else {
      c.abort(c.enclosingPosition, s"The type $valueType does not meet $ModelValueCls requirements. It is not case class nor " +
        s"immutable sealed hierarchy nor immutable collection.")
    }
  }

  def reifyModelPart[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    if (valueType.typeSymbol.isClass && valueType.typeSymbol.asClass.isTrait && !valueType.typeSymbol.asClass.isSealed
          && !isModelSeq(valueType) && !(valueType <:< MutableSeqTpe)) {
      q"""
         implicit val ${TermName(c.freshName())}: $ModelPartCls[$valueType] = null
         ..${
            filterMembers(valueType).collect {
              case s if isAbstractMethod(s) && !takesParameters(s) && s.isMethod => s
            }.map(s => {
              val resultType = s.typeSignatureIn(valueType).resultType
              q"""implicitly[$ModelValueCls[$resultType]]"""
            })
         }
         null
      """
    } else if (isModelCaseClass(valueType)) {
      q"""
         implicit val ${TermName(c.freshName())}: $ModelPartCls[$valueType] = null
         ..${
            filterMembers(valueType).filter(!_.isPrivate)
              .filter(m => m.isMethod && m.asMethod.isCaseAccessor)
              .map(m => {
                val resultType = m.typeSignatureIn(valueType).resultType
                q"""implicitly[$ModelValueCls[$resultType]]"""
              })
         }
         null
       """
    } else {
      c.abort(c.enclosingPosition, s"The type $valueType does not meet $ModelPartCls requirements. It must be trait. (not sealed trait) ")
    }
  }

  def reifyModelValue[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    if (isImmutableValue(valueType) || isModelPart(valueType) || isModelSeq(valueType)) {
      q"""null"""
    } else {
      c.abort(c.enclosingPosition, s"The type $valueType does not meet $ModelValueCls requirements. This is not immutable value nor valid $ModelPartCls nor $ModelSeqCls.")
    }
  }

  def reifyModelSeq[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    if (valueType.dealias.widen.typeSymbol == SeqTpe.typeSymbol && valueType.typeArgs.forall(isModelValue)) {
      q"""null"""
    } else {
      val invalidTypeArgs = valueType.typeArgs.collect { case t if !isModelValue(t) => t }
      c.abort(c.enclosingPosition, s"The type $valueType does not meet $ModelSeqCls requirements. This must be Seq with valid ModelValues as type args." +
        s"Invalid type args: $invalidTypeArgs")
    }
  }

  private def generateValueProperty(tpe: Type): c.Tree = {
    q"""
      new $DirectPropertyImplCls[$tpe](prt, $PropertyCreatorCompanion.newID())(ec){}
    """
  }

  private def generateSeqProperty(tpe: Type): c.Tree = {
    val elementTpe = tpe.typeArgs.head
    q"""{
       implicit val elemCreator = implicitly[$PropertyCreatorCls[$elementTpe]]
       new $DirectSeqPropertyImplCls[$elementTpe](prt, $PropertyCreatorCompanion.newID())(elemCreator, ec)
      }"""
  }

  private def generateModelProperty(tpe: Type): c.Tree = {
    if (isModelCaseClass(tpe)) {
      val order = tpe.members.collectFirst {
          case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
        }.get.paramLists.flatten
        .map(m => m.name.toTermName)
      val members = filterMembers(tpe).filter(!_.isPrivate)
        .filter(m => m.isMethod && m.asMethod.isCaseAccessor)
        .map(m => m.asMethod.name -> m.typeSignatureIn(tpe).resultType).toMap
     q"""
        new $ModelPropertyImplCls[$tpe](prt, $PropertyCreatorCompanion.newID())(ec) {
          override protected def initialize(): Unit = {
            ..${
              members.map {
                case (name, returnTpe) =>
                  q"""properties(${name.toString}) = implicitly[$PropertyCreatorCls[$returnTpe]].newProperty(this)(ec)"""
              }
            }
          }

          def get: $tpe =
            if (!initialized) null.asInstanceOf[$tpe]
            else new ${tpe.typeSymbol}(
             ..${
                order.map { case name =>
                  val returnTpe = members(name)
                  q"""getSubProperty[$returnTpe](${name.toString}).get"""
                }
              }
            )

          def set(newValue: $tpe): Unit = if (newValue != null) {
            $CallbackSequencerCls.sequence {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""getSubProperty[$returnTpe](${name.toString}).set(newValue.$name)"""
                }
              }
            }
          }

          def setInitValue(newValue: $tpe): Unit = if (newValue != null) {
            ..${
              members.map { case (name, returnTpe) =>
                q"""getSubProperty[$returnTpe](${name.toString}).setInitValue(newValue.$name)"""
              }
            }
          }
        }
      """
    } else {
      val members = propertyMembers(tpe).map(method => (method.asMethod.name, method.typeSignatureIn(tpe).resultType))
       q"""
        new $ModelPropertyImplCls[$tpe](prt, $PropertyCreatorCompanion.newID())(ec) {
          override protected def initialize(): Unit = {
            ..${
              members.map {
                case (name, returnTpe) =>
                  q"""properties(${name.toString}) = implicitly[$PropertyCreatorCls[$returnTpe]].newProperty(this)(ec)"""
              }
            }
          }

          def get: $tpe =
            if (!initialized) null.asInstanceOf[$tpe]
            else new $tpe {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""def $name = getSubProperty[$returnTpe](${name.toString}).get"""
                }
              }
            }

          def set(newValue: $tpe): Unit = if (initialized || newValue != null) {
            $CallbackSequencerCls.sequence {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""getSubProperty[$returnTpe](${name.toString}).set(newValue.$name)"""
                }
              }
            }
          }

          def setInitValue(newValue: $tpe): Unit = if (initialized || newValue != null) {
            ..${
              members.map { case (name, returnTpe) =>
                q"""getSubProperty[$returnTpe](${name.toString}).setInitValue(newValue.$name)"""
              }
            }
          }
        }
      """
    }
  }

  def reifySubProperty[A: c.WeakTypeTag, B: c.WeakTypeTag](f: c.Expr[A => B])(ev: c.Expr[_]): c.Tree = {
    val valueType = weakTypeOf[B]
    val model = c.prefix

    val modelPath = getModelPath(f.tree)

    def checkIfPathIsModelPart(tree: Tree): Boolean = tree match {
      case s@Select(next, _) if isModelPart(s.tpe) => checkIfPathIsModelPart(next)
      case Ident(_) => true
      case _ => false
    }

    def checkIfIsValidPath(tree: Tree): Boolean = tree match {
      case s@Select(next, _) if isImmutableValue(s.tpe) || isModelSeq(s.tpe) || isModelPart(s.tpe) => checkIfPathIsModelPart(next)
      case _ => false
    }

    if (!checkIfIsValidPath(modelPath)) {
      c.abort(c.enclosingPosition, s"The path must consist of ModelParts and only leaf can be ImmutableValue or $ModelSeqCls.")
    }

    def parsePath(tree: Tree, acc: List[(Select, TermName)] = List()): List[(Select, TermName)] = tree match {
      case s@Select(next, t@TermName(_)) => parsePath(next, (s, t) :: acc)
      case _ => acc
    }

    val parts = parsePath(modelPath)

    def genTree(source: List[(Select, TermName)], targetTree: Tree): Tree = source match {
      case (select, term) :: tail if isModelSeq(select.tpe) =>
        q"""$targetTree.getSubProperty[${select.tpe}](${term.decodedName.toString})
           .asInstanceOf[$SeqPropertyCls[${select.tpe.typeArgs.head}, $PropertyCls[${select.tpe.typeArgs.head}] with $CastablePropertyCls[${select.tpe.typeArgs.head}]]]"""
      case (select, term) :: tail if isModelPart(select.tpe) =>
        genTree(tail, q"""$targetTree.getSubProperty[${select.tpe}](${term.decodedName.toString}).asInstanceOf[$ModelPropertyImplCls[${select.tpe}]]""")
      case (select, term) :: tail if isModelValue(select.tpe) =>
        q"""$targetTree.getSubProperty[${select.tpe}](${term.decodedName.toString}).asInstanceOf[$PropertyCls[${select.tpe}]]"""
      case Nil => targetTree
    }

    genTree(parts, q"""$model.asInstanceOf[$ModelPropertyImplCls[_]]""")
  }

  private def generatePropertyCreator(tpe: Type, constructor: (Type) => c.Tree): c.Tree = {
    val selfName = c.freshName(TermName("self"))
    q"""
       new $PropertyCreatorCls[$tpe] {
         implicit val $selfName: $PropertyCreatorCls[$tpe] = this
         def newProperty(prt: $ReadablePropertyCls[_])(implicit ec: $ExecutionContextCls): $PropertyCls[$tpe] with $CastablePropertyCls[$tpe]
           = {${constructor.apply(tpe)}}
       }
    """
  }

  def reifyPropertyCreator[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]

    if (isModelSeq(valueType)) {
      generatePropertyCreator(valueType, generateSeqProperty)
    } else if (isModelPart(valueType)) {
      generatePropertyCreator(valueType, generateModelProperty)
    } else if (isModelValue(valueType)) {
      generatePropertyCreator(valueType, generateValueProperty)
    } else {
      c.abort(c.enclosingPosition, s"$valueType should meet requirements for one of $ModelPartCls, $ModelValueCls, $ModelSeqCls.")
    }
  }
}
