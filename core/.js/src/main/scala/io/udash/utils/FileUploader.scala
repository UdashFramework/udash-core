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

  /** Uploads provided `files` in fields named `fieldName[]`. */
  def upload(
    fieldName: String, files: Seq[File], extraData: Map[js.Any, js.Any] = Map.empty
  ): ReadableModelProperty[FileUploadModel] = {
    val data = createFormData(extraData)

    files.foreach(file => {
      data.append(s"$fieldName[]", file)
    })

    prepareAndSendXhrRequest(files, data)
  }

  /** Uploads provided `file` in a field named `fieldName`. */
  def uploadFile(
    fieldName: String, file: File, extraData: Map[js.Any, js.Any] = Map.empty
  ): ReadableModelProperty[FileUploadModel] = {
    val data = createFormData(extraData)
    data.append(fieldName, file)

    prepareAndSendXhrRequest(Seq(file), data)
  }

  private def createFormData(extraData: Map[js.Any, js.Any]) = {
    val data = new FormData()
    extraData.foreach { case (key, value) => data.append(key, value) }
    data
  }

  private def prepareAndSendXhrRequest(files: Seq[File], data: FormData) = {
    val fileUploadModel = ModelProperty[FileUploadModel](
      new FileUploadModel(files, FileUploadState.InProgress, 0, 0, None)
    )
    val xhr = new XMLHttpRequest
    xhr.upload.addEventListener("progress", (ev: ProgressEvent) =>
      if (ev.lengthComputable) {
        fileUploadModel.subProp(_.bytesSent).set(ev.loaded)
        fileUploadModel.subProp(_.bytesTotal).set(ev.total)
      }
    )
    xhr.addEventListener("load", (_: Event) => {
      fileUploadModel.subProp(_.response).set(Some(new HttpResponse(xhr)))
      fileUploadModel.subProp(_.state).set(xhr.status / 100 match {
        case 2 => FileUploadState.Completed
        case _ => FileUploadState.Failed
      })
    }
    )
    xhr.addEventListener("error", (_: Event) =>
      fileUploadModel.subProp(_.state).set(FileUploadState.Failed)
    )
    xhr.addEventListener("abort", (_: Event) =>
      fileUploadModel.subProp(_.state).set(FileUploadState.Cancelled)
    )
    xhr.open(method = "POST", url = url.value)
    xhr.send(data)
    fileUploadModel
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

  class HttpResponse(private val xhr: XMLHttpRequest) {
    def text: Option[String] = Option(xhr.responseText)
    def responseHeader(header: String): Option[String] = Option(xhr.getResponseHeader(header))
    def responseType: Option[String] = if (xhr.responseType.nonEmpty) Some(xhr.responseType) else None
    def url: Option[String] = xhr.responseURL.toOption
    def xml: Option[Document] = Option(xhr.responseXML)
    def statusCode: Int = xhr.status
  }

  class FileUploadModel(
    val files: Seq[File],
    val state: FileUploadState,
    val bytesSent: Double,
    val bytesTotal: Double,
    val response: Option[HttpResponse]
  )
  object FileUploadModel extends HasModelPropertyCreator[FileUploadModel]
}
