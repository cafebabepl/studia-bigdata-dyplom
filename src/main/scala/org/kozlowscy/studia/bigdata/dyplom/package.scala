package org.kozlowscy.studia.bigdata

package object dyplom {

  def * : Any => Boolean = _ => true

  // definicja operatora potoku (http://codereview.stackexchange.com/questions/26707/pipeline-operator-in-scala)
  implicit class Pipeline[A](val value: A) extends AnyVal {
    def |>[B] (f: A => B) = f(value)
  }

  type Text = String
  type Dictionary = Map[String, Set[String]]
  type InvertedDictionary = Map[String, String]

}