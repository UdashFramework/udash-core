package io.udash.macros

import com.avsystem.commons.macros.MacroCommons
import com.avsystem.commons.misc.Opt

import scala.collection.mutable
import scala.reflect.macros.blackbox

class PropertyMacros(val c: blackbox.Context) extends MacroCommons {
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
  private lazy val OptTpe = typeOf[Opt[_]]
  private lazy val SeqTpe = typeOf[Seq[_]]
  private lazy val MutableSeqTpe = typeOf[scala.collection.mutable.Seq[_]]
  private lazy val topLevelSymbols = Set(typeOf[Any], typeOf[AnyRef], typeOf[AnyVal], typeOf[Product], typeOf[Equals]).map(_.typeSymbol)

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

  //Checks, if symbol is abstract field
  private def isAbstractField(symbol: Symbol): Boolean =
    isAbstractVal(symbol) || isAbstractVar(symbol)

  //Checks, if symbol is abstract field
  private def isAbstractVal(symbol: Symbol): Boolean =
    symbol.isAbstract && symbol.isTerm && symbol.asTerm.isVal

  //Checks, if symbol is abstract field
  private def isAbstractVar(symbol: Symbol): Boolean =
    symbol.isAbstract && symbol.isTerm && symbol.asTerm.isVar

  //Checks, if symbol is method and takes any parameters
  private def takesParameters(symbol: Symbol): Boolean =
    symbol.isMethod && symbol.asMethod.paramLists.nonEmpty

  //Checks, if tpe is case class
  private def isCaseClass(tpe: Type) =
    tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass

  //Checks, if tpe is immutable case class
  private def isImmutableCaseClass(tpe: Type) = {
    val members = filterMembers(tpe).filter(m => !m.isPrivate && m.isTerm)
    isCaseClass(tpe) && !members.exists(m => m.asTerm.isVar) && checkModel(
      q"""
        implicit val ${TermName(c.freshName())}: $ImmutableValueCls[$tpe] = null
        ..${
          members.map(m => q"""implicitly[$ImmutableValueCls[${m.typeSignatureIn(tpe).resultType}]]""")
        }
      """
    )
  }

  private def findPrimaryConstructor(tpe: Type): MethodSymbol =
    tpe.members.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get

  //Checks, if tpe is case class which can be used as ModelProperty template
  private def isModelCaseClass(tpe: Type): Boolean =
    isCaseClass(tpe) && findPrimaryConstructor(tpe).paramLists.size == 1 &&
      filterMembers(tpe)
        .filter(m => !m.isPrivate && !(tpe <:< typeOf[Tuple2[_, _]] && m.name.decodedName.toString == "swap"))
        .forall { m =>
          if (m.isMethod && m.asMethod.isAccessor && m.asMethod.accessed.isTerm) m.asMethod.accessed.asTerm.isVal
          else m.isMethod || m.asTerm.isVal
        }

  //Checks, if tpe is sealed and children are immutable
  //TODO: Implement this stuff - children checking
  private def isImmutableSealedHierarchy(tpe: Type): Boolean =
    tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isSealed

  //Checks, if tpe is immutable collection and children are immutable
  private def isImmutableSeq(tpe: Type): Boolean =
    tpe <:< SeqTpe && !(tpe <:< MutableSeqTpe) && tpe.typeArgs.forall(isImmutableValue)

  //Checks, if tpe is immutable Option and children are immutable
  private def isImmutableOption(tpe: Type): Boolean =
    tpe <:< OptionTpe && tpe.typeArgs.forall(isImmutableValue)

  //Checks, if tpe is immutable Opt and children are immutable
  private def isImmutableOpt(tpe: Type): Boolean =
    tpe <:< OptTpe && tpe.typeArgs.forall(isImmutableValue)

  //Returns filtered members without synthetic, top level and constructor of provided type
  private def filterMembers(valueType: Type) =
    valueType.members
      .filterNot(member => member.isSynthetic || isFromTopLevelType(member) || member.isConstructor || member.isType)

  //Checks, if method or val is abstract, without parameters and returns ImmutableValue type
  private def doesMeetTraitModelRequirements(s: Symbol, signatureType: Type): Boolean =
    doesMeetTraitElementsRequirements(s, signatureType) && isModelValue(s.typeSignatureIn(signatureType).resultType)

  //Checks, if method or val is abstract, without parameters and returns ImmutableValue type
  private def doesMeetTraitElementsRequirements(s: Symbol, signatureType: Type): Boolean =
    (isAbstractMethod(s) && !takesParameters(s)) || isAbstractVal(s) ||
      (s.isAbstract && s.isMethod && s.asMethod.isAccessor && s.asMethod.accessed == NoSymbol) // val in trait in scala 2.11

