package org.kozlowscy.studia.bigdata.dyplom.program

import org.jboss.logging.Logger
import org.kozlowscy.studia.bigdata.dyplom.Dictionary
import org.kozlowscy.studia.bigdata.dyplom.dictionary.Dictionaries.getDictionary
import org.kozlowscy.studia.bigdata.dyplom.model.{Comment, Post}
import org.kozlowscy.studia.bigdata.dyplom.program.StackExchangeProgram._

object StackOverflowProgram {

  val log = Logger.getLogger(StackOverflowProgram.getClass)
  
  def main(args: Array[String]): Unit = {
    log.info("Rozpoczęcie programu")

    // odczyt danych wejściowych
    log.debug("Odczyt danych wejściowych")
    val posts = from("c:/temp/stackoverflow.com/Posts.xml", XML_ROW_FILTER, Post.parse)
    val comments = from("c:/temp/stackoverflow.com/Comments.xml", XML_ROW_FILTER, Comment.parse)

    // wczytanie i utworzenie słownika
    log.debug("Wczytanie i utworzenie słownika")
    // słownik utworzony na podstawie https://insights.stackoverflow.com/survey/2017#technology-programming-languages
    val dictionary: Dictionary = getDictionary(StackExchangeProgram.from("data/programming-languages-dictionary.csv"))

    // zgrupowanie tekstów
    log.debug("Zgrupowanie tekstów")
    val texts = getTexts(posts, comments)

    // zasadnicza analiza tekstów
    log.debug("Zasadnicza analiza tekstów")
    analyze(dictionary, texts)

    log.info("Zakończenie programu")
  }

}