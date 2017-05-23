package io.udash.css

import java.io.{File, PrintWriter}

import scalacss.internal.Renderer

class CssStringRenderer(styles: Seq[_ <: CssBase]) {
  def render()(implicit renderer: Renderer[String]): String = {
    val builder = new StringBuilder

    styles.foreach { style =>
      builder.append(style.render)
    }

    builder.mkString
  }
}