  //Returns filtered memebrs which can be treated as Properties in trait based ModelProperty
  private def traitBasedPropertyMembers(tpe: Type): Seq[Symbol] =
    filterMembers(tpe).filter(!_.isPrivate)
      .filter(s => doesMeetTraitModelRequirements(s, tpe)).toSeq

  //Returns filtered memebrs which can be treated as Properties in trait based ModelProperty (with checking subtypes)
  private def traitBasedPropertyPossibleMemebers(tpe: Type): Seq[Symbol] =
    filterMembers(tpe).filter(!_.isPrivate)
      .filter(s => doesMeetTraitElementsRequirements(s, tpe)).toSeq

  //Returns filtered memebrs which can be treated as Properties in case class based ModelProperty
  private def ccBasedPropertyMembers(tpe: Type): Seq[Symbol] =
    filterMembers(tpe)
      .filter(m => !m.isPrivate && !(tpe <:< typeOf[Tuple2[_, _]] && m.name.decodedName.toString == "swap"))
      .filter(m => m.isMethod && m.asMethod.isCaseAccessor).toSeq

  private def checkModel(tree: c.Tree): Boolean =
    c.typecheck(tree, silent = true) != EmptyTree

  private def checkModelWithCache(tree: c.Tree, tpe: Type, cache: mutable.Set[Any]): Boolean =
    if (cache.contains(tpe)) true
    else {
      cache += tpe
      val r = checkModel(tree)
      if (!r) cache -= tpe
      r
    }

  private def isModelPart(tpe: Type): Boolean =
    checkModelWithCache(q"""implicitly[$ModelPartCls[$tpe]]""", tpe, PropertyMacrosCache.isModelPartCache)

  private def isModelValue(tpe: Type): Boolean =
    checkModelWithCache(q"""implicitly[$ModelValueCls[$tpe]]""", tpe, PropertyMacrosCache.isModelValueCache)

  private def isModelSeq(tpe: Type): Boolean =
    checkModelWithCache(q"""implicitly[$ModelSeqCls[$tpe]]""", tpe, PropertyMacrosCache.isModelSeqCache)

  private def isImmutableValue(tpe: Type): Boolean =
    checkModelWithCache(q"""implicitly[$ImmutableValueCls[$tpe]]""", tpe, PropertyMacrosCache.isImmutableCache)

  private def getModelPath(tree: Tree) = tree match {
    case f@Function(_, path) => path
    case _ => c.abort(tree.pos, "Only inline lambdas supported. Please use `subProp(_.path.to.element)`.")
  }

  def isValidSubproperty(tpe: Type, name: Name): Boolean =
    traitBasedPropertyMembers(tpe).map(_.name).contains(name) || ccBasedPropertyMembers(tpe).map(_.name).contains(name)

  def reifyImmutableValue[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]

    val isSeq = isImmutableSeq(valueType)
    lazy val isOption = isImmutableOption(valueType)
    lazy val isOpt = isImmutableOpt(valueType)
    lazy val isSealedHierarchy = isImmutableSealedHierarchy(valueType)
    lazy val isImmutableCC = isImmutableCaseClass(valueType)

