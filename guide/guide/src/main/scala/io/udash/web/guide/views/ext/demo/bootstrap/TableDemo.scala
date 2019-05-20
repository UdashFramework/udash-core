package io.udash.web.guide.views.ext.demo.bootstrap

import com.avsystem.commons.ISeq
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.table.UdashTable
import io.udash.bootstrap.utils.BootstrapStyles.ResponsiveBreakpoint
import io.udash.css.CssView
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.{Property, produce, _}
import scalatags.JsDom

import scala.util.Random

object TableDemo extends AutoDemo with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    val responsive = Property[Option[ResponsiveBreakpoint]](Some(ResponsiveBreakpoint.All))
    val dark = Property(false)
    val striped = Property(true)
    val bordered = Property(true)
    val hover = Property(true)
    val small = Property(false)

    val darkButton = UdashButton.toggle(active = dark)("Dark theme")
    val stripedButton = UdashButton.toggle(active = striped)("Striped")
    val borderedButton = UdashButton.toggle(active = bordered)("Bordered")
    val hoverButton = UdashButton.toggle(active = hover)("Hover")
    val smallButton = UdashButton.toggle(active = small)("Small")

    val items = SeqProperty(
      Seq.fill(7)((Random.nextDouble(), Random.nextDouble(), Random.nextDouble()))
    )
    val table = UdashTable(
      items, responsive, dark,
      striped = striped,
      bordered = bordered,
      hover = hover,
      small = small
    )(
      headerFactory = Some(_ => tr(ISeq("x", "y", "z").map(header => th(b(header)))).render),
      rowFactory = (el, nested) => tr(
        nested(produce(el)(v => ISeq(v._1, v._2, v._3).map(td(_).render)))
      ).render
    )

    div(
      UdashButtonGroup(justified = true.toProperty)(
        darkButton.render,
        stripedButton.render,
        borderedButton.render,
        hoverButton.render,
        smallButton.render
      ).render,
      table.render
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

