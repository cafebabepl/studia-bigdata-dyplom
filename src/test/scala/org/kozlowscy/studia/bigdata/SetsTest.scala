package org.kozlowscy.studia.bigdata

import org.scalatest.{FeatureSpec, GivenWhenThen}
import org.kozlowscy.studia.bigdata.dyplom.Sets._

class SetsTest extends FeatureSpec with GivenWhenThen {

  feature("Silnia") {
    assertResult(1)(!>(0))
    assertResult(6)(!>(3))
    assertResult(362880)(!>(9))
    assertResult(6227020800L)(!>(13))
    assertResult(2432902008176640000L)(!>(20))
    assertResult(BigInt("608281864034267560872252163321295376887552831379210240000000000"))(!>(49))
    assertResult(BigInt("306057512216440636035370461297268629388588804173576999416776741259476533176716867465515291422477573349939147888701726368864263907759003154226842927906974559841225476930271954604008012215776252176854255965356903506788725264321896264299365204576448830388909753943489625436053225980776521270822437639449120128678675368305712293681943649956460498166450227716500185176546469340112226034729724066333258583506870150169794168850353752137554910289126407157154830282284937952636580145235233156936482233436799254594095276820608062232812387383880817049600000000000000000000000000000000000000000000000000000000000000000000000000"))(!>(300))
  }

  feature("Kombinacje bez powtórzeń") {
    scenario("liczba kombinacji bez powtórzeń") {
      // http://www.naukowiec.org/kalkulatory/kombinatoryka.html
      Then("liczba kombinacji 2-elementowych zbioru 4-elementowego")
      assertResult(6)(C(4, 2))
      Then("liczba kombinacji 6-elementowych zbioru 49-elementowego (liczba wyników losowań Lotto)")
      assertResult(13983816)(C(49, 6))
      assertResult(20)(C(6, 3))
    }

    scenario("Kombinacja bez powtórzeń zbioru {a, b, c, d}") {
      // given
      val x = Set("a", "b", "c", "d")
      // when
      val y = combinations(x)
      // then
      assertResult(6)(y.size)
      val e = Set(("a", "b"), ("a", "c"), ("a", "d"), ("b", "c"), ("b", "d"), ("c", "d"))
      assert(e.subsetOf(y))
    }

  }

  feature("Wariacje bez powtórzeń") {
    scenario("liczba wariacji bez powtórzeń") {
      assertResult(120)(V(6, 3))
      assertResult(210)(V(7, 3))
      assertResult(156)(V(13, 2))
      assertResult(600)(V(25, 2))
      assertResult(4032)(V(64, 2))
    }

    scenario("Wariacja bez powtórzeń zbioru {a, b, c}") {
      // given
      val x = Set("a", "b", "c")
      // when
      val y = variations(x)
      // then
      assertResult(6)(y.size)
      val e = Set(("a", "b"), ("a", "c"), ("b", "a"), ("b", "c"), ("c", "a"), ("c", "b"))
      assert(e === y)
    }
  }

}