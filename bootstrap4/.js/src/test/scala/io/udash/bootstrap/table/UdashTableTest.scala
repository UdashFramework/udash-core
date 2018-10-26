package io.udash.bootstrap.table

import io.udash._
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.testing.UdashCoreFrontendTest
import scalatags.JsDom.all._

class UdashTableTest extends UdashCoreFrontendTest {

  case class TestEntity(name: String, age: Int, language: String)
  object TestEntity extends HasModelPropertyCreator[TestEntity]

  "UdashTable component" should {
    "update table content and clean up property listeners" in {
      val items = SeqProperty(
        TestEntity("Adam", 34, "EN"),
        TestEntity("John", 21, "GE"),
        TestEntity("Alice", 24, "FR")
      )
      val responsive = Property[Option[BootstrapStyles.ResponsiveBreakpoint]](Some(BootstrapStyles.ResponsiveBreakpoint.Small))
      val dark = Property(false)
      val striped = Property(false)
      val bordered = Property(false)
      val borderless = Property(true)
      val hover = Property(false)
      val small = Property(false)
      val table = UdashTable(
        items, responsive, dark, striped, bordered, borderless, hover, small
      )(
        (item, nested) => tr(
          td(nested(bind(item.asModel.subProp(_.name)))),
          td(nested(bind(item.asModel.subProp(_.age)))),
          td(nested(bind(item.asModel.subProp(_.language))))
        ).render,
        headerFactory = Some(_ => tr(th("Name"), th("Age"), th("Language")))
      )
      val el = table.render
      el.textContent should be("NameAgeLanguageAdam34ENJohn21GEAlice24FR")

      items.elemProperties(1).asModel.subProp(_.name).set("Test")
      el.textContent should be("NameAgeLanguageAdam34ENTest21GEAlice24FR")

      val tmp = items.elemProperties(1)
      items.remove(1, 1)
      el.textContent should be("NameAgeLanguageAdam34ENAlice24FR")
      tmp.listenersCount() should be(0)
      tmp.asModel.subProp(_.name).listenersCount() should be(0)

      items.append(tmp.get)
      el.textContent should be("NameAgeLanguageAdam34ENAlice24FRTest21GE")

      table.kill()
      ensureNoListeners(items)
      items.elemProperties.foreach(_.asModel.subProp(_.name).listenersCount() should be(0))
      responsive.listenersCount() should be(0)
      dark.listenersCount() should be(0)
      striped.listenersCount() should be(0)
      bordered.listenersCount() should be(0)
      borderless.listenersCount() should be(0)
      hover.listenersCount() should be(0)
      small.listenersCount() should be(0)
    }
  }
}
