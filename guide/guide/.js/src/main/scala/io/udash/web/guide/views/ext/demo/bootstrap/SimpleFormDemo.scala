package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash.css.CssView
import io.udash.properties.{Blank, HasModelPropertyCreator}
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom.all._

object SimpleFormDemo extends AutoDemo with CssView {

  sealed trait ShirtSize

  case object Small extends ShirtSize

  case object Medium extends ShirtSize

  case object Large extends ShirtSize

  trait UserModel {
    def name: String

    def age: Int

    def shirtSize: ShirtSize
  }

  object UserModel extends HasModelPropertyCreator[UserModel] {
    implicit val blank: Blank[UserModel] = Blank.Simple(new UserModel {
      override def name: String = ""

      override def age: Int = 25

      override def shirtSize: ShirtSize = Medium
    })
  }

  private val (rendered, source) = {
    import io.udash._
    import io.udash.bootstrap.button.UdashButton
    import io.udash.bootstrap.form.UdashForm
    import io.udash.bootstrap.utils.BootstrapImplicits._
    import scalatags.JsDom.all._

    /*
    sealed trait ShirtSize

    case object Small extends ShirtSize

    case object Medium extends ShirtSize

    case object Large extends ShirtSize

    trait UserModel {
      def name: String
      def age: Int
      def shirtSize: ShirtSize
    }

    object UserModel extends HasModelPropertyCreator[UserModel] {
      implicit val blank: Blank[UserModel] = Blank.Simple(new UserModel {
        override def name: String = ""
        override def age: Int = 25
        override def shirtSize: ShirtSize = Medium
      })
    }
    */

    def shirtSizeToLabel(size: ShirtSize): String = size match {
      case Small => "S"
      case Medium => "M"
      case Large => "L"
    }

    val user = ModelProperty.blank[UserModel]
    user.subProp(_.age).addValidator(
      (element: Int) =>
        if (element < 0) Invalid("Age should be a non-negative integer!") else Valid
    )

    div(
      UdashForm()(factory => Seq(
        factory.input.formGroup()(
          input = _ => factory.input.textInput(user.subProp(_.name))().render,
          labelContent = Some(_ => "User name": Modifier)
        ),
        factory.input.formGroup()(
          input = _ => factory.input.numberInput(
            user.subProp(_.age).transform(_.toString, _.toInt),
          )().render,
          labelContent = Some(_ => "Age": Modifier),
          invalidFeedback = Some(_ => "Age should be a non-negative integer!")
        ),
        factory.input.radioButtons(
          selectedItem = user.subProp(_.shirtSize),
          options = Seq[ShirtSize](Small, Medium, Large).toSeqProperty,
          inline = true.toProperty,
          validationTrigger = UdashForm.ValidationTrigger.None
        )(labelContent = (item, _, _) => Some(label(shirtSizeToLabel(item)))),
        factory.disabled()(_ => UdashButton()("Send").render)
      ))
    ).render
  }.withSourceCode

  override protected def demoWithSource(): (Modifier, Iterator[String]) = {
    (rendered.setup(_.applyTags(GuideStyles.frame)), source.linesIterator)
  }
}

