package io.udash.bindings

import io.udash._
import org.scalajs.dom
import org.scalajs.dom.{Event, File, html}

import scalatags.generic.Modifier

object FileInput {

  /**
    * Creates file input providing information about selected files.
    *
    * @param inputName Input element name.
    * @param acceptMultipleFiles Accepts more than one file if true.
    * @param selectedFiles This property contains information about files selected by user.
    * @return
    */
  def apply(inputName: String, acceptMultipleFiles: ReadableProperty[Boolean], selectedFiles: SeqProperty[File])
           (inputMds: Modifier[dom.Element]*): html.Input = {
    import scalatags.JsDom.all._
    val inp = input(
      `type` := "file", name := inputName,
      (multiple := "multiple").attrIf(acceptMultipleFiles),
      inputMds
    ).render

    inp.onchange = (ev: Event) => {
      ev.preventDefault()
      CallbackSequencer().sequence {
        selectedFiles.clear()
        for (i <- 0 until inp.files.length) {
          val file: File = inp.files(i)
          selectedFiles.append(file)
        }
      }
    }

    inp
  }
}
