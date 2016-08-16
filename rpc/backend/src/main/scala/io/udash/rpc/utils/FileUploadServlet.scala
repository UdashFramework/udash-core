package io.udash.rpc.utils

import java.io.InputStream
import java.nio.file.Paths
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

/** Template of a servlet handling files upload. It takes files from the request and passes data to the `handleFile` method.
  * @param fileFields Names of file inputs in the HTTP request. */
abstract class FileUploadServlet(fileFields: Set[String]) extends HttpServlet {
  override protected def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    import scala.collection.JavaConversions._

    request.getParts.toStream
      .filter(part => fileFields.contains(part.getName.stripSuffix("[]")))
      .foreach(filePart => {
        val fileName = Paths.get(filePart.getSubmittedFileName).getFileName.toString
        val fileContent = filePart.getInputStream
        handleFile(fileName, fileContent)
      })
  }

  protected def handleFile(name: String, content: InputStream): Unit
}
