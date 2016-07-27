package io.udash.properties

import java.util.UUID

import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

object Property {
  /** Creates empty DirectProperty[T]. */
  def apply[T](implicit pc: PropertyCreator[T], ec: ExecutionContext): CastableProperty[T] =
    pc.newProperty(null)

  /** Creates DirectProperty[T] with initial value. */
  def apply[T](init: T)(implicit pc: PropertyCreator[T], ec: ExecutionContext): CastableProperty[T]=
    pc.newProperty(init, null)
}

/** Property which can be casted. <br/>
  * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works".
  */
trait CastableReadableProperty[A] extends ReadableProperty[A] {
  /** Safely casts DirectProperty[A] to ModelProperty[A] */
  def asModel(implicit ev: ModelPart[A]): ReadableModelProperty[A] =
    this.asInstanceOf[ReadableModelProperty[A]]

  /** Safely casts DirectProperty[Seq[A]] to DirectSeqProperty[A] */
  def asSeq[B](implicit ev: ModelSeq[A], sev: A =:= Seq[B]): ReadableSeqProperty[B, CastableReadableProperty[B]] =
    this.asInstanceOf[ReadableSeqProperty[B, CastableReadableProperty[B]]]
}

/** Property which can be casted. <br/>
  * <b>NOTE:</b> Those casts are checked in compilation time: "it compiles" == "it works".
  */
trait CastableProperty[A] extends CastableReadableProperty[A] with Property[A] {
  /** Safely casts DirectProperty[A] to ModelProperty[A] */
  override def asModel(implicit ev: ModelPart[A]): ModelProperty[A] =
    this.asInstanceOf[ModelProperty[A]]

  /** Safely casts DirectProperty[Seq[A]] to DirectSeqProperty[A] */
  override def asSeq[B](implicit ev: ModelSeq[A], sev: A =:= Seq[B]): SeqProperty[B, CastableProperty[B]] =
    this.asInstanceOf[SeqProperty[B, CastableProperty[B]]]
}

/** Base interface of every Property in Udash. */
trait ReadableProperty[A] {
  protected[this] val listeners: mutable.Set[A => Any] = mutable.Set()

  protected[this] val validators: mutable.Set[Validator[A]] = mutable.Set()
  protected[this] var validationResult: Future[ValidationResult] = null

  implicit protected[properties] def executionContext: ExecutionContext

  /** Unique property ID. */
  def id: UUID

  /** @return Current property value. */
  def get: A

  /** Registers listener which will be called on value change. */
  def listen(l: A => Any): Registration = {
    listeners += l
    new PropertyRegistration(listeners, l)
  }

  /** @return validation result as Future, which will be completed on the validation process ending. It can fire validation process if needed. */
  def isValid: Future[ValidationResult] = {
    if (validationResult == null) validate()
    validationResult
  }

  /**
    * Combines two properties into a new one. Created property will be updated after any change in the origin ones.
    *
    * @param property `Property[B]` to combine with `this`.
    * @param combinedParent Parent of combined property, `null` by default.
    * @param combiner Method combining values A and B into O.
    * @tparam B Type of elements in provided property.
    * @tparam O Output property elements type.
    * @return Property[O] updated on any change in `this` or `property`.
    */
  def combine[B, O : ModelValue](property: ReadableProperty[B], combinedParent: ReadableProperty[_] = null)
                                (combiner: (A, B) => O): ReadableProperty[O] = {
    val pc = implicitly[PropertyCreator[O]]
    val output = pc.newProperty(combinedParent)

    def update(x: A, y: B): Unit =
      output.set(combiner(x, y))

    output.setInitValue(combiner(get, property.get))
    listen(x => update(x, property.get))
    property.listen(y => update(get, y))
    output
  }

  /**
    * Creates ReadableProperty[B] linked to `this`. Changes will be synchronized with `this`.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @tparam B Type of new Property.
    * @return New ReadableProperty[B], which will be synchronised with original ReadableProperty[A].
    */
  def transform[B](transformer: A => B): ReadableProperty[B] =
    new TransformedReadableProperty[A, B](this, transformer, PropertyCreator.newID())

