package io.udash.web.commons.styles.attributes

object Attributes {
  /**
    * States
    */
  val Hidden = "hidden"
  val Active = "active"
  val Disabled = "disabled"
  val Enabled = "enabled"
  val Checked = "checked"
  val Show = "show"
  val Expanded = "expanded"
  val State = "state"
  val Pinned = "pinned"

  /**
    * Generate attribute data-attr
    * @param attr name of attribute
    * @return attribute data-attr
    */
  def data(attr: String) = s"data-$attr"
}
