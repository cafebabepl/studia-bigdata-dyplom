package org.kozlowscy.studia.bigdata.dyplom.dictionary

import org.apache.spark.rdd.RDD
import org.kozlowscy.studia.bigdata.dyplom.{Dictionary, InvertedDictionary}

object Dictionaries {

  /**
    * Utworzenie słownika.
    *
    * @param lines linie zawierające zbiory synonimów
    * @return słownik
    */
  def getDictionary(lines: RDD[String]): Dictionary = {
    lines.collect()
      .map(_.split(';'))
      .map(a => (a.head, a.toSet))
      .toMap
  }

  /**
    * Generacja jednoznacznego odwróconego słownika.
    *
    * @param dictionary słownik
    * @return odwrócony słownik
    */
  def getInvertedDictionary(dictionary: Dictionary): InvertedDictionary = {
    // zwykłe odwrócenie mapy (_.swap) nie wystarcza ponieważ ten sam synonim może wystąpić dla kilku różnych słów
    dictionary.toList
      .flatMap { case (k, v) => v.map((_, k)) }
      .groupBy(_._1)
      .filter(_._2.size == 1)
      .map(_._2.head)
  }

}
