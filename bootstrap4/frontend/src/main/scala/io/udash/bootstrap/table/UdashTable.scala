package io.udash.bootstrap
package table

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.properties.seq
import org.scalajs.dom._
import scalatags.JsDom.all._

final class UdashTable[ItemType, ElemType <: ReadableProperty[ItemType]] private(
  items: seq.ReadableSeqProperty[ItemType, ElemType],
  responsive: ReadableProperty[Option[BootstrapStyles.ResponsiveBreakpoint]],
  dark: ReadableProperty[Boolean],
  striped: ReadableProperty[Boolean],
  bordered: ReadableProperty[Boolean],
  borderless: ReadableProperty[Boolean],
  hover: ReadableProperty[Boolean],
  small: ReadableProperty[Boolean],
  override val componentId: ComponentId
)(
  captionFactory: Option[Binding.NestedInterceptor => Modifier],
  headerFactory: Option[Binding.NestedInterceptor => Modifier],
  rowFactory: (ElemType, Binding.NestedInterceptor) => Element
) extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element = {
    div(
      nestedInterceptor((BootstrapStyles.Table.responsive _).reactiveOptionApply(responsive)),
      table(
        id := componentId,
        BootstrapStyles.Table.table,
        nestedInterceptor(BootstrapStyles.Table.dark.styleIf(dark)),
        nestedInterceptor(BootstrapStyles.Table.striped.styleIf(striped)),
        nestedInterceptor(BootstrapStyles.Table.bordered.styleIf(bordered)),
        nestedInterceptor(BootstrapStyles.Table.borderless.styleIf(borderless)),
        nestedInterceptor(BootstrapStyles.Table.hover.styleIf(hover)),
        nestedInterceptor(BootstrapStyles.Table.small.styleIf(small))
      )(
        captionFactory.map(content => caption(content(nestedInterceptor)).render),
        headerFactory.map(head => thead(head(nestedInterceptor)).render),
        tbody(
          nestedInterceptor(
            repeatWithNested(items) { case (item, nested) =>
              rowFactory(item, nested)
            }
          )
        )
      )
    ).render
  }
}

object UdashTable {

  /**
    * Creates progress bar component.
    * More: <a href="http://getbootstrap.com/css/#tables">Bootstrap Docs</a>.
    *
    * @param items          Elements which will be rendered as rows.
    * @param responsive     If defined, the table will be horizontally scrollable on selected screen size.
    * @param dark           Switch table to the dark theme.
    * @param striped        Turn on zebra-striping.
    * @param bordered       Add vertical borders.
    * @param borderless     Removes all borders.
    * @param hover          Highlight row on hover.
    * @param small          Makes table more compact.
    * @param componentId    Id of the root DOM node.
    * @param rowFactory     Creates row representation of the table element - it should create the `tr` tag.
    * @param headerFactory  Creates table header - it should create the `tr` tag.
    *                       Table without header will be rendered if `None` passed.
    * @param captionFactory Creates table caption - the result will be wrapped into the `caption` tag.
    *                       Table without caption will be rendered if `None` passed.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashTable` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]](
    items: seq.ReadableSeqProperty[ItemType, ElemType],
    responsive: ReadableProperty[Option[BootstrapStyles.ResponsiveBreakpoint]]  = UdashBootstrap.None,
    dark: ReadableProperty[Boolean] = UdashBootstrap.False,
    striped: ReadableProperty[Boolean] = UdashBootstrap.False,
    bordered: ReadableProperty[Boolean] = UdashBootstrap.False,
    borderless: ReadableProperty[Boolean] = UdashBootstrap.False,
    hover: ReadableProperty[Boolean] = UdashBootstrap.False,
    small: ReadableProperty[Boolean] = UdashBootstrap.False,
    componentId: ComponentId = ComponentId.newId()
  )(
    rowFactory: (ElemType, Binding.NestedInterceptor) => Element,
    headerFactory: Option[Binding.NestedInterceptor => Modifier] = None,
    captionFactory: Option[Binding.NestedInterceptor => Modifier] = None
  ): UdashTable[ItemType, ElemType] = {
    new UdashTable(
      items, responsive, dark, striped, bordered, borderless, hover, small, componentId
    )(captionFactory, headerFactory, rowFactory)
  }
}
