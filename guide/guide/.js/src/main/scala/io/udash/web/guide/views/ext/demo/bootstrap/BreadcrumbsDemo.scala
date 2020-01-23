package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object BreadcrumbsDemo extends AutoDemo with CssView {

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap._
    import io.udash.bootstrap.breadcrumb.UdashBreadcrumbs
    import io.udash.bootstrap.breadcrumb.UdashBreadcrumbs._
    import scalatags.JsDom.all._

    val pages = SeqProperty[Breadcrumb](
      new Breadcrumb("Udash", Url("https://udash.io/")),
      new Breadcrumb("Dev's Guide", Url("https://guide.udash.io/")),
      new Breadcrumb("Extensions", Url("https://guide.udash.io/")),
      new Breadcrumb("Bootstrap wrapper", Url("https://guide.udash.io/ext/bootstrap"))
    ).readable

    div(
      UdashBreadcrumbs(pages)(
        (pageProperty, nested) => nested(produce(pageProperty) { page =>
          if (pages.get.last == page) StringFrag(page.name).render
          else a(href := page.link)(page.name).render
        }),
        pages.get.last == _
      )
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

