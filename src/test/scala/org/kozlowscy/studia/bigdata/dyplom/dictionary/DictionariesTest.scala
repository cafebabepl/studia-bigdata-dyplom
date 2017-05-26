package org.kozlowscy.studia.bigdata.dyplom.dictionary

import org.kozlowscy.studia.bigdata.dyplom.program.Spark
import org.kozlowscy.studia.bigdata.dyplom.{Dictionary, InvertedDictionary}
import org.scalatest.{FeatureSpec, GivenWhenThen}

class DictionariesTest extends FeatureSpec with GivenWhenThen {

  val sc = Spark.context

  feature("Słowniki") {

    scenario("Utworzenie prostego słownika") {
      // given
      val lines = sc.parallelize(Seq(
        "dom,budynek,Chata,dach nad głową,szeregowiec",
        "Szeregowy,przeciętny,jeden z wielu,szeregowiec",
        "nauka,wiedza,Studia,uczelnia",
        "samotnik"
      ))
      // when
      val y: Dictionary = Dictionaries.getDictionary(lines)
      // then
      assertResult(4, "Liczba znaczeń słownika")(y.size)
      assertResult(5, "Liczba synonimów dla słowa 'dom'")(y("dom").size)
      assertResult(Set("Studia", "uczelnia", "nauka", "wiedza"), "Równość zbiorów synonimów")(y("nauka"))
      assert(y("samotnik").size == 1, "Słowo bez synonimów")
      assert(y.get("brak").isEmpty, "Nieznane słowo")
    }

    scenario("Utworzenie odwróconego słownika") {
      def set(x: String) = x.split(',').toSet

      // given
      val x: Dictionary = Map(
        "dom" -> set("dom,budynek,buda,chata,dach nad głową,szeregowiec"),
        "szeregowy" -> set("szeregowy,szeregowiec,przeciętny,jeden z wielu"),
        "nauka" -> set("nauka,wiedza,studia,szkoła,buda"),
        "brak" -> set("brak")
      )
      // when
      val y: InvertedDictionary = Dictionaries.getInvertedDictionary(x)
      //then
      assertResult("dom")(y("dom"))
      assertResult("dom")(y("chata"))
      assertResult("nauka")(y("studia"))
      assertResult("brak")(y("brak"))
      assert(y.get("buda").isEmpty, "Buda to zarówno dom jak i szkoła")
      assert(y.get("szeregowiec").isEmpty, "Szeregowiec to zarówno dom jak i żołnierz")
      assertResult(12, "Rozmiar odwróconego słownika")(y.size)
    }
  }

}
