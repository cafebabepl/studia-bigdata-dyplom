package org.kozlowscy.studia.bigdata.dyplom.model

import com.univocity.parsers.csv.{CsvParser, CsvParserSettings}
import org.kozlowscy.studia.bigdata.dyplom.Parser._

case class Country(CommonName: String, FormalName: String, Type: String, ISO2LetterCode: String, ISO3LetterCode: String, ISONumber: Int)

object Country {

  private val parser: CsvParser = new CsvParser(new CsvParserSettings)

  def parse(line: String): Country = {
    // zamiana linii csv na tablicę pól
    val fields = parser.parseLine(line)
    if (fields.length != 14) {
      throw new IllegalStateException("Niewłaściwa liczba pól.")
    }
    // utworzenie i zwrócenie instancji klasy
    Country(
      CommonName = fields(1),
      FormalName = fields(2),
      Type = fields(3),
      ISO2LetterCode = fields(10),
      ISO3LetterCode = fields(11),
      ISONumber = toInt(fields(12))
    )
  }

}