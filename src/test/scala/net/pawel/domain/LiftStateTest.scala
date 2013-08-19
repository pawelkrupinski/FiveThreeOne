package net.pawel.domain

import org.junit.Test
import org.specs2.matcher.JUnitMustMatchers
import org.joda.time.LocalDate

class LiftStateTest extends JUnitMustMatchers {

  class UUIDGenerator extends Function0[String] {
    var next = 1
    def apply(): String = {
      val result = next.toString
      next += 1
      result
    }
  }

  @Test
  def transitions {
    val state = new LiftState(BigDecimal(100), BigDecimal("2.5"), Five, Squat, new UUIDGenerator)
    val (sessionFive, state2) = state.nextSession
    sessionFive.date mustEqual(new LocalDate())
    sessionFive.exercise mustEqual(Squat)
    sessionFive.sets mustEqual(Sets(List(
      Set("1", BigDecimal("35"), 5),
      Set("2", BigDecimal("50"), 5)
    ),
    List(
      Set("3", BigDecimal("65"), 5),
      Set("4", BigDecimal("75"), 5),
      Set("5", BigDecimal("85"), 5)
    )))

    state2.oneRepMax mustEqual(BigDecimal(100))
    state2.increment mustEqual(BigDecimal("2.5"))
    state2.session mustEqual(Three)
    state2.exercise mustEqual(Squat)

    val (sessionThree, state3) = state2.nextSession

    sessionThree.date mustEqual(new LocalDate())
    sessionThree.exercise mustEqual(Squat)
    sessionThree.sets mustEqual(Sets(List(
      Set("6", BigDecimal("40"), 5),
      Set("7", BigDecimal("55"), 5)
    ),
    List(
      Set("8", BigDecimal("70"), 3),
      Set("9", BigDecimal("80"), 3),
      Set("10", BigDecimal("90"), 3)
    )))

    val (sessionOne, state4) = state3.nextSession

    sessionOne.date mustEqual(new LocalDate())
    sessionOne.exercise mustEqual(Squat)
    sessionOne.sets mustEqual(Sets(List(
      Set("11", BigDecimal("30"), 5),
      Set("12", BigDecimal("45"), 5),
      Set("13", BigDecimal("60"), 5)
    ),
    List(
      Set("14", BigDecimal("75"), 5),
      Set("15", BigDecimal("85"), 3),
      Set("16", BigDecimal("95"), 1)
    )))

    val (sessionDeload, state5) = state4.nextSession

    sessionDeload.date mustEqual(new LocalDate())
    sessionDeload.exercise mustEqual(Squat)
    sessionDeload.sets mustEqual(Sets(List[Set](),
    List(
      Set("17", BigDecimal("40"), 5),
      Set("18", BigDecimal("50"), 5),
      Set("19", BigDecimal("60"), 5)
    )))

    val (nextSessionFive, _) = state5.nextSession

    nextSessionFive.date mustEqual(new LocalDate())
    nextSessionFive.exercise mustEqual(Squat)
    nextSessionFive.sets mustEqual(Sets(List(
      Set("20", BigDecimal("37.5"), 5),
      Set("21", BigDecimal("52.5"), 5)
    ),
      List(
        Set("22", BigDecimal("67.5"), 5),
        Set("23", BigDecimal("77.5"), 5),
        Set("24", BigDecimal("87.5"), 5)
      )))
  }
}
