package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.breadcrumb.UdashBreadcrumbs
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

object BreadcrumbsDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._

  private val (rendered, source) = {
    import UdashBreadcrumbs._

    val pages = SeqProperty[Breadcrumb](
      new Breadcrumb("Udash", Url("http://udash.io/")),
      new Breadcrumb("Dev's Guide", Url("http://guide.udash.io/")),
      new Breadcrumb("Extensions", Url("http://guide.udash.io/")),
      new Breadcrumb("Bootstrap wrapper", Url("http://guide.udash.io/ext/bootstrap"))
    ).readable

    val breadcrumbs = UdashBreadcrumbs(pages)(
      (pageProperty, nested) => nested(produce(pageProperty) { page =>
        if (pages.get.last == page) JsDom.StringFrag(page.name).render
        else a(href := page.link)(page.name).render
      }),
      pages.get.last == _
    )

    div(breadcrumbs)
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

