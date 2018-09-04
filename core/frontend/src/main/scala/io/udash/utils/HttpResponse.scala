package io.udash.utils

import com.avsystem.commons.misc.AbstractCase
import org.scalajs.dom.Document
import org.scalajs.dom.raw.XMLHttpRequest

sealed trait HttpResponse {
  def text: Option[String]
  def responseType: Option[String]
  def url: Option[String]
  def xml: Option[Document]
}

object HttpResponse {
  private case class HttpResponseImpl(
    text: Option[String], responseType: Option[String], url: Option[String], xml: Option[Document]
  ) extends AbstractCase with HttpResponse

  def apply(xhr: XMLHttpRequest): HttpResponse =
    HttpResponseImpl(
      Option(xhr.responseText),
      if (xhr.responseType.nonEmpty) Some(xhr.responseType) else None,
      xhr.responseURL.toOption,
      Option(xhr.responseXML)
    )
}