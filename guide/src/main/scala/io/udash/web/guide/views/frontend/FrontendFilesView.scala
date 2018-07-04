package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.guide._
import io.udash.web.guide.components.ForceBootstrap
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.views.References
import io.udash.web.guide.views.frontend.demos._

import scalatags.JsDom

case object FrontendFilesViewFactory extends StaticViewFactory[FrontendFilesState.type](() => new FrontendFilesView)

class FrontendFilesView extends FinalView {
  import io.udash.web.guide.Context._

  import JsDom.all._

  override def getTemplate: Modifier = div(
    h2("Files upload"),
    p(
      "The Udash framework provides a bundle of utilities for files uploading and downloading. It supports both ",
      "the frontend (in ", i("udash-core"), " module) and the backend (in ", i("udash-rpc"), " module) side of an application."
    ),
    p("You can find a working demo application in the ", a(href := References.UdashFilesDemoRepo, target := "_blank")("Udash Demos"), " repositiory."),
    h3("Frontend forms"),
    p(i("FileInput"), " is the file HTML input wrapper providing a property containing selected files. "),
    CodeBlock(
      """import org.scalajs.dom.File
        |val acceptMultipleFiles: Property[Boolean] = Property(true)
        |val selectedFiles: SeqProperty[File] = SeqProperty(Seq.empty)
        |
        |div(
        |  FileInput("files", acceptMultipleFiles, selectedFiles)(),
        |  h4("Selected files"),
        |  ul(GuideStyles.defaultList)(
        |    repeat(selectedFiles) { file =>
        |      li(file.get.name).render
        |    }
        |  )
        |)""".stripMargin
    )(GuideStyles),
    p("Take a look at the following live demo:"),
    ForceBootstrap(new FileInputDemoComponent().getTemplate),
    p(i("FileUploader"), " is a class taking the server URL as a constructor argument and containing two methods:"),
    CodeBlock(
      """def upload(input: html.Input): ReadableModelProperty[FileUploadModel]
        |def upload(
        |  fieldName: String, files: Seq[File],
        |  extraData: Map[Any, Any]
        |): ReadableModelProperty[FileUploadModel]""".stripMargin
    )(GuideStyles),
    p("The first one takes a file HTML input and uploads its content to the server. The second takes a field name, a sequence of files and request's extra data."),
    p("Both methods return property containing ", i("FileUploadModel"), " which provides information about the upload progress."),
    CodeBlock(
      """trait FileUploadModel {
        |  def files: Seq[File]
        |  def state: FileUploadState
        |  def bytesSent: Double
        |  def bytesTotal: Double
        |}
        |
        |sealed trait FileUploadState
        |object FileUploadState {
        |  case object NotStarted extends FileUploadState
        |  case object InProgress extends FileUploadState
        |
        |  sealed trait Done extends FileUploadState
        |  case object Completed extends Done
        |  case object Failed extends Done
        |  case object Cancelled extends Done
        |}""".stripMargin
    )(GuideStyles),
    h3("Backend support"),
    p("The ", i("udash-rpc"), " module contains two servlet templates: ", i("FileUploadServlet"), " and ", i("FileDownloadServlet"), "."),
    p(
      "The ", i("FileUploadServlet"), " contains a ", i("doPost"), " method implementation which takes files from the request ",
      "and passes them to the abstract ", i("handleFile"), " method. You should implement this method in order to handle uploaded files."
    ),
    p(
      "The ", i("FileDownloadServlet"), " contains a ", i("doGet"), " method implementation which calls the abstract ",
      i("resolveFile"), " method and sends selected file to the client. You can override the ", i("presentedFileName"),
      " method to change sent file name and the ", i("resolveFileMimeType"), " to change sent MIME type."
    ),
    h2("What's next?"),
    p(
      "If you want to learn more about client-server communication, check the ",
      a(href := RpcIntroState.url)("RPC"), " chapter. ",
      "You might  find ", a(href := BootstrapExtState.url)("Bootstrap Components"), " interesting later on."
    )
  )
}