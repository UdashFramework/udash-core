package io.udash.properties

import io.udash.properties.model.ModelProperty
import io.udash.properties.seq.SeqProperty
import io.udash.properties.single.Property
import io.udash.testing.{AsyncUdashSharedTest, AsyncUdashSharedTestBase}

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Random

class PropertyValidationTest extends AsyncUdashSharedTest with AsyncUdashSharedTestBase {
  case class C(i: Int, s: String)
  object C extends HasModelPropertyCreator[C]

  trait TT {
    def i: Int
    def s: Option[String]
    def t: ST
  }
  object TT extends HasModelPropertyCreator[TT]

  trait ST {
    def c: C
    def s: Seq[Char]
  }
  object ST extends HasModelPropertyCreator[ST]

  sealed trait T
  case object TO1 extends T
  case object TO2 extends T
  case class TC1(i: Int) extends T
  case class TC2(s: String) extends T

  def newTT(iv: Int, sv: Option[String], cv: C, ssv: Seq[Char]) = new TT {
    override def i: Int = iv
    override def s: Option[String] = sv
    override def t: ST = new ST {
      override def c: C = cv
      override def s: Seq[Char] = ssv
    }
  }

  def randTT() = newTT(Random.nextInt(20), Some(Random.nextString(5)), C(Random.nextInt(20), Random.nextString(5)), Random.nextString(20))

