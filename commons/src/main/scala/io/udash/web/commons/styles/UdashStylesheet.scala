package io.udash.web.commons.styles

import scalacss.ProdDefaults._

/**
  * Created by malchik on 2016-04-29.
  */

/**
  * Use "get" to register and get static styles object
  * Every stylesheet extends UMPStylesheet must override type member T
  */
trait UdashStylesheet extends StyleSheet.Inline {
  type T = this.type
  private lazy val styleSheet: T = {
    StyleRegistry.register(this)
    this
  }

  /**
    * Dirty hack fixes the scala plugin bug
    * @return
    */
  def get = styleSheet
}
