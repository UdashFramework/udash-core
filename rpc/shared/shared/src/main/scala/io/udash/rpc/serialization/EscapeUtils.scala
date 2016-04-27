package io.udash.rpc.serialization

object EscapeUtils {
  def escape(s: String): String = {
    val sb = new StringBuilder
    escape(sb, s)
    sb.result()
  }

  def escape(sb: StringBuilder, s: String): Unit = {
    val length = s.length
    var i = 0
    var j = 0

    @inline def _append(str: String): Unit = {
      if (i > j) sb.append(s.substring(j, i))
      sb.append(str)
      j = i + 1
    }

    while (i < length) {
      val c: Char = s(i)
      if (c < ' ') c match {
        case '\b' => _append("\\b")
        case '\f' => _append("\\f")
        case '\n' => _append("\\n")
        case '\r' => _append("\\r")
        case '\t' => _append("\\t")
        case _ => _append("\\u%04x" format c.toInt)
      }
      else if (c == '"') _append("\\\"")
      else if (c == '\\') _append("\\\\")
      i += 1
    }

    if (j > 0) {
      if (j < i) sb.append(s.substring(j, i))
    } else sb.append(s)
  }

  def unescape(s: String): String = {
    val sb = new StringBuilder
    escape(sb, s)
    sb.result()
  }

  def unescape(sb: StringBuilder, s: String): Unit = {
    val length = s.length
    var i = 0
    var j = 0
    while (i < length - 1) {
      s(i) match {
        case '\\' =>
          if (i > j) sb.append(s.substring(j, i))
          sb.append(s(i+1) match {
            case '\"' => '"'
            case '\\' => '\\'
            case 'b' => '\b'
            case 'f' => '\f'
            case 'n' => '\n'
            case 'r' => '\r'
            case 't' => '\t'
            case 'u' =>
              val r = Integer.parseInt(s.substring(i+2, i+6), 16).toChar
              i += 4
              r
            case _ =>
              i -= 1
              '\\'
          })
          i += 2
          j = i
        case _ =>
          i += 1
      }
    }
    if (i == length - 1) sb.append(s.substring(j, length))
  }
}
