package io.udash.bindings.inputs

import io.udash._
import org.scalajs.dom.html.{Input => JSInput}
import org.scalajs.dom.{Event, File}
import scalatags.JsDom.Modifier
import scalatags.JsDom.all._

object FileInput {

  /**
    * Creates a file input providing information about the selected file.
    *
    * @param selectedFile This property contains information about the file selected by user.
    * @param inputName Input element name.
    * @param inputModifiers Additional modifiers to apply on a generated input.
    * @return
    */
  def single(selectedFile: Property[File])(inputName: String, inputModifiers: Modifier*): InputBinding[JSInput] = {
    apply(selectedFile.transformToSeq(Seq(_), _.head), false.toProperty)(inputName, inputModifiers)
  }

  /**
    * Creates file input providing information about selected files.
    *
    * @param selectedFiles This property contains information about files selected by user.
    * @param acceptMultipleFiles Accepts more than one file if true.
    * @param inputName Input element name.
    * @param inputModifiers Additional modifiers to apply on a generated input.
    * @return
    */
  def apply(
    selectedFiles: SeqProperty[File], acceptMultipleFiles: ReadableProperty[Boolean]
  )(inputName: String, inputModifiers: Modifier*): InputBinding[JSInput] = {
    new InputBinding[JSInput] {
      private val in = input(
        inputModifiers, `type` := "file", name := inputName,
        nestedInterceptor((multiple := "multiple").attrIf(acceptMultipleFiles))
      ).render

      in.onchange = (ev: Event) => {
        ev.preventDefault()
        selectedFiles.replace(0, selectedFiles.size, (0 until in.files.length).map { idx => in.files(idx) }: _*)
      }

      override def render: JSInput = in
    }
  }
}
