package io.udash.web.homepage.views

import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.generic.Namespace

object Image {
  import scalatags.JsDom.all._
  def apply(name: String, altText: String, xs: Modifier*): JsDom.TypedTag[dom.html.Image] = {
    img(src := s"assets/images/$name", alt := altText, xs)
  }
}

object SVG {
  import scalatags.JsDom.svgTags
  import scalatags.JsDom.svgAttrs
  def apply(name: String, size: Size, xs: Modifier*): JsDom.TypedTag[Element] = {
    div(style := s"position: relative; width: 100%; padding-top: ${100 * size.height / size.width}%")(
      svgTags.svg(xmlns := Namespace.svgNamespaceConfig.uri, svgAttrs.viewBox := s"0 0 ${size.width} ${size.height}", style := "position: absolute; top: 0; left: 0; width: 100%; height: 100%;")(
        svgTags.use(svgAttrs.xmlnsXlink := Namespace.svgXlinkNamespaceConfig.uri, svgAttrs.xLinkHref := s"assets/svg/$name")
      )
    )
  }
}

case class Size(width: Int, height: Int)
