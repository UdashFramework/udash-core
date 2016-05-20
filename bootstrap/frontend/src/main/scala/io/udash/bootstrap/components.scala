import com.karasiq.bootstrap._
import com.karasiq.bootstrap.alert.AlertStyle
import com.karasiq.bootstrap.buttons.{ButtonBuilder, ButtonSize, ButtonStyle}
import io.udash._
import io.udash.bootstrap.UdashBootstrapImplicits
import org.scalajs.dom

import scalatags.JsDom.all._

object Alert {
  def apply(style: AlertStyle, md: Modifier*): ConcreteHtmlTag[dom.html.Div] = alert.Alert(style, md: _*)
}

object Button {
  // Shortcut to ButtonBuilder()
  def apply(style: ButtonStyle = ButtonStyle.default, size: ButtonSize = ButtonSize.default, block: Boolean = false, active: Boolean = false, disabled: Boolean = false): ButtonBuilder =
    buttons.Button(style, size, block, active, disabled)
}

object Carousel extends UdashBootstrapImplicits {
  /*
    def reactive(data: SeqProperty[Modifier, Property[Modifier]], id: String = Bootstrap.newId): BoostrapCarousel =
      BoostrapCarousel.reactive(data, id)
  */

  def apply(content: Modifier*): carousel.Carousel =
    carousel.Carousel(content: _*)

}

object Collapse {
  def apply(btnTitle: String)(content: Modifier*): Modifier = collapse.Collapse(btnTitle)(content: _*)
}

object Dropdown {
  def item(md: Modifier*): Tag = dropdown.Dropdown.item(md: _*)

  def link(target: String, md: Modifier*): Tag = dropdown.Dropdown.link(target, md: _*)

  def apply(title: Modifier, items: Modifier*): Tag = dropdown.Dropdown(title, items: _*)

  def dropup(title: Modifier, items: Modifier*): Tag = dropdown.Dropdown.dropup(title, items: _*)
}

object Form {

  def submit(text: Modifier): ConcreteHtmlTag[dom.html.Button] = com.karasiq.bootstrap.form.Form.submit(text)

  def apply(md: Modifier*): ConcreteHtmlTag[dom.html.Form] = com.karasiq.bootstrap.form.Form(md: _*)

  def inline(md: Modifier*): ConcreteHtmlTag[dom.html.Form] = com.karasiq.bootstrap.form.Form.inline(md: _*)
}

object FormInput extends UdashBootstrapImplicits {

  import com.karasiq.bootstrap.form._

  def ofType(tpe: String, label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.ofType(tpe, label, md: _*)

  def text(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.text(label, md: _*)

  def number(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.number(label, md: _*)

  def email(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.email(label, md: _*)

  def password(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.password(label, md: _*)

  def file(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.file(label, md: _*)

  def textArea(title: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.textArea(title, md: _*)

  def checkbox(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput.checkbox(label, md: _*)

  def radio(title: String, radioName: String, radioValue: String, radioId: String = Bootstrap.newId): FormRadio =
    com.karasiq.bootstrap.form.FormInput.radio(title, radioName, radioValue, radioId)

  def radioGroup(radios: FormRadio*): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios: _*)

  def radioGroup(radios: SeqProperty[FormRadio]): FormRadioGroup = com.karasiq.bootstrap.form.FormInput.radioGroup(radios)

  def select(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options: _*)

  def select(title: Modifier, options: SeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.select(title, options)

  def multipleSelect(title: Modifier, options: String*): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options: _*)

  def multipleSelect(title: Modifier, options: SeqProperty[String]): FormSelect = com.karasiq.bootstrap.form.FormInput.multipleSelect(title, options)

  def apply(label: Modifier, md: Modifier*): Tag = com.karasiq.bootstrap.form.FormInput(label, md: _*)
}
