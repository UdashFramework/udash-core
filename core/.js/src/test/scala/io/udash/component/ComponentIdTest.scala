package io.udash
package component

import io.udash.testing.UdashFrontendTest

class ComponentIdTest extends UdashFrontendTest {
  class TestA {
    val id: ComponentId = ComponentId.generate()
  }

  class TestB {
    val id: ComponentId = ComponentId.generate()
  }

  class Origin(fromOuterScope: () => ComponentId) {
    def doGenerateFromOuterScope(): ComponentId = fromOuterScope()
    def generateFromInnerScope: ComponentId = ComponentId.generate()
  }

  "ComponentId" should {
    "allow arbitrary ID value" in {
      val provided: String = "xyz"
      ComponentId(provided).value shouldBe provided
      ComponentId(provided).value shouldBe provided
    }

    "add sequential numbers to arbitrary values" in {
      val provided: String = "xyz"
      ComponentId.forName(provided).value shouldBe s"$provided-0"
      ComponentId.forName(provided).value shouldBe s"$provided-1"
    }

    "contain fully qualified class name" in {
      ComponentId.generate().value should startWith("io-udash-component-ComponentIdTest-")
    }

    "support suffixes" in {
      val parent = ComponentId.forName("parent")
      val number = parent.value.stripPrefix("parent-").toInt

      parent.withSuffix("child").value shouldBe s"parent-$number-child"
      parent.withSuffix("child").value shouldBe s"parent-$number-child"
      parent.withSuffix("child2").value shouldBe s"parent-$number-child2"
    }

    "support unique suffixes" in {
      val parent = ComponentId.forName("parent")
      val number = parent.value.stripPrefix("parent-").toInt

      parent.withUniqueSuffix("child").value shouldBe s"parent-$number-child-${number + 1}"
      parent.withUniqueSuffix("child").value shouldBe s"parent-$number-child-${number + 2}"
      parent.withUniqueSuffix("child2").value shouldBe s"parent-$number-child2-${number + 3}"
    }

    "carry info on definition place through containing generated id prefix for generated ids" in {
      val definedInOuterScope: () => ComponentId = () => ComponentId.generate()

      val origin: Origin = new Origin(definedInOuterScope)

      val thisClassName: String = this.getClass.getSimpleName
      val originClassName: String = origin.getClass.getSimpleName

      val fromInnerScope: ComponentId = origin.generateFromInnerScope
      fromInnerScope.value should include(thisClassName)
      fromInnerScope.value should include(originClassName)

      val fromOuterScope: ComponentId = origin.doGenerateFromOuterScope()
      fromOuterScope.value should include(thisClassName)
      fromOuterScope.value shouldNot include(originClassName)
    }

    "contain invoking encloser name for generated id" in {
      val id: ComponentId = ComponentId.generate()
      id.value should include(this.getClass.getSimpleName)

      val a1: TestA = new TestA
      a1.id.value should include(a1.getClass.getSimpleName)
    }

    "be different for multiple generated ids in given class" in {
      val a1: TestA = new TestA
      val a2: TestA = new TestA

      a1.id should not equal a2.id
    }
  }

}
