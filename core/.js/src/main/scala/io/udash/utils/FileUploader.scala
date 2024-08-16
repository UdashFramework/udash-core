package io.udash.utils

import com.avsystem.commons.misc.{AbstractCase, CaseMethods}
import io.udash._
import io.udash.properties.{Blank, HasModelPropertyCreator, PropertyCreator}
import org.scalajs.dom._

import scala.scalajs.js.|

class FileUploader(url: Url) {

  import FileUploader._

  /** Uploads files selected in provided `input`. */
  def upload(input: html.Input): ReadableModelProperty[FileUploadModel] =
    upload(
      input.name,
      (0 until input.files.length).map(input.files.item)
    )

  /** Uploads provided `file` in a field named `fieldName` with optional additional request headers. */
  def uploadFile(
    fieldName: String, file: File, extraData: Map[String, String | Blob] = Map.empty, additionalRequestHeaders: Map[RequestName, RequestValue] = Map.empty
  ): ReadableModelProperty[FileUploadModel] =
    upload(fieldName, Seq(file), extraData = extraData, additionalRequestHeaders = additionalRequestHeaders)

  /** Uploads provided `files` in a field named `fieldName` with optional additional request headers. */
  def upload(
    fieldName: String, files: Seq[File], extraData: Map[String, String | Blob] = Map.empty, additionalRequestHeaders: Map[RequestName, RequestValue] = Map.empty
  ): ReadableModelProperty[FileUploadModel] = {
    val p = ModelProperty[FileUploadModel](
      new FileUploadModel(Seq.empty, FileUploadState.InProgress, 0, 0, None)
    )
    val data = new FormData()

    extraData.foreach {
      case (key, value) =>
        (value: Any) match {
          case string: String => data.append(key, string)
          case blob: Blob => data.append(key, blob)
        }
    }
    files.foreach(file => {
      data.append(fieldName, file)
      p.subSeq(_.files).append(file)
    })

    val xhr = new XMLHttpRequest
    xhr.upload.addEventListener("progress", (ev: ProgressEvent) =>
      if (ev.lengthComputable) {
        p.subProp(_.bytesSent).set(ev.loaded)
        p.subProp(_.bytesTotal).set(ev.total)
      }
    )
    xhr.addEventListener("load", (_: Event) => {
      p.subProp(_.response).set(Some(new HttpResponse(xhr)))
      p.subProp(_.state).set(xhr.status / 100 match {
        case 2 => FileUploadState.Completed
        case _ => FileUploadState.Failed
      })
    })
    xhr.addEventListener("error", (_: Event) =>
      p.subProp(_.state).set(FileUploadState.Failed)
    )
    xhr.addEventListener("abort", (_: Event) =>
      p.subProp(_.state).set(FileUploadState.Cancelled)
    )
    xhr.open(method = "POST", url = url.value)
    additionalRequestHeaders.foreach { case (name, value) => xhr.setRequestHeader(name.name, value.value) }
    xhr.send(data)
    p
  }
}

final case class RequestName(name: String) extends AnyVal with CaseMethods
final case class RequestValue(value: String) extends AnyVal with CaseMethods

object FileUploader {
  sealed trait FileUploadState extends AbstractCase
  object FileUploadState {
    case object NotStarted extends FileUploadState
    case object InProgress extends FileUploadState

    sealed trait Done extends FileUploadState
    case object Completed extends Done
    case object Failed extends Done
    case object Cancelled extends Done

    implicit val blank: Blank[FileUploadState] = Blank.Simple(NotStarted)
  }

  final class HttpResponse(private val xhr: XMLHttpRequest) {
    def text: Option[String] = Option(xhr.responseText)
    def responseHeader(header: String): Option[String] = Option(xhr.getResponseHeader(header))
    def responseType: Option[String] = if (xhr.responseType.nonEmpty) Some(xhr.responseType) else None
    def url: Option[String] = xhr.responseURL.toOption
    def xml: Option[Document] = Option(xhr.responseXML)
    def statusCode: Int = xhr.status
  }
  object HttpResponse {
    implicit val pc: PropertyCreator[HttpResponse] = PropertyCreator.materializeSingle
  }

  final class FileUploadModel(
    val files: Seq[File],
    val state: FileUploadState,
    val bytesSent: Double,
    val bytesTotal: Double,
    val response: Option[HttpResponse]
  )
  object FileUploadModel extends HasModelPropertyCreator[FileUploadModel]
}
