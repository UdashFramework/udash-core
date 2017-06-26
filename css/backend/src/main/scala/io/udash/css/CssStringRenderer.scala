package io.udash.css

import scalacss.internal.Renderer

/** Renders provided styles into `String`. Keeps styles order from provided `Seq`. */
class CssStringRenderer(styles: Seq[CssBase]) {
  def render()(implicit renderer: Renderer[String]): String = {
    val builder = new StringBuilder

    styles.foreach { style =>
      builder.append(style.render)
    }

    builder.mkString
  }
}
