package org.kozlowscy.studia.bigdata.dyplom.program

import org.jboss.logging.Logger
import org.kozlowscy.studia.bigdata.dyplom.dictionary.Dictionaries.getDictionary
import org.kozlowscy.studia.bigdata.dyplom.model.{Comment, Post}
import org.kozlowscy.studia.bigdata.dyplom.{*, Dictionary}
import org.kozlowscy.studia.bigdata.dyplom.program.StackExchangeProgram._

object PoliticsStackExchangeProgram {

  val log = Logger.getLogger(PoliticsStackExchangeProgram.getClass)

  def main(args: Array[String]): Unit = {
    log.info("Rozpoczęcie programu")

    // odczyt danych wejściowych
    log.debug("Odczyt danych wejściowych")
    val posts = from("data/politics.stackexchange.com/Posts.xml", XML_ROW_FILTER, Post.parse)
    val comments = from("data/politics.stackexchange.com/Comments.xml", XML_ROW_FILTER, Comment.parse)

    // wczytanie i utworzenie słownika
    log.debug("Wczytanie i utworzenie słownika")
    val dictionary: Dictionary = getDictionary(from("data/countries-dictionary.csv"))

    // zgrupowanie tekstów
    log.debug("Zgrupowanie tekstów")
    val texts = getTexts(posts, comments)

    // zasadnicza analiza tekstów
    log.debug("Zasadnicza analiza tekstów")
    analyze(dictionary, texts)

    log.info("Zakończenie programu")
  }

}