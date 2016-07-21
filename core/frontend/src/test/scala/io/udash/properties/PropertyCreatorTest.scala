package io.udash.properties

import io.udash.testing.UdashFrontendTest

class PropertyCreatorTest extends UdashFrontendTest {
  // DO NOT REMOVE THIS IMPORT!
  import io.udash.properties.single._
  import io.udash.properties.model._
  import io.udash.properties.seq._

  "PropertyCreator" should {
    "create Property for basic types (and handle init value)" in {
      """val p = Property[String]""".stripMargin should compile

      """val p = Property[String]("ABC")""".stripMargin should compile

      """val p = Property[String](2)""".stripMargin shouldNot typeCheck

      """val p = Property[Int](2)""".stripMargin should compile
    }

    "create Property dom.Element" in {
      """val p = Property[org.scalajs.dom.Element]""".stripMargin should compile
    }

    "create Property for immutable case class" in {
      """case class A(s: String, i: Int)
        |val p = Property[A]""".stripMargin should compile

      """case class A(s: String, i: Int)
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """case class A(s: String, var i: Int)
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) { var x = "x" }
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) { def x = "x" }
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) { def x = "x" }
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """class C { def x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """class C { var x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin shouldNot compile

      """class C { val x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """class C { var x = 5 }
        |case class A(s: String, i: Int, c: C)
        |val p = Property[A](A("bla", 5, new C))""".stripMargin shouldNot compile

      """case class A(s: String, i: Int)
        |val p = Property[A](5)""".stripMargin shouldNot typeCheck

      """case class A(s: String, i: Int)
        |val p = Property[A]("bla")""".stripMargin shouldNot typeCheck
    }

    "create Property for sealed trait" in {
      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T]""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T](A)""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T](B)""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |case class C(t: T) extends T
        |val p = Property[T](C(C(A)))""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |case object C
        |val p = Property[T](C)""".stripMargin shouldNot typeCheck
    }

    "create ModelProperty for trait" in {
      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[T].asModel""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def x: Int = 5
        |}
        |val p = Property[T].asModel""".stripMargin should compile

      """trait X {
        |  def a: String
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |val p = Property[T].asModel
        |val x = p.subModel(_.x)""".stripMargin should compile
    }

    "create ModelProperty for recursive trait" in {
      """trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |}
        |val p = Property[T].asModel""".stripMargin should compile

      """trait X {
        |  def a: String
        |  def t: T
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |val p = Property[T].asModel
        |val x = p.subModel(_.x)""".stripMargin should compile
    }

    "create model property for simple case classes" in {
      """case class Simple(i: Int, s:  String)
        |val p = ModelProperty[Simple](Simple(1, "x"))
        |p.subProp(_.i).set(5)
        |p.subProp(_.s).set("3")
        |""".stripMargin should compile

      """case class A(s: String, i: Int)
        |val p = Property[A].asModel""".stripMargin should compile

      """case class A(s: Seq[String], i: Seq[Int])
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int)
        |case class B(a: A)
        |val p = ModelProperty[B]
        |val sub = p.subModel(_.a)""".stripMargin should compile

      """case class A(s: Seq[String], i: Seq[Int])
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """case class A(s: Seq[String], i: A)
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile
    }

    "create ModelProperty for tuples" in {
      """val p = ModelProperty(Tuple1("String"))""".stripMargin should compile
      """val p = ModelProperty(("String", 25))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4, 5))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4, 5, 6))""".stripMargin should compile
    }

    "not create ModelProperty for anything other than trait or simple case class" in {
      """val p = Property[Int].asModel""".stripMargin shouldNot compile

      """val p = ModelProperty[String]""".stripMargin shouldNot compile

      """val p = Property[Seq[Int]].asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T].asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[T]].asModel""".stripMargin shouldNot compile

      """case class A(s: String, i: Int)
        |val p = Property[Seq[A]].asModel""".stripMargin shouldNot compile

      """case class NotSimple(i: Int, var s: String)
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """case class NotSimple(i: Int, s: String) { val t = 5 }
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """case class NotSimple(i: Int, s: String) { var t = 5 }
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """case class NotSimple(i: Int, s: String) { def t = 5 }
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """class C { val x = 5 }
        |case class NotSimple(i: Int, s: String) extends C
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """class C { var x = 5 }
        |case class NotSimple(i: Int, c: C)
        |val p = ModelProperty[NotSimple]""".stripMargin shouldNot compile

      """case class A(s: Seq[String], i: scala.collection.mutable.Seq[Int])
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin shouldNot compile
    }

    "create SeqProperty for Seq" in {
      """val p = Property[Seq[Int]].asSeq[Int]""".stripMargin should compile

      """val p = Property[Seq[Seq[Int]]](Seq(Seq(1,2))).asSeq[Seq[Int]]
        |val p2 = p.elemProperties.head.asSeq[Int]
        |val i: Property[Int] = p2.elemProperties.head
        |i.set(5)""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[T]].asSeq[T]""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[Seq[T]]].asSeq[T]""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |}
        |val p = Property[Seq[T]].asSeq[T]
        |val m: ModelProperty[T] = p.elemProperties.head.asModel
        |val sub = m.subProp(_.s)
        |val sub2 = m.subModel(_.t)""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |}
        |
        |val p = Property[Seq[Seq[T]]].asSeq[Seq[T]]
        |val p2 = p.elemProperties.head.asSeq[T]
        |val m: ModelProperty[T] = p2.elemProperties.head.asModel
        |val sub = m.subProp(_.s)
        |val sub2 = m.subModel(_.t)""".stripMargin should compile

      """trait X {
        |  def a: String
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |val p = Property[Seq[T]].asSeq[T]""".stripMargin should compile

      """trait X {
        |  def a: String
        |  def t: T
        |  def x: X
        |  def st: Seq[T]
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |  def x: X
        |  def sx: Seq[X]
        |}
        |val p = Property[Seq[T]].asSeq[T]
        |val p2 = Property[Seq[Seq[T]]].asSeq[Seq[T]]""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]].asSeq[T]
        |val m: Property[T] = p.elemProperties.head""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]].asSeq[T]
        |val m: ModelProperty[T] = p.elemProperties.head.asModel""".stripMargin shouldNot compile

      """val p = SeqProperty[(String, Option[String])]""".stripMargin should compile

      """val items = SeqProperty(
        |  Seq.fill(7)((3.1, 2.5, 4.564))
        |)""".stripMargin should compile
    }

    "not create SeqProperty for anything other than Seq" in {
      """val p = Property[Int].asSeq""".stripMargin shouldNot compile

      """val p = Property[String].asSeq""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T].asSeq""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]].asSeq""".stripMargin shouldNot compile

      """case class A(s: String, i: Int)
        |val p = Property[A].asSeq""".stripMargin shouldNot compile

      """case class A(s: String, i: Int)
        |val p = Property[Seq[A]].asSeq""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[T]].asSeq""".stripMargin shouldNot compile

      """trait T {
        |  def i: Seq[Int]
        |  def s: String
        |  def t: T
        |}
        |val p = Property[T].asSeq""".stripMargin shouldNot compile

      """trait X {
        |  def a: String
        |  def t: T
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: Seq[X]
        |}
        |val p = Property[T].asSeq""".stripMargin shouldNot compile
    }

    "create complex properties" in {
      """case class C(i: Int, s: String)
        |trait T {
        |  def i: Int
        |  def s: Option[String]
        |  def t: ST
        |}
        |trait ST {
        |  def c: C
        |  def s: Seq[Char]
        |}
        |val p = Property[T].asModel
        |val s = p.subModel(_.t).subSeq(_.s)
        |val s2 = p.subSeq(_.t.s)
        |val i = p.subProp(_.t.c.i)""".stripMargin should compile

      """case class RestExampleClass(i: Int, s: String, tuple: (Double, String))
        |trait ExampleModel {
        |  def string: String
        |  def int: Int
        |  def cls: RestExampleClass
        |}
        |val responsesModel = ModelProperty[ExampleModel]""".stripMargin should compile

      """sealed trait Fruit
        |case class Apple(name: String) extends Fruit
        |case object Orange extends Fruit
        |
        |case class DemoCaseClass(x: String, i: Int)
        |
        |trait GenCodecsDemoModel {
        |  def int: Int
        |  def double: Double
        |  def string: String
        |  def seq: Seq[String]
        |  def map: Seq[(String, Int)]
        |  def caseClass: DemoCaseClass
        |  def clsInt: Int
        |  def clsString: String
        |  def clsVar: Int
        |  def sealedTrait: Fruit
        |}
        |
        |val GenCodecs = ModelProperty[GenCodecsDemoModel]""".stripMargin should compile
    }

    "not create property for mutable class" in {
      """val p = Property[scala.collection.mutable.ArrayBuffer[Int]]""".stripMargin shouldNot compile

      """class C {
        |  var i = 0
        |  def inc() = i += 1
        |}
        |val p = Property[C]""".stripMargin shouldNot compile

      """trait T {
        |  var i = 0
        |}
        |class C extends T {
        |  def inc() = i += 1
        |}
        |val p = Property[C]""".stripMargin shouldNot compile
    }
  }
}
