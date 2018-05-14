package io.udash.properties

import com.avsystem.commons.misc.Opt
import io.udash.properties.model.ModelProperty
import io.udash.properties.single.Property
import io.udash.testing.UdashSharedTest

class DefaultValueTest extends UdashSharedTest {
  import DefaultValueTest._

  "DefaultValue" should {
    "should be used as initial value of a property" in {
      Property.empty[Double].get should be(0.0)
      Property.empty[Float].get should be(0.0f)
      Property.empty[Long].get should be(0L)
      Property.empty[Int].get should be(0)
      Property.empty[Boolean].get should be(false)
      Property.empty[String].get should be("")
      Property.empty[Option[String]].get should be(None)
      Property.empty[Opt[String]].get should be(Opt.empty)
      Property.empty[Map[String, String]].get should be(Map.empty)
      Property.empty[Set[String]].get should be(Set.empty)
      Property.empty[Seq[String]].get should be(Seq.empty)
    }

    "should be used as initial value of a model property" in {
      ModelProperty.empty[Entity].get.i should be(5)
      ModelProperty.empty[Entity].get.s should be("asd")
      ModelProperty.empty[Entity].subProp(_.i).get should be(5)
      ModelProperty.empty[Entity].subProp(_.s).get should be("asd")
    }
  }
}

object DefaultValueTest {
  case class Entity(i: Int, s: String)
  object Entity extends HasModelPropertyCreator[Entity] {
    implicit val default: DefaultValue[Entity] = DefaultValue.Simple(Entity(5, "asd"))
  }
}