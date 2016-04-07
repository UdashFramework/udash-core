package io.udash.homepage.wrappers

import io.udash.wrappers.jquery.JQuery

/**
  * Created by malchik on 2016-04-05.
  * Wrapper for jQuery custom content scroller
  * Sources:
  * http://manos.malihu.gr/jquery-custom-content-scroller/
  */
package object scrollbar {

  /**
    * A map of option values, which JSOptionBuilder builds up.
    */
  type OptMap = Map[String, Any]

  /**
    * An initial empty map of option values, which you use to begin building up
    * the options object.
    */
  val noOpts = Map.empty[String, Any]

  def jq2CustomScrollbar(jq:JQuery): CustomScrollbar = jq.asInstanceOf[CustomScrollbar]
  implicit def customScrollbar2Commands(scrollbar: CustomScrollbar): CustomScrollCommands = new CustomScrollCommands(scrollbar)
}
