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
    * Creates ReadableProperty[B] linked to `this`. Changes will be bidirectionally synchronized between `this` and new property.
    *
    * @param transformer Method transforming type A of existing Property to type B of new Property.
    * @tparam B Type of new Property.
    * @return New ReadableProperty[B], which will be synchronised with original ReadableProperty[A].
    */
  def transform[B](transformer: A => B): ReadableProperty[B] =
    new TransformedReadableProperty[A, B](this, transformer, PropertyCreator.newID())

  def combine[B, O : ModelValue](property: Property[B])(combiner: (A, B) => O): Property[O] = {
    val output = Property[O]
    def update(x: A, y: B): Unit =
      output.set(combiner(x, y))

    listen(x => update(x, property.get))
    property.listen(y => update(get, y))
    update(get, property.get)
    output
  }

  protected[properties] def parent: Property[_]

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

  override protected[properties] def parent: Property[_] =
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
  /** Changes current property value. Fires value change listeners. */
  def set(t: A): Unit

  /** Changes current property value. Does not fire value change listeners. */
  def setInitValue(t: A): Unit

  /** Adds new validator and clears current validation result. It does not fire validation process. */
  def addValidator(v: Validator[A]): Registration = {
    validators += v
    validationResult = null
    new PropertyRegistration(validators, v)
  }

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

abstract class DirectPropertyImpl[A](val parent: Property[_], override val id: UUID)
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