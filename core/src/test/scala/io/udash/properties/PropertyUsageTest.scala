package io.udash.properties

import io.udash.testing.UdashCoreTest

class PropertyUsageTest extends UdashCoreTest {
  // DO NOT REMOVE THESE IMPORTS!
  import io.udash.properties.single._
  import io.udash.properties.model._
  import io.udash.properties.seq._

  "CastableProperty" should {
    "allow safe casting to ModelProperty or SeqProperty " in {
      """val p = Property[Int]
        |val m: ModelProperty[Int] = p.asModel""".stripMargin shouldNot compile

      """val p = Property[Int]
        |val s: SeqProperty[Int, _ <: Property[Int]] = p.asSeq[Int]""".stripMargin shouldNot compile

      """trait T { def a: Int }
        |object T extends HasModelPropertyCreator[T]
        |val p = Property[T]
        |val m: ModelProperty[T] = p.asModel""".stripMargin should compile

      """trait T { def a: Int }
        |val p = Property[T]
        |val s: SeqProperty[T, _ <: Property[T]] = p.asSeq[T]""".stripMargin shouldNot compile

      """val p = Property[Seq[Int]]
        |val m: ModelProperty[Seq[Int]] = p.asModel""".stripMargin shouldNot compile

      """val p = Property[Seq[Int]]
        |val s: SeqProperty[Int, _ <: Property[Int]] = p.asSeq[Int]""".stripMargin should compile

      """trait T { def a: Int }
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]]
        |val m: ModelProperty[Seq[T]] = p.asModel""".stripMargin shouldNot compile

      """trait T { def a: Int }
        |object T extends HasModelPropertyCreator[T]
        |
        |val p = Property[Seq[T]]
        |val el = p.asSeq[T].elemProperties(0)
        |val m: ModelProperty[T] = el.asModel""".stripMargin should compile
    }
  }

  "ReadableModelProperty" should {
    "grant read only access to subproperties" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: Property[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile
    }
  }

  "ModelProperty" should {
    "grant read&write access to subproperties" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: Property[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |    def s: Seq[String]
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile
    }
  }

  "ReadableModelProperty based on CC" should {
    "grant read only access to subproperties" in {
      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: Property[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile
    }
  }

  "ModelProperty based on CC" should {
    "grant read&write access to subproperties" in {
      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: Property[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """object Model {
        |  case class T(i: Int, t: T, s: Seq[String])
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ModelProperty[Model.T] = ModelProperty[Model.T]
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile
    }
  }

  "ReadableSeqProperty" should {
    "grant read only access to contained properties" in {
      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty[Int]
        |val cm: ModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: Property[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: Property[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile
    }

    "allow transformation of elements to ReadableProperty" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val t: ReadableSeqProperty[Int, ReadableProperty[Int]] = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).get.i
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: ReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }

    "allow filtering of elements" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: ReadableSeqProperty[Model.T, _ <: ReadableProperty[Model.T]] = SeqProperty[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }
  }

  "SeqProperty" should {
    "grant read&write access to contained properties" in {
      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val p2 = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val p2 = SeqProperty[Int]
        |val cm: ModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val p2 = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: Property[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """object Model {
        |  trait T { def i: Int }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val p2 = SeqProperty[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: Property[Int] = p2.elemProperties(0)
        |""".stripMargin should compile
    }

    "allow transformation of elements to ReadableProperty" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).get.i
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }

    "allow filtering of elements" in {
      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p = SeqProperty[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin should compile

      """object Model {
        |  trait T {
        |    def i: Int
        |    def t: T
        |  }
        |  object T extends HasModelPropertyCreator[T]
        |}
        |
        |val p: SeqProperty[Model.T, _ <: ReadableProperty[Model.T] = SeqProperty[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }
  }
}
