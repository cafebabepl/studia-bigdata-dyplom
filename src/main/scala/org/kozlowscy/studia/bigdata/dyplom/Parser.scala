package org.kozlowscy.studia.bigdata.dyplom

import org.jsoup.Jsoup

object Parser {

  def toInt(s: String, default: Int = 0): Int =
    try {
      s.toInt
    } catch {
      case _: Exception => default
    }

  def fromHtml(s: String): String =
    try {
      Jsoup.parse(s).body().text()
    } catch {
      case _: Exception => ""
    }

}
