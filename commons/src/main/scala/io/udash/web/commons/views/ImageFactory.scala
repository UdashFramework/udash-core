package io.udash.web.commons.views

import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scalatags.JsDom
import scalatags.JsDom.all._
import scalatags.generic.Namespace

class ImageFactoryPrefix(val value: String)
object ImageFactoryPrefixSet {
  case object Intro extends ImageFactoryPrefix(value = "/assets/images/quick")
  case object Boostrapping extends ImageFactoryPrefix(value = "/assets/images/views/bootstrapping")
  case object Frontend extends ImageFactoryPrefix(value = "/assets/images/views/frontend")
}

object ClickableImageFactory {
  def apply(prefix: ImageFactoryPrefix, name: String, altText: String, xs: Modifier*): JsDom.TypedTag[dom.html.Anchor] =
    a(href := "/" + prefix.value + "/" + name, target := "_blank", xs)(
      (new ImageFactory(prefix.value))(name, altText)
    )
}

class ImageFactory(prefix: String) {
  import scalatags.JsDom.all._
  def apply(name: String, altText: String, xs: Modifier*): JsDom.TypedTag[dom.html.Image] = {
    img(src := s"$prefix/$name", alt := altText, xs)
  }
}

object Image extends ImageFactory("/assets/images")

object SVG {
  import scalatags.JsDom.{svgAttrs, svgTags}
  def apply(name: String, size: Size, xs: Modifier*): JsDom.TypedTag[Element] = {
    div(style := s"position: relative; width: 100%; padding-top: ${100 * size.height / size.width}%")(
      svgTags.svg(xmlns := Namespace.svgNamespaceConfig.uri, svgAttrs.viewBox := s"0 0 ${size.width} ${size.height}", style := "position: absolute; top: 0; left: 0; width: 100%; height: 100%;")(
        svgTags.use(svgAttrs.xmlnsXlink := Namespace.svgXlinkNamespaceConfig.uri, svgAttrs.xLinkHref := s"/assets/svg/$name")
      )
    )
  }
}

case class Size(width: Int, height: Int)