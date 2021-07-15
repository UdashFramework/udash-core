package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.ForceBootstrap
import io.udash.web.guide._
import io.udash.web.guide.views.References
import io.udash.web.guide.views.frontend.demos._
import scalatags.JsDom

case object FrontendFilesViewFactory extends StaticViewFactory[FrontendFilesState.type](() => new FrontendFilesView)

class FrontendFilesView extends View {

  import JsDom.all._
  import io.udash.web.guide.Context._

  private val (fileInputDemo, fileInputSnippet) = FileInputDemo.demoWithSnippet()

  override def getTemplate: Modifier = div(
    h2("Files upload"),
    p(
      "The Udash framework provides a bundle of utilities for files uploading and downloading. It supports both ",
      "the frontend (in ", i("udash-core"), " module) and the backend (in ", i("udash-rpc"), " module) side of an application."
    ),
    p("You can find a working demo application in the ", a(href := References.UdashFilesDemoRepo, target := "_blank")("Udash Demos"), " repositiory."),
    h3("Frontend forms"),
    p(i("FileService"), " is an object that allows to convert ", i("Array[Byte]"), " to URL, save it as file from frontend ",
      " and asynchronously convert ", i("File"), " to ", i("Future[Array[Byte]]"), "."),
    p(i("FileInput"), " is the file HTML input wrapper providing a property containing selected files. "),
    fileInputSnippet,
    p("Take a look at the following live demo:"),
    ForceBootstrap(fileInputDemo),
    p(
      i("FileUploader"), " facilitates sending files to the server.",
      "There are helpers for uploading file specified via an HTML input or a sequence of files including additional data."
    ),
    p("All methods return a property containing ", i("FileUploadModel"), ", which provides information about the upload progress."),
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