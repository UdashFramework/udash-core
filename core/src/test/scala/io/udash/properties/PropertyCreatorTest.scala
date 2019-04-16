package io.udash.properties

import io.udash.testing.UdashCoreTest

class PropertyCreatorTest extends UdashCoreTest {
  // DO NOT REMOVE THIS IMPORT!
  import io.udash.properties.Properties._

  "PropertyCreator" should {
    "create Property for basic types (and handle init value)" in {
      """val p = Property.blank[String]""".stripMargin should compile

      """val p = Property[String]("ABC")""".stripMargin should compile

      """val p = Property[String](2)""".stripMargin shouldNot typeCheck

      """val p = Property[Int](2)""".stripMargin should compile
    }

    "create Property for class (mutable and immutable)" in {
      """case class A(s: String, i: Int)
        |val p = Property[A](null: A)""".stripMargin should compile

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

      """object Model {
        |  case class A(s: String, i: Int)(x: Int)
        |  object A {
        |    implicit val blank: Blank[A] = null
        |  }
        |}
        |
        |val p = Property.blank[Model.A]""".stripMargin should compile
    }

    "create Property for sealed trait" in {
      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T](null)""".stripMargin should compile

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
        |val p = Property[T](null).asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """trait T {
        |  val i: Int
        |  val s: String
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  def x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  var x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel""".stripMargin shouldNot compile

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
        |val p = Property[T2](null).asModel""".stripMargin shouldNot compile

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
        |val p = Property[T2](null).asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int = 5
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |  val x: Int
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel
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
        |val p = Property[Model.T](null).asModel
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
        |val p = Property[T](null).asModel
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
        |val p = Property[T](null).asModel
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
        |val p = Property[T](null).asModel""".stripMargin should compile

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
        |val p = Property[T](null).asModel
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
        |val p = Property[A](null).asModel""".stripMargin should compile

      """case class A(s: String, i: Int)(x: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel""".stripMargin shouldNot compile

      """case class A(s: String, var i: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  val x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int) {
        |  val x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subProp(_.s)
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  def x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subProp(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int) {
        |  def x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subProp(_.s)
        |val x = p.subProp(_.x)""".stripMargin shouldNot compile

      """case class A(s: String, i: Int) {
        |  var x: String = "Udash Properties"
        |}
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel""".stripMargin shouldNot compile

      """case class A(s: Seq[String], i: Seq[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """case class A(s: Vector[String], i: List[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.s)""".stripMargin should compile

      """case class A(s: String, i: Int)
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |case class B(a: A)
        |implicit val propertyCreator2: ModelPropertyCreator[B] = ModelPropertyCreator.materialize[B]
        |val p = ModelProperty[B](null)
        |val sub = p.subModel(_.a)""".stripMargin should compile

      """case class A(s: Seq[String], i: Seq[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.i)""".stripMargin should compile

      """case class A(s: Vector[String], i: List[Int])
        |implicit val propertyCreator: ModelPropertyCreator[A] = ModelPropertyCreator.materialize[A]
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.i)""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: A)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """object Model {
        |  case class A(s: List[String], i: A)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """object Test {
        |  case class Todo(name: String, editName: String, completed: Boolean = false, editing: Boolean = false)
        |  object Todo extends HasModelPropertyCreator[Todo]
        |  val x = ModelProperty[Todo](null)
        |}""".stripMargin should compile
    }

    "create ModelProperty for simple classes" in {
      """class A(val s: Seq[String], val i: A)
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """class A(val s: Vector[String], val i: A)
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subModel(_.i)""".stripMargin should compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A](null).asModel
        |val test = p.subSeq(_.test)""".stripMargin shouldNot compile

      """class A(val s: Seq[String], val i: A) {
        |  val test: Int = 5
        |  def x: String = "qwe"
        |}
        |object A extends HasModelPropertyCreator[A](ModelPropertyCreator.materialize)
        |
        |val p = Property[A](null).asModel
        |val test = p.subSeq(_.x)""".stripMargin shouldNot compile
    }

    "not create ModelProperty for anything other than trait or simple case class" in {
      """val p = Property[Int](null.asInstanceOf[Int]).asModel""".stripMargin shouldNot compile

      """val p = ModelProperty[String](null)""".stripMargin shouldNot compile

      """val p = Property[Seq[Int]](null).asModel""".stripMargin shouldNot compile

      """val p = Property[Vector[Int]](null).asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[T](null).asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]](null).asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[List[T]](null).asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]](null).asModel""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Vector[T]](null).asModel""".stripMargin shouldNot compile

      """object Model {
        |  case class A(s: String, i: Int)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Seq[Model.A]](null).asModel""".stripMargin shouldNot compile

      """object Model {
        |  case class A(s: String, i: Int)
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[List[Model.A]](null).asModel""".stripMargin shouldNot compile

      """object Model {
        |  case class SimpleWithVar(i: Int, var s: String)
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar](null)""".stripMargin shouldNot compile

      """object Model {
        |  case class Simple(i: Int, s: String) { val t = 5 }
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple](null)""".stripMargin should compile

      """object Model {
        |  case class SimpleWithVar(i: Int, s: String) { var t = 5 }
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar](null)""".stripMargin shouldNot compile

      """object Model {
        |  case class Simple(i: Int, s: String) { def t = 5 }
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple](null)""".stripMargin should compile

      """object Model {
        |  class C { val x = 5 }
        |  case class Simple(i: Int, s: String) extends C
        |  object Simple extends HasModelPropertyCreator[Simple]
        |}
        |val p = ModelProperty[Model.Simple](null)""".stripMargin should compile

      """object Model {
        |  class C { var x = 5 }
        |  case class SimpleWithVar(i: Int, c: C)
        |  object SimpleWithVar extends HasModelPropertyCreator[SimpleWithVar]
        |}
        |val p = ModelProperty[Model.SimpleWithVar](null)""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: Set[Int])
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A](null).asModel
        |val s = p.subSeq(_.s)
        |val i = p.subProp(_.i)""".stripMargin should compile

      """object Model {
        |  case class A(s: Seq[String], i: Set[Int])
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A](null).asModel
        |val s = p.subSeq(_.i)""".stripMargin shouldNot compile

      """object Model {
        |  case class A(s: Seq[String], i: scala.collection.mutable.Seq[Int])
        |  object A extends HasModelPropertyCreator[A]
        |}
        |val p = Property[Model.A](null).asModel
        |val s = p.subSeq(_.s)
        |val si = p.subSeq(_.i)
        |val i = p.subProp(_.i)""".stripMargin should compile
    }

    "create SeqProperty for any Seq" in {
      """val p = Property[Seq[Int]](null).asSeq[Int]""".stripMargin should compile

      """val p = Property[scala.collection.immutable.Seq[Int]](null).asSeq[Int]""".stripMargin should compile

      """val p = Property[Seq[Seq[Int]]](Seq(Seq(1,2))).asSeq[Seq[Int]]
        |val p2 = p.elemProperties.head.asSeq[Int]
        |val i: Property[Int] = p2.elemProperties.head
        |i.set(5)""".stripMargin should compile

      """val p = Property[List[Vector[Int]]](List(Vector(1,2))).asSeq[Vector[Int]]
        |val p2 = p.elemProperties.head.asSeq[Int]
        |val i: Property[Int] = p2.elemProperties.head
        |i.set(5)""".stripMargin should compile

      """val p = Property[Seq[List[Int]]](List(List(1,2))).asSeq[List[Int]]
        |val p2 = p.elemProperties.head.asSeq[Int]
        |val i: Property[Int] = p2.elemProperties.head
        |i.set(5)""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[T]](null).asSeq[T]
        |val ip = Property[scala.collection.immutable.Seq[T]](null).asSeq[T]
        |val mp = Property[scala.collection.mutable.Seq[T]](null).asSeq[T]
        |val vp = Property[Vector[T]](null).asSeq[T]""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[Seq[T]]](null).asSeq[T]""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[List[T]]](null).asSeq[T]""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Vector[Seq[T]]](null).asSeq[T]""".stripMargin shouldNot compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[List[scala.collection.mutable.Seq[T]]](null).asSeq[T]""".stripMargin shouldNot compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def s: String
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize)
        |}
        |val p = Property[Seq[Model.T]](null).asSeq[Model.T]
        |val m: ModelProperty[Model.T] = p.elemProperties.head.asModel
        |val sub = m.subProp(_.s)
        |val sub2 = m.subModel(_.t)

        |val ip = Property[scala.collection.immutable.Seq[Model.T]](null).asSeq[Model.T]
        |val im: ModelProperty[Model.T] = ip.elemProperties.head.asModel
        |val isub = im.subProp(_.s)
        |val isub2 = im.subModel(_.t)""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def s: String
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T](ModelPropertyCreator.materialize)
        |}
        |
        |val p = Property[Seq[Seq[Model.T]]](null).asSeq[Seq[Model.T]]
        |val p2 = p.elemProperties.head.asSeq[Model.T]
        |val m: ModelProperty[Model.T] = p2.elemProperties.head.asModel
        |val sub = m.subProp(_.s)
        |val sub2 = m.subModel(_.t)

        |val ip = Property[List[Vector[Model.T]]](null).asSeq[scala.collection.immutable.Seq[Model.T]]
        |val ip2 = ip.elemProperties.head.asSeq[Model.T]
        |val im: ModelProperty[Model.T] = ip2.elemProperties.head.asModel
        |val isub = im.subProp(_.s)
        |val isub2 = im.subModel(_.t)""".stripMargin should compile

      """trait X {
        |  def a: String
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def x: X
        |}
        |val p1 = Property[Seq[T]](null).asSeq[T]
        |val p2 = Property[List[T]](null).asSeq[T]
        |val p3 = Property[Vector[T]](null).asSeq[T]
        |val p4 = Property[scala.collection.mutable.Seq[T]](null).asSeq[T]
        |val p5 = Property[scala.collection.immutable.Seq[T]](null).asSeq[T]""".stripMargin should compile

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
        |val p = Property[Seq[T]](null).asSeq[T]
        |val p2 = Property[Seq[Seq[T]]](null).asSeq[Seq[T]]
        |val ip = Property[Vector[T]](null).asSeq[T]
        |val ip2 = Property[List[Vector[T]]](null).asSeq[Vector[T]]""".stripMargin should compile

      """trait X {
        |  def a: String
        |  def t: T
        |  def x: X
        |  def st: List[T]
        |}
        |
        |trait T {
        |  def i: Int
        |  def s: String
        |  def t: T
        |  def x: X
        |  def sx: scala.collection.mutable.Seq[X]
        |}
        |val p = Property[Seq[T]](null).asSeq[T]
        |val p2 = Property[Seq[Seq[T]]](null).asSeq[Seq[T]]
        |val ip = Property[Vector[T]](null).asSeq[T]
        |val ip2 = Property[List[Vector[T]]](null).asSeq[Vector[T]]""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]](null).asSeq[T]
        |val m: Property[T] = p.elemProperties.head
        |val ip = Property[Vector[T]](null).asSeq[T]
        |val im: Property[T] = ip.elemProperties.head""".stripMargin should compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]](null).asSeq[T]
        |val m: ModelProperty[T] = p.elemProperties.head.asModel""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Vector[T]](null).asSeq[T]
        |val m: ModelProperty[T] = p.elemProperties.head.asModel""".stripMargin shouldNot compile

      """val p = SeqProperty.blank[(String, Option[String])]""".stripMargin should compile

      """val items = SeqProperty(
        |  Seq.fill(7)((3.1, 2.5, 4.564))
        |)""".stripMargin should compile

      """val items = SeqProperty(
        |  Vector.fill(7)((3.1, 2.5, 4.564))
        |)""".stripMargin should compile

      """val items = SeqProperty(
        |  scala.collection.mutable.Buffer.fill(7)((3.1, 2.5, 4.564))
        |)""".stripMargin should compile
    }

    "not create SeqProperty for anything not extending Seq" in {
      """val p = Property[Int](null.asInstanceOf[Int]).asSeq""".stripMargin shouldNot compile

      """val p = Property[String](null).asSeq""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[T](null).asSeq""".stripMargin shouldNot compile

      """sealed trait T
        |case object A extends T
        |case object B extends T
        |val p = Property[Seq[T]](null).asSeq
        |val mp = Property[scala.collection.mutable.Buffer[T]](null).asSeq
        |val ip = Property[Vector[T]](null).asSeq""".stripMargin should compile

      """case class A(s: String, i: Int)
        |val p = Property[A](null).asSeq""".stripMargin shouldNot compile

      """case class A(s: String, i: Int)
        |val p = Property[Seq[A]](null).asSeq
        |val ip = Property[scala.collection.immutable.Seq[A]](null).asSeq
        |val mp = Property[scala.collection.mutable.Seq[A]](null).asSeq""".stripMargin should compile

      """trait T {
        |  def i: Int
        |  def s: String
        |}
        |val p = Property[Seq[T]](null).asSeq
        |val ip = Property[Vector[T]](null).asSeq""".stripMargin should compile

      """trait T {
        |  def i: Seq[Int]
        |  def s: String
        |  def t: T
        |}
        |val p = Property[T](null).asSeq""".stripMargin shouldNot compile

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
        |val p = Property[T](null).asSeq""".stripMargin shouldNot compile
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
        |val p = Property[Model.T](null).asModel
        |val s = p.subModel(_.t).subSeq(_.s)
        |val s2 = p.subSeq(_.t.s)
        |val i = p.subProp(_.t.c.i)""".stripMargin should compile

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
        |    def s: List[Char]
        |  }
        |  object ST extends HasModelPropertyCreator[ST]
        |}
        |
        |val p = Property[Model.T](null).asModel
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
        |val p = Property[Model.T](null).asModel
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
        |val responsesModel = ModelProperty[Model.ExampleModel](null)""".stripMargin should compile

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
        |val GenCodecs = ModelProperty[Model.GenCodecsDemoModel](null)""".stripMargin should compile
    }

    "not create property for mutable class" in {
      """val p = Property[scala.collection.mutable.ArrayBuffer[Int]](null)""".stripMargin should compile

      """class C {
        |  var i = 0
        |  def inc() = i += 1
        |}
        |val p = Property[C](null)""".stripMargin should compile

      """trait T {
        |  var i = 0
        |}
        |class C extends T {
        |  def inc() = i += 1
        |}
        |val p = Property[C](null)""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model" in {
      """trait T {
        |  def x: T
        |}
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator[T]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class)" in {
      """case class T(a: Int, t: T)
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator[T]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class with Seq)" in {
      """case class T(a: Int, t: T, st: Seq[T])
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator[T]
        |  implicit val pcS: PropertyCreator[Seq[T]] = PropertyCreator[Seq[T]]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class with Vector, PropertyCreator[Seq[T]])" in {
      """case class T(a: Int, t: T, st: Vector[T])
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator[T]
        |  implicit val pcS: PropertyCreator[Seq[T]] = PropertyCreator[Seq[T]]
        |}""".stripMargin should compile
    }

    "handle explicit creation of property creator for recursive model (case class with Vector, PropertyCreator[Vector[T]])" in {
      """case class T(a: Int, t: T, st: Vector[T])
        |object Test {
        |  implicit val pc: PropertyCreator[T] = PropertyCreator[T]
        |  implicit val pcS: PropertyCreator[Vector[T]] = PropertyCreator[Vector[T]]
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

    "work with Seq[_] in model" in {
      """object Test {
        |  class A[T](val a: T)
        |  case class B(x: A[_], y: String, z: Seq[_])
        |  object B extends HasModelPropertyCreator[B]
        |}
        |
        |val t = ModelProperty[Test.B](null)
        |println(t.subProp(_.x).get)
        |println(t.subProp(_.y).get)
        |println(t.subProp(_.z).get)
        |println(t.subSeq(_.z).get)
        |""".stripMargin should compile
    }

    "work with immutable Seq[_] in model" in {
      """object Test {
        |  case class B(x: String, y: scala.collection.immutable.Seq[_])
        |  object B extends HasModelPropertyCreator[B]
        |}
        |
        |val t = ModelProperty[Test.B](null)
        |println(t.subProp(_.x).get)
        |println(t.subProp(_.y).get)
        |println(t.subSeq(_.y).get)
        |""".stripMargin should compile
    }

    "fail implicit search for SeqPropertyCreator[Nothing]" in {
      "SeqProperty.apply[Nothing](Seq.empty)" shouldNot compile
    }

    //https://github.com/UdashFramework/udash-core/issues/271
    "fail implicit search for SeqPropertyCreator on default generic value" in {
      """
        |case class Chunk[T](
        |  data: SeqProperty[T] = SeqProperty.blank
        |)
      """.stripMargin shouldNot compile
    }
  }
}
