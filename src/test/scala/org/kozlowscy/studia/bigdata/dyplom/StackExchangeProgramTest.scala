package org.kozlowscy.studia.bigdata.dyplom

import org.apache.spark.rdd.RDD
import org.kozlowscy.studia.bigdata.dyplom.model.{Comment, Post}
import org.kozlowscy.studia.bigdata.dyplom.program.Spark
import org.kozlowscy.studia.bigdata.dyplom.program.StackExchangeProgram._
import org.scalatest.{FeatureSpec, GivenWhenThen}

class StackExchangeProgramTest extends FeatureSpec with GivenWhenThen {

  val sc = Spark.context

  feature("Grupowanie treści") {

    def post(id: Int, postTypeId: Int, parentId: Int = 0): Post = Post(id = id, postTypeId = postTypeId, parentId = parentId, body = "Treść wpisu %d".format(id), title = "Tytuł wpisu %d".format(id))

    def comment(id: Int, postId: Int): Comment = Comment(id = id, postId = postId, text = "Treść komentarza %d".format(id))

    /** Pomocnicza metoda, która porównuje tekst ze zbiorem wpisów i komentarzy */
    def eq(text: String, posts: RDD[Post], comments: RDD[Comment]): Boolean = {
      var _text = text
      // usunięcie treści i tytułu wpisu
      posts.collect().foreach(x => {
        // nie może być posts.foreach bo RDD wykona się równolegle
        _text = _text.replaceFirst(x.body, "")
        _text = _text.replaceFirst(x.title, "")
      })
      // usunięcie treści komentarza
      comments.collect().foreach(x => _text = _text.replaceFirst(x.text, ""))
      // usunięcie znaków połączenia
      _text = _text.replaceAll("\n", "")
      _text.isEmpty
    }

    scenario("Pojedynczy wpis z komentarzami") {
      // given
      val posts = sc.parallelize(Seq(
        post(id = 1, postTypeId = 1)
      ))
      val comments = sc.parallelize(Seq(
        comment(id = 1, postId = 1),
        comment(id = 2, postId = 1),
        comment(id = 3, postId = 2)
      ))
      // when
      val y = getTexts(posts, comments).collectAsMap()
      // then
      assertResult(1)(y.size)
      assert(eq(y.get(1).get, posts.filter(p => Array(1).contains(p.id)), comments.filter(c => Array(1, 2).contains(c.id))))
    }

    scenario("Wiele wpisów z komentarzami") {
      // given
      val posts = sc.parallelize(Seq(
        post(id = 1, postTypeId = 1),
        post(id = 2, postTypeId = 1),
        post(id = 5, postTypeId = 1)
      ))
      val comments = sc.parallelize(Seq(
        comment(id = 1, postId = 1),
        comment(id = 2, postId = 1),
        comment(id = 3, postId = 2),
        comment(id = 4, postId = 2),
        comment(id = 5, postId = 2),
        comment(id = 6, postId = 3),
        comment(id = 7, postId = 6),
        comment(id = 8, postId = 6)
      ))
      // when
      val y = getTexts(posts, comments).collectAsMap()
      // then
      assertResult(3, "Powinna powstać grupa 3 tekstów.")(y.size)
      // wykorzystujemy pomocniczą metodę eq, która porównuje tekst ze zbiorem wpisów i komentarzy, przy czym kolejność złączonych treści nie ma znaczenia
      assert(eq(y.get(1).get, posts.filter(p => Array(1).contains(p.id)), comments.filter(c => Array(1, 2).contains(c.id))))
      assert(eq(y.get(2).get, posts.filter(p => Array(2).contains(p.id)), comments.filter(c => Array(3, 4, 5).contains(c.id))))
      assert(eq(y.get(5).get, posts.filter(p => Array(5).contains(p.id)), sc.emptyRDD))
    }

    scenario("Wpisy z odpowiedziami i komentarzami (drzewo wpisów)") {
      // given
      val posts = sc.parallelize(Seq(
        post(id = 1, postTypeId = 1),
        post(id = 2, postTypeId = 1),
        post(id = 3, postTypeId = 2, parentId = 2),
        post(id = 4, postTypeId = 2, parentId = 2),
        post(id = 5, postTypeId = 1),
        post(id = 6, postTypeId = 2, parentId = 5)
      ))
      val comments = sc.parallelize(Seq(
        comment(id = 1, postId = 1),
        comment(id = 2, postId = 1),
        comment(id = 3, postId = 2),
        comment(id = 4, postId = 2),
        comment(id = 5, postId = 2),
        comment(id = 6, postId = 3),
        comment(id = 7, postId = 6),
        comment(id = 8, postId = 6)
      ))
      // when
      val y = getTexts(posts, comments).collectAsMap()
      // then
      assertResult(3, "Powinna powstać grupa 3 tekstów.")(y.size)
      assert(eq(y.get(1).get, posts.filter(p => Array(1).contains(p.id)), comments.filter(c => Array(1, 2).contains(c.id))))
      assert(eq(y.get(2).get, posts.filter(p => Array(2, 3, 4).contains(p.id)), comments.filter(c => Array(3, 4, 5, 6).contains(c.id))))
      assert(eq(y.get(5).get, posts.filter(p => Array(5, 6).contains(p.id)), comments.filter(c => Array(7, 8).contains(c.id))))
    }
  }

  feature("Słowa kluczowe") {

    // przygotowanie testowego słownika
    val dictionary: InvertedDictionary = Map(
      "Polska" -> "Polska",
      "Polacy" -> "Polska",
      "Polskę" -> "Polska",
      "polskie" -> "Polska",
      "Niemcy" -> "Niemcy",
      "niemiecka" -> "Niemcy",
      "Niemiecka" -> "Niemcy",
      "Niemcami" -> "Niemcy",
      "Rosją" -> "Rosja"
    )

    scenario("Tekst zawierający wiele linii") {
      // given
      val text: Text = "Wybuch wojny.\nNiemiecka agresja na Polskę w '39 r. rozpoczęła II wojnę światową."
      // when
      val y = getKeywordsSet(text, dictionary)
      // then
      assertResult(Set("Polska", "Niemcy"), "Polska i Niemcy")(y)
    }

    scenario("Wystąpienie kilku synonimów dla jednego znaczenia") {
      // given
      val text: Text = "Polska to piękny kraj, a Polacy to wspaniali ludzie."
      // when
      val y = getKeywordsSet(text, dictionary)
      // then
      assertResult(Set("Polska"))(y)
    }

    scenario("Wiele różnych słów kluczowych") {
      // given
      val text: Text = "Polska graniczy z Niemcami, Czechami, Słowacją, Ukrainą, Białorusią i Rosją."
      // when
      val y = getKeywordsSet(text, dictionary)
      // then
      assertResult(Set("Polska", "Niemcy", "Rosja"))(y)
    }

  }

}