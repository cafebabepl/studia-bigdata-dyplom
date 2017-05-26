package org.kozlowscy.studia.bigdata.dyplom.model

import java.util.Date
import scala.xml.XML
import org.kozlowscy.studia.bigdata.dyplom.Parser._

case class Post(id: Int, postTypeId: Int, parentId: Int = 0, acceptedAnswerId: Int = 0, creationDate: Date = null, score: Int = 0, viewCount: Int = 0, body: String, ownerUserId: Int = 0, title: String, tags: String = "", answerCount: Int = 0, commentCount: Int = 0, favoriteCount: Int = 0) extends Row

object Post {

  def parse(line: String): Post = {
    val row = XML.loadString(line)
    Post(
      id = toInt(row \@ "Id"),
      postTypeId = toInt(row \@ "PostTypeId"),
      parentId = toInt(row \@ "ParentId"),
      score = toInt(row \@ "Score"),
      body = fromHtml(row \@ "Body"),
      ownerUserId = toInt(row \@ "OwnerUserId"),
      title = row \@ "Title",
      tags = row \@ "Tags")
  }

}
