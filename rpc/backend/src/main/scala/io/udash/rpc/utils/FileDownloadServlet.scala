package io.udash.rpc.utils

import java.io.File
import java.nio.file.Files
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

/** Template of a servlet handling files download. */
abstract class FileDownloadServlet extends HttpServlet {
  /** Resolves a file basing on the request parameters. */
  protected def resolveFile(request: HttpServletRequest): File

  /** Returned value will be send to the client as a file name.
    * By default returns the original file name. */
  protected def presentedFileName(name: String): String = name

  /** Returns MIME type of the file as string. */
  protected def resolveFileMimeType(file: File): String =
    Option(getServletContext.getMimeType(file.getAbsolutePath)).getOrElse("application/octet-stream")

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val file = resolveFile(request)

    if (!file.exists()) response.sendError(404, "File not found!")
    else {
      // MIME type
      response.setContentType(resolveFileMimeType(file))
      // content length
      response.setContentLengthLong(file.length)
      // file name
      response.setHeader("Content-Disposition", s"""attachment; filename="${presentedFileName(file.getName)}"""")

      val outStream = response.getOutputStream
      Files.copy(file.toPath, outStream)
      outStream.close()
    }
  }
}
