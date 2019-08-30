package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.PropertyCreator
import org.scalajs.dom.Event
import org.scalajs.dom.html.Select
import scalatags.JsDom.all._

private[inputs] class SelectBinding[T : PropertyCreator](
  options: ReadableSeqProperty[T], label: T => Modifier, selectModifiers: Modifier*
)(
  checkedIf: T => ReadableProperty[Boolean],
  refreshSelection: Seq[T] => Unit,
  onChange: Select => Event => Unit
) extends InputBinding[Select] {
  private val selector = select(selectModifiers)(
    produce(options) { opts =>
      kill()
      refreshSelection(opts)

      opts.zipWithIndex.map { case (opt, idx) =>
        val el = option(value := idx.toString, label(opt)).render

        val selected = checkedIf(opt)
        propertyListeners += selected.listen(el.selected = _, initUpdate = true)
        el
      }
    }
  ).render

  selector.onchange = onChange(selector)

  override def render: Select = selector
}