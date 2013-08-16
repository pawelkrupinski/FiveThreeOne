package net.pawel.domain

import org.junit.Test
import org.specs2.matcher.JUnitMustMatchers

class SetMultiplierTest extends JUnitMustMatchers {

  @Test
  def testRounding {
    val roundTo = BigDecimal("2.5")
    SetMultiplier("0.6").weight(roundTo * (BigDecimal("1") / BigDecimal("0.6"))) mustEqual(roundTo)
    SetMultiplier("0.6").weight(BigDecimal("10")) mustEqual(BigDecimal("7.5"))
    SetMultiplier("0.75").weight(BigDecimal("10")) mustEqual(BigDecimal("7.5"))
    SetMultiplier("0.76").weight(BigDecimal("10")) mustEqual(BigDecimal("10"))
    SetMultiplier("0.99999").weight(BigDecimal("10")) mustEqual(BigDecimal("10"))
    SetMultiplier("0.1").weight(BigDecimal("99")) mustEqual(BigDecimal("10"))
  }
}
