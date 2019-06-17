package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
import io.udash.bootstrap.pagination.UdashPagination
import io.udash.bootstrap.utils.BootstrapStyles
import io.udash.bootstrap.utils.BootstrapStyles.{Side, SpacingSize}
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object PaginationDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    import UdashPagination._

    val showArrows = Property(true)
    val highlightActive = Property(true)
    val toggleArrows = UdashButton.toggle(active = showArrows)(
      "Toggle arrows"
    )
    val toggleHighlight = UdashButton.toggle(active = highlightActive)(
      "Toggle highlight"
    )

    val pages = SeqProperty(0 to 7)
    val selected = Property(0)
    val pagination = UdashPagination(
      pages, selected,
      showArrows = showArrows, highlightActive = highlightActive
    )(defaultPageFactory).render.setup(_.firstElementChild.applyTags(
      BootstrapStyles.Flex.justifyContent(
        BootstrapStyles.FlexContentJustification.Center
      )
    ))

    div(
      div(BootstrapStyles.Spacing.margin(
        side = Side.Bottom, size = SpacingSize.Normal
      ))(
        UdashButtonGroup()(
          toggleArrows.render,
          toggleHighlight.render
        )
      ),
      div(BootstrapStyles.Spacing.margin(
        side = Side.Bottom, size = SpacingSize.Normal
      ))("Selected page index: ", bind(selected)),
      div(pagination)
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.linesIterator.drop(1))
  }
}