  /**
    * Creates ReadableSeqProperty[B] linked to `this`. Changes will be synchronized with `this`.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original ReadableProperty[A].
    */
    def transform[B : ModelValue](transformer: A => Seq[B]): ReadableSeqProperty[B, ReadableProperty[B]] =
      new ReadableSeqPropertyFromSingleValue(this, transformer, executionContext)

  protected[properties] def parent: ReadableProperty[_]

  protected[properties] def fireValueListeners(): Unit = {
    val t = get
    CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => listeners.foreach(_.apply(t)))
  }

  protected[properties] def valueChanged(): Unit = {
    validationResult = null
    fireValueListeners()
    if (parent != null) parent.valueChanged()
  }

  protected[properties] def validate(): Unit = {
    if (validators.nonEmpty) {
      CallbackSequencer.queue(s"${this.id.toString}:fireValidation", () => {
        import Validator._
        validationResult = Future.sequence(
          validators.collect { case v => v(this.get) }.toSeq
        ).foldValidationResult
      })
    } else validationResult = Future.successful(Valid)
  }
}

abstract class BaseReadableSeqPropertyFromSingleValue[A, B : ModelValue](origin: ReadableProperty[A], transformer: A => Seq[B],
                                                                         override val executionContext: ExecutionContext)
  extends ReadableSeqProperty[B, ReadableProperty[B]] {

  override val id: UUID = PropertyCreator.newID()
  override protected[properties] val parent: Property[_] = null
  protected val structureListeners: mutable.Set[Patch[Property[B]] => Any] = mutable.Set()

  val pc = implicitly[PropertyCreator[B]]
  protected val children = mutable.ListBuffer.empty[Property[B]]

  update(origin.get)
  origin.listen(update)

  private def structureChanged(patch: Patch[Property[B]]): Unit =
    CallbackSequencer.queue(
      s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}",
      () => structureListeners.foreach(_.apply(patch))
    )

  private def update(v: A): Unit = {
    val transformed = transformer(v)
    val current = get

    def commonIdx(s1: Iterator[B], s2: Iterator[B]): Int =
      math.max(0,
        s1.zipAll(s2, null, null).zipWithIndex
          .indexWhere { case (((x, y), idx)) => x != y })

    val commonBegin = commonIdx(transformed.iterator, current.iterator)
    val commonEnd = commonIdx(transformed.reverseIterator, current.reverseIterator)

    val patch = if (transformed.size > current.size) {
      val added: Seq[CastableProperty[B]] = Seq.fill(transformed.size - current.size)(pc.newProperty(this)(executionContext))
      children.insertAll(commonBegin, added)
      Some(Patch(commonBegin, Seq(), added, false))
    } else if (transformed.size < current.size) {
      val removed = children.slice(commonBegin, commonBegin + current.size - transformed.size)
      children.remove(commonBegin, current.size - transformed.size)
      Some(Patch(commonBegin, removed, Seq(), transformed.isEmpty))
    } else None

    CallbackSequencer.sequence {
      transformed.zip(children)
        .slice(commonBegin, math.max(commonBegin + transformed.size - current.size, transformed.size - commonEnd))
        .foreach { case (pv, p) => p.set(pv) }
      patch.foreach(structureChanged)
      valueChanged()
    }
  }

  override def get: Seq[B] =
    children.map(_.get)

  override def elemProperties: Seq[ReadableProperty[B]] =
    children.map(_.transform((id: B) => id))
}

