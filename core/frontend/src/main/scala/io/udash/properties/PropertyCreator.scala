package io.udash.properties

import java.util.UUID

import com.avsystem.commons.misc.Opt
import io.udash.properties.single.{CastableProperty, ReadableProperty}

trait PropertyCreator[T] {
  def newProperty(prt: ReadableProperty[_]): CastableProperty[T]

  def newProperty(value: T, prt: ReadableProperty[_]): CastableProperty[T] = {
    val prop = newProperty(prt)
    prop.setInitValue(value)
    prop
  }
}

class MacroPropertyCreator[T](creator: (ReadableProperty[_]) => CastableProperty[T]) extends PropertyCreator[T] {
  def newProperty(prt: ReadableProperty[_]): CastableProperty[T] =
    creator(prt)
}

object PropertyCreator {
  def propertyCreator[T]: PropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyPropertyCreator[T]

  implicit def autoPropertyCreator[T]: PropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.autoReifyPropertyCreator[T]

  implicit val propertyCreatorInt: PropertyCreator[Int] = propertyCreator[Int]
  implicit val propertyCreatorLong: PropertyCreator[Long] = propertyCreator[Long]
  implicit val propertyCreatorDouble: PropertyCreator[Double] = propertyCreator[Double]
  implicit val propertyCreatorFloat: PropertyCreator[Float] = propertyCreator[Float]
  implicit val propertyCreatorString: PropertyCreator[String] = propertyCreator[String]
  implicit val propertyCreatorChar: PropertyCreator[Char] = propertyCreator[Char]
  implicit val propertyCreatorBoolean: PropertyCreator[Boolean] = propertyCreator[Boolean]

  implicit val propertyCreatorSInt: PropertyCreator[Seq[Int]] = propertyCreator[Seq[Int]]
  implicit val propertyCreatorSLong: PropertyCreator[Seq[Long]] = propertyCreator[Seq[Long]]
  implicit val propertyCreatorSDouble: PropertyCreator[Seq[Double]] = propertyCreator[Seq[Double]]
  implicit val propertyCreatorSFloat: PropertyCreator[Seq[Float]] = propertyCreator[Seq[Float]]
  implicit val propertyCreatorSString: PropertyCreator[Seq[String]] = propertyCreator[Seq[String]]
  implicit val propertyCreatorSChar: PropertyCreator[Seq[Char]] = propertyCreator[Seq[Char]]
  implicit val propertyCreatorSBoolean: PropertyCreator[Seq[Boolean]] = propertyCreator[Seq[Boolean]]

  implicit val propertyCreatorOInt: PropertyCreator[Option[Int]] = propertyCreator[Option[Int]]
  implicit val propertyCreatorOLong: PropertyCreator[Option[Long]] = propertyCreator[Option[Long]]
  implicit val propertyCreatorODouble: PropertyCreator[Option[Double]] = propertyCreator[Option[Double]]
  implicit val propertyCreatorOFloat: PropertyCreator[Option[Float]] = propertyCreator[Option[Float]]
  implicit val propertyCreatorOString: PropertyCreator[Option[String]] = propertyCreator[Option[String]]
  implicit val propertyCreatorOChar: PropertyCreator[Option[Char]] = propertyCreator[Option[Char]]
  implicit val propertyCreatorOBoolean: PropertyCreator[Option[Boolean]] = propertyCreator[Option[Boolean]]

  implicit val propertyCreatorOptInt: PropertyCreator[Opt[Int]] = propertyCreator[Opt[Int]]
  implicit val propertyCreatorOptLong: PropertyCreator[Opt[Long]] = propertyCreator[Opt[Long]]
  implicit val propertyCreatorOptDouble: PropertyCreator[Opt[Double]] = propertyCreator[Opt[Double]]
  implicit val propertyCreatorOptFloat: PropertyCreator[Opt[Float]] = propertyCreator[Opt[Float]]
  implicit val propertyCreatorOptString: PropertyCreator[Opt[String]] = propertyCreator[Opt[String]]
  implicit val propertyCreatorOptChar: PropertyCreator[Opt[Char]] = propertyCreator[Opt[Char]]
  implicit val propertyCreatorOptBoolean: PropertyCreator[Opt[Boolean]] = propertyCreator[Opt[Boolean]]

  implicit val propertyCreatorDomElement: PropertyCreator[org.scalajs.dom.Element] = PropertyCreator.propertyCreator[org.scalajs.dom.Element]
  implicit val propertyCreatorSDomElement: PropertyCreator[Seq[org.scalajs.dom.Element]] = PropertyCreator.propertyCreator[Seq[org.scalajs.dom.Element]]
  implicit val propertyCreatorODomElement: PropertyCreator[Option[org.scalajs.dom.Element]] = PropertyCreator.propertyCreator[Option[org.scalajs.dom.Element]]

  implicit val propertyCreatorJavaDate: PropertyCreator[java.util.Date] = PropertyCreator.propertyCreator[java.util.Date]
  implicit val propertyCreatorSJavaDate: PropertyCreator[Seq[java.util.Date]] = PropertyCreator.propertyCreator[Seq[java.util.Date]]
  implicit val propertyCreatorOJavaDate: PropertyCreator[Option[java.util.Date]] = PropertyCreator.propertyCreator[Option[java.util.Date]]

  def newID(): UUID = UUID.randomUUID()
}