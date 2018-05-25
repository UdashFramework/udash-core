package io.udash.utils

import io.udash._
import io.udash.properties.{Blank, HasModelPropertyCreator}
import org.scalajs.dom._
import org.scalajs.dom.raw.{FormData, XMLHttpRequest}

import scala.scalajs.js

class FileUploader(url: Url) {
  import FileUploader._

  /** Uploads files selected in provided `input`. */
  def upload(input: html.Input): ReadableModelProperty[FileUploadModel] =
    upload(
      input.name,
      (0 until input.files.length).map(input.files.item)
    )

  /** Uploads provided `files` in a field named `fieldName`. */
  def upload(fieldName: String, files: Seq[File],
             extraData: Map[js.Any, js.Any] = Map.empty): ReadableModelProperty[FileUploadModel] = {
    val p = ModelProperty[FileUploadModel](
      new FileUploadModel(Seq.empty, FileUploadState.InProgress, 0, 0)
    )
    val data = new FormData()

    extraData.foreach { case (key, value) => data.append(key, value) }
    files.foreach(file => {
      data.append(s"$fieldName[]", file)
      p.subSeq(_.files).append(file)
    })

    val xhr = new XMLHttpRequest
    xhr.upload.addEventListener("progress", (ev: ProgressEvent) =>
      if (ev.lengthComputable) {
        p.subProp(_.bytesSent).set(ev.loaded)
        p.subProp(_.bytesTotal).set(ev.total)
      }
    )
    xhr.addEventListener("load", (ev: Event) =>
      p.subProp(_.state).set(xhr.status / 100 match {
        case 2 => FileUploadState.Completed
        case _ => FileUploadState.Failed
      })
    )
    xhr.addEventListener("error", (ev: Event) =>
      p.subProp(_.state).set(FileUploadState.Failed)
    )
    xhr.addEventListener("abort", (ev: Event) =>
      p.subProp(_.state).set(FileUploadState.Cancelled)
    )
    xhr.open(method = "POST", url = url.value)
    xhr.send(data)

    p
  }
}

object FileUploader {
  sealed trait FileUploadState
  object FileUploadState {
    case object NotStarted extends FileUploadState
    case object InProgress extends FileUploadState

    sealed trait Done extends FileUploadState
    case object Completed extends Done
    case object Failed extends Done
    case object Cancelled extends Done

    implicit val blank: Blank[FileUploadState] = Blank.Simple(NotStarted)
  }

  class FileUploadModel(
    val files: Seq[File],
    val state: FileUploadState,
    val bytesSent: Double,
    val bytesTotal: Double
  )
  object FileUploadModel extends HasModelPropertyCreator[FileUploadModel]
}