    if (isSeq || isOption || isOpt || isSealedHierarchy || isImmutableCC) {
      q"""null"""
    } else {
      val isCC = isCaseClass(valueType)
      c.abort(c.enclosingPosition,
        s"""
           |The type `$valueType` does not meet immutable value requirements.
           |
           |Immutable value checks:
           |  * isImmutableCaseClass: $isImmutableCC
           |    * isCaseClass: $isCC
           |    * members: ${
                    if (isCC) {
                      val members = filterMembers(valueType).filter(m => !m.isPrivate && m.isTerm)
                      members.map(m => {
                        val resultType = m.typeSignatureIn(valueType).resultType
                        val stable: Boolean = m.asTerm.isStable
                        val isImmutable = checkModel(
                          q"""
                            implicit val ${TermName(c.freshName())}: $ImmutableValueCls[$valueType] = null
                            implicitly[$ImmutableValueCls[$resultType]]
                          """
                        )
                        s"${m.name}: $resultType -> stable: $stable, isImmutableValue: $isImmutable; "
                      }).map(s => s"\n      - $s").mkString("") +
                        s"\n    Use ImmutableValue.isImmutable[${members.headOption.map(_.typeSignatureIn(valueType).resultType).getOrElse("T")}] to get more details about `isImmutableValue` check."
                    } else "Visible only for case classes."
                  }
           |  * isImmutableSealedHierarchy: $isSealedHierarchy
           |  * isImmutableSeq: $isSeq - for example: Seq[String]
           |  * isImmutableOption: $isOption - for example: Option[String]
           |  * isImmutableOpt: $isOpt - for example: Opt[String]
           |
           |""".stripMargin
      )
    }
  }

  def reifyModelValue[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    val immutable = isImmutableValue(valueType)
    lazy val modelPart = isModelPart(valueType)
    lazy val modelSeq = isModelSeq(valueType)
    if (immutable || modelPart || modelSeq) {
      q"""null"""
    } else {
      c.abort(c.enclosingPosition,
        s"""
           |The type `$valueType` does not meet model value requirements. This is not immutable value nor valid model part nor model seq.
           |
           |Model value checks:
           |  * isImmutable: $immutable (check: ImmutableValue.isImmutable[$valueType] for details)
           |  * isModelPart: $modelPart
           |  * isModelSeq: $modelSeq
           |
           |Use ModelPart.isModelPart[$valueType] and ModelSeq.isModelSeq[$valueType] to get more details about `isModelPart` and `isModelSeq` checks.
           |
           |""".stripMargin
      )
    }
  }

  def reifyModelPart[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]

    val isClass: Boolean = valueType.typeSymbol.isClass
    val isTrait = isClass && valueType.typeSymbol.asClass.isTrait
    val isNotSealedTrait = isClass && !valueType.typeSymbol.asClass.isSealed
    val isNotSeq = !(valueType <:< SeqTpe) && !(valueType <:< MutableSeqTpe)

    lazy val isModelCC: Boolean = isModelCaseClass(valueType)

    val members = filterMembers(valueType)
    val unimplementableTraitMembers = members.collect {
      case s if isAbstractMethod(s) && takesParameters(s) => s
    }
    val doesNotContainVars = !members.exists {
      case s if s.isTerm && s.asTerm.isVar => true
      case _ => false
    }
    val isImplementableTrait = isTrait && unimplementableTraitMembers.isEmpty && doesNotContainVars

    val propertyElementsToTypeCheck = if (isTrait && isNotSealedTrait && isNotSeq) {
      traitBasedPropertyPossibleMemebers(valueType)
    } else if (isModelCC) {
      ccBasedPropertyMembers(valueType)
    } else Seq.empty

    if (isTrait && isNotSealedTrait && isNotSeq && isImplementableTrait) {
      q"""
         implicit val ${TermName(c.freshName())}: $ModelPartCls[$valueType] = null
         ..${
           propertyElementsToTypeCheck.map(s => {
             val resultType = s.typeSignatureIn(valueType).resultType
             q"""implicitly[$ModelValueCls[$resultType]]"""
           })
         }
         null
      """
    } else if (isModelCC) {
        q"""
         implicit val ${TermName(c.freshName())}: $ModelPartCls[$valueType] = null
         ..${
           propertyElementsToTypeCheck.map(m => {
             val resultType = m.typeSignatureIn(valueType).resultType
             q"""implicitly[$ModelValueCls[$resultType]]"""
           })
         }
         null
       """
    } else {
      val isCC = isCaseClass(valueType)
      c.abort(c.enclosingPosition,
        s"""
           |The type `$valueType` does not meet model part requirements. It must be (not sealed) trait or immutable case class.
           |
           |Model part checks:
           |* for traits:
           |  * isTrait: $isTrait
           |  * isImplementableTrait: $isImplementableTrait
           |  * isNotSealedTrait: $isNotSealedTrait
           |  * isNotSeq: $isNotSeq
           |  * doesNotContainVars: $doesNotContainVars
           |  * members: ${
                  if (isTrait && isNotSealedTrait && isNotSeq)
                    propertyElementsToTypeCheck.map(m => s"${m.name}: ${m.typeSignatureIn(valueType).resultType} " +
                      s"-> isModelValue: ${isModelValue(m.typeSignatureIn(valueType).resultType)};"
                    ).map(s => s"\n    - $s").mkString("")
                  else "Visible only for traits."
                }
           |  * unimplementableTraitMembers: ${
                  if (isTrait && isNotSealedTrait && isNotSeq)
                    unimplementableTraitMembers
                      .map(m => s"${m.name}: ${m.typeSignatureIn(valueType).resultType}")
                      .map(s => s"\n    - $s").mkString("")
                  else "Visible only for traits."
                }
           |* for case class:
           |  * isCaseClass: $isCC
           |  * hasOneParamsListInPrimaryConstructor: ${
                isCC && findPrimaryConstructor(valueType).paramLists.size == 1
              }
           |  * members: ${
                  if (isCC)
                    if (propertyElementsToTypeCheck.nonEmpty) {
                      propertyElementsToTypeCheck
                        .map(m => s"${m.name}: ${m.typeSignatureIn(valueType).resultType} " +
                          s"-> isCaseAccessor: ${m.isMethod && m.asMethod.isCaseAccessor}, " +
                          s"isModelValue: ${isModelValue(m.typeSignatureIn(valueType).resultType)};"
                        ).map(s => s"\n    - $s").mkString("")
                    } else "No members found."
                  else "Visible only for case classes."
                }
           |
           |Use ModelValue.isModelValue[${propertyElementsToTypeCheck.headOption.getOrElse("T")}] to get more details about `isModelValue` check.
           |
           |""".stripMargin
      )
    }
  }

  def reifyModelSeq[T: c.WeakTypeTag]: c.Tree = {
    val valueType = weakTypeOf[T]
    val isSeq = valueType.dealias.widen.typeSymbol == SeqTpe.typeSymbol
    lazy val typeArgsCheck = valueType.typeArgs.map(m => (m, isModelValue(m)))
    if (isSeq && typeArgsCheck.forall(_._2)) {
      q"""null"""
    } else {
      c.abort(c.enclosingPosition,
        s"""
           |The type `$valueType` does not meet model seq requirements. This must be Seq with valid model value as type arg.
           |
           |Model Seq checks:
           |  * isSeqType: $isSeq
           |  * type arg: ${
                  if (isSeq) typeArgsCheck.map(result => s"${result._1} -> isModelValue: ${result._2}; ").head
                  else "Visible only for Seqs."
                }
           |
           |Use ModelValue.isModelValue[${typeArgsCheck.head._1}] to get more details.
           |
           |""".stripMargin
      )
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
    def impl(members: Map[TermName, Type], getCreator: Tree): Tree = {
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
            if (isEmpty) null.asInstanceOf[$tpe]
            else $getCreator

          def set(newValue: $tpe, force: Boolean = false): Unit = if (newValue != null) {
            isEmpty = false
            $CallbackSequencerCls.sequence {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""getSubProperty[$returnTpe](${name.toString}).set(newValue.$name, force)"""
                }
              }
            }
          } else if (!isEmpty) {
            $CallbackSequencerCls.sequence {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""getSubProperty[$returnTpe](${name.toString}).set(null.asInstanceOf[$returnTpe])"""
                }
              }
            }
          }

          def setInitValue(newValue: $tpe): Unit = if (newValue != null) {
            isEmpty = false
            ..${
              members.map { case (name, returnTpe) =>
                q"""getSubProperty[$returnTpe](${name.toString}).setInitValue(newValue.$name)"""
              }
            }
          } else if (!isEmpty) {
            $CallbackSequencerCls.sequence {
              ..${
                members.map { case (name, returnTpe) =>
                  q"""getSubProperty[$returnTpe](${name.toString}).setInitValue(null.asInstanceOf[$returnTpe])"""
                }
              }
            }
          }

          def touch(): Unit = $CallbackSequencerCls.sequence {
            ..${
              members.map { case (name, returnTpe) =>
                q"""getSubProperty[$returnTpe](${name.toString}).touch()"""
              }
            }
          }
        }
      """
    }

    if (isCaseClass(tpe)) {
      val order = findPrimaryConstructor(tpe).paramLists.flatten.map(m => m.name.toTermName)
      val members = ccBasedPropertyMembers(tpe)
        .map(m => m.asMethod.name -> m.typeSignatureIn(tpe).resultType).toMap

      impl(members,
        q"""
          new ${tpe.typeSymbol}(
            ..${
              order.map { case name =>
                val returnTpe = members(name)
                q"""getSubProperty[$returnTpe](${name.toString}).get"""
              }
            }
          )
         """
      )
    } else {
      val members = traitBasedPropertyMembers(tpe).map(method => (method.asMethod.name, method.typeSignatureIn(tpe).resultType))

      impl(members.toMap,
        q"""
          new $tpe {
            ..${
              members.map { case (name, returnTpe) =>
                q"""override val $name: $returnTpe = getSubProperty[$returnTpe](${name.toString}).get"""
              }
            }
          }
        """
      )
    }
  }

  def reifySubProperty[A: c.WeakTypeTag, B: c.WeakTypeTag](f: c.Expr[A => B])(ev: c.Expr[_]): c.Tree = {
    val model = c.prefix
    val modelPath = getModelPath(f.tree)

    def checkIfPathIsModelPart(tree: Tree): Boolean = tree match {
      case Ident(_) =>
        true
      case s@Select(next, t) if isModelPart(s.tpe) && isValidSubproperty(next.tpe, t) =>
        checkIfPathIsModelPart(next)
      case s@Select(next, t) =>
        c.abort(c.enclosingPosition,
          s"""
             |The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.
             | * $t ${if (isValidSubproperty(next.tpe, t)) "is" else "is NOT"} a valid subproperty (abstract val/def for trait based model or constructor element for case class based model)
             | * ${s.tpe} ${if (isModelPart(s.tpe)) "is" else "is NOT"} a ModelPart (check ModelPart.isModelPart[${s.tpe}])
             |
             |""".stripMargin
        )
      case _ =>
        c.abort(c.enclosingPosition, s"The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.")
    }

    def checkIfIsValidPath(tree: Tree): Boolean = tree match {
      case s@Select(next, t) if (isImmutableValue(s.tpe) || isModelSeq(s.tpe) || isModelPart(s.tpe)) && isValidSubproperty(next.tpe, t) =>
        checkIfPathIsModelPart(next)
      case s@Select(next, t) =>
        c.abort(c.enclosingPosition,
          s"""
             |The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.
             | * $t ${if (isValidSubproperty(next.tpe, t)) "is" else "is NOT"} a valid subproperty (abstract val/def for trait based model or constructor element for case class based model)
             | * ${s.tpe} ${if (isImmutableValue(s.tpe)) "is" else "is NOT"} an immutable value (check ImmutableValue.isImmutable[${s.tpe}])
             | * ${s.tpe} ${if (isModelPart(s.tpe)) "is" else "is NOT"} a ModelPart (check ModelPart.isModelPart[${s.tpe}])
             | * ${s.tpe} ${if (isModelSeq(s.tpe)) "is" else "is NOT"} a ModelSeq (check ModelSeq.isModelSeq[${s.tpe}])
             |
             |""".stripMargin
        )
      case _ =>
        c.abort(c.enclosingPosition, s"The path must consist of ModelParts and only leaf can be ImmutableValue or ModelSeq.")
    }

    checkIfIsValidPath(modelPath)

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
    val implicitSelfPc =
      if (!c.inferImplicitValue(getType(tq"$PropertyCreatorCls[$tpe]"), withMacrosDisabled = true).isEmpty) q""
      else q"implicit val ${c.freshName(TermName("self"))}: $PropertyCreatorCls[$tpe] = this"
    q"""
       new $PropertyCreatorCls[$tpe] {
         $implicitSelfPc
         def newProperty(prt: $ReadablePropertyCls[_])(implicit ec: $ExecutionContextCls): $PropertyCls[$tpe] with $CastablePropertyCls[$tpe] = {
           ${constructor.apply(tpe)}
         }
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
      c.abort(c.enclosingPosition,
        s"""
           |`$valueType` should meet requirements for one of:
           |  * model value - it has to be an immutable value (io.udash.properties.ImmutableValue[$valueType] has to exist), model part or model seq
           |  * model part - it has to be a trait with abstract methods returning valid model values or an immutable case class
           |  * model seq - it has to be Seq[T] where T is a valid model value
           |
           |Try to call one of these methods in your code to get more details:
           |  * ImmutableValue.isImmutable[$valueType]
           |  * ModelValue.isModelValue[$valueType]
           |  * ModelPart.isModelPart[$valueType]
           |  * ModelSeq.isModelSeq[$valueType]
           |
           |""".stripMargin
      )
    }
  }

  def autoReifyPropertyCreator[T: c.WeakTypeTag]: c.Tree = {
    val valueType: c.universe.Type = weakTypeOf[T]

    if (PropertyMacrosCache.creators.contains(valueType))
      c.warning(c.enclosingPosition,
        s"""Generating PropertyCreator[$valueType] more than once. You should create it in the companion object explicitly.
           |Example: implicit val pc: PropertyCreator[$valueType] = PropertyCreator.propertyCreator
           |
           |""".stripMargin)
    else
      PropertyMacrosCache.creators.add(valueType)

    reifyPropertyCreator[T]
  }
}

private object PropertyMacrosCache {
  val creators: mutable.Set[Any] = mutable.Set.empty
  val isModelValueCache: mutable.Set[Any] = mutable.Set.empty
  val isModelPartCache: mutable.Set[Any] = mutable.Set.empty
  val isModelSeqCache: mutable.Set[Any] = mutable.Set.empty
  val isImmutableCache: mutable.Set[Any] = mutable.Set.empty
}
