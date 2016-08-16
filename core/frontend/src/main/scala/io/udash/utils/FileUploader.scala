package io.udash.utils

import io.udash._
import org.scalajs.dom.raw.{FormData, XMLHttpRequest}
import org.scalajs.dom._

import scala.concurrent.ExecutionContext
import scala.scalajs.js

class FileUploader(url: Url)(implicit ec: ExecutionContext) {
  import FileUploader._

  def upload(input: html.Input): ReadableModelProperty[FileUploadModel] = {
    val builder = Seq.newBuilder[File]
    val files = input.files
    for (i <- 0 until files.length)
      builder += files(i)

    upload(input.name, builder.result())
  }

  def upload(fieldName: String, files: Seq[File],
             extraData: Map[js.Any, js.Any] = Map.empty): ReadableModelProperty[FileUploadModel] = {
    val p = ModelProperty[FileUploadModel]
    val data = new FormData()

    extraData.foreach(item => data.append(item._1, item._2))
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
      p.subProp(_.state).set(FileUploadState.Completed)
    )
    xhr.addEventListener("error", (ev: Event) =>
      p.subProp(_.state).set(FileUploadState.Failed)
    )
    xhr.addEventListener("abort", (ev: Event) =>
      p.subProp(_.state).set(FileUploadState.Cancelled)
    )
    xhr.open(method = "POST", url = url.value)
    xhr.send(data)

    p.subProp(_.state).set(FileUploadState.InProgress)
    p
  }
}

object FileUploader {
  sealed trait FileUploadState
  object FileUploadState {
    case object InProgress extends FileUploadState
    case object Completed extends FileUploadState
    case object Failed extends FileUploadState
    case object Cancelled extends FileUploadState
  }

  trait FileUploadModel {
    def files: Seq[File]
    def state: FileUploadState
    def bytesSent: Double
    def bytesTotal: Double
  }
}
