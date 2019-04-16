package io.udash.properties

import io.udash.testing.UdashCoreTest

class PropertyUsageTest extends UdashCoreTest {
  // DO NOT REMOVE THESE IMPORTS!
  import io.udash.properties.model._
  import io.udash.properties.seq._
  import io.udash.properties.single._

  trait T {
    def i: Int
  }

  "CastableProperty" should {
    "allow safe casting to ModelProperty or SeqProperty " in {
      """val p = Property[Int](5)
        |val m: ModelProperty[Int] = p.asModel""".stripMargin shouldNot compile

      """val p = Property[Int](5)
        |val s: SeqProperty[Int, _ <: Property[Int]] = p.asSeq[Int]""".stripMargin shouldNot compile

      """
        |implicit val mpc: ModelPropertyCreator[T] = ModelPropertyCreator.materialize
        |val p = Property[T](null: T)
        |val m: ModelProperty[T] = p.asModel""".stripMargin should compile

      """
        |val p = Property[T](null: T)
        |val s: SeqProperty[T, _ <: Property[T]] = p.asSeq[T]""".stripMargin shouldNot compile

      """val p = Property.blank[Seq[Int]]
        |val m: ModelProperty[Seq[Int]] = p.asModel""".stripMargin shouldNot compile

      """val p = Property.blank[Seq[Int]]
        |val s: SeqProperty[Int, _ <: Property[Int]] = p.asSeq[Int]""".stripMargin should compile

      """
        |implicit val mpc: ModelPropertyCreator[T] = ModelPropertyCreator.materialize
        |
        |val p = Property.blank[Seq[T]]
        |val m: ModelProperty[Seq[T]] = p.asModel""".stripMargin shouldNot compile

      """
        |implicit val mpc: ModelPropertyCreator[T] = ModelPropertyCreator.materialize
        |
        |val p = Property.blank[Seq[T]]
        |val el = p.asSeq[T].elemProperties(0)
        |val m: ModelProperty[T] = el.asModel""".stripMargin should compile
    }
  }

  object Model {
    trait T {
      def i: Int
      def t: T
      def s: Seq[String]
    }
    object T extends HasModelPropertyCreator[T]
  }

  "ReadableModelProperty" should {
    "grant read only access to subproperties" in {
      """
        |val p: ReadableModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ReadableModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: Property[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.roSubModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile
    }
  }

  "ModelProperty" should {
    "grant read&write access to subproperties" in {
      """
        |val p: ModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: Property[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ModelProperty[Model.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[Model.T] = ModelProperty(null: Model.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[Model.T] = p.subModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile
    }
  }

  object ClassModel {
    case class T(i: Int, t: T, s: Seq[String])
    object T extends HasModelPropertyCreator[T]
  }

  "ReadableModelProperty based on CC" should {
    "grant read only access to subproperties" in {
      """
        |val p: ReadableModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ReadableModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: Property[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ModelProperty[ClassModel.T] = p.roSubModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.roSubProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.roSubModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.roSubSeq(_.s)
        |""".stripMargin shouldNot compile
    }
  }

  "ModelProperty based on CC" should {
    "grant read&write access to subproperties" in {
      """
        |val p: ModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: Property[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ModelProperty[ClassModel.T] = p.subModel(_.t)
        |val s: ReadableSeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile

      """
        |val p: ModelProperty[ClassModel.T] = ModelProperty(null: ClassModel.T)
        |val i: ReadableProperty[Int] = p.subProp(_.i)
        |val t: ReadableModelProperty[ClassModel.T] = p.subModel(_.t)
        |val s: SeqProperty[String, _ <: ReadableProperty[String]] = p.subSeq(_.s)
        |""".stripMargin should compile
    }
  }

  "ReadableSeqProperty" should {
    "grant read only access to contained properties" in {
      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty.blank[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty.blank[Int]
        |val cm: ModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty.blank[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: Property[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile

      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val p2: ReadableSeqProperty[Int, _ <: CastableReadableProperty[Int]] = SeqProperty.blank[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: Property[Int] = p2.elemProperties(0)
        |""".stripMargin shouldNot compile
    }

    "allow transformation of elements to ReadableProperty" in {
      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val t: ReadableSeqProperty[Int, ReadableProperty[Int]] = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).get.i
        |""".stripMargin should compile

      """
        |val p: ReadableSeqProperty[Model.T, _ <: ReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }

    "allow filtering of elements" in {
      """
        |val p: ReadableSeqProperty[Model.T, _ <: CastableReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin should compile

      """
        |val p: ReadableSeqProperty[Model.T, _ <: ReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }
  }

  "SeqProperty" should {
    "grant read&write access to contained properties" in {
      """|val p = SeqProperty.blank[Model.T]
         |val p2 = SeqProperty.blank[Int]
         |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
         |val c: ReadableProperty[Model.T] = p.elemProperties(0)
         |val c2: ReadableProperty[Int] = p2.elemProperties(0)
         |""".stripMargin should compile

      """
        |val p = SeqProperty.blank[Model.T]
        |val p2 = SeqProperty.blank[Int]
        |val cm: ModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """
        |val p = SeqProperty.blank[Model.T]
        |val p2 = SeqProperty.blank[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: Property[Model.T] = p.elemProperties(0)
        |val c2: ReadableProperty[Int] = p2.elemProperties(0)
        |""".stripMargin should compile

      """
        |val p = SeqProperty.blank[Model.T]
        |val p2 = SeqProperty.blank[Int]
        |val cm: ReadableModelProperty[Model.T] = p.elemProperties(0).asModel
        |val c: ReadableProperty[Model.T] = p.elemProperties(0)
        |val c2: Property[Int] = p2.elemProperties(0)
        |""".stripMargin should compile
    }

    "allow transformation of elements to ReadableProperty" in {
      """
        |val p = SeqProperty.blank[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).get.i
        |""".stripMargin should compile

      """
        |val p = SeqProperty.blank[Model.T]
        |val t = p.transform((el: Model.T) => el.i)
        |p.transform((el: Model.T) => el.t).elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }

    "allow filtering of elements" in {
      """
        |val p = SeqProperty.blank[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin should compile

      """
        |val p: SeqProperty[Model.T, _ <: ReadableProperty[Model.T]] = SeqProperty.blank[Model.T]
        |val t = p.filter((el: Model.T) => el.i % 2 == 0)
        |t.elemProperties(0).asModel
        |""".stripMargin shouldNot compile
    }
  }
}
