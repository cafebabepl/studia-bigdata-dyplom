package org.kozlowscy.studia.bigdata.dyplom.dictionary.impl

import org.kozlowscy.studia.bigdata.dyplom.dictionary.CountryDictionaryGenerator
import org.scalatest.FunSuite

class SynonymComThesaurusTest extends FunSuite {

  val thesaurus = (new SynonymComThesaurus).setAmbiguousFilter(CountryDictionaryGenerator.countryFilter())

  test("Synonimy dla 'Poland'") {
    // when
    val y = thesaurus.get("Poland")
    // then
    assert(!y.isEmpty, "Zbiór synonimów nie powinien być pusty")
    assert(y.contains("Polska"), "Brak synonimu 'Polska'")
  }

  test("Synonimy dla 'Georgia' - słowo występuje w znaczeniu państwa na Kaukazie i stanu w USA") {
    // when
    val y = thesaurus.get("Georgia")
    // then
    assert(!(y isEmpty), "Brak synonimów dla słowa")
    assert(y contains "Tbilisi")
    assert(y contains "Sakartvelo")
    assert(!(y contains "USA"), "Interesuje nas państwo Gruzja, a nie stan w USA")
  }

  test("Synonimy dla 'Peter I Island' - brak synonimów") {
    // when
    val y = thesaurus.get("Peter I Island")
    // then
    assert(y.isEmpty, "Nie powinno być żadnych synonimów ponieważ: 'We couldn't find any exact matches, but here are some similar words.'")
  }

  test("Synonimy dla 'Australia' - słowo występuje w znaczeniu państwa i kontynentu") {
    // when
    val y = thesaurus.get("Australia")
    // then
    assert(!(y isEmpty), "Brak synonimów dla 'Australia'")
    assert(y contains "Canberra")
    assert(y contains "Queensland")
  }

  test("Synonimy dla 'Barbados' (colony)") {
    // when
    val y = thesaurus.get("Barbados")
    // then
    assert(!(y isEmpty), "Brak synonimów dla 'Barbados'")
    assert(y contains "Bridgetown")
  }

  test("Synonimy dla 'Jordan' (kingdom)") {
    // when
    val y = thesaurus.get("Jordan")
    // then
    assert(!(y isEmpty))
  }

  test("Synonimy dla 'Mauritius' (parliamentary state)") {
    // when
    val y = thesaurus.get("Mauritius")
    // then
    assert(!(y isEmpty))
  }

  test("Synonimy dla 'Vietnam' (communist state)") {
    // when
    val y = thesaurus.get("Vietnam")
    // then
    assert(!(y isEmpty))
  }

  test("Synonimy dla 'Nagorno-Karabakh', który nie ma nic wspólnego z Japonią mimo podobieństwa do słowa 'Nagano'") {
    // when
    val y = thesaurus.get("Nagorno-Karabakh")
    // then
    assert(!(y contains "Japan"))
    assert(!(y contains "Nippon"))
  }

  test("Synonimy dla Korea") {
    // when
    val y1 = thesaurus.get("Democratic People's Republic of Korea")
    val y2 = thesaurus.get("Republic of Korea")
    // then
    assert(y1 contains "North Korea")
    assert(!(y1 contains "South Korea"))
    assert(y1 contains "DPRK")
    assert(y2 contains "South Korea")
    assert(!(y2 contains "North Korea"))
  }
}