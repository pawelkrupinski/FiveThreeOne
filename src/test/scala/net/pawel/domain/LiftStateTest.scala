package net.pawel.domain

import org.junit.Test
import org.specs2.matcher.JUnitMustMatchers
import org.joda.time.LocalDate

class LiftStateTest extends JUnitMustMatchers {

  @Test
  def transitions {
    val state = new LiftState(BigDecimal(100), BigDecimal("2.5"), Five, Squat)
    val (session, nextState) = state.nextSession
    session.date mustEqual(new LocalDate())
    session.exercise mustEqual(Squat)
    session.sets mustEqual(Sets(List(
      Set(BigDecimal("35"), 5),
      Set(BigDecimal("50"), 5)
    ),
    List(
      Set(BigDecimal("65"), 5),
      Set(BigDecimal("75"), 5),
      Set(BigDecimal("85"), 5)
    )))

    nextState.oneRepMax mustEqual(BigDecimal(100))
    nextState.increment mustEqual(BigDecimal("2.5"))
    nextState.session mustEqual(Three)
    nextState.exercise mustEqual(Squat)

    val (nextSession, nextNextState) = nextState.nextSession

    nextSession.date mustEqual(new LocalDate())
    nextSession.exercise mustEqual(Squat)
    nextSession.sets mustEqual(Sets(List(
      Set(BigDecimal("40"), 5),
      Set(BigDecimal("55"), 5)
    ),
    List(
      Set(BigDecimal("70"), 3),
      Set(BigDecimal("80"), 3),
      Set(BigDecimal("90"), 3)
    )))
  }
}
