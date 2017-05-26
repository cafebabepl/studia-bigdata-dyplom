package org.kozlowscy.studia.bigdata.dyplom.model

import scala.xml.XML
import org.kozlowscy.studia.bigdata.dyplom.Parser._

case class User(id: Int, reputation: Int = 0, displayName: String, location: String = null, aboutMe: String = null, views: Int = 0, upVotes: Int = 0, downVotes: Int = 0, age: Int = 0, accountId: Int = 0) extends Row

object User {

  def parse(line: String): User = {
    val row = XML.loadString(line)
    User(
      id = toInt(row \@ "Id"),
      reputation = toInt(row \@ "Reputation"),
      displayName = row \@ "DisplayName",
      location = row \@ "Location",
      age = toInt(row \@ "Age"))
  }

}