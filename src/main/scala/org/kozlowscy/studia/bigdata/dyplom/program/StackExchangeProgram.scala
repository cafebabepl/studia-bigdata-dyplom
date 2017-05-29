package org.kozlowscy.studia.bigdata.dyplom.program

import org.apache.spark.rdd.RDD
import org.jboss.logging.Logger
import org.kozlowscy.studia.bigdata.dyplom.dictionary.Dictionaries
import org.kozlowscy.studia.bigdata.dyplom.model.{Comment, Post}
import org.kozlowscy.studia.bigdata.dyplom.{*, Dictionary, InvertedDictionary, Sets, Text}

import scala.reflect.ClassTag

// TODO słabe słowa: po -> Italy, man -> isle of man (obowiązkowo!), moze słownik wykluczeń? - excludes
// TODO nie może być case-insensitive albo co najwyzej konfigurowalne bo "as" - American Samoa, a "gent" - pan to Belgia bo miasto "Gent" = Gandawa
/**
  * Analiza plików https://archive.org/details/stackexchange.
  */
object StackExchangeProgram {

  private val log = Logger.getLogger(StackExchangeProgram.getClass)

  // filtr wiersza danych z pliku XML
  def XML_ROW_FILTER: String => Boolean = _.trim.startsWith("<row ")

  /**
    * Wczytuje i zwraca zawartość pliku.
    *
    * @param file   nazwa pliku do wczytania
    * @param filter definicja filtru linii pliku
    * @param mapper definicja konwersji linii pliku
    * @tparam Y docelowy typ konwersji
    * @return zbiór obiektów
    */
  def from[Y: ClassTag](file: String, filter: String => Boolean = *, mapper: String => Y): RDD[Y] = {
    log.infof("Wczytanie pliku %s", file)
    Spark.context
      .textFile(file)
      .filter(filter)
      .map(mapper)
  }

  def from(file: String): RDD[String] = {
    from(file, *, identity)
  }

  /**
    * Grupowanie wpisów i komentarzy do tekstów.
    *
    * @param posts    wpisy
    * @param comments komentarze
    * @return zbiór zgrupowanych wątków (id, treść)
    */
  def getTexts(posts: RDD[Post], comments: RDD[Comment]): RDD[(Int, Text)] = {

    def join(x: Array[String]): String = {
      x.mkString("\n")
    }

    val _posts = posts.map(post => (post.id, post)) // (postId, post)
    val _comments = comments.map(comment => (comment.postId, comment)).groupByKey() // (postId, [comments])

    _posts
      .leftOuterJoin(_comments) // (postId, (post, [comments]))
      .map {
        case (postId, (post, comments)) => {
          val threadId = if (post.parentId > 0) post.parentId else postId
          (threadId, join(post.title +: post.body +: comments.getOrElse(Set.empty).map(_.text).toArray))
        }
      } // (threadId, text)
      // złączenie tekstów dla każdego wątku
      .reduceByKey((a, b) => join(Array(a, b)))
  }

  /**
    * Określenie zbioru słów kluczowych w tekście na podstawie odwróconego słownika.
    *
    * @param text       tekst wejściowy
    * @param dictionary odwrócony słownik
    * @return zbiór słów kluczowych
    */
  def getKeywordsSet(text: String, dictionary: InvertedDictionary): Set[String] = {
    getKeywordsMap(text, dictionary).values.toSet
  }

  /**
    * Określenie mapy słów kluczoywch w tekście na podstawie odróconego słownika.
    *
    * @param text       tekst wejściowy
    * @param dictionary odwrócony słownik
    * @return mapa słów kluczowych (synonim -> słowo kluczowe)
    */
  def getKeywordsMap(text: String, dictionary: InvertedDictionary): Map[String, String] = {
    val _text = " " + text.replaceAll("\n", " ").replace(',', ' ').replace('.', ' ') + " "
    dictionary.filter(x => _text.contains(" " + x._1 + " "))
    // TODO ale to wtedy jest wolne ;-(
    //    dictionary.filter(x => _text.matches(".*\\W" + x._1 + "\\W.*"))
  }

  def analyze(dictionary: Dictionary, texts: RDD[(Int, Text)]) = {
    log.info("Rozpoczęcie tworzenia grafu relacji")
    //    texts
    //      .take(100)
    //      .foreach {
    //      case (threadId, text) => {
    //        println("thId: " + threadId)
    //        //println(text)
    //        println("why:  " + getKeywords2(text, dictionary))
    //        println("then: " + getKeywords(text, dictionary))
    //        println("-----------------------------------------------------------------------------------")
    //      }
    //    }

    // utworzenie odwróconego słownika
    log.debug("Utworzenie odwróconego słownika")
    val invertedDictionary = Dictionaries.getInvertedDictionary(dictionary)

    // utworzenie listy krawędzi
    log.debug("Utworzenie listy krawędzi")
    val edges = texts
      // zbiór słów kluczowych na podstawie odwróconego słownika dla każdego tekstu
      .mapValues(getKeywordsSet(_, invertedDictionary))
      // wyznaczenie wariacji bez powtórzeń w zbiorze słów kluczowych
      .map(x => Sets.variations(x._2))
      // określenie wagi relacji (stałej lub ważonej, np. 1.0 / x.size)
      .flatMap(x => x.map((_, 1.0)))
      // redukcja (sumowanie wag) gałęzi
      .reduceByKey(_ + _)
    edges.cache()
    log.debugf("Liczba krawędzi: %d", edges.count())

    // zapisanie wierzchołków
    log.debug("Zapisanie wierzchołków")
    edges
      .map { case ((a, b), w) => (a, w) }
      .reduceByKey(_ + _)
      .coalesce(1)
      .sortBy(_._2, ascending = false)
      .map { case (a, w) => s"${a};${w}" }
      .saveAsTextFile("data/out/nodes")

    // zapisanie krawędzi
    log.debug("Zapisanie krawędzi")
    edges
      .filter { case ((a, b), w) => a < b }
      .coalesce(1)
      .sortBy(_._2, ascending = false)
      .map { case ((a, b), w) => s"${a};${b};${w}" }
      .saveAsTextFile("data/out/edges")
  }

}