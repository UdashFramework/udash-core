package io.udash.bindings.inputs

import com.avsystem.commons.BSeq
import io.udash._
import io.udash.properties.PropertyCreator
import org.scalajs.dom.html.{Div, Input => JSInput}
import org.scalajs.dom.{Event, Node}
import scalatags.JsDom.all._

import scala.util.Random

private[inputs] class GroupedButtonsBinding[T : PropertyCreator](
  options: ReadableSeqProperty[T], decorator: BSeq[(JSInput, T)] => BSeq[Node], inputModifiers: Modifier*
)(
  inputTpe: String,
  checkedIf: T => ReadableProperty[Boolean],
  refreshSelection: BSeq[T] => Unit,
  onChange: (JSInput, T) => Event => Unit
) extends InputBinding[Div] {
  private val groupIdPrefix: Long = Random.nextLong

  private val buttons = div(
    produce(options) { opts =>
      kill()
      refreshSelection(opts)

      decorator(
        opts.zipWithIndex.map { case (opt, idx) =>
          val in = input(
            id := s"$groupIdPrefix-$idx", // default id, can be replaced by `inputModifiers`
            inputModifiers, tpe := inputTpe, value := idx.toString
          ).render

          val selected = checkedIf(opt)
          propertyListeners += selected.listen(in.checked = _, initUpdate = true)
          in.onchange = onChange(in, opt)

          (in, opt)
        }
      )
    }
  ).render

  override def render: Div = buttons
}
