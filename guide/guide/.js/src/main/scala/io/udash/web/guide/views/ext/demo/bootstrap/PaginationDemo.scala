package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object PaginationDemo extends AutoDemo {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.{UdashButton, UdashButtonGroup}
    import io.udash.bootstrap.pagination.UdashPagination
    import io.udash.bootstrap.pagination.UdashPagination._
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import io.udash.bootstrap.utils.BootstrapStyles._
    import io.udash.css.CssView._
    import scalatags.JsDom.all._

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
    )(defaultPageFactory).render
    pagination.firstElementChild.applyTags(
      Flex.justifyContent(FlexContentJustification.Center)
    )

    div(
      div(Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      ))(
        UdashButtonGroup()(
          toggleArrows.render,
          toggleHighlight.render
        )
      ),
      div(Spacing.margin(
        side = Side.Bottom,
        size = SpacingSize.Normal
      ))("Selected page index: ", bind(selected)),
      div(pagination)
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    import io.udash.css.CssView._
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

