package io.udash.properties

import com.avsystem.commons._
import io.udash.properties.model.ModelProperty
import io.udash.properties.single.Property
import io.udash.testing.UdashCoreTest

class BlankTest extends UdashCoreTest {
  import BlankTest._

  "Blank" should {
    "should be used as initial value of a property" in {
      Property.blank[Double].get should be(0.0)
      Property.blank[Float].get should be(0.0f)
      Property.blank[Long].get should be(0L)
      Property.blank[Int].get should be(0)
      Property.blank[Boolean].get should be(false)
      Property.blank[String].get should be("")
      Property.blank[Option[String]].get should be(None)
      Property.blank[Opt[String]].get should be(Opt.empty)
      Property.blank[Map[String, String]].get should be(Map.empty)
      Property.blank[Set[String]].get should be(Set.empty)
      Property.blank[Seq[String]].get should be(Seq.empty)
    }

    "should be used as initial value of a model property" in {
      ModelProperty.blank[Entity].get.i should be(5)
      ModelProperty.blank[Entity].get.s should be("asd")
      ModelProperty.blank[Entity].subProp(_.i).get should be(5)
      ModelProperty.blank[Entity].subProp(_.s).get should be("asd")
    }
  }
}

object BlankTest {
  case class Entity(i: Int, s: String)
  object Entity extends HasModelPropertyCreator[Entity] {
    implicit val default: Blank[Entity] = Blank.Simple(Entity(5, "asd"))
  }
}