class ReadableSeqPropertyFromSingleValue[A, B : ModelValue](origin: ReadableProperty[A], transformer: A => Seq[B],
                                                                 override val executionContext: ExecutionContext)
  extends BaseReadableSeqPropertyFromSingleValue(origin, transformer, executionContext) {
  /** Registers listener, which will be called on every property structure change. */
  override def listenStructure(l: (Patch[ReadableProperty[B]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}

/** Represents ReadableProperty[A] transformed to ReadableProperty[B]. */
class TransformedReadableProperty[A, B](private val origin: ReadableProperty[A],
                                        transformer: A => B,
                                        override val id: UUID) extends ReadableProperty[B] {
  override def listen(l: (B) => Any): Registration =
    origin.listen((a: A) => l(transformer(a)))

  override def get: B =
    transformer(origin.get)

  override protected[properties] def fireValueListeners(): Unit =
    origin.fireValueListeners()

  override protected[properties] def parent: ReadableProperty[_] =
    origin.parent

  override def validate(): Unit =
    origin.validate()

  override protected[properties] def valueChanged(): Unit =
    origin.valueChanged()

  override implicit protected[properties] def executionContext: ExecutionContext =
    origin.executionContext
}

/** Property which can be modified. */
trait Property[A] extends ReadableProperty[A] {
  /** Changes current property value. Fires value change listeners.
    * @param t Should not be null! */
  def set(t: A): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new PropertyRegistration(validators, v)
  }

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(f: (A) => ValidationResult): Registration =
    addValidator(Validator(f))

  /**
    * Creates Property[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @param revert Method transforming type B of new Property to type A of existing Property.
    * @tparam B Type of new Property.
    * @return New Property[B], which will be synchronised with original Property[A].
    */
  def transform[B](transformer: A => B, revert: B => A): Property[B] =
    new TransformedProperty[A, B](this, transformer, revert, PropertyCreator.newID())

  /**
    * Creates SeqProperty[B] linked to `this`. Changes will be synchronized with `this` in both directions.
    *
    * @param transformer Method transforming type A of existing Property to type Seq[B] of new Property.
    * @param revert Method transforming type Seq[B] to A.
    * @tparam B Type of elements in new SeqProperty.
    * @return New ReadableSeqProperty[B], which will be synchronised with original Property[A].
    */
  def transform[B : ModelValue](transformer: A => Seq[B], revert: Seq[B] => A): SeqProperty[B, Property[B]] =
    new SeqPropertyFromSingleValue(this, transformer, revert, executionContext)
}

class SeqPropertyFromSingleValue[A, B : ModelValue](origin: Property[A], transformer: A => Seq[B], revert: Seq[B] => A,
                                                    override val executionContext: ExecutionContext)
  extends BaseReadableSeqPropertyFromSingleValue[A, B](origin, transformer, executionContext) with SeqProperty[B, Property[B]] {

  override def replace(idx: Int, amount: Int, values: B*): Unit = {
    val current = mutable.ListBuffer(get:_*)
    current.remove(idx, amount)
    current.insertAll(idx, values)
    origin.set(revert(current))
  }

  override def set(t: Seq[B]): Unit =
    origin.set(revert(t))

  override def setInitValue(t: Seq[B]): Unit =
    origin.setInitValue(revert(t))

  override def elemProperties: Seq[Property[B]] =
    children

  override def listenStructure(l: (Patch[Property[B]]) => Any): Registration = {
    structureListeners += l
    new PropertyRegistration(structureListeners, l)
  }
}

/** Represents Property[A] transformed to Property[B]. */
class TransformedProperty[A, B](private val origin: Property[A],
                                transformer: A => B,
                                revert: B => A,
                                override val id: UUID) extends TransformedReadableProperty[A, B](origin, transformer, id) with Property[B] {
  override def set(t: B): Unit =
    origin.set(revert(t))

  override def setInitValue(t: B): Unit =
    origin.setInitValue(revert(t))

  override def addValidator(v: Validator[B]): Registration =
    origin.addValidator(new Validator[A] {
      override def apply(element: A)(implicit ec: ExecutionContext): Future[ValidationResult] =
        v(transformer(element))(ec)
    })
}

abstract class DirectPropertyImpl[A](val parent: ReadableProperty[_], override val id: UUID)
                                    (implicit val executionContext: ExecutionContext)
  extends CastableProperty[A] {

  private var value: A = _

  override def get: A = value

  override def set(t: A): Unit = if (value != t) {
    value = t
    valueChanged()
  }

  override def setInitValue(t: A): Unit =
    value = t
}