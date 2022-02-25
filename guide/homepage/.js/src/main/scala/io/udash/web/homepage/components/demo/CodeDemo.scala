package io.udash.web.homepage.components.demo

import com.avsystem.commons.SharedExtensions._
import com.avsystem.commons.misc.{AbstractValueEnum, AbstractValueEnumCompanion, EnumCtx}
import io.udash._
import scalatags.JsDom.all._

trait CodeDemo {
  def rendered: Modifier
  def source: String
}

object HelloDemo extends CodeDemo {
  val (rendered, source) = {
    val name = Property.blank[String]
    div(
      TextInput(name)(),
      p("Hello, ", bind(name), "!"),
    )
  }.withSourceCode
}

object SelectDemo extends CodeDemo {
  val (rendered, source) = {
    final class Fruit(implicit ctx: EnumCtx) extends AbstractValueEnum
    object Fruit extends AbstractValueEnumCompanion[Fruit] {
      final val Apple, Banana, Orange: Value = new Fruit
    }
    val favoriteFruits = SeqProperty(Fruit.Banana)

    div(
      div(Select(favoriteFruits, Fruit.values.toSeqProperty)(_.name)),
      div(produce(favoriteFruits)(_.mkString(",").render)),
    )
  }.withSourceCode
}
