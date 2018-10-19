package io.udash.legacyrest

import io.udash.rpc.JsonStr
import io.udash.utils.URLEncoder

trait RESTConverters {
  val framework: UdashRESTFramework

  // for ServerREST

  def rawToHeaderArgument(raw: framework.RawValue): String = stripQuotes(raw.json)
  def rawToQueryArgument(raw: framework.RawValue): String = stripQuotes(raw.json)
  def rawToURLPart(raw: framework.RawValue): String = URLEncoder.encode(stripQuotes(raw.json))

  private def stripQuotes(s: String): String =
    s.stripPrefix("\"").stripSuffix("\"")

  // for ExposesREST

  def headerArgumentToRaw(raw: String, isStringArg: Boolean): framework.RawValue = rawArg(raw, isStringArg)
  def queryArgumentToRaw(raw: String, isStringArg: Boolean): framework.RawValue = rawArg(raw, isStringArg)
  def urlPartToRaw(raw: String, isStringArg: Boolean): framework.RawValue =
    rawArg(URLEncoder.decode(raw), isStringArg)

  private def rawArg(raw: String, isStringArg: Boolean): framework.RawValue =
    JsonStr(if (isStringArg) s""""$raw"""" else raw)
}