  "Property validation" should {
    "work with standard Property" in {
      val p = Property[C](C(2, "asd"))
      p.addValidator(new Validator[C] {
        override def apply(element: C): Future[ValidationResult] = Future {
          if (element.i < element.s.length) Valid
          else Invalid("i should be smaller then s length.")
        }
      })
      p.addValidator(new Validator[C] {
        override def apply(element: C): Future[ValidationResult] = Future {
          if (element.i < 5) Valid
          else Invalid("i should be smaller then 5.")
        }
      })

      for {
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.set(C(4, "asd")))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("i should be smaller then s length.")))
        _ <- Future(p.set(C(5, "qweasdzxc")))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("i should be smaller then 5.")))
        _ <- Future(p.set(C(5, "asd")))
        _ <- retrying(p.isValid.value.get.get.asInstanceOf[Invalid[ValidationError]].errors.size should be(2))
        _ <- Future(p.clearValidators())
        r <- retrying(p.isValid.value.get.get should be(Valid))
      } yield r
    }

    "start on isValid call (only if needed)" in {
      val p = Property[Int](5)
      var fired = false
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] = {
          fired = true
          Future {
            if (element < 0) Valid
            else Invalid("Error")
          }
        }
      })

      for {
        _ <- retrying(fired should be(false))

        _ <- Future(p.set(7))
        _ <- retrying(fired should be(false))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("Error")))
        _ <- retrying(fired should be(true))

        _ <- Future { fired = false }
        _ <- retrying(p.isValid.value.get.get should be(Invalid("Error"))) //it should not rerun validation
        _ <- retrying(fired should be(false))

        _ <- Future(p.set(-3))
        _ <- retrying(p.isValid.value.get.get should be(Valid)) //it should rerun validation
        r <- retrying(fired should be(true))
      } yield r
    }

    "work with ModelProperty" in {
      val p = ModelProperty[TT](newTT(5, Some(""), C(5, "01234567890123"), "0123456"))
      p.addValidator(new Validator[TT] {
        override def apply(element: TT): Future[ValidationResult] = Future {
          if (element.i == element.t.c.i) Valid
          else Invalid("Error")
        }
      })
      p.subModel(_.t).addValidator(new Validator[ST] {
        override def apply(element: ST): Future[ValidationResult] = Future {
          if (element.s.length > 5) Valid
          else Invalid("Error2")
        }
      })
      p.subProp(_.t.c).addValidator(new Validator[C] {
        override def apply(element: C): Future[ValidationResult] = Future {
          if (element.s.length > 10) Valid
          else Invalid("Error3")
        }
      })

      for {
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.set(newTT(5, Some(""), C(5, "01234567890123"), "0123")))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error2"))
          p.subModel(_.t).isValid.value.get.get should be(Invalid("Error2"))
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future(p.set(newTT(5, Some(""), C(2, "012"), "01234567")))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error", "Error3"))
          p.subModel(_.t).isValid.value.get.get should be(Invalid("Error3"))
          p.subProp(_.t.c).isValid.value.get.get should be(Invalid("Error3"))
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future(p.subProp(_.t.c).set(C(5, "012323")))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error3"))
          p.subModel(_.t).isValid.value.get.get should be(Invalid("Error3"))
          p.subProp(_.t.c).isValid.value.get.get should be(Invalid("Error3"))
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future(p.subProp(_.t.c).set(C(5, "01234567890123")))
        _ <- retrying {
          p.isValid.value.get.get should be(Valid)
          p.subModel(_.t).isValid.value.get.get should be(Valid)
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future(p.subProp(_.i).set(2))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error"))
          p.subModel(_.t).isValid.value.get.get should be(Valid)
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future {
          p.subProp(_.i).set(5)
          p.subSeq(_.t.s).set("0")
        }
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error2"))
          p.subModel(_.t).isValid.value.get.get should be(Invalid("Error2"))
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future {
          p.subProp(_.i).set(2)
          p.subSeq(_.t.s).set("0123123123")
        }
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error"))
          p.subModel(_.t).isValid.value.get.get should be(Valid)
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
        _ <- Future {
          p.clearValidators()
        }
        r <- retrying {
          p.isValid.value.get.get should be(Valid)
          p.subModel(_.t).isValid.value.get.get should be(Valid)
          p.subProp(_.t.c).isValid.value.get.get should be(Valid)
          p.subSeq(_.t.s).isValid.value.get.get should be(Valid)
        }
      } yield r
    }

    "work with SeqProperty" in {
      val p = SeqProperty[T](TO1, TO2)
      p.addValidator(new Validator[Seq[T]] {
        override def apply(element: Seq[T]): Future[ValidationResult] = Future {
          if (element.contains(TO1)) Valid
          else Invalid("Error")
        }
      })
      p.listenStructure(patch => {
        patch.added.foreach(_.addValidator(new Validator[T] {
          override def apply(element: T): Future[ValidationResult] = Future {
            element match {
              case TC1(i) if i < 0 => Invalid("ElemError1")
              case TC2(s) if s.length > 5 => Invalid("ElemError2")
              case _ => Valid
            }
          }
        }))
      })

      for {
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.remove(TO1))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error"))
          p.elemProperties.foreach(sp => sp.isValid.value.get.get should be(Valid))
        }
        _ <- Future(p.append(TO1, TC1(1), TC2("asd")))
        _ <- retrying {
          p.isValid.value.get.get should be(Valid)
        }
        _ <- Future(p.elemProperties(2).set(TC1(-3)))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("ElemError1"))
        }
        _ <- Future(p.remove(TO1))
        _ <- retrying {
          p.isValid.value.get.get should be(Invalid("Error", "ElemError1"))
        }
        _ <- Future {
          p.remove(TC1(-3))
          p.remove(TC2("asd"))
          p.append(TO1)
        }
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.append(TC1(-21), TC2("blablablabla")))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("ElemError1", "ElemError2")))
        _ <- Future(p.remove(TO1))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("Error", "ElemError1", "ElemError2")))
        _ <- Future(p.replace(0, 3, TO1))
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.set(Seq.empty))
        _ <- retrying(p.isValid.value.get.get should be(Invalid("Error")))
        _ <- Future(p.clearValidators())
        r <- retrying(p.isValid.value.get.get should be(Valid))
      } yield r
    }

    "allow usage of custom validation error types" in {
      sealed trait RangeValidationErrors
      case class ValueIsTooSmall(value: Int, min: Int) extends ValidationError {
        override def message: String =
          s"$value is smaller than $min!"
      }
      case class ValueIsTooBig(value: Int, max: Int) extends ValidationError {
        override def message: String =
          s"$value is bigger than $max!"
      }

      val validRange = (0, 10)
      val p = Property(5)
      p.addValidator((element: Int) => {
          if (element < validRange._1) Invalid(ValueIsTooSmall(element, validRange._1))
          else if (element > validRange._2) Invalid(ValueIsTooBig(element, validRange._2))
          else Valid
        }
      )

      for {
        _ <- retrying(p.isValid.value.get.get should be(Valid))
        _ <- Future(p.set(-4))
        _ <- retrying {
          (p.isValid.value.get.get match {
            case Invalid(errors) =>
              errors.forall {
                case ValueIsTooSmall(-4, 0) => true
                case _ => false
              }
            case _ => false
          }) should be(true)
        }
        _ <- Future(p.set(21))
        r <- retrying {
          (p.isValid.value.get.get match {
            case Invalid(errors) =>
              errors.forall {
                case ValueIsTooBig(21, 10) => true
                case _ => false
              }
            case _ => false
          }) should be(true)
        }
      } yield r
    }

    "return valid result future" in {
      val futures = mutable.ArrayBuffer[Future[ValidationResult]]()
      val p = Property("Test")
      p.listen((_) => {
        val f = Seq(p.isValid, p.isValid, p.isValid)
        futures ++= f
        f.foreach(_ shouldNot be(null))
      })
      p.addValidator((_) => Valid)

      for {
        _ <- retrying(p.isValid shouldNot be(null))
        _ <- Future(p.set("Test 2"))
        _ <- retrying(p.isValid shouldNot be(null))
        _ <- Future(CallbackSequencer().sequence { p.set("Test 3") })
        _ <- retrying(p.isValid shouldNot be(null))
        _ <- Future(CallbackSequencer().sequence {
          p.set("Test 4")
          p.set("Test 5")
          p.set("Test 6")
        })
        _ <- retrying(p.isValid shouldNot be(null))
        _ <- Future {
          p.set("Test 4")
          p.set("Test 5")
          p.set("Test 6")
        }
        _ <- retrying(p.isValid shouldNot be(null))
        r <- retrying(futures.forall(_.isCompleted) should be(true))
      } yield r
    }

    "provide property with validation result" in {
      val p = Property("Test")
      p.addValidator((s) => if (s.length > 3) Valid else Invalid("Too short."))

      p.set("T")
      val v = p.valid

      for {
        _ <- retrying(v.get shouldNot be(Valid))
        _ <- Future(p.set("Test"))
        _ <- retrying(v.get should be(Valid))
        _ <- Future(CallbackSequencer().sequence {
          p.set("Test 4")
          p.set("Test")
          p.set("Te")
        })
        _ <- retrying(v.get shouldNot be(Valid))
        _ <- Future(CallbackSequencer().sequence {
          p.set("Test 4")
          p.set("T")
          p.set("Test 6")
        })
        _ <- retrying(v.get should be(Valid))
        _ <- Future(p.set("T"))
        _ <- retrying(v.get shouldNot be(Valid))
        _ <- Future(p.clearValidators())
        r <- retrying(v.get should be(Valid))
      } yield r
    }
  }
}
