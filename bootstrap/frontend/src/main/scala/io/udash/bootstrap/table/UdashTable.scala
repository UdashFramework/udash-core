package io.udash.bootstrap
package table

import io.udash._
import org.scalajs.dom
import org.scalajs.dom._

class UdashTable[ItemType, ElemType <: Property[ItemType]] private
                (striped: Property[Boolean], bordered: Property[Boolean], hover: Property[Boolean], condensed: Property[Boolean])
                (items: properties.SeqProperty[ItemType, ElemType])
                (headerFactory: Option[() => dom.Element],
                 rowFactory: (ElemType) => dom.Element) extends UdashBootstrapComponent {

  lazy val render: Element = {
    import scalatags.JsDom.all._

    table(
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

  def apply[ItemType, ElemType <: Property[ItemType]]
           (striped: Property[Boolean] = Property(false), bordered: Property[Boolean] = Property(false),
            hover: Property[Boolean] = Property(false), condensed: Property[Boolean] = Property(false))
           (items: properties.SeqProperty[ItemType, ElemType])
           (rowFactory: (ElemType) => dom.Element,
            headerFactory: Option[() => dom.Element] = None): UdashTable[ItemType, ElemType] =
    new UdashTable(striped, bordered, hover, condensed)(items)(headerFactory, rowFactory)
}
