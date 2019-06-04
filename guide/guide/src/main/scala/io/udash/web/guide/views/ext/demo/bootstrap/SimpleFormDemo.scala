package io.udash.web.guide.views.ext.demo.bootstrap

import io.udash._
import io.udash.bootstrap.button.UdashButton
import io.udash.bootstrap.form.UdashForm
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.web.guide.demos.AutoDemo
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

import scala.concurrent.Future

object SimpleFormDemo extends AutoDemo with CrossLogging with CssView {

  import JsDom.all._
  import io.udash.bootstrap.utils.BootstrapImplicits._
  import io.udash.web.guide.Context._

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
    user.subProp(_.age).addValidator(new Validator[Int] {
      override def apply(element: Int): Future[ValidationResult] =
        Future {
          if (element < 0) Invalid("Age should be a non-negative integer!")
          else Valid
        }
    })

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
          user.subProp(_.shirtSize),
          Seq[ShirtSize](Small, Medium, Large).toSeqProperty,
          inline = true.toProperty,
          validationTrigger = UdashForm.ValidationTrigger.None
        )(labelContent = (item, _, _) => Some(label(shirtSizeToLabel(item)))),
        factory.disabled()(_ => UdashButton()("Send").render)
      ))
    )
  }.withSourceCode

  override protected def demoWithSource(): (JsDom.all.Modifier, Iterator[String]) = {
    (div(GuideStyles.frame)(rendered), source.lines.drop(1))
  }
}

