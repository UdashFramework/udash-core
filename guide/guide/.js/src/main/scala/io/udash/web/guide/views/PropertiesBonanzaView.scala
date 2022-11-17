package io.udash.web.guide.views

import com.avsystem.commons.misc.AbstractCase
import io.udash.bindings.inputs.TextInput
import io.udash.core.{Presenter, View, ViewFactory}
import io.udash.css.CssView
import io.udash.properties.Blank
import io.udash.properties.Properties.ModelProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{PropertiesBonanzaState, RoutingState}
import io.udash.{Application, HasModelPropertyCreator, bind}
import scalatags.JsDom.all.{br, _}

final case class PropertiesBonanzaModel(
  inputContent: String,
  lastDistinctInputContent: String,
  numberOfValueChanges: Int,
) extends AbstractCase

object PropertiesBonanzaModel extends HasModelPropertyCreator[PropertiesBonanzaModel] {
  implicit val blank: Blank[PropertiesBonanzaModel] = Blank.Simple(PropertiesBonanzaModel("", "", 0))
}

final case class PropertiesBonanzaViewFactory()(implicit application: Application[RoutingState])
  extends ViewFactory[PropertiesBonanzaState] {

  override def create(): (View, Presenter[PropertiesBonanzaState]) = {
    val model = ModelProperty.blank[PropertiesBonanzaModel]
    (new PropertiesBonanzaView(model), new PropertiesBonanzaPresenter(model))
  }
}
class PropertiesBonanzaPresenter(model: ModelProperty[PropertiesBonanzaModel])(implicit application: Application[RoutingState]) extends Presenter[PropertiesBonanzaState] {

  override def handleState(state: PropertiesBonanzaState): Unit = {
    model.set(PropertiesBonanzaModel(
      inputContent = state.currentInputContent,
      lastDistinctInputContent = state.lastDistinctInput,
      numberOfValueChanges = state.numberOfValueChanges
    ))
  }

  private val inputContentRegistration = model.subProp(_.inputContent).listen { value =>
    val (lastDistinctInput, numberOfValueChanges) =
      if (value != model.subProp(_.lastDistinctInputContent).get)
        (value, model.subProp(_.numberOfValueChanges).get + 1)
      else
        (model.subProp(_.lastDistinctInputContent).get, model.subProp(_.numberOfValueChanges).get)
    application.goTo(PropertiesBonanzaState(currentInputContent = value, lastDistinctInput = lastDistinctInput, numberOfValueChanges = numberOfValueChanges))
  }

  override def onClose(): Unit = {
    inputContentRegistration.cancel()
    super.onClose()
  }
}

final class PropertiesBonanzaView(model: ModelProperty[PropertiesBonanzaModel]) extends View with CssView {

  override def getTemplate: Modifier = div(GlobalStyles.body)(
    div(GuideStyles.main)(
      TextInput(model.subProp(_.inputContent))(),
      br(),
      span("Number of value changes: "),
      bind(model.subProp(_.numberOfValueChanges)),
      br(),
      span("Last distinct value: "),
      bind(model.subProp(_.lastDistinctInputContent)),
    ),
  )
}
