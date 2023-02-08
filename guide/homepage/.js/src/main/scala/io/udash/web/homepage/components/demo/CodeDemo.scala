package io.udash.web.homepage.components.demo

import com.avsystem.commons.SharedExtensions._
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
    import com.avsystem.commons.misc.AutoNamedEnum

    sealed trait Fruit extends AutoNamedEnum
    case object Apple extends Fruit
    case object Banana extends Fruit
    case object Orange extends Fruit

    val fruits = Seq(Apple, Banana, Orange)
    val favoriteFruits = SeqProperty[Fruit](Banana)

    div(
      div(Select(favoriteFruits, fruits.toSeqProperty)(_.name)),
      div(produce(favoriteFruits)(_.mkString(",").render)),
    )
  }.withSourceCode
}
