package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.model.ModelProperty
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single.Property
import io.udash.testing.UdashCoreTest

import scala.collection.mutable

class ModelPropertyTest extends UdashCoreTest {
  class C(val i: Int, val s: String) {
    var variable: Int = 7
    override def equals(obj: Any): Boolean = obj match {
      case self if self.asInstanceOf[AnyRef] eq this.asInstanceOf[AnyRef] => true
      case c: C => c.i == this.i && c.s == this.s
      case _ => false
    }
  }

  trait TT {
    def i: Int
    def s: Option[String]
    def t: ST

    override def equals(obj: Any): Boolean = obj match {
      case tt: TT =>
        i == tt.i && s == tt.s && t == tt.t
      case _ => false
    }
  }
  object TT extends HasModelPropertyCreator[TT] {
    implicit val default: Blank[TT] = Blank.Simple(null)
  }

  trait ST {
    def c: C
    def s: Seq[Char]

    override def equals(obj: Any): Boolean = obj match {
      case st: ST =>
        c == st.c && s == st.s
      case _ => false
    }
  }
  object ST extends HasModelPropertyCreator[ST]

  def newTT(iv: Int, sv: Option[String], cv: C, ssv: Seq[Char]) = new TT {
    override def i: Int = iv
    override def s: Option[String] = sv
    override def t: ST = new ST {
      override def c: C = cv
      override def s: Seq[Char] = ssv
    }
  }

  case class Bla(s: Seq[_])
  object Bla extends HasModelPropertyCreator[Bla]

  object Test {
    class A[G](val a: G)
    case class B(x: A[_], y: String)
    object B extends HasModelPropertyCreator[B]
  }

  "ModelProperty" should {
    "update value and provide access to subproperties" in {
      val p = ModelProperty(null: TT)

      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))

      p.get.i should be(5)
      p.get.s should be(Some("s"))
      p.get.t.c should be(new C(123, "asd"))
      p.get.t.s.size should be(3)

      p.subProp(_.i).set(42)
      p.get.i should be(42)

      p.subModel(_.t).subSeq(_.s).insert(0, 'e')
      p.get.t.s.size should be(4)

      p.subSeq(_.t.s).insert(0, 'f')
      p.get.t.s.size should be(5)

      p.subProp(_.t.c).set(new C(321, "dsa"))
      p.get.t.c should be(new C(321, "dsa"))

