package io.udash.properties

import io.udash.testing.UdashSharedTest

class PropertyCreatorTest extends UdashSharedTest {
  // DO NOT REMOVE THIS IMPORT!
  import io.udash.properties.Properties._

  "PropertyCreator" should {
    "create Property for basic types (and handle init value)" in {
      """val p = Property[String]""".stripMargin should compile

      """val p = Property[String]("ABC")""".stripMargin should compile

      """val p = Property[String](2)""".stripMargin shouldNot typeCheck

      """val p = Property[Int](2)""".stripMargin should compile
    }

    "create Property for class (mutable and immutable)" in {
      """case class A(s: String, i: Int)
        |val p = Property[A]""".stripMargin should compile

      """case class A(s: String, i: Int)
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """case class A(s: String, var i: Int)
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """case class A(s: String, i: Int) { var xasdasdasdasd = "x" }
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """case class A(s: String, i: Int) { def x = "x" }
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """class C { def x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """class C { var x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """class C { val x = "x" }
        |case class A(s: String, i: Int) extends C
        |val p = Property[A](A("bla", 5))""".stripMargin should compile

      """class C { var x = 5 }
        |case class A(s: String, i: Int, c: C)
        |val p = Property[A](A("bla", 5, new C))""".stripMargin should compile

      """case class A(s: String, i: Int)
        |val p = Property[A](5)""".stripMargin shouldNot typeCheck

      """case class A(s: String, i: Int)
        |val p = Property[A]("bla")""".stripMargin shouldNot typeCheck

      """case class A(s: String, i: Int)(x: Int)
        |val p = Property.empty[A]""".stripMargin should compile
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
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """trait T {
        |  val i: Int
        |  val s: String
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |trait T2 {
        |  def t: T
        |}
        |object T2 extends HasModelPropertyCreator[T2]
        |
        |val p = Property[T2].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |trait T2 {
        |  val t: T
        |}
        |object T2 extends HasModelPropertyCreator[T2]
        |
        |val p = Property[T2].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val x = p.subProp(_.x)""".stripMargin should compile

      """object Model {
        |  trait X {
        |    def a: String
        |  }
        |  object X extends HasModelPropertyCreator[X]
        |
        |  trait T {
        |    def i: Int
        |    def s: String
        |    def x: X
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = Property[Model.T].asModel
        |val x = p.subModel(_.x)
        |val a = p.subProp(_.x.a)""".stripMargin should compile

      """trait X {
        |  def a: String
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val x = p.subModel(_.x)""".stripMargin shouldNot compile

      """trait X {
        |  def a: String
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel
        |val x = p.subModel(_.x.a)""".stripMargin shouldNot compile
    }

    "create ModelProperty for recursive trait" in {
      """trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |}
        |object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize[T])
        |
        |val p = Property[T].asModel""".stripMargin should compile

      """trait X {
        |  def a: String
        |  def t: T
        |}
        |object X extends HasModelPropertyCreator[X](ModelPropertyCreator.materialize[X])
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize[T])
        |
        |val p = Property[T].asModel
        |val x = p.subModel(_.x)
        |val t = p.subModel(_.x.t.x.t)""".stripMargin should compile
    }

    "create model property for case classes" in {
      """case class Simple(i: Int, s:  String)
        |implicit val propertyCreator: ModelPropertyCreator[Simple] = ModelPropertyCreator.materialize[Simple]
        |val p = ModelProperty[Simple](Simple(1, "x"))
        |p.subProp(_.i).set(5)
        |p.subProp(_.s).set("3")
        |""".stripMargin should compile

      """case class A(s: String, i: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel""".stripMargin should compile

      """case class A(s: String, i: Int)(x: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel""".stripMargin shouldNot compile

      """case class A(s: String, var i: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  val x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int) {
        |  val x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subProp(_.s)
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  def x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int) {
        |  def x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subProp(_.s)
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  var x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel""".stripMargin shouldNot compile

      """case class A(s: Seq[String], i: Seq[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |case class B(a: A)
        |implicit val propertyCreator2: ModelPropertyCreator[B] = ModelPropertyCreator.materialize[B]
        |val p = ModelProperty[B]
        |val sub = p.subModel(_.a)""".stripMargin should compile

      """case class A(s: Seq[String], i: Seq[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: A)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """object Test {
        |  case class Todo(name: String, editName: String, completed: Boolean = false, editing: Boolean = false)
        |  object Todo extends HasModelPropertyCreator[Todo]
        |  val x = ModelProperty[Todo]
        |}""".stripMargin should compile
    }

    "create ModelProperty for simple classes" in {
      """class A(val s: Seq[String], val i: A)
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A].asModel
        |val test = p.subSeq(_.test)""".stripMargin shouldNot compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A].asModel
        |val test = p.subSeq(_.x)""".stripMargin shouldNot compile
    }

    "create ModelProperty for tuples" in {
      """val p = ModelProperty(Tuple1("String"))""".stripMargin should compile
      """val p = ModelProperty(("String", 25))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4, 5))""".stripMargin should compile
      """val p = ModelProperty(("String", 25, 3, 4, 5, 6))""".stripMargin should compile

      """val p = ModelProperty(("String", 25, 3))
        |val s1 = p.subProp(_._1)
        |val s2 = p.subProp(_._2)
        |val s3 = p.subProp(_._3)
      """.stripMargin should compile

      """val p = ModelProperty(("String", 25, 3))
        |val s1 = p.subProp(_._1)
        |val s2 = p.subProp(_._2)
        |val s3 = p.subProp(_._4)
      """.stripMargin shouldNot compile
    }

    "not create ModelProperty for anything other than trait or simple case class" in {
      """val p = Property[Int].asModel""".stripMargin shouldNot compile

      """val p = ModelProperty[String]""".stripMargin shouldNot compile

      """val p = Property[Seq[Int]].asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T].asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]].asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]].asModel""".stripMargin shouldNot compile

      """object Model {
        |  case class A(s: String, i: Int)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Seq[Model.A]].asModel""".stripMargin shouldNot compile

      """object Model {
        |  case class SimpleWithVar(i: Int, var s: String)
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar]""".stripMargin shouldNot compile

      """object Model {
        |  case class Simple(i: Int, s: String) { val t = 5 }
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple]""".stripMargin should compile

      """object Model {
        |  case class SimpleWithVar(i: Int, s: String) { var t = 5 }
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar]""".stripMargin shouldNot compile

      """object Model {
        |  case class Simple(i: Int, s: String) { def t = 5 }
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple]""".stripMargin should compile

      """object Model {
        |  class C { val x = 5 }
        |  case class Simple(i: Int, s: String) extends C
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple]""".stripMargin should compile

      """object Model {
        |  class C { var x = 5 }
        |  case class SimpleWithVar(i: Int, c: C)
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar]""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: Set[Int])
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subProp(_.i)""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: scala.collection.mutable.Seq[Int])
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A].asModel
        |val s = p.subSeq(_.s)
        |val i = p.subProp(_.i)""".stripMargin should compile
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

      """object Model {
        |  trait T {
        |    def i: Int
        |    def s: String
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize)
        |}
        |val p = Property[Seq[Model.T]].asSeq[Model.T]
        |val m: ModelProperty[Model.T] = p.elemProperties.head.asModel
        |val sub = m.subProp(_.s)
        |val sub2 = m.subModel(_.t)""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def s: String
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize)
        |}
        |
        |val p = Property[Seq[Seq[Model.T]]].asSeq[Seq[Model.T]]
        |val p2 = p.elemProperties.head.asSeq[Model.T]
        |val m: ModelProperty[Model.T] = p2.elemProperties.head.asModel
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
      """object Model {
        |  case class C(i: Int, s: String)
        |  object C extends HasModelPropertyCreator[C]
        |
        |  trait T {
        |    def i: Int
        |    def s: Option[String]
        |    def t: ST
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |
        |  trait ST {
        |    def c: C
        |    def s: Seq[Char]
        |  }
        |  object ST extends HasModelPropertyCreator[ST]
        |}
        |
        |val p = Property[Model.T].asModel
        |val s = p.subModel(_.t).subSeq(_.s)
        |val s2 = p.subSeq(_.t.s)
        |val i = p.subProp(_.t.c.i)""".stripMargin should compile

      """object Model {
        |  case class C(i: Int, s: String)
        |
        |  trait T {
        |    def i: Int
        |    def s: Option[String]
        |    def t: ST
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |
        |  trait ST {
        |    def c: C
        |    def s: Seq[Char]
        |  }
        |  object ST extends HasModelPropertyCreator[ST]
        |}
        |
        |val p = Property[Model.T].asModel
        |val s = p.subModel(_.t).subSeq(_.s)
        |val s2 = p.subSeq(_.t.s)
        |val i = p.subProp(_.t.c.i)""".stripMargin shouldNot compile

      """object Model {
        |  case class RestExampleClass(i: Int, s: String, tuple: (Double, String))
        |  object RestExampleClass extends HasModelPropertyCreator[RestExampleClass]
        |
        |  trait ExampleModel {
        |    def string: String
        |    def int: Int
        |    def cls: RestExampleClass
        |  }
        |  object ExampleModel extends HasModelPropertyCreator[ExampleModel]
        |}
        |
        |val responsesModel = ModelProperty[Model.ExampleModel]""".stripMargin should compile

      """object Model {
        |  sealed trait Fruit
        |  case class Apple(name: String) extends Fruit
        |  case object Orange extends Fruit
        |
        |  case class DemoCaseClass(x: String, i: Int)
        |  object DemoCaseClass extends HasModelPropertyCreator[DemoCaseClass]
        |
        |  trait GenCodecsDemoModel {
        |    def int: Int
        |    def double: Double
        |    def string: String
        |    def seq: Seq[String]
        |    def map: Seq[(String, Int)]
        |    def caseClass: DemoCaseClass
        |    def clsInt: Int
        |    def clsString: String
        |    def clsVar: Int
        |    def sealedTrait: Fruit
        |  }
        |  object GenCodecsDemoModel extends HasModelPropertyCreator[GenCodecsDemoModel]
        |}
        |
        |val GenCodecs = ModelProperty[Model.GenCodecsDemoModel]""".stripMargin should compile
    }

    "not create property for mutable class" in {
      """val p = Property[scala.collection.mutable.ArrayBuffer[Int]]""".stripMargin should compile

      """class C {
        |  var i = 0
        |  def inc() = i += 1
        |}
        |val p = Property[C]""".stripMargin should compile

      """trait T {
        |  var i = 0
        |}
        |class C extends T {
        |  def inc() = i += 1
        |}
        |val p = Property[C]""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model" in {
      """trait T {
        |  def x: T
        |}
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator.propertyCreator[T]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class)" in {
      """case class T(a: Int, t: T)
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator.propertyCreator[T]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class with Seq)" in {
      """case class T(a: Int, t: T, st: Seq[T])
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator.propertyCreator[T]
        |  implicit val pcS: PropertyCreator[Seq[T]] = PropertyCreator.propertyCreator[Seq[T]]
        |}""".stripMargin should compile
    }

    "work with generic subproperties" in {
      """object Test {
        |  class A[T](val a: T)
        |  case class B(x: A[_], y: String)
        |  object B extends HasModelPropertyCreator[B]
        |}
        |
        |val t = ModelProperty[Test.B](null)
        |println(t.subProp(_.x).get)
        |println(t.subProp(_.y).get)
        |""".stripMargin should compile
    }

    "not allow to use Seq[_] in model" in {
      """object Test {
        |  class A[T](val a: T)
        |  case class B(x: A[_], y: String, z: Seq[_])
        |  object B extends HasModelPropertyCreator[B]
        |}
        |
        |val t = ModelProperty[Test.B](null)
        |println(t.subProp(_.x).get)
        |println(t.subProp(_.y).get)
        |""".stripMargin shouldNot compile
    }
  }
}
