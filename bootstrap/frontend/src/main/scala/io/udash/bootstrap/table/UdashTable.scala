package io.udash.bootstrap
package table

import io.udash._
import io.udash.bootstrap.UdashBootstrap.ComponentId
import io.udash.properties.seq
import org.scalajs.dom
import org.scalajs.dom._

class UdashTable[ItemType, ElemType <: Property[ItemType]] private
                (striped: Property[Boolean], bordered: Property[Boolean], hover: Property[Boolean],
                 condensed: Property[Boolean], override val componentId: ComponentId)
                (val items: seq.SeqProperty[ItemType, ElemType])
                (headerFactory: Option[() => dom.Element],
                 rowFactory: (ElemType) => dom.Element) extends UdashBootstrapComponent {

  override lazy val render: Element = {
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
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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
  def apply[ItemType, ElemType <: Property[ItemType]]
           (striped: Property[Boolean] = Property(false), bordered: Property[Boolean] = Property(false),
            hover: Property[Boolean] = Property(false), condensed: Property[Boolean] = Property(false),
            componentId: ComponentId = UdashBootstrap.newId())
           (items: seq.SeqProperty[ItemType, ElemType])
           (rowFactory: (ElemType) => dom.Element,
            headerFactory: Option[() => dom.Element] = None): UdashTable[ItemType, ElemType] =
    new UdashTable(striped, bordered, hover, condensed, componentId)(items)(headerFactory, rowFactory)
}
