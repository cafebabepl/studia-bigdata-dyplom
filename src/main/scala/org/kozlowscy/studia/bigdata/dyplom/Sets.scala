package org.kozlowscy.studia.bigdata.dyplom

object Sets {

  /**
    * Silnia liczby naturalnej n.
    *
    * @param n liczba n
    * @return silnia liczby naturalnej
    */
  def !>(n: Int): BigInt = n match {
    case 0 => 1
    case _ => n * !>(n - 1)
  }

  /**
    * Liczba permutacji bez powtórzeń.
    *
    * @param n liczba elementów zbioru
    * @return liczba permutacji bez powtórzeń
    */
  def P(n: Int): BigInt = !>(n)

  /**
    * Liczba wariacji bez powtórzeń.
    *
    * @param n liczba elementów zbioru
    * @param k liczba elementów wariacji
    * @return liczba wariacji bez powtórzeń
    */
  def V(n: Int, k: Int): BigInt = !>(n) / !>(n - k)
  // nPr

  /**
    * Liczba kombinacji bez powtórzeń.
    *
    * @param n liczba elementów zbioru
    * @param k liczba elementów kombinacji
    * @return liczba kombinacji bez powtórzeń
    */
  def C(n: Int, k: Int): BigInt = {
    if (k <= n)
      !>(n) / (!>(k) * !>(n - k))
    else
      0
  }
  // nCr

  /**
    * Wariacja bez powtórzeń.
    * https://pl.wikipedia.org/wiki/Wariacja_bez_powt%C3%B3rze%C5%84
    */
  def variations[A](x: Set[A]): Set[(A, A)] = {
    for (a <- x; b <- x; if a != b) yield (a, b)
  }

  /**
    * Kombinacja bez powtórzeń.
    * https://pl.wikipedia.org/wiki/Kombinacja_bez_powt%C3%B3rze%C5%84
    */
  def combinations[A](x: Set[A]): Set[(A, A)] = {
    x.toSeq.combinations(2).map{ case Seq(a, b) => (a, b)}.toSet
  }

}