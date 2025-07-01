package io.udash.properties

import com.avsystem.commons.serialization.json.JsonStringOutput
import io.udash.properties.model.ModelProperty
import io.udash.testing.UdashCoreTest

class HasGenCodecAndModelPropertyCreatorTest extends UdashCoreTest {
  import HasGenCodecAndModelPropertyCreatorTest._

  "HasGenCodecAndModelPropertyCreator class" should {
    "provide valid GenCodec" in {
      JsonStringOutput.write(Entity(1, "a", Some(Entity(2, "b", None)))) should be(
        """{"i":1,"s":"a","e":{"i":2,"s":"b","e":null}}"""
      )
    }

    "provide valid ModelPropertyCreator" in {
      val p = ModelProperty(Entity(1, "a", Some(Entity(2, "b", None))))

      p.subProp(_.i).get should be(1)
      p.subProp(_.s).get should be("a")
    }
  }
}

object HasGenCodecAndModelPropertyCreatorTest {
  final case class Entity(i: Int, s: String, e: Option[Entity])
  object Entity extends HasGenCodecAndModelPropertyCreator[Entity]
}
