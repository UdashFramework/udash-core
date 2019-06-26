package io.udash.bootstrap
package table

import io.udash._
import io.udash.bootstrap.utils.UdashBootstrapComponent
import io.udash.properties.seq
import org.scalajs.dom._

final class UdashTable[ItemType, ElemType <: ReadableProperty[ItemType]] private
                      (striped: ReadableProperty[Boolean], bordered: ReadableProperty[Boolean], hover: ReadableProperty[Boolean],
                       condensed: ReadableProperty[Boolean], override val componentId: ComponentId)
                      (val items: seq.ReadableSeqProperty[ItemType, ElemType])
                      (headerFactory: Option[() => Element],
                       rowFactory: (ElemType) => Element)
  extends UdashBootstrapComponent {

  import io.udash.css.CssView._

  override val render: Element = {
    import scalatags.JsDom.all._

    table(
      id := componentId,
      BootstrapStyles.Table.table,
      BootstrapStyles.Table.tableStriped.styleIf(striped),
      BootstrapStyles.Table.tableBordered.styleIf(bordered),
      BootstrapStyles.Table.tableHover.styleIf(hover),
      BootstrapStyles.Table.tableCondensed.styleIf(condensed)
    )(
      headerFactory.map(head => thead(head()).render),
      tbody(
        repeat(items)(item =>
          rowFactory(item)
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
    * @param striped       Turn on zebra-striping.
    * @param bordered      Add borders.
    * @param hover         Highlight row on hover.
    * @param condensed     Make table more compact.
    * @param componentId   Id of the root DOM node.
    * @param items         Elements which will be represented as rows.
    * @param rowFactory    Creates row representation of the table element.
    * @param headerFactory Creates table header. Table without header will be rendered if `None` passed.
    * @tparam ItemType Single element type in `items`.
    * @tparam ElemType Type of the property containing every element in `items` sequence.
    * @return `UdashTable` component, call render to create DOM element.
    */
  def apply[ItemType, ElemType <: ReadableProperty[ItemType]]
           (striped: ReadableProperty[Boolean] = Property(false), bordered: ReadableProperty[Boolean] = Property(false),
            hover: ReadableProperty[Boolean] = Property(false), condensed: ReadableProperty[Boolean] = Property(false),
            componentId: ComponentId = ComponentId.newId())
           (items: seq.ReadableSeqProperty[ItemType, ElemType])
           (rowFactory: (ElemType) => Element,
            headerFactory: Option[() => Element] = None): UdashTable[ItemType, ElemType] =
    new UdashTable(striped, bordered, hover, condensed, componentId)(items)(headerFactory, rowFactory)
}
