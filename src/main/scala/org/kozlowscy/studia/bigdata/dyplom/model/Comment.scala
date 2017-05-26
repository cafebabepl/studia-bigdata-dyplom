package org.kozlowscy.studia.bigdata.dyplom.model

import java.util.Date
import scala.xml.XML
import org.kozlowscy.studia.bigdata.dyplom.Parser._

case class Comment(id: Int, postId: Int, score: Int = 0, text: String, creationDate: Date = null, userId: Int = 0) extends Row

object Comment {
  def parse(line: String): Comment = {
    val row = XML.loadString(line)
    Comment(
      id = toInt(row \@ "Id"),
      postId = toInt(row \@ "PostId"),
      score = toInt(row \@ "Score"),
      text = fromHtml(row \@ "Text"),
      userId = toInt(row \@ "UserId"))
  }
}