      p.touch()
      p.get.i should be(42)
      p.get.s should be(Some("s"))
      p.get.t.s.size should be(5)
      p.get.t.c should be(new C(321, "dsa"))
    }

    "fire listeners on value change" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v
      val oneTimeValues = mutable.ArrayBuffer[Any]()
      val oneTimeListener = (v: Any) => oneTimeValues += v

      val p = ModelProperty(null: TT)
      p.listen(listener, initUpdate = true)
      p.listenOnce(oneTimeListener)

      values.size should be(1)
      values.clear()

      val init = newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c'))
      p.setInitValue(newTT(123123, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))
      values.size should be(0)
      p.set(init)
      values.size should be(1)

      p.subProp(_.i).set(42)
      p.subProp(_.i).set(42)
      p.subProp(_.i).set(42)
      values.size should be(2)

      p.subModel(_.t).subSeq(_.s).insert(0, 'e')
      values.size should be(3)

      p.subSeq(_.t.s).insert(0, 'f')
      values.size should be(4)

      p.subProp(_.t.c).set(new C(321, "dsa"))
      values.size should be(5)

      CallbackSequencer().sequence {
        p.subSeq(_.t.s).insert(0, 'g')
        p.subSeq(_.t.s).insert(0, 'h')
        p.subSeq(_.t.s).insert(0, 'i')
        p.subProp(_.i).set(123)
      }
      values.size should be(6)

      p.touch()
      values.size should be(7)

      p.subSeq(_.t.s).touch()
      values.size should be(8)

      p.subProp(_.s).touch()
      values.size should be(9)

      p.subModel(_.t).touch()
      values.size should be(10)

      p.subProp(_.t.s).set("qweasd")
      values.size should be(11)

      p.subProp(_.t.s).set("asd2", force = true)
      values.size should be(12)

      p.subProp(_.t.s).set("asd2", force = true)
      values.size should be(13)

      p.subProp(_.t.s).set("asd2")
      values.size should be(13)

      p.clearListeners()
      p.subModel(_.t).touch()
      p.subProp(_.t.s).set("qwerty")
      values.size should be(13)

      oneTimeValues.size should be(1)
      oneTimeValues.head should be(init)
    }

    "transform and synchronize value" in {
      val values = mutable.ArrayBuffer[Any]()
      val listener = (v: Any) => values += v

      val p = ModelProperty(null: TT)
      val t = p.transform[Int](
        (p: TT) => p.i + p.t.c.i,
        (x: Int) => newTT(x / 2, None, new C(x / 2, ""), Seq.empty)
      )

      val r1 = p.listen(listener)
      val r2 = t.listen(listener)

      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))
      t.get should be(128)

      t.set(64)
      p.get.i should be(32)
      p.get.t.c.i should be(32)

      t.touch()
      p.get.i should be(32)
      p.get.t.c.i should be(32)

      values.size should be(6)
      values should contain(64)
      values should contain(128)

      r1.cancel()
      r2.cancel()

      p.set(newTT(2, Some("s"), new C(3, "asd"), Seq('a', 'b', 'c')))
      t.get should be(5)

      t.set(32)
      p.get.i should be(16)
      p.get.t.c.i should be(16)
    }

    "work with simple case class" in {
      case class Simple(i: Int, s: String)
      implicit val propertyCreator: ModelPropertyCreator[Simple] = ModelPropertyCreator.materialize

      val p = ModelProperty(Simple(1, "xxx"))
      p.get should be(Simple(1, "xxx"))
      val i = p.subProp(_.i)
      i.set(5)
      val s = p.subProp(_.s)
      s.set("asd")
      p.get should be(Simple(5, "asd"))
    }

    "work with tuples" in {
      val init = (123, "sth", true, new C(42, "s"))
      implicit val pc: ModelPropertyCreator[(Int, String, Boolean, C)] = ModelPropertyCreator.materialize
      val p = ModelProperty(init)

      var changeCount = 0
      p.listen(_ => changeCount += 1)

      p.get should be(init)
      p.subProp(_._1).set(333)
      p.subProp(_._2).set("sth2")
      p.subProp(_._3).set(false)
      p.subProp(_._4).set(new C(24, "s2"))
      p.get should be((333, "sth2", false, new C(24, "s2")))
      changeCount should be(4)
    }

    "work with Tuple2" in {
      val init = (123, "sth")
      implicit val pc: ModelPropertyCreator[(Int, String)] = ModelPropertyCreator.materialize
      val p = ModelProperty(init)

      var changeCount = 0
      p.listen(_ => changeCount += 1)

      p.get should be(init)
      p.subProp(_._1).set(333)
      p.subProp(_._2).set("sth2")
      p.get should be((333, "sth2"))
      changeCount should be(2)
    }

    "work with recursive case class" in {
      import ReqModels._

      val p = ModelProperty(Simple(1, null))
      p.get should be(Simple(1, null))
      val i = p.subProp(_.i)
      i.set(5)
      val s = p.subModel(_.s)
      s.set(Simple(2, Simple(3, null)))
      p.get should be(Simple(5, Simple(2, Simple(3, null))))
      s.subProp(_.i).get should be(2)
      s.subProp(_.s.i).get should be(3)
    }

    "work with recursive trait" in {
      import ReqModels._

      val p = ModelProperty[ReqT](new ReqT {
        def t: ReqT = null
      })
      p.get.t should be(null)
      val s = p.subModel(_.t)
      s.set(new ReqT {
        def t: ReqT = null
      })
      s.get.t should be(null)
      p.get.t shouldNot be(null)
    }

    "work with recursive case class containing Seq" in {
      import ReqModels._

      val p = ModelProperty(SimpleSeq(Seq[SimpleSeq](), null))
      p.get should be(SimpleSeq(Seq(), null))
      val i = p.subSeq(_.i)
      i.set(Seq(SimpleSeq(Seq(), SimpleSeq(Seq(SimpleSeq(Seq(), null)), null))))
      val s = p.subModel(_.s)
      s.set(SimpleSeq(Seq(), SimpleSeq(Seq(), null)))
      p.get should be(SimpleSeq(Seq(SimpleSeq(Seq(), SimpleSeq(Seq(SimpleSeq(Seq(), null)), null))), SimpleSeq(Seq(), SimpleSeq(Seq(), null))))
      s.subProp(_.i).get should be(Seq())
      s.subProp(_.s.i).get should be(Seq())
      i.elemProperties.isEmpty should be(false)
    }

    "not get partial value from child property" in {
      case class CCWithRequire(a: Int, b: Int) {
        require((a > 0 && b > 0) || (a < 0 && b < 0))
      }
      implicit val propertyCreatorCC: ModelPropertyCreator[CCWithRequire] = ModelPropertyCreator.materialize[CCWithRequire]
      case class TopModel(child: CCWithRequire)
      implicit val propertyCreator: ModelPropertyCreator[TopModel] = ModelPropertyCreator.materialize[TopModel]

      val p = ModelProperty[TopModel](TopModel(CCWithRequire(1, 2)))
      val c = p.subModel(_.child)

      c.set(CCWithRequire(-10, -5))

      c.get.a should be(-10)
      c.get.b should be(-5)

      c.touch()

      c.get.a should be(-10)
      c.get.b should be(-5)
    }

    "handle trait with implemented defs and vals" in {
      trait ModelWithImplDef {
        def x: Int
        def y: Int = 5
      }
      implicit val propertyCreator: ModelPropertyCreator[ModelWithImplDef] = ModelPropertyCreator.materialize[ModelWithImplDef]
      trait ModelWithImplVal {
        val x: Int
        val y: Int = 5
      }
      implicit val propertyCreatorVal: ModelPropertyCreator[ModelWithImplVal] = ModelPropertyCreator.materialize[ModelWithImplVal]

      val p1 = ModelProperty(null: ModelWithImplDef)
      val p2 = ModelProperty(null: ModelWithImplVal)

      p1.subProp(_.x).set(12)
      p1.subProp(_.x).get should be(12)
      p1.get.x should be(12)
      p1.get.y should be(5)

      p2.subProp(_.x).set(12)
      p2.subProp(_.x).get should be(12)
      p2.get.x should be(12)
      p2.get.y should be(5)
    }

    "handle case class with implemented defs and vals" in {
      trait Utils {
        val userLabel: String = "User:"
      }
      case class User(login: String, name: Option[String]) extends Utils {
        val displayName: String = name.getOrElse(login)

        def withLabel: String =
          s"$userLabel $displayName"
      }
      implicit val propertyCreator: ModelPropertyCreator[User] = ModelPropertyCreator.materialize[User]

      val p = ModelProperty[User](User("udash", Some("Udash Framework")))
      p.get.withLabel should be("User: Udash Framework")

      p.subProp(_.name).set(None)
      p.get.withLabel should be("User: udash")

      p.subProp(_.login).set("tester")
      p.get.withLabel should be("User: tester")

      p.subProp(_.name).set(Some("Test Test"))
      p.get.withLabel should be("User: Test Test")
    }

    "handle empty model property after subProp call" in {
      case class SubTest(x: Int)
      implicit val propertyCreatorSub: ModelPropertyCreator[SubTest] = ModelPropertyCreator.materialize[SubTest]
      case class Test(a: SubTest, s: SubTest)
      implicit val propertyCreator: ModelPropertyCreator[Test] = ModelPropertyCreator.materialize[Test]

      val p = ModelProperty(null: Test)
      val sub = p.subModel(_.s)

      p.get should be(null)
      sub.get should be(null)

      sub.subProp(_.x).set(7)

      p.get should be(Test(null, SubTest(7)))
      sub.get should be(SubTest(7))
    }

    "handle empty model property after subProp call (trait version)" in {
      trait SubTest {
        def x: Int
      }
      implicit val propertyCreatorSub: ModelPropertyCreator[SubTest] = ModelPropertyCreator.materialize[SubTest]
      trait Test {
        def a: SubTest
        def s: SubTest
      }
      implicit val propertyCreator: ModelPropertyCreator[Test] = ModelPropertyCreator.materialize[Test]

      val p = ModelProperty(null: Test)
      val sub = p.subModel(_.s)

      p.get should be(null)
      sub.get should be(null)

      sub.subProp(_.x).set(7)

      p.get.a should be(null)
      p.get.s.x should be(7)
      sub.get.x should be(7)
    }

    "handle Seq[_]" in {
      val mp = ModelProperty(Bla(Seq(1, 8L)))
      val s = mp.subSeq(_.s)

      s.get should contain inOrderOnly(1, 8L)

      s.prepend("0")
      s.replace(1, 2, 0.0, 7)

      s.get should contain inOrderOnly("0", 0.0, 7)
    }

    "handle generic types" in {
      object Outer {
        case class Bla[Type](x: Int, s: String, t: Type)
        object Bla {
          implicit def pc[Type: PropertyCreator]: ModelPropertyCreator[Bla[Type]] =
            ModelPropertyCreator.materialize[Bla[Type]]
        }
      }

      implicit val mpc: ModelPropertyCreator[(Outer.Bla[Outer.Bla[Int]], Int, String)] = ModelPropertyCreator.materialize
      val s = SeqProperty(Seq.tabulate(10)(_ => (Outer.Bla(5, "asd2", Outer.Bla(7, "qwe", 1)), 8, "asd")))
      s.elemProperties.foreach { v =>
        val p = v.asModel
        p.subProp(_._1.x).get should be(5)
        p.subProp(_._1.s).get should be("asd2")
        p.subProp(_._1.t.x).get should be(7)
        p.subProp(_._1.t.s).get should be("qwe")
        p.subProp(_._1.t.t).get should be(1)
        p.subProp(_._2).get should be(8)
        p.subProp(_._3).get should be("asd")
      }
    }

    "fail on static creator mismatch" in {
      case class Clazz[Type](x: Int, s: String, t: Type)

      val p = Property((Clazz(5, "asd2", Clazz(7, "qwe", 1)), 8, "asd"))

      an[IllegalStateException] shouldBe thrownBy(p.asModel(ModelPropertyCreator.materialize))

      val p2 = Property(Seq(1, 2, 3))(new SinglePropertyCreator[BSeq[Int]])
      an[IllegalStateException] shouldBe thrownBy(p2.asSeq)
    }

    "cache subproperties" in {
      val p = ModelProperty(null: TT)
      p.set(newTT(5, Some("s"), new C(123, "asd"), Seq('a', 'b', 'c')))

      p.subProp(_.i) should be theSameInstanceAs p.subProp(_.i)
      p.subProp(_.s) should be theSameInstanceAs p.subProp(_.s)
      p.subModel(_.t) should be theSameInstanceAs p.subModel(_.t)
      p.subModel(_.t) should be theSameInstanceAs p.subProp(_.t)
      p.subSeq(_.t.s) should be theSameInstanceAs p.subSeq(_.t.s)
      p.subSeq(_.t.s) should be theSameInstanceAs p.subProp(_.t.s)
    }

    "work with generic wildcard" in {
      val t = ModelProperty[Test.B](Test.B(new Test.A("a"), "y"))
      t.subProp(_.x).get.a should be("a")
      t.subProp(_.y).get should be("y")
      t.subProp(_.x).set(new Test.A("qwe"))
      t.subProp(_.x).get.a should be("qwe")
    }

    "handle Seq subclasses and aliases" in {
      val mp = ModelProperty(AliasedSeqModel(Vector("abc"), Vector("def"), Vector(1), Vector("123")))

      mp.subSeq(_.s1).get should ===(Seq("abc"))
      mp.subProp(_.s1).get shouldBe a[Seq[_]]
      mp.subProp(_.s1).get should ===(Vector("abc"))

      mp.subSeq(_.s2).get should ===(Seq("def"))
      mp.subProp(_.s2).get shouldBe a[Vector[_]]
      mp.subProp(_.s2).get should ===(Vector("def"))

      mp.subSeq[Int, Seq](_.s3).get should ===(Seq(1))
      mp.subProp(_.s3).get shouldBe a[Seq[_]]
      mp.subProp(_.s3).get should ===(Vector(1))

      mp.subSeq(_.s4).get should ===(Seq("123"))
      mp.subProp(_.s4).get shouldBe a[Seq[_]]
      mp.subProp(_.s4).get should ===(Vector("123"))

      val zipped = mp.subSeq(_.s1).zip(mp.subSeq(_.s2))(_ + _)
      zipped.get should ===(Seq("abcdef"))

      mp.subSeq(_.s2).prepend("abc")
      mp.subSeq(_.s2).get should ===(Seq("abc", "def"))
      mp.subProp(_.s2).get should ===(Vector("abc", "def"))
      zipped.get should ===(Seq("abcabc"))

      mp.subProp(_.s2).set(Vector("xyz"))

      mp.subSeq(_.s2).get should ===(Seq("xyz"))
      mp.subProp(_.s2).get should ===(Vector("xyz"))
      zipped.get should ===(Seq("abcxyz"))
    }
  }

  type SeqAlias[A] = Seq[A]
  type VectorAlias[A] = Vector[A]
  type IntSeq[A] = Seq[Int]
  type WeirdSeq[A, B] = Seq[B]
  case class AliasedSeqModel(
    s1: SeqAlias[String],
    s2: VectorAlias[String],
    s3: IntSeq[String],
    s4: WeirdSeq[Int, String]
  )
  object AliasedSeqModel extends HasModelPropertyCreator[AliasedSeqModel]
}
