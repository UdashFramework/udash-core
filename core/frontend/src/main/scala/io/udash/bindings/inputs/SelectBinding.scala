package io.udash.bindings.inputs

import io.udash._
import io.udash.properties.PropertyCreator
import org.scalajs.dom.Event
import org.scalajs.dom.html.Select
import scalatags.JsDom.all._

private[inputs] class SelectBinding[T : PropertyCreator](
  options: ReadableProperty[Seq[T]], label: T => Modifier, selectModifiers: Modifier*
)(
  checkedIf: T => ReadableProperty[Boolean],
  refreshSelection: Seq[T] => Unit,
  onChange: Select => Event => Unit
) extends InputBinding[Select] {
  private val selector = select(selectModifiers)(
    nestedInterceptor(
      produceWithNested(options) { case (opts, nested) =>
        refreshSelection(opts)

        opts.zipWithIndex.map { case (opt, idx) =>
          option(
            value := idx.toString,
            nested((selected := "selected").attrIf(checkedIf(opt)))
          )(label(opt)).render
        }
      }
    )
  ).render

  selector.onchange = onChange(selector)

  override def render: Select = selector
}