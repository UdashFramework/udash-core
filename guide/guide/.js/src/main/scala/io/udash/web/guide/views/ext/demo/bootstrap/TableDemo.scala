package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object TableDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import BootstrapStyles.ResponsiveBreakpoint
    import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
    import io.udash.bootstrap.table.UdashTable
    import scalatags.JsDom.all._

    import scala.util.Random

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

    val items = SeqProperty(Seq.fill(7, 3)(Random.nextDouble()))
    val table = UdashTable(items, responsive, dark, striped, bordered, hover, small)(
      headerFactory = Some(_ => tr(Seq("x", "y", "z").map(header => th(b(header)))).render),
      rowFactory = (el, nested) => tr(
        nested(produce(el)(_.map(td(_).render)))
      ).render
    )

    div(
      UdashButtonGroup(justified = true.toProperty)(
        darkButton.render,
        stripedButton.render,
        borderedButton.render,
        hoverButton.render,
        smallButton.render
      ),
      table
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, String) =
    (rendered.setup(_.applyTags(GuideStyles.frame)), source)
}

